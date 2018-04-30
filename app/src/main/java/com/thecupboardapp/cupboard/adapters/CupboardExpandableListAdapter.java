package com.thecupboardapp.cupboard.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.activities.ManualEntryActivity;
import com.thecupboardapp.cupboard.database.Database;
import com.thecupboardapp.cupboard.fragments.CupboardFragment;
import com.thecupboardapp.cupboard.models.FoodItem;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;
import com.thecupboardapp.cupboard.utils.DateUtilities;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kyle on 4/19/2018.
 */

public class CupboardExpandableListAdapter extends BaseExpandableListAdapter {
    public static final String TAG = "ExpandableFoodAdapter";
    private final LayoutInflater mLayoutInflater;
    private Context mContext;
    private CupboardFragment mFragment;

    private List<FoodItem> mFoodItems;
    private List<FoodItem> mQueryFoodItems;

    public CupboardExpandableListAdapter(Context context, List<FoodItem> foodItems, CupboardFragment fragment) {
        mContext = context;
        mFragment = fragment;
        mLayoutInflater = LayoutInflater.from(context);
        mFoodItems = foodItems;
        mQueryFoodItems = new ArrayList<>(mFoodItems);
    }

    @Override
    public int getGroupCount() {
        return mQueryFoodItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public FoodItem getGroup(int groupPosition) {
        return mQueryFoodItems.get(groupPosition);
    }

    //Returns text that child of FoodItem will display
    @Override
    public String getChild(int groupPosition, int childPosition) {
        FoodItem item = mQueryFoodItems.get(groupPosition);
        String date, quantity;

        if (item.getExpiration() == 0) {
            date = "Never";
        } else {
            date = DateUtilities.longToDate(item.getExpiration());
        }

        quantity = Float.toString(item.getQuantity()) + " " + item.getUnits();

        return String.format("Quantity: %s\nExpires: %s\nDate Added: %s\nDescription: %s",
                quantity,
                date,
                DateUtilities.longToDate(item.getDateAdded()),
                item.getDescription());
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    //Returns view of child of FoodItem
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        FoodItem foodItem = mQueryFoodItems.get(groupPosition);

        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.holder_food_child, parent, false);
            holder = new ViewHolder();

            holder.text = convertView.findViewById(R.id.lblListItem);
            holder.editButton = convertView.findViewById(R.id.edit_food_button);
            holder.addToListButton = convertView.findViewById(R.id.add_food_to_list_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(getChild(groupPosition, childPosition));

        //Pressing editButton sends user to ManualEntry Screen with data from item pressed
        holder.editButton.setFocusable(false);
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ManualEntryActivity.class);
            intent.putExtra(ManualEntryActivity.FOOD_ID_EXTRA_KEY, foodItem.getId());
            mFragment.startActivityForResult(intent, ManualEntryActivity.EDIT_ENTRY_REQUEST);
        });

        //Pressing addToListButton displays dialog to choose which list to add to
        holder.addToListButton.setFocusable(false);
        holder.addToListButton.setOnClickListener(v -> {
            Database.getDatabase(mContext).sListDao().getAllSingle()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(sLists -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(String.format("Which list would you like to add \"%s\" to?", foodItem.getName()));

                        List<String> choices = new ArrayList<>();
                        for (SList sList: sLists) {
                            choices.add(sList.getName());
                        }
                        String[] arr = choices.toArray(new String[choices.size()]);

                        builder.setItems(arr, (dialog, which) -> {
                            SListItem sListItem = new SListItem(foodItem.getName(), false);
                            sListItem.setParentId(sLists.get(which).getId());
                            AsyncTask.execute( () -> {
                                Database.getDatabase(mContext).sListItemDao().insertAll(sListItem);
                            });

                            Toast.makeText(mContext, String.format("%s added to: %s", foodItem.getName(), arr[which]), Toast.LENGTH_SHORT).show();
                        });
                        builder.show();
                    });
        });

        return convertView;
    }

    //Returns View for group
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        FoodItem foodItem = mQueryFoodItems.get(groupPosition);

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.holder_food_title, parent, false);

            holder = new ViewHolder();
            holder.text = convertView.findViewById(R.id.lblListHeader);
            holder.deleteButton = convertView.findViewById(R.id.delete_food_button);
            holder.quantityButton = convertView.findViewById(R.id.quantity_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //pressing quantityButton displays dialog with picker to change quantity of food
        holder.quantityButton.setFocusable(false);
        holder.quantityButton.setText(Float.toString(foodItem.getQuantity()));
        holder.quantityButton.setOnClickListener(view -> {
            final NumberPicker numberPicker = new NumberPicker(mContext);
            final NumberPicker.OnValueChangeListener valueChangeListener = (picker, oldVal, newVal) -> {
                foodItem.setQuantity(newVal);
                AsyncTask.execute(() -> Database.getDatabase(mContext).foodItemDao().update(foodItem));
            };

            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(999);
            numberPicker.setValue((int) foodItem.getQuantity());

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(numberPicker);
            builder.setTitle("Current Quantity: "
                    + Float.toString(foodItem.getQuantity())
                    + " " + foodItem.getUnits());
            builder.setMessage("For fractions, you'll need to navigate to the \"Edit Food\" screen");
            builder.setPositiveButton("OK", (dialog, which) -> {
                valueChangeListener.onValueChange(numberPicker, numberPicker.getValue(), numberPicker.getValue());
            });
            builder.setView(numberPicker);
            builder.show();
        });

        holder.text.setText(getGroup(groupPosition).getName());
        holder.deleteButton.setFocusable(false);
        holder.deleteButton.setOnClickListener(v -> {
            AsyncTask.execute(()-> {
                Database.getDatabase(mContext).foodItemDao().delete(foodItem);
            });
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    //Filtering method for search bar, displays only items that contain 'query' in name of item
    public void filterDataByQuery(String query) {
        query = query.toLowerCase();
        mQueryFoodItems.clear();

        if(query.isEmpty()){
            mQueryFoodItems = new ArrayList<>(mFoodItems);
        }
        else {
            for (FoodItem foodItem: mFoodItems) {
                if (foodItem.getName().toLowerCase().contains(query)) {
                    mQueryFoodItems.add(foodItem);
                }
            }
        }

        notifyDataSetChanged();
    }

    //Filtering method for categories, displays only items with category 'category' (or all)
    public void filterDataByCategory(String category) {
        mQueryFoodItems.clear();
        if (category.equals("All")) {
            mQueryFoodItems = new ArrayList<>(mFoodItems);
        } else {
            for (FoodItem foodItem: mFoodItems){
                if (foodItem.getCategory().equals(category)){
                    mQueryFoodItems.add(foodItem);
                }
            }
        }

        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView text;
        ImageButton deleteButton;
        Button quantityButton;
        ImageButton editButton;
        ImageButton addToListButton;
    }
}

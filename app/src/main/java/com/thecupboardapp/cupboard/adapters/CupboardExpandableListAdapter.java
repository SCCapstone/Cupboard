package com.thecupboardapp.cupboard.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.thecupboardapp.cupboard.R;

import java.util.ArrayList;

/**
 * Created by Kyle on 4/19/2018.
 */

public class CupboardExpandableListAdapter extends BaseExpandableListAdapter {
    private final LayoutInflater inf;
    private String[] groups;
    private String[] fullGroups;
    private String[][] children;
    private String[][] fullChildren;

    public CupboardExpandableListAdapter(Context context, String[] groups, String[] fullGroups,
                                         String[][] children, String[][] fullChildren) {
        this.groups = groups;
        this.fullGroups = fullGroups;
        this.children = children;
        this.fullChildren = fullChildren;

        inf = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return groups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children[groupPosition][childPosition];
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

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inf.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();

            holder.text = convertView.findViewById(R.id.lblListItem);
            holder.editButton = convertView.findViewById(R.id.edit_food_button);
            holder.addToListButton = convertView.findViewById(R.id.add_food_to_list_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(getChild(groupPosition, childPosition).toString());
        holder.editButton.setFocusable(false);
        // holder.editButton.setOnClickListener(v -> {
        //     FoodItem foodToUpdate = UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString());
        //     Intent intent = new Intent(getActivity(), ManualEntryActivity.class);
        //     intent.putExtra("foodName", foodToUpdate.getName());
        //     intent.putExtra("foodExpires", foodToUpdate.getExpirationAsLong());
        //     intent.putExtra("foodQuantity", foodToUpdate.getQuantity());
        //     intent.putExtra("foodCategory", foodToUpdate.getCategory());
        //     intent.putExtra("foodDesc", foodToUpdate.getDescription());
        //     intent.putExtra("requestCode", UPDATE_ENTRY_REQUEST);
        //     startActivityForResult(intent, UPDATE_ENTRY_REQUEST);
        // });

        holder.addToListButton.setFocusable(false);
        // holder.addToListButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         //Toast.makeText(v.getContext(),"Need to add to a list: " + getGroup(groupPosition).toString(),Toast.LENGTH_SHORT).show();
        //         String foodName = getGroup(groupPosition).toString();
        //         final SListItem foodToAdd = new SListItem(foodName);
        //
        //         final CharSequence lists[] = UserData.get(getActivity()).getShoppingListsNames();
        //         final CharSequence choices[] = new CharSequence[lists.length+1];
        //         for(int i=0;i<choices.length;i++){
        //             if (i!=choices.length-1) choices[i] = lists[i];
        //             else choices[i] = "Add to a New List";
        //         }
        //         AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        //         builder.setTitle("Which list would you like to add " + foodName + " to?");
        //         builder.setItems(choices, new DialogInterface.OnClickListener() {
        //             @Override
        //             public void onClick(DialogInterface dialog, int which) {
        //                 //user clicked on choices[which]
        //                 SList list;
        //                 if (which==choices.length-1) {
        //                     list = new SList();
        //                     UserData.get(getActivity()).addShoppingList(list);
        //                 }
        //                 else
        //                 list = UserData.get(getActivity()).getShoppingList(choices[which].toString());
        //                 list.addShoppingListItem(foodToAdd);
        //                 Toast.makeText(getContext(),"Added " + getGroup(groupPosition).toString() +" to "+list.getName(),Toast.LENGTH_SHORT).show();
        //             }
        //         });
        //         builder.show();
        //
        //
        //     }
        // });

        return convertView;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = inf.inflate(R.layout.list_group, parent, false);

            holder = new ViewHolder();
            holder.text = convertView.findViewById(R.id.lblListHeader);
            holder.deleteButton = convertView.findViewById(R.id.delete_food_button);
            holder.quantityButton = convertView.findViewById(R.id.quantity_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.quantityButton.setFocusable(false);
        // holder.quantityButton.setText(String.valueOf((int)(UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString()).getQuantity())));
        // holder.quantityButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         final NumberPicker numberPicker = new NumberPicker(getActivity());
        //         final NumberPicker.OnValueChangeListener valueChangeListener = new NumberPicker.OnValueChangeListener() {
        //             @Override
        //             public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        //                 FoodItem foodToBeChanged = UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString());
        //                 foodToBeChanged.setQuantity(newVal);
        //                 UserData.get(getActivity()).editFoodItemQuantity(foodToBeChanged);
        //             }
        //         };
        //         numberPicker.setMinValue(0);
        //         numberPicker.setMaxValue(999);
        //         numberPicker.setValue((int)(UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString()).getQuantity()));
        //         AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //         builder.setView(numberPicker);
        //         builder.setTitle("Change the quantity");
        //         builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        //             @Override
        //             public void onClick(DialogInterface dialog, int which) {
        //                 valueChangeListener.onValueChange(numberPicker,numberPicker.getValue(),numberPicker.getValue());
        //             }
        //         });
        //         builder.setView(numberPicker);
        //         builder.show();
        //     }
        // });
        holder.text.setText(getGroup(groupPosition).toString());
        holder.deleteButton.setFocusable(false);
        holder.deleteButton.setOnClickListener(v -> {
            // FoodItem foodToDelete = UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString());
            // UserData.get(getActivity()).removeFoodItem(foodToDelete);
            // onActivityResult(NEW_ENTRY_REQUEST,RESULT_OK,null);
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void filterData(String query){

        query = query.toLowerCase();
        Log.v("MyListAdapter", String.valueOf(groups.length));
        String[] newGroups;
        String[][] newChildren;
        ArrayList<String> newGroupsList = new ArrayList<>();
        ArrayList<String> newChildrenList = new ArrayList<>();

        if(query.isEmpty()){
            // updateFoods();
            newGroups = fullGroups;
            newChildren = fullChildren;
        }
        else {
            int i=0;
            for(String foodName: fullGroups){
                if(foodName.toLowerCase().contains(query)){
                    newGroupsList.add(foodName);
                    newChildrenList.add(fullChildren[i][0]);
                }
                i++;
            }
            newGroups = new String[newGroupsList.size()];
            newChildren = new String[newChildrenList.size()][1];
            for(i=0;i<newGroupsList.size();i++){
                newGroups[i] = newGroupsList.get(i);
                newChildren[i][0] = newChildrenList.get(i);
            }

        }

        Log.v("MyListAdapter", String.valueOf(newGroupsList.size()));
        groups = newGroups;
        children = newChildren;
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

package com.thecupboardapp.cupboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Kyle on 1/12/2018.
 */

public class CupboardFragment extends Fragment
implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{

    ExpandableListView lv;
    private String[] groups;
    private String[] fullGroups;
    private String[][] children;
    private ExpandableListAdapter mAdapter;
    private FloatingActionButton manEntFAB;
    private int NEW_ENTRY_REQUEST = 0;
    private int UPDATE_ENTRY_REQUEST = 1;
    long NO_EXP_DATE = 4133987474999L;
    private List<FoodItem> mFoodItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateFoods();
        setListener();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.cupboard_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

    }

    @Override
    public boolean onClose() {
        mAdapter.filterData("");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mAdapter.filterData(query);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.filterData(query);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_sort_alphabetically:
                sortAlphabetically();
                return true;
            case R.id.menu_sort_expires_soon:
                sortExpiresSoon();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sortAlphabetically(){
        UserData.get(getActivity()).sortFoodItems("alphabetically");
        onActivityResult(NEW_ENTRY_REQUEST,RESULT_OK,null);
    }

    public void sortExpiresSoon(){
        UserData.get(getActivity()).sortFoodItems("expiresSoon");
        onActivityResult(NEW_ENTRY_REQUEST,RESULT_OK,null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_ENTRY_REQUEST || requestCode == UPDATE_ENTRY_REQUEST) {
            if (resultCode == RESULT_OK) {
                updateFoods();
                //mAdapter.notifyDataSetChanged();
                mAdapter = new ExpandableListAdapter(groups, fullGroups, children);
                lv.setAdapter(mAdapter);
            }
        }
    }

    public void setListener(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference refFood = database.getReference("foods/" + user.getUid());
        refFood.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onActivityResult(NEW_ENTRY_REQUEST,RESULT_OK,null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateFoods() {
        getActivity().setTitle(R.string.title_cupboard);
        mFoodItems = UserData.get(getActivity()).getFoodItems();
        groups = new String[mFoodItems.size()];
        fullGroups = new String[mFoodItems.size()];
        children = new String[mFoodItems.size()][1];

        for(int i=0;i<mFoodItems.size();i++){
            groups[i] = mFoodItems.get(i).getName();
            fullGroups[i] = groups[i];

            String info = "Expires: ";
            if(mFoodItems.get(i).getExpirationAsLong() == NO_EXP_DATE) {info = info.concat("Never");}
            else {info = info.concat(mFoodItems.get(i).getExpirationAsString());}

            info = info.concat("\nDate Added: " + mFoodItems.get(i).getDateAddedAsString());

            children[i][0] = info;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            View v = inflater.inflate(R.layout.sign_in_fragment, container, false);
            return v;
        }

        View v = inflater.inflate(R.layout.cupboard_fragment, container, false);
        return v;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new ExpandableListAdapter(groups, fullGroups, children);
        lv = (ExpandableListView) view.findViewById(R.id.accordion);
        lv.setAdapter(mAdapter);
        lv.setGroupIndicator(null);

        manEntFAB = (FloatingActionButton)view.findViewById(R.id.add_food_fab);
        manEntFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManualEntry.class);
                intent.putExtra("requestCode", NEW_ENTRY_REQUEST);
                startActivityForResult(intent, NEW_ENTRY_REQUEST);
            }
        });

    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private final LayoutInflater inf;
        private String[] groups;
        private String[] fullGroups;
        private String[][] children;

        public ExpandableListAdapter(String[] groups, String[] fullGroups, String[][] children) {
            this.groups = groups;
            this.fullGroups = fullGroups;
            this.children = children;
            inf = LayoutInflater.from(getActivity());
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
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = inf.inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();

                holder.text = (TextView) convertView.findViewById(R.id.lblListItem);
                holder.editButton = (ImageButton) convertView.findViewById(R.id.edit_food_button);
                holder.addToListButton = (ImageButton) convertView.findViewById(R.id.add_food_to_list_button);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(getChild(groupPosition, childPosition).toString());
            holder.editButton.setFocusable(false);
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(),"Need to update " + getGroup(groupPosition).toString(),Toast.LENGTH_SHORT).show();
                    FoodItem foodToUpdate = UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString());
                    Intent intent = new Intent(getActivity(), ManualEntry.class);
                    intent.putExtra("foodName", foodToUpdate.getName());
                    intent.putExtra("foodExpires", foodToUpdate.getExpirationAsLong());
                    intent.putExtra("foodQuantity", foodToUpdate.getQuantity());
                    intent.putExtra("foodCategory", foodToUpdate.getCategory());
                    intent.putExtra("requestCode", UPDATE_ENTRY_REQUEST);
                    startActivityForResult(intent, UPDATE_ENTRY_REQUEST);
                }
            });

            holder.addToListButton.setFocusable(false);
            holder.addToListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(),"Need to add to a list: " + getGroup(groupPosition).toString(),Toast.LENGTH_SHORT).show();
                    String foodName = getGroup(groupPosition).toString();
                    final ShoppingListItem foodToAdd = new ShoppingListItem(foodName);

                    final CharSequence lists[] = UserData.get(getActivity()).getShoppingListsNames();
                    final CharSequence choices[] = new CharSequence[lists.length+1];
                    for(int i=0;i<choices.length;i++){
                        if (i!=choices.length-1) choices[i] = lists[i];
                        else choices[i] = "Add to a New List";
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Which list would you like to add " + foodName + " to?");
                    builder.setItems(choices, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //user clicked on choices[which]
                            ShoppingList list;
                            if (which==choices.length-1) {
                                list = new ShoppingList();
                                UserData.get(getActivity()).addShoppingList(list);
                            }
                            else
                            list = UserData.get(getActivity()).getShoppingList(choices[which].toString());
                            list.addShoppingListItem(foodToAdd);
                            Toast.makeText(getContext(),"Added " + getGroup(groupPosition).toString() +" to "+list.getName(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();


                }
            });

            return convertView;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                convertView = inf.inflate(R.layout.list_group, parent, false);

                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.lblListHeader);
                holder.deleteButton = (ImageButton) convertView.findViewById(R.id.delete_food_button);
                holder.numPicker = (NumberPicker) convertView.findViewById(R.id.numPicker);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.numPicker.setMinValue(1);
            holder.numPicker.setMaxValue(1000);
            holder.numPicker.setValue((int)(UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString()).getQuantity()));
            holder.numPicker.setClickable(true);

            holder.numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
               public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                   FoodItem foodToBeChanged = UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString());
                   foodToBeChanged.setQuantity(newVal);
                   UserData.get(getActivity()).editFoodItemQuantity(foodToBeChanged);
               }
            });
            /*holder.numPicker.setOnClickListener(new View.OnClickListener() {
            holder.numPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            holder.numPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog d = new Dialog(getContext());
                    d.setTitle("number Picker");
                    d.setContentView(R.layout.dialogpicker);
                    Button b1 = (Button) d.findViewById(R.id.button1);
                    Button b2 = (Button) d.findViewById(R.id.button2);
                    final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
                    np.setMaxValue(1000); // max value 100
                    np.setMinValue(0);   // min value 0
                    np.setValue(holder.numPicker.getValue());
                    //np.setWrapSelectorWheel(false);
                    //np.setOnValueChangedListener(np);
                    b1.setOnClickListener(new Button.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) {
                            FoodItem foodToBeChanged = UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString());
                            foodToBeChanged.setQuantity(np.getValue());
                            Log.d("npval: ", Integer.toString(np.getValue()));
                            UserData.get(getActivity()).editFoodItemQuantity(foodToBeChanged);
                            holder.numPicker.setValue(np.getValue());
                            //tv.setText(String.valueOf(np.getValue())); //set the value to textview
                            d.dismiss();
                        }
                    });
                    b2.setOnClickListener(new Button.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) {
                            d.dismiss(); // dismiss the dialog
                        }
                    });
                    d.show();
                    *//*final NumberPicker numberPicker = new NumberPicker(getActivity());
                    final NumberPicker.OnValueChangeListener valueChangeListener = new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            FoodItem foodToBeChanged = UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString());
                            foodToBeChanged.setQuantity(newVal);
                            UserData.get(getActivity()).editFoodItemQuantity(foodToBeChanged);
                        }
                    };
                    numberPicker.setMinValue(1);
                    numberPicker.setMaxValue(1000);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(numberPicker);
                    builder.setTitle("Change the quantity");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dialogHost.onPositiveButton(numberPicker.getValue());
                            valueChangeListener.onValueChange(numberPicker,numberPicker.getValue(),numberPicker.getValue());

                        }
                    });
                    builder.setView(numberPicker);
                    builder.show();*//*
                }
            });*/
            holder.numPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            holder.numPicker.setFocusable(false);
            holder.text.setText(getGroup(groupPosition).toString());
            holder.deleteButton.setFocusable(false);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(),"Need to delete " + getGroup(groupPosition).toString(),Toast.LENGTH_SHORT).show();
                    FoodItem foodToDelete = UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString());
                    UserData.get(getActivity()).removeFoodItem(foodToDelete);
                    onActivityResult(NEW_ENTRY_REQUEST,RESULT_OK,null);
                }
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
            ArrayList<String> newGroupsList = new ArrayList<>();

            if(query.isEmpty()){
                updateFoods();
                newGroups = fullGroups;
            }
            else {

                for(String foodName: fullGroups){

                    if(foodName.toLowerCase().contains(query)){
                        newGroupsList.add(foodName);
                    }

                }
                newGroups = new String[newGroupsList.size()];
                for(int i=0;i<newGroupsList.size();i++){
                    newGroups[i] = newGroupsList.get(i);
                }

            }

            Log.v("MyListAdapter", String.valueOf(newGroupsList.size()));
            groups = newGroups;
            notifyDataSetChanged();

        }

        private class ViewHolder {
            TextView text;
            ImageButton deleteButton;
            NumberPicker numPicker;
            ImageButton editButton;
            ImageButton addToListButton;
        }
    }
}


package com.thecupboardapp.cupboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
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

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Kyle on 1/12/2018.
 */

public class CupboardFragment extends Fragment
implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{

    ExpandableListView lv;
    private String mCategoryShown = "";
    private String[] groups;
    private String[] fullGroups;
    private String[][] children;
    private String[][] fullChildren;
    private ExpandableListAdapter mAdapter;
    private FloatingActionButton manEntFAB;
    private int NEW_ENTRY_REQUEST = 0;
    private int UPDATE_ENTRY_REQUEST = 1;
    private int SHOW_REQUEST = 2;
    long NO_EXP_DATE = 4133987474999L;
    private List<FoodItem> mFoodItems;
    Activity mActivity; // ONLY USE WHEN NECESSARY, USE GETACTIVITY EVERYWHERE ELSE

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            updateFoods();
            setListener();
            getActivity().setTitle(R.string.title_cupboard);
        }
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
            case R.id.menu_show_pantry:
                showByCategory("Pantry");
                return true;
            case R.id.menu_show_fridge:
                showByCategory("Fridge");
                return true;
            case R.id.menu_show_freezer:
                showByCategory("Freezer");
                return true;
            case R.id.menu_show_all:
                showAll();
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

    public void showByCategory(String aCategory){
        String[] newGroups;
        ArrayList<FoodItem> newGroupsList = new ArrayList<>();

        for(FoodItem item: mFoodItems){

            if(item.getCategory().equals(aCategory)){
                newGroupsList.add(item);
            }

        }
        newGroups = new String[newGroupsList.size()];
        for(int i=0;i<newGroupsList.size();i++){
            newGroups[i] = newGroupsList.get(i).getName();
        }

        mCategoryShown = aCategory;
        groups = newGroups;
        onActivityResult(SHOW_REQUEST,RESULT_OK,null);
    }

    public void showAll(){
        mCategoryShown = "";
        onActivityResult(NEW_ENTRY_REQUEST,RESULT_OK,null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_ENTRY_REQUEST || requestCode == UPDATE_ENTRY_REQUEST) {
            if (resultCode == RESULT_OK) {
                updateFoods();
                mAdapter = new ExpandableListAdapter(groups, fullGroups, children, fullChildren);
                lv.setAdapter(mAdapter);
            }
        }
        else if(requestCode == SHOW_REQUEST){
            if (resultCode == RESULT_OK) {
                mAdapter = new ExpandableListAdapter(groups, fullGroups, children, fullChildren);
                lv.setAdapter(mAdapter);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();

    }

    public void setListener(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference refFood;
        if(user!=null){
            refFood = database.getReference("foods/" + user.getUid());
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
    }

    public void updateFoods() {
        mFoodItems = UserData.get(getActivity()).getFoodItems();
        groups = new String[mFoodItems.size()];
        fullGroups = new String[mFoodItems.size()];
        children = new String[mFoodItems.size()][1];
        fullChildren = new String[mFoodItems.size()][1];

        for(int i=0;i<mFoodItems.size();i++){
            groups[i] = mFoodItems.get(i).getName();
            fullGroups[i] = groups[i];

            String info = "Expires: ";
            if(mFoodItems.get(i).getExpirationAsLong() == NO_EXP_DATE) {info = info.concat("Never");}
            else {info = info.concat(mFoodItems.get(i).getExpirationAsString());}

            info = info.concat("\nDate Added: " + mFoodItems.get(i).getDateAddedAsString());
            info = info.concat("\nDescription: " + mFoodItems.get(i).getDescription());
            children[i][0] = info;
            fullChildren[i][0] = children[i][0];
        }
        if(!mCategoryShown.equals("")) showByCategory(mCategoryShown);
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
        mAdapter = new ExpandableListAdapter(groups, fullGroups, children, fullChildren);
        lv = view.findViewById(R.id.accordion);
        if(lv!=null){
            lv.setAdapter(mAdapter);
            lv.setGroupIndicator(null);
        }

        manEntFAB = view.findViewById(R.id.add_food_fab);
        if(manEntFAB!=null)
        manEntFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManualEntry.class);
                intent.putExtra(getString(R.string.request_code), NEW_ENTRY_REQUEST);
                startActivityForResult(intent, NEW_ENTRY_REQUEST);
            }
        });

    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private final LayoutInflater inf;
        private String[] groups;
        private String[] fullGroups;
        private String[][] children;
        private String[][] fullChildren;

        public ExpandableListAdapter(String[] groups, String[] fullGroups, String[][] children, String[][] fullChildren) {
            this.groups = groups;
            this.fullGroups = fullGroups;
            this.children = children;
            this.fullChildren = fullChildren;
            // NOTE: ONLY PLACE THAT USES mActivity IS NEXT LINE BECAUSE OF BUG
            inf = LayoutInflater.from(mActivity);
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

                holder.text = convertView.findViewById(R.id.lblListItem);
                holder.editButton = convertView.findViewById(R.id.edit_food_button);
                holder.addToListButton = convertView.findViewById(R.id.add_food_to_list_button);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(getChild(groupPosition, childPosition).toString());
            holder.editButton.setFocusable(false);
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FoodItem foodToUpdate = UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString());
                    Intent intent = new Intent(getActivity(), ManualEntry.class);
                    intent.putExtra(getString(R.string.food_name), foodToUpdate.getName());
                    intent.putExtra(getString(R.string.food_expires),
                            foodToUpdate.getExpirationAsLong());
                    intent.putExtra(getString(R.string.food_quantity), foodToUpdate.getQuantity());
                    intent.putExtra(getString(R.string.food_category), foodToUpdate.getCategory());
                    intent.putExtra(getString(R.string.food_description),
                            foodToUpdate.getDescription());
                    intent.putExtra(getString(R.string.request_code), UPDATE_ENTRY_REQUEST);
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
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("lists")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .push();

                                ref.child("name").setValue(list.getName());
                                ref.child("lastModified").setValue(System.currentTimeMillis());
                                ShoppingListItem item = new ShoppingListItem(foodToAdd.getName());
                                DatabaseReference listref = ref.child("items").push();
                                item.setFirebaseId(listref.getKey());
                                listref.child("name").setValue(item.getName());
                                listref.child("checked").setValue(item.isChecked());
                                
                                list.setFirebaseId(ref.getKey());
                                list.setLastModified(System.currentTimeMillis());

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
                holder.quantityButton = (Button) convertView.findViewById(R.id.quantity_button);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.quantityButton.setFocusable(false);
            holder.quantityButton.setText(String.valueOf((int)(UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString()).getQuantity())));
            holder.quantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final NumberPicker numberPicker = new NumberPicker(getActivity());
                    final NumberPicker.OnValueChangeListener valueChangeListener = new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            FoodItem foodToBeChanged = UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString());
                            foodToBeChanged.setQuantity(newVal);
                            UserData.get(getActivity()).editFoodItemQuantity(foodToBeChanged);
                        }
                    };
                    numberPicker.setMinValue(0);
                    numberPicker.setMaxValue(999);
                    numberPicker.setValue((int)(UserData.get(getActivity()).getFoodItem(getGroup(groupPosition).toString()).getQuantity()));
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(numberPicker);
                    builder.setTitle("Change the quantity");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            valueChangeListener.onValueChange(numberPicker,numberPicker.getValue(),numberPicker.getValue());
                        }
                    });
                    builder.setView(numberPicker);
                    builder.show();
                }
            });
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
            String[][] newChildren;
            ArrayList<String> newGroupsList = new ArrayList<>();
            ArrayList<String> newChildrenList = new ArrayList<>();

            if(query.isEmpty()){
                updateFoods();
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
}


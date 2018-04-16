package com.thecupboardapp.cupboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;

/**
 * Created by Kyle on 1/13/2018.
 * Editied by Jacob Strom on 2/19/2018.
 */

public class HomeFragment extends Fragment{

    private List<FoodItem> mFoods;
    private List<ShoppingList> mLists;
    private TextView mNextExpiring;
    private TextView mLastModifiedList;

    private ExpandableListAdapter mAccordion;
    private ExpandableListView mExpandableListView;
    private List<String> mHeaders;
    private HashMap<String, List<String>> mListChild;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setTitle(R.string.title_dashboard);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            View v = inflater.inflate(R.layout.sign_in_fragment, container, false);
            return v;
        }

        View v = inflater.inflate(R.layout.home_fragment, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            DatabaseReference refFood = database.getReference("foods/" + user.getUid());
            refFood.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<FoodItem> foodItems = new ArrayList<FoodItem>();
                    for (DataSnapshot food : dataSnapshot.getChildren()) {
                        FoodItem foodItem = new FoodItem();

                        foodItem.setFirebaseId(food.getKey());
                        foodItem.setName(food.child("name").getValue().toString());
                        //Log.d("getFoods", food.child("name").getValue().toString());

                        try {
                            //Log.d("getFoods", "entering try block");
                            //Log.d("getFoods", food.child("expirationAsLong").getValue().toS
                            Date dateAdded = new Date(Long.parseLong(food.child("dateAddedAsLong").getValue().toString()));
                            foodItem.setDateAdded(dateAdded);
                            foodItem.setName(food.child("name").getValue().toString());
                            //Log.d("getFoods", food.child("name").getValue().toString());

                        }
                        catch (Exception e) {
                        }

                        foodItems.add(foodItem);
                        prepareAccordion();
                        mAccordion = new ExpandableListAdapter(getContext(), mHeaders, mListChild);

                        mExpandableListView.setAdapter(mAccordion);
                    }
                    UserData.get(getContext()).setFoodItems(foodItems);

                    //updateNextExpiring();
                    //updateLastModifiedList(); //This is the line that needs to be commented out to launch the app

                    prepareAccordion();
                    //mAccordion = new ExpandableListAdapter(getContext(), mHeaders, mListChild);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        //FoodItem f = new FoodItem();
        //UserData.get(getActivity()).addFoodItem(f);

        //mNextExpiring = (TextView) v.findViewById(R.id.next_expiring);
        //mLastModifiedList = (TextView) v.findViewById(R.id.last_modified_list);
        //Log.d("mNextExpiring", mNextExpiring.toString());
        UserData.get(getActivity()).updateFromFirebase();


        mExpandableListView = (ExpandableListView) v.findViewById(R.id.home_fragment_expandable_list_view);
        prepareAccordion();

        mAccordion = new ExpandableListAdapter(getContext(), mHeaders, mListChild);

        mExpandableListView.setAdapter(mAccordion);

        return v;
    }

    private void updateNextExpiring() {
        mFoods = UserData.get(getActivity()).getFoodItems();
        //UserData.get(getA)
        Collections.sort(mFoods);
        //FoodItem mExpFood1 = ;
        try {
            if (!mFoods.isEmpty()) {
            /*Log.d("mFoods", "not null");
            FoodItem f = mFoods.get(0);
            for (FoodItem food: mFoods) {
                Log.d("mFoods", food.getName());
            }*/
                String mNextThreeExpiring = "";
                for (int i = 0; i < 3; ++i){
                    if (mFoods.size() >  i) {
                        mNextThreeExpiring += mFoods.get(i).getName();
                        mNextThreeExpiring += "\n";
                    }
                }
                mNextExpiring.setText(mNextThreeExpiring);
            }
            else {
                //Log.d("mFoods", "mFoods equals null");
                //Log.d("mFoods", "UID = );
                String s = "no foods";
                mNextExpiring.setText(s);
            }
        }
        catch (Exception e) {
            mNextExpiring.setText("error in next expiring");
        }

    }

    private void updateLastModifiedList() {
        mLists = UserData.get(getActivity()).getShoppingLists();
        String s = "";

        try {
            if (mLists.isEmpty()) {
                s = "create a list";
            }

            else {
                Collections.sort(mLists);


                s = mLists.get(0).getName() + "\n";
                List<ShoppingListItem> items = mLists.get(0).getShoppingListItems();
                for ( ShoppingListItem item : items) {
                    s += item.getName() + "\n";
                }
            }
        }
        catch (Exception e) {
            s = "something went wrong";
        }




        mLastModifiedList.setText(s);

    }

    private void prepareAccordion() {


        mHeaders = new ArrayList<String>();
        mListChild = new HashMap<String, List<String>>();

        mHeaders.add("Food Expiring Soon");
        //mHeaders.add("Last Modified Shopping List");

        List<String> mExpiringSoon = setExpiringSoon(7);
        Log.d("mExpiringSoon", "size: "+mExpiringSoon.size());
        mListChild.put(mHeaders.get(0), mExpiringSoon);


        //List<String> mLastShoppingList = setLastShoppingList();

        /*List<String> mDummyList = new ArrayList<String>();
        for (int i = 0; i < 6; ++i) {
            mDummyList.add("food "+i);
        }

        mListChild.put(mHeaders.get(1), mDummyList);*/

        //mAccordion = new ExpandableListAdapter(getContext(), mHeaders, mListChild);

        //mExpandableListView.setAdapter(mAccordion);



        mLists = UserData.get(getActivity()).getShoppingLists();


        try {
            if (mLists.isEmpty()) {
                mHeaders.add("You don't have any Shopping Lists :(");
                ArrayList<String> empty = new ArrayList<String>();
                empty.add("Create a Shopping List to display the last shopping list you modified on the home screen.");
                mListChild.put(mHeaders.get(1), empty);
            }

            else {
                Collections.sort(mLists);

/*
                for (ShoppingList list : mLists) {
                    mHeaders.add(list.getName());
                    ArrayList<String> lastList = new ArrayList<String>();
                    List<ShoppingListItem> items = list.getShoppingListItems();
                    for ( ShoppingListItem item : items) {
                        lastList.add(item.getName());
                    }
                    mListChild.put(mHeaders.get(mHeaders.size()), lastList);
                }
*/


                mHeaders.add("Last Modified List: " + mLists.get(0).getName());
                ArrayList<String> lastList = new ArrayList<String>();
                List<ShoppingListItem> items = mLists.get(0).getShoppingListItems();
                for ( ShoppingListItem item : items) {
                     lastList.add(item.getName());
                }
                mListChild.put(mHeaders.get(1), lastList);

            }
        }
        catch (Exception e) {

        }



    }

    /*private List<String> setLastShoppingList() {
        mLists = UserData.get(getActivity()).getShoppingLists();
        ArrayList<String> retVal = new ArrayList<String>();

        try {
            if (mLists.isEmpty()) {
                retVal.add("You don't have any shopping lists. Go to Shopping Lists to add one.");
            }

            else {
                Collections.sort(mLists);


                s = mLists.get(0).getName() + "\n";
                List<ShoppingListItem> items = mLists.get(0).getShoppingListItems();
                for ( ShoppingListItem item : items) {
                    s += item.getName() + "\n";
                }
            }
        }
        catch (Exception e) {
            s = "something went wrong";
        }
    }*/

    private List<String> setExpiringSoon(int days) {
        mFoods = UserData.get(getActivity()).getFoodItems();
        //UserData.get(getA)
        Collections.sort(mFoods);
        //FoodItem mExpFood1 = ;
        List<String> retVal = new ArrayList<String>();

        long millisPerDay = 86400*1000;
        Log.d("expiringSoon", "millisPerDay\t\t\t" + (Long.toString(millisPerDay)));
        Log.d("expiringSoon", "millisPerWeek\t\t" + (Long.toString(millisPerDay*7)));
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Log.d("expiringSoon", "currentTime\t\t" + (Long.toString(currentTime)));
        long expiringTime = currentTime+(millisPerDay*days);

        try {
            if (!mFoods.isEmpty()) {
                int counter = 0;
                try {
                    Log.d("expiringSoon", mFoods.get(counter).getName() + " expires at \t" + mFoods.get(counter).getExpirationAsLong());
                    while (mFoods.get(counter).getExpirationAsLong() < expiringTime) {
                        String s = formatFood(mFoods.get(counter));
                        retVal.add(s);
                        ++counter;
                    }
                }
                catch (Exception e){

                }

            }
            else {
                //Log.d("mFoods", "mFoods equals null");
                //Log.d("mFoods", "UID = );
                String s = "no foods";
                mNextExpiring.setText(s);
            }
        }
        catch (Exception e) {
           // mNextExpiring.setText("error in next expiring");
        }

        if (retVal.isEmpty()){
            retVal.add("You do not have any food about to expire :)");
        }
        return retVal;
    }

    private String formatFood(FoodItem f) {
        String s = "";
        s += f.getName();
        s += "\nExpiration Date: ";
        s += f.getExpirationAsString();

        return s;
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context mContext;
        private List<String> mListHeaders;
        private HashMap<String, List<String>> mListChild;

        public ExpandableListAdapter(Context context, List<String> listHeaders, HashMap<String, List<String>> listChild) {
            this.mContext = context;
            this.mListHeaders = listHeaders;
            this.mListChild = listChild;
        }

        @Override
        public int getGroupCount() {
            return this.mListHeaders.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            //TODO some problem here, null pointer
            /*if (null == this.mListChild.get(this.getGroup(groupPosition))) {
                return 0;
            }*/
            return this.mListChild.get(this.getGroup(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.mListHeaders.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.mListChild.get(mListHeaders.get(groupPosition)).get(childPosition);
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
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String mHeaderTitle = (String) getGroup(groupPosition);

            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.home_group,null);
            }

            TextView mHomeGroupHeader = (TextView) convertView.findViewById(R.id.home_group_header);
            mHomeGroupHeader.setText(mHeaderTitle);

            return  convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final String mChildText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.home_item, null);
            }

            TextView mChildTextView = (TextView) convertView.findViewById(R.id.home_item_list_item);
            mChildTextView.setText(mChildText);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}





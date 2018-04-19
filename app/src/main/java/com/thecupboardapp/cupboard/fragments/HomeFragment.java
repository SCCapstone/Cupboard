package com.thecupboardapp.cupboard.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.database.Database;
import com.thecupboardapp.cupboard.models.FoodItem;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;

import java.util.List;

/**
 * Created by Kyle on 1/13/2018.
 * Editied by Jacob Strom on 2/19/2018.
 */

public class HomeFragment extends Fragment{

    private List<FoodItem> mFoods;
    private List<SList> mLists;
    private TextView mNextExpiring;
    private TextView mLastModifiedList;

    private Button mTestButton2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setTitle(R.string.title_dashboard);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mTestButton2 = v.findViewById(R.id.test_button_2);
        mTestButton2.setOnClickListener(view -> testButtonPress2());

        // if(user != null) {
        //     DatabaseReference refFood = database.getReference("foods/" + user.getUid());
        //     refFood.addListenerForSingleValueEvent(new ValueEventListener() {
        //         @Override
        //         public void onDataChange(DataSnapshot dataSnapshot) {
        //             List<FoodItem> foodItems = new ArrayList<FoodItem>();
        //             for (DataSnapshot food : dataSnapshot.getChildren()) {
        //                 FoodItem foodItem = new FoodItem();
        //
        //                 foodItem.setFirebaseId(food.getKey());
        //                 foodItem.setName(food.child("name").getValue().toString());
        //                 //Log.d("getFoods", food.child("name").getValue().toString());
        //
        //                 try {
        //                     //Log.d("getFoods", "entering try block");
        //                     //Log.d("getFoods", food.child("expirationAsLong").getValue().toString());
        //                     Date expDate = new Date(Long.parseLong(food.child("expirationAsLong").getValue().toString()));
        //
        //                     foodItem.setExpiration(expDate);
        //                     Date dateAdded = new Date(Long.parseLong(food.child("dateAddedAsLong").getValue().toString()));
        //                     foodItem.setDateAdded(dateAdded);
        //                 } catch (Exception e) {
        //                 }
        //
        //                 foodItems.add(foodItem);
        //             }
        //             UserData.get(getContext()).setFoodItems(foodItems);
        //
        //             updateNextExpiring();
        //             updateLastModifiedList(); //This is the line that needs to be commented out to launch the app
        //         }
        //
        //         @Override
        //         public void onCancelled(DatabaseError databaseError) {
        //
        //         }
        //     });
        // }
        //FoodItem f = new FoodItem();
        //UserData.get(getActivity()).addFoodItem(f);

        // mNextExpiring = (TextView) v.findViewById(R.id.next_expiring);
        // mLastModifiedList = (TextView) v.findViewById(R.id.last_modified_list);
        // Log.d("mNextExpiring", mNextExpiring.toString());
        // UserData.get(getActivity()).updateFromFirebase();

        return v;
    }

    // private void updateNextExpiring() {
    //     mFoods = UserData.get(getActivity()).getFoodItems();
    //     //UserData.get(getA)
    //     Collections.sort(mFoods);
    //     //FoodItem mExpFood1 = ;
    //     try {
    //         if (!mFoods.isEmpty()) {
    //         /*Log.d("mFoods", "not null");
    //         FoodItem f = mFoods.get(0);
    //         for (FoodItem food: mFoods) {
    //             Log.d("mFoods", food.getName());
    //         }*/
    //             String mNextThreeExpiring = "";
    //             for (int i = 0; i < 3; ++i){
    //                 if (mFoods.size() >  i) {
    //                     mNextThreeExpiring += mFoods.get(i).getName();
    //                     mNextThreeExpiring += "\n";
    //                 }
    //             }
    //             mNextExpiring.setText(mNextThreeExpiring);
    //         }
    //         else {
    //             //Log.d("mFoods", "mFoods equals null");
    //             //Log.d("mFoods", "UID = );
    //             String s = "no foods";
    //             mNextExpiring.setText(s);
    //         }
    //     }
    //     catch (Exception e) {
    //         mNextExpiring.setText("error in next expiring");
    //     }
    //
    // }
    //
    // private void updateLastModifiedList() {
    //     mLists = UserData.get(getActivity()).getSLists();
    //     String s = "";
    //
    //     try {
    //         if (mLists.isEmpty()) {
    //             s = "create a list";
    //         }
    //
    //         else {
    //             Collections.sort(mLists);
    //
    //
    //             s = mLists.get(0).getName() + "\n";
    //             List<SListItem> items = mLists.get(0).getShoppingListItems();
    //             for ( SListItem item : items) {
    //                 s += item.getName() + "\n";
    //             }
    //         }
    //     }
    //     catch (Exception e) {
    //         s = "something went wrong";
    //     }
    //
    //
    //
    //
    //     mLastModifiedList.setText(s);
    //
    // }


    private void testButtonPress2(){
        getActivity().deleteDatabase("cupboard");
    }
}

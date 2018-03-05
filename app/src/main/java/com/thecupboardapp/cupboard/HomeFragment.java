package com.thecupboardapp.cupboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setTitle(R.string.title_dashboard);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                            //Log.d("getFoods", food.child("expirationAsLong").getValue().toString());
                            Date expDate = new Date(Long.parseLong(food.child("expirationAsLong").getValue().toString()));

                            foodItem.setExpiration(expDate);
                            Date dateAdded = new Date(Long.parseLong(food.child("dateAddedAsLong").getValue().toString()));
                            foodItem.setDateAdded(dateAdded);
                        } catch (Exception e) {
                        }

                        foodItems.add(foodItem);
                    }
                    UserData.get(getContext()).setFoodItems(foodItems);

                    updateNextExpiring();
                    updateLastModifiedList(); //This is the line that needs to be commented out to launch the app
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        //FoodItem f = new FoodItem();
        //UserData.get(getActivity()).addFoodItem(f);

        mNextExpiring = (TextView) v.findViewById(R.id.next_expiring);
        mLastModifiedList = (TextView) v.findViewById(R.id.last_modified_list);
        Log.d("mNextExpiring", mNextExpiring.toString());
        UserData.get(getActivity()).updateFromFirebase();

        return v;
    }

    private void updateNextExpiring() {
        mFoods = UserData.get(getActivity()).getFoodItems();
        //UserData.get(getA)
        Collections.sort(mFoods);
        //FoodItem mExpFood1 = ;

        if (!mFoods.isEmpty()) {
            /*Log.d("mFoods", "not null");
            FoodItem f = mFoods.get(0);
            for (FoodItem food: mFoods) {
                Log.d("mFoods", food.getName());
            }*/
            String mNextThreeExpiring = "";
            for (int i = 0; i < 3; ++i){
                if (mFoods.get(i) != null) {
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

    private void updateLastModifiedList() {
        mLists = UserData.get(getActivity()).getShoppingLists();
        String s = "";

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



        mLastModifiedList.setText(s);

    }
}

package com.thecupboardapp.cupboard;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kyle on 1/15/2018.
 */

public class UserData {
    private static final String TAG = "UserData";
    private static UserData sUserData;
    private List<ShoppingList> mShoppingLists;
    private List<FoodItem> mFoodItems;

    public static UserData get(Context context) {
        if (sUserData == null) {
            sUserData = new UserData(context);
        }
        return sUserData;
    }

    private UserData(Context context) {

        mShoppingLists = new ArrayList<ShoppingList>();
        for (int j = 0; j < 5; j++) {
            List<ShoppingListItem> shoppingListItems = new ArrayList<ShoppingListItem>();
            for (int i = 0; i < 10; i++) {
                String name = "List item - " + i;
                Boolean checked = i % 2 == 0;
                shoppingListItems.add(new ShoppingListItem(name, checked));
            }

            mShoppingLists.add(new ShoppingList("List " + j, shoppingListItems));
        }
    }

    public List<ShoppingList> getShoppingLists() {
        return mShoppingLists;
    }

    public ShoppingList getShoppingList(UUID id) {
        for (ShoppingList item : mShoppingLists) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public void addShoppingList(ShoppingList shoppingList){
        mShoppingLists.add(shoppingList);
    }

    public void updateFromFirebase(FirebaseUser user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get shopping lists
        DatabaseReference ref = database.getReference("lists/" + user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();
                for (DataSnapshot list : dataSnapshot.getChildren()) {
                    ShoppingList shoppingList = new ShoppingList();

                    shoppingList.setFirebaseId(list.getKey());
                    shoppingList.setName(list.child("title").getValue().toString());

                    for (DataSnapshot item : list.child("items").getChildren()) {
                        ShoppingListItem shoppingListItem = new ShoppingListItem();
                        shoppingListItem.setFirebaseId(item.getKey());

//                        shoppingListItem.setName(item.child("title").getValue().toString());
//                        shoppingListItem.setChecked((boolean) item.child("checked").getValue());

                        shoppingList.addShoppingListItem(item.child("title").getValue().toString(),
                                (boolean) item.child("checked").getValue());
                    }
                    shoppingLists.add(shoppingList);
                }
                mShoppingLists = shoppingLists;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<FoodItem> getFoodItems() {
        return mFoodItems;
    }

    public FoodItem getFoodItem(UUID id) {
        for (FoodItem item : mFoodItems) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public void addFoodItems(FoodItem afoodItem) {
        mFoodItems.add(afoodItem);
    }
}

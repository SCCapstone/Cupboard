package com.thecupboardapp.cupboard;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private FirebaseUser mUser;

    public FirebaseUser getUser() {
        return mUser;
    }

    public void setUser(FirebaseUser user) {
        mUser = user;
    }

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

        //adding dummy food items
        mFoodItems = new ArrayList<FoodItem>();
        for (int j = 0; j < 5; j++) {
            List<FoodItem> FoodItems = new ArrayList<FoodItem>();
            for (int i = 0; i < 10; i++) {
                String name = "Food item - " + i;
                FoodItems.add(new FoodItem(name));
            }

            mFoodItems.add(new FoodItem());
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
        setUser(user);

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

        DatabaseReference refFood = database.getReference("foods/" + user.getUid());
        refFood.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<FoodItem> foodItems = new ArrayList<FoodItem>();
                for (DataSnapshot food : dataSnapshot.getChildren()) {
                    FoodItem foodItem = new FoodItem();

                    foodItem.setFirebaseId(food.getKey());

                    //legacy food name in firebase was 'title', now it's 'name'
                    if(food.hasChild("title")){
                        foodItem.setName(food.child("title").getValue().toString());
                    }
                    else foodItem.setName(food.child("name").getValue().toString());

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    //We will delete these other ifs once we are sure how we want to handle expiration
                    try{
                        Date expDate;
                        if (food.hasChild("expirationAsLong")){
                            expDate = new Date(Long.parseLong(food.child("expirationAsLong").getValue().toString()));
                        }
                        else if(!food.hasChild("expiration")){
                            expDate = sdf.parse("01-01-1990");
                        }
                        else if(food.child("expiration").getValue().toString() == null){
                            expDate = sdf.parse("01-01-1990");
                        }
                        else {
                            expDate = sdf.parse(food.child("expiration").getValue().toString());
                        }
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(expDate);
                        foodItem.setExpiration(cal);
                    }
                    catch(java.text.ParseException parseException){

                    }
                    catch(Exception e){

                    }


                    foodItems.add(foodItem);
                }
                mFoodItems = foodItems;
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

    public void addFoodItem(FoodItem aFoodItem) {
        //local change
        mFoodItems.add(aFoodItem);

        //update firebase with converted foodItem
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("foods/" + getUser().getUid());
        ref.push().setValue(aFoodItem);
    }
}

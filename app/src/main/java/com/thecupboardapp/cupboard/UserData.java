package com.thecupboardapp.cupboard;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // If signed in do shit

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            getListsFromFirebase();
            getFoodsFromFirebase();
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

    public void getListsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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

                        shoppingListItem.setName(item.child("title").getValue().toString());
                        shoppingListItem.setChecked((boolean) item.child("checked").getValue());
                        shoppingListItem.setFirebaseId(item.getKey());
                        shoppingListItem.setRef(item.getRef());

                        shoppingList.addShoppingListItem(shoppingListItem);
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

    public void getFoodsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference refFood = database.getReference("foods/" + user.getUid());
        refFood.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<FoodItem> foodItems = new ArrayList<FoodItem>();
                for (DataSnapshot food : dataSnapshot.getChildren()) {
                    FoodItem foodItem = new FoodItem();

                    foodItem.setFirebaseId(food.getKey());
                    foodItem.setName(food.child("name").getValue().toString());

                    try{
                        Date expDate = new Date(Long.parseLong(food.child("expirationAsLong").getValue().toString()));
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(expDate);
                        foodItem.setExpiration(cal);
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

    public void updateFromFirebase(FirebaseUser user) {
        getListsFromFirebase();
        getFoodsFromFirebase();
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

    public FoodItem getFoodItem(String foodName){
        for (FoodItem item: mFoodItems){
            if(item.getName().equals(foodName)) return item;
        }
        return null;
    }

    public void addFoodItem(FoodItem aFoodItem) {
        //local change
        mFoodItems.add(aFoodItem);

        //update firebase with converted foodItem
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("foods/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        //generate key beforehand so we know firebase key locally without having to close and reopen My Cupboard
        String key = ref.push().getKey();
        ref.child(key).setValue(aFoodItem);
        aFoodItem.setFirebaseId(key);
    }

    public void editFoodItemQuantity(FoodItem aFoodItem){
        //update firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("foods/" + user.getUid() + "/" + aFoodItem.getFirebaseId());
        Map<String, Object> update = new HashMap<>();
        update.put("quantity", aFoodItem.getQuantity());
        ref.updateChildren(update);
    }

    public void removeFoodItem(FoodItem aFoodItem){
        //local change
        mFoodItems.remove(aFoodItem);

        //update firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("foods/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + aFoodItem.getFirebaseId());
        ref.removeValue();
    }
  
    public void updateFoodItem(FoodItem newFoodItem, FoodItem oldFoodItem){
        int i = mFoodItems.indexOf(oldFoodItem);
        mFoodItems.set(i, newFoodItem);

        String key = oldFoodItem.getFirebaseId();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("foods/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + key);

        Map<String, Object> update = new HashMap<>();
        update.put("name", newFoodItem.getName());
        update.put("quantity", newFoodItem.getQuantity());
        update.put("expirationAsLong", newFoodItem.getExpirationAsLong());
        ref.updateChildren(update);
    }
}

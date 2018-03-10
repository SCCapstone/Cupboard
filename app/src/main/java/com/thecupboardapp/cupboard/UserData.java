package com.thecupboardapp.cupboard;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thecupboardapp.cupboard.database.Repository;
import com.thecupboardapp.cupboard.models.FoodItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private List<FoodItem> mFoodItems;
    private List<String> mRecipes;

    public Repository db;

    public static UserData get(Context context) {
        if (sUserData == null) {
            sUserData = new UserData(context);
        }
        return sUserData;
    }

    private UserData(Context context) {
        mFoodItems = new ArrayList<FoodItem>();
        mRecipes = new ArrayList<String>();

        // db = Room.databaseBuilder(context, Repository.class, "cupboard_db").build();

        // final com.thecupboardapp.cupboard.models.SList list = new com.thecupboardapp.cupboard.models.SList("another", 6);
        // list.setId(1);

        // db.sListDao().getAll().subscribe(lists -> {
        //     Log.d(TAG, "UserData: size of all = " + lists.size());
        // });

        // If signed in do shit
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateFromFirebase();
        }
    }

    public void reset() {
        mFoodItems = new ArrayList<FoodItem>();
        mRecipes = new ArrayList<String>();
    }

    // public void getListsFromFirebase() {
    //     FirebaseDatabase database = FirebaseDatabase.getInstance();
    //     FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    //
    //     // Get shopping lists
    //     if(user != null) {
    //     DatabaseReference ref = database.getReference("lists/" + user.getUid());
    //     ref.addListenerForSingleValueEvent(new ValueEventListener() {
    //         @Override
    //         public void onDataChange(DataSnapshot dataSnapshot) {
    //             List<SList> sLists = new ArrayList<SList>();
    //             for (DataSnapshot list : dataSnapshot.getChildren()) {
    //                 SList sList = new SList();
    //
    //                 sList.setFirebaseId(list.getKey());
    //                 sList.setName(list.child("name").getValue().toString());
    //                 sList.setLastModified((Long) list.child("lastModified").getValue());
    //
    //                 for (DataSnapshot item : list.child("items").getChildren()) {
    //                     SListItem sListItem = new SListItem();
    //
    //                     sListItem.setName(item.child("name").getValue().toString());
    //                     sListItem.setChecked((boolean) item.child("checked").getValue());
    //                     sListItem.setFirebaseId(item.getKey());
    //                     sListItem.setRef(item.getRef());
    //
    //                     sList.addShoppingListItem(sListItem);
    //                 }
    //                 sLists.add(sList);
    //             }
    //             mSLists = sLists;
    //         }
    //
    //         @Override
    //         public void onCancelled(DatabaseError databaseError) {
    //
    //         }
    //     });
    //     }
    // }

    public void getFoodsFromFirebase() {
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
                    mFoodItems = foodItems;
                    sortFoodItems("alphabetically");
                    //sortFoodItems("expiresSoon");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    public void updateFromFirebase() {
        // getListsFromFirebase();
        getFoodsFromFirebase();
    }

    public List<FoodItem> getFoodItems() {
        return mFoodItems;
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        mFoodItems = foodItems;
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

    public void updateShoppingLists() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
    }
  
    public void updateFoodItem(FoodItem newFoodItem, FoodItem oldFoodItem){
        int i = mFoodItems.indexOf(oldFoodItem);
        mFoodItems.set(i, newFoodItem);

        String key = oldFoodItem.getFirebaseId();
        newFoodItem.setFirebaseId(key);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("foods/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + key);

        Map<String, Object> update = new HashMap<>();
        update.put("name", newFoodItem.getName());
        update.put("quantity", newFoodItem.getQuantity());
        update.put("expirationAsLong", newFoodItem.getExpirationAsLong());
        ref.updateChildren(update);
    }

    public void sortFoodItems(final String sortMethod){
        if (mFoodItems.size() > 0) {
            Collections.sort(mFoodItems, new Comparator<FoodItem>() {
                @Override
                public int compare(final FoodItem object1, final FoodItem object2) {
                    if (sortMethod.equals("alphabetically"))
                    return object1.getName().compareToIgnoreCase(object2.getName());
                    //sortMethod.equals("expiresSoon")
                    else return Long.compare(object1.getExpirationAsLong(),object2.getExpirationAsLong());
                }
            });
        }
    }
}

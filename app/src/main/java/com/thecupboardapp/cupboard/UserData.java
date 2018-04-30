package com.thecupboardapp.cupboard;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Relation;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thecupboardapp.cupboard.database.Database;
import com.thecupboardapp.cupboard.models.FoodItem;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Kyle on 1/15/2018.
 */

public class UserData {
    private static final String TAG = "UserData";
    private static UserData sUserData;
    private Database mDatabase;
    private static SharedPreferences sSharedPreferences;

    private DatabaseReference mSListsRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mFoodsRef;

    private CompositeDisposable mCompositeDisposable;

    private String mUserId;

    public static UserData get(Context context) {
        if (sUserData == null) {
            sUserData = new UserData(context);
        }
        return sUserData;
    }

    private UserData(Context context) {
        mDatabase = Database.getDatabase(context);
        mCompositeDisposable = new CompositeDisposable();
        sSharedPreferences = context.getSharedPreferences("name", MODE_PRIVATE);
    }

    public void syncWithFirebase(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        // mUserRef.child("lastModified").addListenerForSingleValueEvent(new ValueEventListener() {
        //     @Override
        //     public void onDataChange(DataSnapshot dataSnapshot) {
        //         long firebaseLastModified = (long) dataSnapshot.getValue();
        //         if (firebaseLastModified < sSharedPreferences.getLong("lastModified", 0)) {
        //
        //         } else {
        //
        //         }
        //     }
        //
        //     @Override
        //     public void onCancelled(DatabaseError databaseError) {
        //
        //     }
        // });

        // if (sSharedPreferences.getLong("lastModified"), 0) == mU {
        //
        // }

        compositeDisposable.add(mDatabase.sListAndItemsDao().getAllSingle()
                .subscribeOn(Schedulers.io())
                .subscribe(sListAndItems -> {
                    Long timeNow = System.currentTimeMillis();
                    Map<String, Object> map = convertListsToFirebaseMap(sListAndItems);
                    mSListsRef.setValue(map);

                    // set firebase lastmodified
                    mUserRef.child("lastModified").setValue(timeNow);

                    // set android lastmodified
                    // editor.putLong("lastModified", timeNow);
                    // editor.apply();
                }));

        compositeDisposable.add(mDatabase.foodItemDao().getAllSingle()
                .subscribeOn(Schedulers.io())
                .subscribe(foodItems -> {
                    Long timeNow = System.currentTimeMillis();
                    Map<String, Object> map = convertFoodsToFirebaseMap(foodItems);
                    mFoodsRef.setValue(map);

                    // set firebase last modified
                    mUserRef.child("lastModified").setValue(timeNow);

                    // set android last modified
                    // editor.putLong("lastModified", timeNow);
                    // editor.apply();
                }));

        // compositeDisposable.dispose();
    }

    public void setUpListeners() {
        // SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();

        mCompositeDisposable.add(mDatabase.sListAndItemsDao().getAllFlowable()
                .subscribeOn(Schedulers.io())
                .subscribe(sListAndItems -> {
                    Long timeNow = System.currentTimeMillis();
                    Map<String, Object> map = convertListsToFirebaseMap(sListAndItems);
                    mSListsRef.setValue(map);

                    // set firebase lastmodified
                    mUserRef.child("lastModified").setValue(timeNow);

                    // // set android lastmodified
                    // editor.putLong("lastModified", timeNow);
                    // editor.apply();
                }));

        mCompositeDisposable.add(mDatabase.foodItemDao().getAllFlowable()
                .subscribeOn(Schedulers.io())
                .subscribe(foodItems -> {
                    Long timeNow = System.currentTimeMillis();
                    Map<String, Object> map = convertFoodsToFirebaseMap(foodItems);
                    mFoodsRef.setValue(map);

                    // set firebase last modified
                    mUserRef.child("lastModified").setValue(timeNow);

                    // // set android last modified
                    // editor.putLong("lastModified", timeNow);
                    // editor.apply();
                }));
    }

    public void stopListeners() {
        mCompositeDisposable.clear();
    }

    private Map<String, Object> convertListsToFirebaseMap(List<SListAndItems> sListAndItems) {
        Map<String, Object> map = new HashMap<>();
        for (SListAndItems listAndItems: sListAndItems) {
            Map<String, Object> inner = new HashMap<>();
            inner.put("name", listAndItems.getSList().getName());
            inner.put("lastModified", listAndItems.getSList().getLastModified());

            if (listAndItems.getSList().getFirebaseKey() == null) {
                String key = mSListsRef.push().getKey();
                listAndItems.getSList().setFirebaseKey(key);

                AsyncTask.execute( () -> {
                    mDatabase.sListDao().update(listAndItems.getSList());
                });
            }

            Map<String, Object> itemMap = new HashMap<>();
            for (SListItem sListItem: listAndItems.getSListItems()) {
                if (sListItem.getFirebaseKey() == null) {
                    // generate a key
                    String key = mSListsRef.child(listAndItems.getSList().getFirebaseKey()).child("items").push().getKey();
                    sListItem.setFirebaseKey(key);
                    itemMap.put(key, sListItem);

                    // update the entry
                    AsyncTask.execute( () -> {
                        mDatabase.sListItemDao().update(sListItem);
                    });
                } else {
                    itemMap.put(sListItem.getFirebaseKey(), sListItem);
                }
            }

            inner.put("items", itemMap);
            map.put(listAndItems.getSList().getFirebaseKey(), inner);
        }
        return map;
    }

    private Map<String, Object> convertFoodsToFirebaseMap(List<FoodItem> foodItems) {
        Map<String, Object> map = new HashMap<>();

        for (FoodItem item: foodItems) {
            if (item.getFirebaseKey() == null) {
                item.setFirebaseKey(mFoodsRef.push().getKey());
                AsyncTask.execute( () -> {
                    mDatabase.foodItemDao().update(item);
                });
            }

            map.put(item.getFirebaseKey(), item);
        }

        return map;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static class SListAndItems {
        @Embedded
        private SList mSList;

        @Relation(parentColumn = "id", entityColumn = "parent_id")
        private List<SListItem> mSListItems;

        public SListAndItems() { }

        public SList getSList() {
            return mSList;
        }

        public void setSList(SList SList) {
            mSList = SList;
        }

        public List<SListItem> getSListItems() {
            return mSListItems;
        }

        public void setSListItems(List<SListItem> SListItems) {
            mSListItems = SListItems;
        }
    }

    @Dao
    public interface SListAndItemsDao {
        @Query("SELECT * FROM slists")
        Flowable<List<SListAndItems>> getAllFlowable();

        @Query("SELECT * FROM slists")
        Single<List<SListAndItems>> getAllSingle();
    }

    public void setReferences() {
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mSListsRef = FirebaseDatabase.getInstance().getReference()
                .child("lists").child(mUserId);

        mUserRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mUserId);

        mFoodsRef = FirebaseDatabase.getInstance().getReference()
                .child("foods").child(mUserId);
    }
}

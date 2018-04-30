package com.thecupboardapp.cupboard;

import android.app.Application;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Relation;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thecupboardapp.cupboard.database.Database;
import com.thecupboardapp.cupboard.models.FoodItem;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kyle on 3/8/2018.
 */

public class Cupboard extends Application {
    public static final String TAG = "Cupboard";
    private String mUserId;

    private DatabaseReference mSListsRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mFoodsRef;

    private CompositeDisposable mCompositeDisposable;

    private Disposable mListsDisposable;
    private Disposable mFoodsDisposable;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        Stetho.initializeWithDefaults(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mCompositeDisposable = new CompositeDisposable();
            initialize();
            setUpListeners();
        }
    }

    // private void syncFromFirebase() {
    //     FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
    //         @Override
    //         public void onDataChange(DataSnapshot dataSnapshot) {
    //             for (DataSnapshot listSnapshot: dataSnapshot.getChildren()) {
    //                 SList list = new SList();
    //                 list.setFirebaseKey(listSnapshot.getKey());
    //             }
    //         }
    //
    //         @Override
    //         public void onCancelled(DatabaseError databaseError) {
    //             Log.d(TAG, "onCancelled: ");
    //         }
    //     });
    // }

    private void initialPushToFirebase(){
        SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(Database.getDatabase(this).sListAndItemsDao().getAllSingle()
                .subscribeOn(Schedulers.io())
                .subscribe(sListAndItems -> {
                    Long timeNow = System.currentTimeMillis();
                    Map<String, Object> map = convertListsToFirebaseMap(sListAndItems);
                    mSListsRef.setValue(map);

                    // set firebase lastmodified
                    mUserRef.child("lastModified").setValue(timeNow);

                    // set android lastmodified
                    editor.putLong("lastModified", timeNow);
                    editor.apply();
                }));

        compositeDisposable.add(Database.getDatabase(this).foodItemDao().getAllSingle()
                .subscribeOn(Schedulers.io())
                .subscribe(foodItems -> {
                    Long timeNow = System.currentTimeMillis();
                    Map<String, Object> map = convertFoodsToFirebaseMap(foodItems);
                    mFoodsRef.setValue(map);

                    // set firebase last modified
                    mUserRef.child("lastModified").setValue(timeNow);

                    // set android last modified
                    editor.putLong("lastModified", timeNow);
                    editor.apply();
                }));

        // compositeDisposable.dispose();
    }

    private void setUpListeners() {
        SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();

        mCompositeDisposable.add(Database.getDatabase(this).sListAndItemsDao().getAllFlowable()
                .subscribeOn(Schedulers.io())
                .subscribe(sListAndItems -> {
                    Long timeNow = System.currentTimeMillis();
                    Map<String, Object> map = convertListsToFirebaseMap(sListAndItems);
                    mSListsRef.setValue(map);

                    // set firebase lastmodified
                    mUserRef.child("lastModified").setValue(timeNow);

                    // set android lastmodified
                    editor.putLong("lastModified", timeNow);
                    editor.apply();
                }));

        mCompositeDisposable.add(Database.getDatabase(this).foodItemDao().getAllFlowable()
                .subscribeOn(Schedulers.io())
                .subscribe(foodItems -> {
                    Long timeNow = System.currentTimeMillis();
                    Map<String, Object> map = convertFoodsToFirebaseMap(foodItems);
                    mFoodsRef.setValue(map);

                    // set firebase last modified
                    mUserRef.child("lastModified").setValue(timeNow);

                    // set android last modified
                    editor.putLong("lastModified", timeNow);
                    editor.apply();
                }));
    }

    public Map<String, Object> convertListsToFirebaseMap(List<SListAndItems> sListAndItems) {
        Map<String, Object> map = new HashMap<>();
        for (SListAndItems listAndItems: sListAndItems) {
            Map<String, Object> inner = new HashMap<>();
            inner.put("name", listAndItems.getSList().getName());
            inner.put("lastModified", listAndItems.getSList().getLastModified());

            if (listAndItems.getSList().getFirebaseKey() == null) {
                String key = mSListsRef.push().getKey();
                listAndItems.getSList().setFirebaseKey(key);

                AsyncTask.execute( () -> {
                    Database.getDatabase(this).sListDao().update(listAndItems.getSList());
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
                        Database.getDatabase(this).sListItemDao().update(sListItem);
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

    public Map<String, Object> convertFoodsToFirebaseMap(List<FoodItem> foodItems) {
        Map<String, Object> map = new HashMap<>();

        for (FoodItem item: foodItems) {
            if (item.getFirebaseKey() == null) {
                item.setFirebaseKey(mFoodsRef.push().getKey());
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

    public void initialize() {
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mSListsRef = FirebaseDatabase.getInstance().getReference()
                .child("lists").child(mUserId);

        mUserRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mUserId);

        mFoodsRef = FirebaseDatabase.getInstance().getReference()
                .child("foods").child(mUserId);

        initialPushToFirebase();
        setUpListeners();
    }
}

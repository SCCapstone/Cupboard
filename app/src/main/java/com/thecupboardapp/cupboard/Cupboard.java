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
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
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

    private Disposable mListsDisposable;
    private Disposable mSListItemsDisposable;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        Stetho.initializeWithDefaults(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            initialize();
        }
    }


    private void syncFromFirebase() {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot listSnapshot: dataSnapshot.getChildren()) {
                    SList list = new SList();
                    list.setFirebaseKey(listSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }

    private void syncToFirebase(){
        SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
        if (mSListItemsDisposable != null && mSListItemsDisposable.isDisposed()) { mListsDisposable.dispose(); }

        mListsDisposable = Database.getDatabase(this).sListAndItemsDao().getAllFlowable()
                .subscribeOn(Schedulers.io())
                .subscribe(sListAndItems -> {
                    Long timeNow = System.currentTimeMillis();
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
                    mSListsRef.setValue(map);

                    mUserRef.child("lastModified").setValue(timeNow);
                    editor.putLong("lastModified", timeNow);
                    editor.apply();
                });
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
    }

    public void initialize() {
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mSListsRef = FirebaseDatabase.getInstance().getReference()
                .child("lists").child(mUserId);

        mUserRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mUserId);

        mUserRef.child("lastModified").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long lastModified = (long) dataSnapshot.getValue();
                if (lastModified < getSharedPreferences("name", MODE_PRIVATE).getLong("lastModified", -1)) {
                    // syncFromFirebase();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        syncToFirebase();
    }
}

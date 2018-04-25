package com.thecupboardapp.cupboard;

import android.app.Application;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Relation;

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
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kyle on 3/8/2018.
 */

public class Cupboard extends Application {
    public static final String TAG = "Cupboard";
    private String mUserId;
    private DatabaseReference mSListsRef;
    private DatabaseReference mUserRef;

    private Disposable mDisposable;
    private Disposable mSListItemsDisposable;

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mSListsRef = FirebaseDatabase.getInstance().getReference()
                    .child("lists")
                    .child(mUserId);

            mUserRef = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(mUserId);

            // mSListsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            //     @Override
            //     public void onDataChange(DataSnapshot dataSnapshot) {
            //         dataSnapshot.
            //     }
            //
            //     @Override
            //     public void onCancelled(DatabaseError databaseError) {
            //
            //     }
            // });

            syncWithFirebase();
        }
    }

    private void syncWithFirebase(){
        mDisposable = Database.getDatabase(this).sListAndItemsDao().getALlFlowable()
                .subscribeOn(Schedulers.io())
                .subscribe(sListAndItems -> {
                    Map<String, Object> map = new HashMap<>();
                    for (SListAndItems listAndItems: sListAndItems) {
                        Map<String, Object> inner = new HashMap<>();
                        inner.put("name", listAndItems.getSList().getName());
                        inner.put("lastModified", listAndItems.getSList().getLastModified());

                        Map<String, Object> itemMap = new HashMap<>();
                        for (SListItem sListItem: listAndItems.getSListItems()) {
                            itemMap.put(String.valueOf(sListItem.getId()), sListItem);
                        }

                        inner.put("items", itemMap);

                        map.put(String.valueOf(listAndItems.getSList().getId()), inner);
                    }
                    mSListsRef.setValue(map);
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

        public SListAndItems() {
        }

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
        Flowable<List<SListAndItems>> getALlFlowable();
    }
}

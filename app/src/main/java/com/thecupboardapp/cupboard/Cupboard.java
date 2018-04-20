package com.thecupboardapp.cupboard;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thecupboardapp.cupboard.database.Database;
import com.thecupboardapp.cupboard.models.SList;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kyle on 3/8/2018.
 */

public class Cupboard extends Application {
    private Disposable mDisposable;
    private String mUserId;
    private DatabaseReference mSListsRef;

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mSListsRef = FirebaseDatabase.getInstance().getReference()
                    .child("lists")
                    .child(mUserId);
            syncWithFirebase();
        }
    }

    private void syncWithFirebase(){
        mDisposable = Database.getDatabase(this).sListDao().getAllFlowable()
                .subscribeOn(Schedulers.io())
                .subscribe(sLists -> {
                    Map<String, Object> map = new HashMap<>();
                    for (SList sList : sLists) {
                        map.put(Long.toString(sList.getId()), sList);

                        Map<String, Object> items = new HashMap<>();
                    }
                    mSListsRef.setValue(map);
                });
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getUid();
    }
}

package com.thecupboardapp.cupboard;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Kyle on 3/8/2018.
 */

public class Cupboard extends Application {

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            syncWithFirebase();
        }
    }

    private void syncWithFirebase(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("lists")
                .child(getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getUid();
    }
}

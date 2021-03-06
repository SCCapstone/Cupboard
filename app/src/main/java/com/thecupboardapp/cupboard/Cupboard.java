package com.thecupboardapp.cupboard;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Kyle on 3/8/2018.
 */

public class Cupboard extends Application {
    public static final String TAG = "Cupboard";

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UserData.get(this).setReferences();
            UserData.get(this).syncWithFirebase();
            UserData.get(this).setUpListeners();
        }
    }
}

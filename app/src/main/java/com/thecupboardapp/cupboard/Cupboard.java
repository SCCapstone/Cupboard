package com.thecupboardapp.cupboard;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Kyle on 3/8/2018.
 */

public class Cupboard extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);


    }
}

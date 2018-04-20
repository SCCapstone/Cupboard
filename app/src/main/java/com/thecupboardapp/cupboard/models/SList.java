package com.thecupboardapp.cupboard.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Kyle on 3/8/2018.
 */

@IgnoreExtraProperties
@Entity(tableName = "slists")
public class SList {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private int index;

    @ColumnInfo(name = "last_modified")
    private long lastModified;

    @ColumnInfo(name = "firebase_key")
    private String firebaseKey;

    public SList() {
    }

    @Ignore
    public SList(String name) {
        this.name = name;
        this.lastModified = System.currentTimeMillis();
    }

    @Ignore
    public SList(String name, int index) {
        this.name = name;
        this.index = index;
        this.lastModified = System.currentTimeMillis();
    }

    @Exclude
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Exclude
    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    @Ignore
    public String toString() {
        String str = "SList{ id: " + id + " , " +
                "name: " + name + " , " +
                "index: " + index + " , " +
                "lastModified: " + lastModified + " , " +
                "firebaseKey: " + firebaseKey + "}";

        return str;
    }
}
package com.thecupboardapp.cupboard.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Kyle on 3/8/2018.
 */

@Entity(tableName = "slists")
public class SList {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private int index;

    @ColumnInfo(name = "last_modified")
    private long lastModified;

    @ColumnInfo(name = "firebase_key")
    private String firebaseKey;

    public SList() {
    }

    @Ignore
    public SList(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    @Ignore
    public void setLastModifiedNow() {
        this.lastModified = System.currentTimeMillis();
    }

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

    public void setId(int id) {
        this.id = id;
    }
}
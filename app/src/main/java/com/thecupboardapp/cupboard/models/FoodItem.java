package com.thecupboardapp.cupboard.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.firebase.database.Exclude;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Kyle on 1/12/2018.
 */

@Entity(tableName = "food_items")
public class FoodItem implements Comparable<FoodItem> {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private int index;
    private float quantity;
    private String units;
    private String description;
    private String category;

    @ColumnInfo(name = "date_added")
    private long dateAdded;
    private long expiration;

    @ColumnInfo(name = "firebase_key")
    private String firebaseKey;

    public FoodItem() {}

    public FoodItem(String name) {
        this.name = name;
    }

    public FoodItem(String name, long expiration) {
        this.name = name;
        this.expiration = expiration;
    }

    public FoodItem(String name, long expiration, float quantity) {
        this.name = name;
        this.expiration = expiration;
        this.quantity = quantity;
    }

    public FoodItem(String name, long expiration, float quantity, String category) {
        this.name = name;
        this.expiration = expiration;
        this.quantity = quantity;
        this.category = category;
    }

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

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Ignore
    public static String longToDate(long dateInMillis) {
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(dateInMillis);
    }

    @Ignore
    public int compareTo(FoodItem f2) {
        long comparison = this.expiration - f2.getExpiration();

        if (comparison < 0) {
            return -1;
        } else if(comparison > 0) {
            return 1;
        } else {
            return 0; // Values are equal
        }
    }
}
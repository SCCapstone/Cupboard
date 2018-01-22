package com.thecupboardapp.cupboard;

import java.util.UUID;

/**
 * Created by Kyle on 1/12/2018.
 */

public class FoodItem {
    private UUID mId;
    private String mFirebaseId;
    private String mName;
    private float mQuantity;
    private String mUnits;
    private String mCategory;
    private String mExpiration;

    public FoodItem() {
        mId = UUID.randomUUID();
        mQuantity = 1;
    }

    public FoodItem(String aName, String aExpiration) {
        mName = aName;
        mExpiration = aExpiration;
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public float getQuantity() {
        return mQuantity;
    }

    public void setQuantity(float quantity) {
        mQuantity = quantity;
    }

    public String getExpiration() {
        return mExpiration;
    }

    public void setExpiration(String expiration) {
        mExpiration = expiration;
    }

    public String getUnits() {
        return mUnits;
    }

    public void setUnits(String units) {
        mUnits = units;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getFirebaseId() {
        return mFirebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        mFirebaseId = firebaseId;
    }
}

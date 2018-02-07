package com.thecupboardapp.cupboard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by William on 2/6/2018.
 */

public class FirebaseFoodItem {
    private UUID mId;
    private String mFirebaseId;
    private String mName;
    private float mQuantity;
    private String mUnits;
    private String mCategory;
    private String mExpiration;
    private String mDateAdded;

    public FirebaseFoodItem() {
        mId = UUID.randomUUID();
        mName = "apple";
        mQuantity = 1;

    }

    public FirebaseFoodItem(String aName, Calendar aExpiration) {
        mName = aName;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        mExpiration = sdf.format(aExpiration.getTime());
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getFirebaseId() {
        return mFirebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        mFirebaseId = firebaseId;
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

    public String getExpiration() {
        return mExpiration;
    }

    public void setExpiration(String expiration) {
        mExpiration = expiration;
    }

    public String getDateAdded() {
        return mDateAdded;
    }

    public void setDateAdded(String dateAdded) {
        mDateAdded = dateAdded;
    }
}

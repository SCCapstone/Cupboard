package com.thecupboardapp.cupboard;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Kyle on 1/12/2018.
 */

public class FoodItem implements Comparable<FoodItem> {
    private UUID mId;
    private String mFirebaseId;
    private String mName;
    private float mQuantity;
    private String mUnits;
    private String mCategory;
    private Calendar mExpiration;
    private Calendar mDateAdded;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

    public FoodItem() {
        mId = UUID.randomUUID();
        mName = "apple";
        mQuantity = 1;

    }

    public FoodItem(String aName, Calendar aExpiration) {
        mName = aName;
        mExpiration = aExpiration;
    }
    public FoodItem(String aName) {
        mName = aName;
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

    @Exclude
    public Calendar getExpiration() {
        return mExpiration;
    }

    @Exclude
    public void setExpiration(Calendar expiration) {
        mExpiration = expiration;
    }

    @Exclude
    public void setExpiration(Date expiration){
        Calendar cal = Calendar.getInstance();
        cal.setTime(expiration);
        mExpiration = cal;
    }

    public long getExpirationAsLong(){
        return mExpiration.getTimeInMillis();
    }

    @Exclude
    public String getExpirationAsString(){
        return sdf.format(mExpiration.getTime());
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

    @Exclude
    public Calendar getDateAdded() {
        return mDateAdded;
    }

    public long getDateAddedAsLong(){
        return mDateAdded.getTimeInMillis();
    }

    @Exclude
    public String getDateAddedAsString() {
        return sdf.format(mDateAdded.getTime());
    }

    @Exclude
    public void setDateAdded(Calendar dateAdded) {
        mDateAdded = dateAdded;
    }

    @Exclude
    public void setDateAdded(Date dateAdded){
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateAdded);
        mDateAdded = cal;
    }

    public int compareTo(FoodItem f2) {
        long l = this.getExpirationAsLong() - f2.getExpirationAsLong();
        //return Math.toIntExact(Long.parseLong(f1.getExpiration()) - Long.parseLong(f2.getExpiration()));
        return safeLongToInt(l);
    }
    private static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }


}



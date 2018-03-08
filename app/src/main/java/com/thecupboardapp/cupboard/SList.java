package com.thecupboardapp.cupboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kyle on 1/12/2018.
 * Edited by Jacob Strom on 3/3/2018.
 */

public class SList implements Comparable<SList> {
    private UUID mId;
    private String mFirebaseId;
    private String mName;
    private List<SListItem> mShoppingSListItems;
    private Long lastModified;

    public SList() {
       mId = UUID.randomUUID();
       mName = "New Shopping List";
       mShoppingSListItems = new ArrayList<SListItem>();
       lastModified = System.currentTimeMillis();
    }

    public SList(String name, List<SListItem> items) {
        mId = UUID.randomUUID();
        mName = name;
        mShoppingSListItems = items;
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

    public String getFirebaseId() {
        return mFirebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        mFirebaseId = firebaseId;
    }


    public List<SListItem> getShoppingListItems() {
        return mShoppingSListItems;
    }

    public void addShoppingListItem(SListItem item){
        mShoppingSListItems.add(item);
    }

    public void removeShoppingListItem(UUID id) {
        int index = -1;
        for (int i = 0; i < mShoppingSListItems.size(); i++) {
            if (id == mShoppingSListItems.get(i).getId()) {
                index = i;
            }
        }

        if (index != -1) {
            mShoppingSListItems.remove(index);
        }
    }

    public int compareTo(SList s2) {
        long l = s2.getLastModified()- this.getLastModified();
        //return Math.toIntExact(Long.parseLong(f1.getExpiration()) - Long.parseLong(f2.getExpiration()));
        //return safeLongToInt(l);
        int retVal = 0;
        if (l > 0)  retVal = 1;
        else if (l < 0) retVal = -1;
        return retVal;
    }

    /*private static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }*/

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }
    
}

package com.thecupboardapp.cupboard;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.UUID;

/**
 * Created by Kyle on 1/12/2018.
 */

public class SListItem {
    @Exclude private UUID mId;
    @Exclude private String mFirebaseId;
    private String mName;
    private boolean mIsChecked;
    private DatabaseReference mRef;

    public SListItem() {
        mId = UUID.randomUUID();
        mIsChecked = false;
        mName = "";
    }

    public SListItem(String name) {
        mId = UUID.randomUUID();
        mName = name;
        mIsChecked = false;
    }

    public SListItem(String name, boolean isChecked) {
        mId = UUID.randomUUID();
        mName = name;
        mIsChecked = isChecked;
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

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }

    public String getFirebaseId() {
        return mFirebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        mFirebaseId = firebaseId;
    }

    public void setRef(DatabaseReference ref) {
        mRef = ref;
    }
}

package com.thecupboardapp.cupboard.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Kyle on 1/12/2018.
 */

@Entity(tableName = "slist_items",
        foreignKeys = @ForeignKey(
                entity = SList.class,
                parentColumns = "id",
                childColumns = "parent_id",
                onDelete = CASCADE
        )
)
public class SListItem implements Comparable<SListItem>{
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "is_checked")
    private Boolean checked;

    @ColumnInfo(name = "firebase_key")
    private String firebaseKey;

    @ColumnInfo(name = "parent_id")
    private long parentId;

    @ColumnInfo(name = "index")
    private int index;

    public SListItem() {
    }

    public SListItem(String name, Boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    public SListItem(String name, Boolean checked, String firebaseId) {
        this.name = name;
        this.checked = checked;
        this.firebaseKey = firebaseId;
    }

    public SListItem(String name, Boolean checked, int index) {
        this.name = name;
        this.checked = checked;
        this.index = index;
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

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Ignore
    @Override
    public String toString() {
        String str = "SListItem{ id: " + id + " , " +
                "name: " + name + " , " +
                "checked: " + checked + " , " +
                "parentId: " + parentId + " , " +
                "index: " + index + " , " +
                "firebaseKey: " + firebaseKey + "}";

        return str;
    }

    @Ignore
    @Override
    public int compareTo(@NonNull SListItem o) {
        if (this.id == o.id) {
            return 0;
        } else {
            return -1;
        }
    }
}

package com.thecupboardapp.cupboard.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Kyle on 1/12/2018.
 */

@IgnoreExtraProperties
@Entity(tableName = "slist_items",
        foreignKeys = @ForeignKey(
                entity = SList.class,
                parentColumns = "id",
                childColumns = "parent_id",
                onDelete = CASCADE
        )
)
public class SListItem implements Comparable<SListItem>{
    @Exclude
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "is_checked")
    private Boolean checked;

    @ColumnInfo(name = "firebase_key")
    private String firebaseKey;

    @Exclude
    @ColumnInfo(name = "parent_id")
    private long parentId;

    @Exclude
    @ColumnInfo(name = "index")
    private int index;

    public SListItem() {
    }

    @Ignore
    public SListItem(String name, Boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    @Ignore
    public SListItem(String name, Boolean checked, String firebaseId) {
        this.name = name;
        this.checked = checked;
        this.firebaseKey = firebaseId;
    }

    @Ignore
    public SListItem(String name, Boolean checked, int index) {
        this.name = name;
        this.checked = checked;
        this.index = index;
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

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    @Exclude
    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    @Exclude
    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Exclude
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
    /* Compares on item to another, if they're not the same, sort by index */
    public int compareTo(@NonNull SListItem sListItem) {
        if (this.id == sListItem.getId()
                && this.name.equals(sListItem.name)
                && this.checked == sListItem.checked
                && this.parentId == sListItem.parentId)  {
            return 0;
        }

        if (this.index < sListItem.getIndex()) {
            return -1;
        } else {
            return 1;
        }
    }
}

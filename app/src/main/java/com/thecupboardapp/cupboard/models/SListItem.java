package com.thecupboardapp.cupboard.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

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
public class SListItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "is_checked")
    private Boolean checked;

    @ColumnInfo(name = "firebase_key")
    private String firebaseKey;

    @ColumnInfo(name = "parent_id")
    private int parentId;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @Ignore
    @Override
    public String toString() {
        String str = "SList{ id: " + id + " , " +
                "name: " + name + " , " +
                "checked: " + checked + " , " +
                "parentId: " + parentId + " , " +
                "firebaseKey: " + firebaseKey + "}";

        return str;
    }

}

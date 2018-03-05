package com.thecupboardapp.cupboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kyle on 1/12/2018.
 */

public class ShoppingList {
    private UUID mId;
    private String mFirebaseId;
    private String mName;
    private List<ShoppingListItem> mShoppingShoppingListItems;
    private Long lastModified;

    public ShoppingList() {
       mId = UUID.randomUUID();
       mName = "New Shopping List";
       mShoppingShoppingListItems = new ArrayList<ShoppingListItem>();
    }

    public ShoppingList(String name, List<ShoppingListItem> items) {
        mId = UUID.randomUUID();
        mName = name;
        mShoppingShoppingListItems = items;
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

    public List<ShoppingListItem> getShoppingListItems() {
        return mShoppingShoppingListItems;
    }

    public void addShoppingListItem(ShoppingListItem item){
        mShoppingShoppingListItems.add(item);
    }

    public void removeShoppingListItem(UUID id) {
        int index = -1;
        for (int i = 0; i < mShoppingShoppingListItems.size(); i++) {
            if (id == mShoppingShoppingListItems.get(i).getId()) {
                index = i;
            }
        }

        if (index != -1) {
            mShoppingShoppingListItems.remove(index);
        }
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }
}

package com.thecupboardapp.cupboard.models.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.util.Log;

import com.thecupboardapp.cupboard.database.Repository;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by Kyle on 3/9/2018.
 */

public class SListViewModel extends ViewModel{
    private final String TAG = "SListViewModel";
    private Flowable<List<SList>> mSListFlowable;
    private Repository mRepository;

    public static final int SORT_ALPHABETICAL = 0;
    public static final int SORT_LAST_MODIFIED = 1;
    public static final int SORT_DATE_CREATED = 2;

    public void SListViewModelFactory(Context context) {
        mRepository = Repository.getDatabase(context);
        mSListFlowable = mRepository.sListDao().getAllFlowable();
    }

    // Get all lists
    public Flowable<List<SList>> getLists(){
        return mSListFlowable;
    }

    // Update the list title of a list with id
    public void updateListTitle(long id, String name){
        Log.d(TAG, "updateListTitle: ");
        AsyncTask.execute(() -> {
            mRepository.sListDao().updateName(name, id);
        });
    }

    // Remove a list
    public void removeList(SList sList){
        AsyncTask.execute(() -> {
            mRepository.sListDao().delete(sList);
        });
    }

    // Get the list titles and sort them using a method
    public void sort(int method){
        AsyncTask.execute(() -> {
            mRepository.sListDao().getAllSingle().subscribe(sLists -> {
                switch (method) {
                    case SORT_ALPHABETICAL: {
                        Collections.sort(sLists, (sList, t1) -> sList.getName().compareToIgnoreCase(t1.getName()));
                        break;
                    }
                    case SORT_LAST_MODIFIED: {
                        Collections.sort(sLists, (sList, t1) -> Long.compare(t1.getLastModified(), sList.getLastModified()));
                        break;
                    }
                    case SORT_DATE_CREATED: {
                        Collections.sort(sLists, (sList, t1) -> Long.compare(sList.getId(), t1.getId()));
                        break;
                    }
                }
                for (int i = 0; i < sLists.size(); i++) {
                    sLists.get(i).setIndex(i);
                }
                mRepository.sListDao().update(sLists);
            });
        });
    }

    // Put a new list into the database
    public long newList(SList sList) {
        return mRepository.sListDao().insertList(sList);
    }

    // Get a single list by Id
    public Flowable<SList> getListById(long id) {
        Log.d(TAG, "getFlowableListById: ");
        return mRepository.sListDao().getFlowableListById(id);
    }

    // Get a single list by Id
    public Single<SList> getSingleListById(long id) {
        return mRepository.sListDao().getSingleListById(id);
    }

    // Get the items of a list with id
    public Flowable<List<SListItem>> getListItemsById(long id){
        return mRepository.sListItemDao().getSListItemById(id);
    }

    // Update list items
    public void update(List<SListItem> oldItems, List<SListItem> newItems) {
        Log.d(TAG, "update: OLD: " + oldItems);
        Log.d(TAG, "update: NEW: " + newItems);
        AsyncTask.execute(() -> {
            for (SListItem item: oldItems) {
                if (newItems.indexOf(item) == -1) {
                    mRepository.sListItemDao().delete(item);
                }
            }

            for (SListItem item: newItems) {
                if (oldItems.indexOf(item) == -1) {
                    mRepository.sListItemDao().insertAll(item);
                }
            }

            // Update the rest
            mRepository.sListItemDao().update(newItems);
        });
    }

    public void updateLastModified(long id) {
        AsyncTask.execute(() -> {
            mRepository.sListDao().updateLastModified(id, System.currentTimeMillis());
        });
    }
}

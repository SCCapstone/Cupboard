package com.thecupboardapp.cupboard.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.thecupboardapp.cupboard.database.Database;
import com.thecupboardapp.cupboard.database.SListDao;
import com.thecupboardapp.cupboard.database.SListItemDao;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;

import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by Kyle on 3/9/2018.
 */

public class SListEditViewModel extends AndroidViewModel {
    private static final String TAG = "SListViewModel";
    private SListDao mSListDao;
    private SListItemDao mSListItemDao;

    private Flowable<List<SListItem>> mSListItemFlowable;
    private Flowable<SList> mSListFlowable;
    private List<SListItem> oldItems;

    public SListEditViewModel(@NonNull Application application) {
        super(application);

        mSListDao = Database.getDatabase(application).sListDao();
        mSListItemDao = Database.getDatabase(application).sListItemDao();
    }

    public void setSListItems(long id) {
        mSListItemFlowable = mSListItemDao.getSListItemById(id);
    }

    public void setSList(long id) {
        mSListFlowable = mSListDao.getFlowableListById(id);
    }

    // Update the list title of a list with id
    public void updateListTitle(long id, String name) {
        AsyncTask.execute(() -> {
            mSListDao.updateName(name, id);
        });
    }

    public void updateSList(SList sList) {
        AsyncTask.execute(() -> {
            mSListDao.update(sList);
        });
    }

    // Put a new list into the database
    public long insertSList(SList sList) {
        return mSListDao.insertList(sList);
    }

    // Get a single list by Id
    public Flowable<SList> getListById(long id) {
        return mSListDao.getFlowableListById(id);
    }

    // Get a single list by Id
    public Single<SList> getSingleListById(long id) {
        return mSListDao.getSingleListById(id);
    }

    // Get the items of a list with id
    public Flowable<List<SListItem>> getListItemsById(long id) {
        return mSListItemDao.getSListItemById(id);
    }

    // Update list items
    public void update(List<SListItem> oldItems, List<SListItem> newItems) {
        AsyncTask.execute(() -> {
            for (SListItem item : oldItems) {
                if (newItems.indexOf(item) == -1) {
                    mSListItemDao.delete(item);
                }
            }

            for (SListItem item : newItems) {
                if (oldItems.indexOf(item) == -1) {
                    Log.d(TAG, "update: " + item.toString());
                    mSListItemDao.insertAll(item);
                }
            }

            // Update the rest
            mSListItemDao.update(newItems);
        });
    }

    public void updateLastModified(long id) {
        AsyncTask.execute(() -> {
            mSListDao.updateLastModified(id, System.currentTimeMillis());
        });
    }

    public List<SListItem> getOldItems() {
        return oldItems;
    }

    public void setOldItems(List<SListItem> oldItems) {
        this.oldItems = oldItems;
    }
}

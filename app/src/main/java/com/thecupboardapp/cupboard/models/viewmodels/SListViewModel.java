package com.thecupboardapp.cupboard.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.thecupboardapp.cupboard.database.Database;
import com.thecupboardapp.cupboard.database.SListDao;
import com.thecupboardapp.cupboard.database.SListItemDao;
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

public class SListViewModel extends AndroidViewModel{
    private final String TAG = "SListViewModel";
    private Flowable<List<SList>> mSListFlowable;
    private SListDao mSListDao;

    public static final int SORT_ALPHABETICAL = 0;
    public static final int SORT_LAST_MODIFIED = 1;
    public static final int SORT_DATE_CREATED = 2;

    public SListViewModel(@NonNull Application application) {
        super(application);
        mSListDao = Database.getDatabase(application).sListDao();
        mSListFlowable = mSListDao.getAllFlowable();
    }

    // Get all lists
    public Flowable<List<SList>> getLists(){
        return mSListFlowable;
    }

    // Remove a list
    public void removeList(SList sList){
        AsyncTask.execute(() -> mSListDao.delete(sList));
    }

    // Get the list titles and sort them using a method
    public void sort(int method, List<SList> sLists) {
        List<SList> sortedList = new ArrayList<>(sLists);
        AsyncTask.execute(() -> {
            switch (method) {
                case SORT_ALPHABETICAL: {
                    Log.d(TAG, "sort: " + sLists);
                    Collections.sort(sortedList, (sListA, sListB) ->
                            sListA.getName().compareToIgnoreCase(sListB.getName()));
                    break;
                }
                case SORT_LAST_MODIFIED: {
                    Collections.sort(sortedList, (sListA, sListB) ->
                            Long.compare(sListB.getLastModified(), sListA.getLastModified()));
                    break;
                }
                case SORT_DATE_CREATED: {
                    Collections.sort(sortedList, (sListA, sListB) ->
                            Long.compare(sListA.getId(), sListB.getId()));
                    break;
                }
            }

            for (int i = 0; i < sortedList.size(); i++){
                sortedList.get(i).setIndex(i);
            }

            mSListDao.update(sortedList);
        });
    }
}

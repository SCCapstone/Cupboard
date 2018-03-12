package com.thecupboardapp.cupboard.models.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thecupboardapp.cupboard.database.Repository;
import com.thecupboardapp.cupboard.models.SList;

import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;

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

    public Flowable<List<SList>> getLists(){
        return mSListFlowable;
    }

    public void updateListTitle(int id, String name){
        Log.d(TAG, "updateListTitle: ");
        AsyncTask.execute(() -> {
            mRepository.sListDao().getSingleListById(id).subscribe(sList -> {
                sList.setName(name);
                sList.setLastModifiedNow();
                mRepository.sListDao().update(sList);
            });
        });
    }

    public void removeList(SList sList){
        AsyncTask.execute(() -> {
            mRepository.sListDao().delete(sList);
        });
    }

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
                        Collections.sort(sLists, (sList, t1) -> sList.getId() - t1.getId());
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

    public void newList(SList sList) {
        AsyncTask.execute(() -> {
            mRepository.sListDao().insertAll(sList);
        });
    }

    public Flowable<SList> getListById(int id) {
        Log.d(TAG, "getFlowableListById: ");
        return mRepository.sListDao().getFlowableListById(id);
    }
}

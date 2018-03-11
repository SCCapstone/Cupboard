package com.thecupboardapp.cupboard.models;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thecupboardapp.cupboard.database.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * Created by Kyle on 3/9/2018.
 */

public class SListViewModel extends ViewModel{
    private final String TAG = "SListViewModel";
    private Flowable<List<SList>> mSListFlowable;
    private Repository mRepository;

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

    public void updateOrder(List<SList> newListOrder){
    }

    public void alphabetize() {
        AsyncTask.execute(() -> {
            mRepository.sListDao().getAllSingle().subscribe(sLists -> {
                Collections.sort(sLists, (sList, t1) -> sList.getName().compareToIgnoreCase(t1.getName()));
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

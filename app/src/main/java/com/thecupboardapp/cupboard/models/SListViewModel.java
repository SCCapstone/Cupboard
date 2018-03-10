package com.thecupboardapp.cupboard.models;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.thecupboardapp.cupboard.database.Repository;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Kyle on 3/9/2018.
 */

public class SListViewModel extends ViewModel{
    private final String TAG = "SListViewModel";

    private Flowable<List<SList>> mSListFlowable;
    private Repository mRepository;

    public void SListViewModelFactory(Context context) {
        mRepository = Repository.getDatabase(context);
        mSListFlowable = mRepository.sListDao().getAll();
    }

    public Flowable<List<SList>> getLists(){
        return mSListFlowable;
    }

    public void updateList(SList list){

    }

    public void removeList(SList sList){
        AsyncTask.execute(() -> {
            mRepository.sListDao().delete(sList);
        });
    }

    public void updateOrder(List<SList> newListOrder){

    }

    public void newList(SList sList) {
        AsyncTask.execute(() -> {
            mRepository.sListDao().insertAll(sList);
        });
    }
}

package com.thecupboardapp.cupboard.models;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;

import com.thecupboardapp.cupboard.database.Repository;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Kyle on 3/9/2018.
 */

public class SListItemViewModel extends ViewModel{
    private final String TAG = "SListItemViewModel";

    private Flowable<List<SListItem>> mSListFlowable;
    private Repository mRepository;

    public void SListViewModelFactory(Context context) {
        mRepository = Repository.getDatabase(context);
    }

    public Flowable<List<SListItem>> getListItemsById(int id){
        return mRepository.sListItemDao().getSListItemById(id);
    }
}

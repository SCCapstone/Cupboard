package com.thecupboardapp.cupboard.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thecupboardapp.cupboard.activities.SListEditActivity;
import com.thecupboardapp.cupboard.models.SListHolder;
import com.thecupboardapp.cupboard.models.SList;

import java.util.List;

/**
 * Created by Kyle on 3/9/2018.
 */

public class SListAdapter extends RecyclerView.Adapter<SListHolder> {
    private static final String TAG = "SListAdapter";
    public List<SList> mSLists;

    public SListAdapter(List<SList> sLists) {
        mSLists = sLists;
    }

    @NonNull
    @Override
    public SListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new SListHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull SListHolder holder, int position) {
        SList sList = mSLists.get(position);
        holder.bind(sList);
    }

    @Override
    public int getItemCount() {
        return mSLists.size();
    }
}

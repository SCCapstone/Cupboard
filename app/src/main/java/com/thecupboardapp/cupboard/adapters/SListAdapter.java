package com.thecupboardapp.cupboard.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thecupboardapp.cupboard.activities.SListEditActivity;
import com.thecupboardapp.cupboard.fragments.SListsFragment;
import com.thecupboardapp.cupboard.models.SListHolder;
import com.thecupboardapp.cupboard.models.SList;

import java.util.List;

/**
 * Created by Kyle on 3/9/2018.
 */

public class SListAdapter extends RecyclerView.Adapter<SListHolder> {
    private static final String TAG = "SListAdapter";
    private List<SList> mSLists;

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

    public List<SList> getSLists() {
        return mSLists;
    }

    public void swap(List<SList> sLists) {
        SListDiffCallback callback = new SListDiffCallback(this.mSLists, sLists);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        mSLists.clear();
        mSLists.addAll(sLists);
        diffResult.dispatchUpdatesTo(this);
    }

    private class SListDiffCallback extends DiffUtil.Callback {

        private List<SList> oldLists;
        private List<SList> newLists;

        public SListDiffCallback(List<SList> oldLists, List<SList> newLists) {
            this.oldLists = oldLists;
            this.newLists = newLists;
        }

        @Override
        public int getOldListSize() {
            return oldLists.size();
        }

        @Override
        public int getNewListSize() {
            return newLists.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            /* Look at this first */
            return oldLists.get(oldItemPosition).getIndex() == newLists.get(newItemPosition).getIndex();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            SList oldList = oldLists.get(oldItemPosition);
            SList newList = newLists.get(newItemPosition);

            return oldList.getName().equals(newList.getName());
        }
    }
}

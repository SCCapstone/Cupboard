package com.thecupboardapp.cupboard.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.activities.SListEditActivity;

import java.util.List;

/**
 * Created by Kyle on 3/10/2018.
 */

public class SListItemAdapter extends RecyclerView.Adapter<SListItemHolder> {
    private List<SListItem> mSListItems;

    public SListItemAdapter(List<SListItem> items) {
        mSListItems = items;
    }

    @Override
    public SListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new SListItemHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(SListItemHolder holder, int position) {
        SListItem item = mSListItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mSListItems.size();
    }

    @Override
    public void onViewAttachedToWindow(SListItemHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (SListEditActivity.mIsEnterPressed) {
            holder.itemView.findViewById(R.id.shopping_list_item_name).requestFocus();
            SListEditActivity.mIsEnterPressed = false;
        }
    }

    public void clear() {
        mSListItems.clear();
    }

    public void updateList(List<SListItem> sListItems) {
        mSListItems.clear();
        mSListItems.addAll(sListItems);
    }
}

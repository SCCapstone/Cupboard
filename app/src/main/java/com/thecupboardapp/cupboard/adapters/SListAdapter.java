package com.thecupboardapp.cupboard.adapters;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.activities.SListEditActivity;
import com.thecupboardapp.cupboard.models.SList;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kyle on 3/9/2018.
 */

public class SListAdapter extends RecyclerView.Adapter<SListAdapter.SListHolder> {
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
        AsyncTask.execute(() -> {
            SListDiffCallback callback = new SListDiffCallback(this.mSLists, sLists);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
            mSLists.clear();
            mSLists.addAll(sLists);
            diffResult.dispatchUpdatesTo(this);
        });
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
            return oldLists.get(oldItemPosition).getIndex() == newLists.get(newItemPosition).getIndex();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            SList oldList = oldLists.get(oldItemPosition);
            SList newList = newLists.get(newItemPosition);

            return oldList.getName().equals(newList.getName());
        }
    }

    static class SListHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "SListHolder";
        private TextView mTitleTextView;

        public SListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.holder_slist, parent, false));

            mTitleTextView = itemView.findViewById(R.id.shopping_list_title);
        }

        public void bind(SList sList) {
            mTitleTextView.setText(sList.getName());

            mTitleTextView.setOnClickListener(view -> {
                Intent intent = SListEditActivity.newIntent(view.getContext(), sList.getId());
                view.getContext().startActivity(intent);
            });

            mTitleTextView.setOnLongClickListener(view -> {
                String name = mTitleTextView.getText().toString();
                String[] options = {"option 1"};

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(String.format("\"%s\" Options", name));
                builder.setItems(options, (dialog, which) -> Log.d(TAG, "onClick:"));
                builder.show();
                return true;
            });
        }
    }
}

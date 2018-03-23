package com.thecupboardapp.cupboard.adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.activities.SListEditActivity;
import com.thecupboardapp.cupboard.models.SListItem;

import java.util.Collections;
import java.util.List;

import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;

/**
 * Created by Kyle on 3/10/2018.
 */

public class SListItemAdapter extends RecyclerView.Adapter<SListItemAdapter.SListItemHolder> {
    private static final String TAG = "SListItemAdapter";
    private List<SListItem> mSListItems;
    private RecyclerView mRecyclerView;
    private boolean isEnterPressed;

    public SListItemAdapter(List<SListItem> items) {
        mSListItems = items;
        SListItem emptyItem = new SListItem("", false);
        emptyItem.setIndex(items.size());
        items.add(emptyItem);
    }

    @NonNull
    @Override
    public SListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        mRecyclerView = (RecyclerView) parent;
        return new SListItemHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull SListItemHolder holder, int position) {
        SListItem item = mSListItems.get(position);

        holder.bind(item);

        if (holder.getAdapterPosition() == mSListItems.size() - 1 && item.getName().isEmpty()) {
            holder.mSListItemEditText.setHint(R.string.slist_item_hint);
        }

        // Delete the item from the list
        holder.mSListItemDeleteButton.setOnClickListener(v -> {
            mSListItems.remove(holder.getAdapterPosition());
            this.notifyItemRemoved(holder.getAdapterPosition());
        });

        // Change the value of the text in the data
        holder.mSListItemEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (holder.getAdapterPosition() == mSListItems.size() -1 && item.getName().isEmpty()) {
                    holder.mSListItemEditText.setHint(null);
                    mSListItems.add(new SListItem("", false, mSListItems.size()));
                    mRecyclerView.post(() -> notifyItemInserted(holder.getAdapterPosition() + 1 ));
                }
                item.setName(s.toString());
                Log.d(TAG, "onTextChanged: pos = " + holder.getAdapterPosition() + ", itemname: " + item.getName());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Whenever the enter button is pressed, add an item under it
        holder.mSListItemEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == IME_ACTION_NEXT) {
                if (holder.getAdapterPosition() == mSListItems.size() - 2) {
                    mRecyclerView.scrollToPosition(holder.getAdapterPosition()+1);
                    mRecyclerView.postDelayed(() -> {
                        mRecyclerView.findViewHolderForAdapterPosition(holder.getAdapterPosition()+1)
                                .itemView.findViewById(R.id.slist_item_name).requestFocus();
                    }, 50);

                } else {
                    SListItem newItem = new SListItem("", false, holder.getAdapterPosition() + 1);
                    mSListItems.add(holder.getAdapterPosition() + 1, newItem);
                    notifyItemInserted(holder.getAdapterPosition() + 1);
                    isEnterPressed = true;
                }
                return true;
            }
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return mSListItems.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SListItemHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (isEnterPressed) {
            holder.mSListItemEditText.requestFocus();
            isEnterPressed = false;
        }
    }

    public void updateList(List<SListItem> sListItems) {
        mSListItems.clear();
        mSListItems.addAll(sListItems);
    }

    public List<SListItem> getSListItems() {
        return mSListItems;
    }

    public void sortItemsByIndex(){
        Collections.sort(mSListItems, (sListItem, t1) -> sListItem.getIndex() - t1.getIndex());
    }

    public void reflowItems() {
        for (int i = 0; i < mSListItems.size(); i++) {
            mSListItems.get(i).setIndex(i);
        }
    }

    static class SListItemHolder extends RecyclerView.ViewHolder {
        private EditText mSListItemEditText;
        private CheckBox mSListItemCheckBox;
        private ImageButton mSListItemDeleteButton;

        SListItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.holder_slist_item, parent, false));

            mSListItemEditText = itemView.findViewById(R.id.slist_item_name);
            mSListItemCheckBox = itemView.findViewById(R.id.slist_item_checkbox);
            mSListItemDeleteButton = itemView.findViewById(R.id.slist_item_delete);

            if (SListEditActivity.mIsEnterPressed) {
                itemView.findViewById(R.id.slist_item_name).requestFocus();
                SListEditActivity.mIsEnterPressed = false;
            }
        }

        void bind(SListItem item) {
            mSListItemEditText.setText(item.getName());
            mSListItemCheckBox.setChecked(item.getChecked());

            // Change the value of the check in the data
            mSListItemCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> item.setChecked(isChecked));



        }
    }

    private class SListItemDiffCallback extends DiffUtil.Callback {
        List<SListItem> oldItems;
        List<SListItem> newItems;

        public SListItemDiffCallback(List<SListItem> oldItems, List<SListItem> newItems) {
            this.oldItems = oldItems;
            this.newItems = newItems;
        }

        @Override
        public int getOldListSize() {
            return oldItems.size();
        }

        @Override
        public int getNewListSize() {
            return newItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldItems.get(oldItemPosition).getIndex() == newItems.get(newItemPosition).getIndex();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            SListItem oldListItem = oldItems.get(oldItemPosition);
            SListItem newListItem = newItems.get(newItemPosition);

            return oldListItem.equals(newListItem);
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            SListItem oldListItem = oldItems.get(oldItemPosition);
            SListItem newListItem = newItems.get(newItemPosition);
            Bundle diffBundle = new Bundle();



            return diffBundle;
        }
    }
}
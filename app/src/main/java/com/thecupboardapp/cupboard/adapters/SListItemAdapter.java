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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.activities.SListEditActivity;
import com.thecupboardapp.cupboard.models.SListItem;

import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;

/**
 * Created by Kyle on 3/10/2018.
 */

public class SListItemAdapter extends RecyclerView.Adapter<SListItemAdapter.SListItemHolder> {
    private List<SListItem> mSListItems;
    private RecyclerView mRecyclerView;
    private long mParentId;
    private boolean isEnterPressed;

    public SListItemAdapter(List<SListItem> items, long parentId) {
        mSListItems = items;
        mParentId = parentId;

        SListItem emptyItem = new SListItem("", false, items.size());
        emptyItem.setParentId(mParentId);
        items.add(emptyItem);
    }

    @NonNull
    @Override
    public SListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mRecyclerView = (RecyclerView) parent;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        return new SListItemHolder(layoutInflater,
                mRecyclerView,
                new ItemTextWatcher(),
                new ItemOnEditorActionListener(),
                new ItemOnCheckedChangedListener());
    }

    @Override
    public void onBindViewHolder(@NonNull SListItemHolder holder, int position) {
        SListItem item = mSListItems.get(position);
        holder.bind(item);

        // Set the hint if its an empty list or its the last element
        if (holder.getAdapterPosition() == mSListItems.size() - 1 && item.getName().isEmpty()) {
            holder.mSListItemEditText.setHint(R.string.slist_item_hint);
        }

        // Delete the item from the list
        holder.mSListItemDeleteButton.setOnClickListener(v -> {
            if (mSListItems.size() == 1) return; // Never delete the last one
            mSListItems.remove(holder.getAdapterPosition());
            this.notifyItemRemoved(holder.getAdapterPosition());
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

    public void setParentId(long id){
        for (SListItem item : mSListItems) {
            item.setParentId(id);
        }
    }

    public List<SListItem> getSListItems() {
        List<SListItem> newList = new ArrayList<>(mSListItems);
        newList.remove(newList.size()-1);
        return newList;
    }

    static class SListItemHolder extends RecyclerView.ViewHolder {
        private EditText mSListItemEditText;
        private CheckBox mSListItemCheckBox;
        private ImageButton mSListItemDeleteButton;

        private ItemTextWatcher mItemTextWatcher;
        private ItemOnEditorActionListener mItemOnEditorActionListener;
        private ItemOnCheckedChangedListener mItemOnCheckedChangeListener;

        SListItemHolder(LayoutInflater inflater,
                        ViewGroup parent,
                        ItemTextWatcher textWatcher,
                        ItemOnEditorActionListener itemOnEditorActionListener,
                        ItemOnCheckedChangedListener onCheckedChangeListener) {
            super(inflater.inflate(R.layout.holder_slist_item, parent, false));

            mSListItemEditText = itemView.findViewById(R.id.slist_item_name);
            mSListItemCheckBox = itemView.findViewById(R.id.slist_item_checkbox);
            mSListItemDeleteButton = itemView.findViewById(R.id.slist_item_delete);

            mItemTextWatcher = textWatcher;
            mSListItemEditText.addTextChangedListener(mItemTextWatcher);

            mItemOnEditorActionListener = itemOnEditorActionListener;
            mSListItemEditText.setOnEditorActionListener(mItemOnEditorActionListener);

            mItemOnCheckedChangeListener = onCheckedChangeListener;
            mSListItemCheckBox.setOnCheckedChangeListener(mItemOnCheckedChangeListener);

            if (SListEditActivity.mIsEnterPressed) {
                itemView.findViewById(R.id.slist_item_name).requestFocus();
                SListEditActivity.mIsEnterPressed = false;
            }
        }

        void bind(SListItem item) {
            // Update items in the listeners
            mItemTextWatcher.updateSListItem(item);
            mItemOnEditorActionListener.updateSListItem(item);
            mItemOnCheckedChangeListener.updateSListItem(item);

            // Set the values
            mSListItemEditText.setText(item.getName());
            mSListItemCheckBox.setChecked(item.getChecked());
        }
    }

    private class ItemTextWatcher implements TextWatcher {
        private SListItem mSListItem;

        void updateSListItem(SListItem sListItem) {
            mSListItem = sListItem;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            int index = mSListItems.indexOf(mSListItem);
            if (index == mSListItems.size() -1 && mSListItem.getName().isEmpty()) {
                if (s.toString().isEmpty()) return;
                // EditText something = mRecyclerView.getChildAt(position).findViewById(R.id.slist_item_name);
                // something.setHint(null);

                SListItem newItem = new SListItem("", false, mSListItems.size());
                newItem.setParentId(mParentId);
                mSListItems.add(newItem);
                mRecyclerView.post(() -> notifyItemInserted(index  + 1 ));
            }

            mSListItem.setName(s.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    }

    private class ItemOnEditorActionListener implements TextView.OnEditorActionListener {
        private SListItem mSListItem;

        void updateSListItem(SListItem sListItem) {
            mSListItem = sListItem;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            int index = mSListItems.indexOf(mSListItem);
            if (actionId == IME_ACTION_NEXT) {
                if (index == mSListItems.size() - 2) {
                    mRecyclerView.scrollToPosition(index + 1);
                    mRecyclerView.postDelayed(() -> {
                        mRecyclerView.findViewHolderForAdapterPosition(index + 1)
                                .itemView.findViewById(R.id.slist_item_name).requestFocus();
                    }, 50);
                } else {
                    SListItem newItem = new SListItem("", false, index + 1);
                    mSListItems.add(index + 1, newItem);
                    notifyItemInserted(index + 1);
                    isEnterPressed = true;
                }
                return true;
            }
            return false;
        }
    }

    private class ItemOnCheckedChangedListener implements CompoundButton.OnCheckedChangeListener {
        private SListItem mSListItem;

        void updateSListItem(SListItem sListItem) {
            mSListItem = sListItem;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int index = mSListItems.indexOf(mSListItem);
            mSListItems.get(index).setChecked(isChecked);
        }
    }
}
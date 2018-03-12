package com.thecupboardapp.cupboard.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.activities.SListEditActivity;
import com.thecupboardapp.cupboard.models.SListItem;

import java.util.List;

/**
 * Created by Kyle on 3/10/2018.
 */

public class SListItemAdapter extends RecyclerView.Adapter<SListItemAdapter.SListItemHolder> {
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
    public void onBindViewHolder(@NonNull SListItemHolder holder, int position) {
        SListItem item = mSListItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mSListItems.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SListItemHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (SListEditActivity.mIsEnterPressed) {
            holder.itemView.findViewById(R.id.shopping_list_item_name).requestFocus();
            SListEditActivity.mIsEnterPressed = false;
        }
    }

    public void updateList(List<SListItem> sListItems) {
        mSListItems.clear();
        mSListItems.addAll(sListItems);
    }

    static class SListItemHolder extends RecyclerView.ViewHolder {
        private EditText mSListItemEditText;
        private CheckBox mSListItemCheckBox;
        private ImageButton mSListItemDeleteButton;

        SListItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.slist_item_holder, parent, false));

            mSListItemEditText = itemView.findViewById(R.id.shopping_list_item_name);
            mSListItemCheckBox = itemView.findViewById(R.id.shopping_list_item_checkbox);
            mSListItemDeleteButton = itemView.findViewById(R.id.shopping_list_item_delete);

            if (SListEditActivity.mIsEnterPressed) {
                itemView.findViewById(R.id.shopping_list_item_name).requestFocus();
                SListEditActivity.mIsEnterPressed = false;
            }
        }

        void bind(SListItem item) {
            mSListItemEditText.setText(item.getName());
            mSListItemCheckBox.setChecked(item.getChecked());

            // Change the value of the check in the data
            mSListItemCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> item.setChecked(isChecked));

            // Change the value of the text in the data
            mSListItemEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    item.setName(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // // Whenever the enter button is pressed, add an item under it
            // mSListItemEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            //     @Override
            //     public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //         if (actionId == IME_ACTION_NEXT) {
            //             SListItem item = new SListItem();
            //             item.setFirebaseId(FirebaseDatabase.getInstance().getReference().push().getKey());
            //
            //             mSList.getShoppingListItems().add(getAdapterPosition() + 1, item);
            //             mAdapter.notifyItemInserted(getAdapterPosition() + 1);
            //             mRecyclerView.scrollToPosition(getAdapterPosition() + 1);
            //             mIsEnterPressed = true;
            //             return true;
            //         }
            //         return false;
            //     }
            // });
            //
            mSListItemDeleteButton.setOnClickListener(v -> {
                /* remove the item*/
            });
        }
    }
}
package com.thecupboardapp.cupboard.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.activities.SListEditActivity;

/**
 * Created by Kyle on 3/10/2018.
 */

public class SListItemHolder extends RecyclerView.ViewHolder {
    private EditText mSListItemEditText;
    private CheckBox mSListItemCheckBox;
    private ImageButton mSListItemDeleteButton;

    public SListItemHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.shopping_list_item_holder, parent, false));

        mSListItemEditText = itemView.findViewById(R.id.shopping_list_item_name);
        mSListItemCheckBox = itemView.findViewById(R.id.shopping_list_item_checkbox);
        mSListItemDeleteButton = itemView.findViewById(R.id.shopping_list_item_delete);

        if (SListEditActivity.mIsEnterPressed) {
            itemView.findViewById(R.id.shopping_list_item_name).requestFocus();
            SListEditActivity.mIsEnterPressed = false;
        }
    }

    public void bind(SListItem item) {
        mSListItemEditText.setText(item.getName());
        mSListItemCheckBox.setChecked(item.getChecked());

        // // Change the value of the check in the data
        // mSListItemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        //     @Override
        //     public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //         mSListItem.setChecked(isChecked);
        //     }
        // });
        //
        // // Change the value of the text in the data
        // mSListItemEditText.addTextChangedListener(new TextWatcher() {
        //     @Override
        //     public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //         // This is intentionally blank
        //     }
        //
        //     @Override
        //     public void onTextChanged(CharSequence s, int start, int before, int count) {
        //         mSListItem.setName(s.toString());
        //     }
        //
        //     @Override
        //     public void afterTextChanged(Editable s) {
        //         // this is also intentionally blank
        //     }
        // });
        //
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
        // mSListItemDeleteButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         mSList.getShoppingListItems().remove(getAdapterPosition());
        //         mAdapter.notifyItemRemoved(getAdapterPosition());
        //     }
        // });
    }

}

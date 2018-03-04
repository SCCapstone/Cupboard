package com.thecupboardapp.cupboard;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;

public class ShoppingListActivity extends AppCompatActivity {
    private static final String EXTRA_SHOPPING_LIST_ID =
            "com.thecupboardapp.cupboard.shopping_list_id";
    private String TAG = "ShoppingListActivity";
    private ShoppingList mShoppingList;
    private RecyclerView mRecyclerView;
    private ShoppingListItemAdapter mAdapter;

    private boolean mIsEnterPressed = false;

    @Override
    public void onBackPressed() {
        Log.d(TAG, "back pressed");
        Intent resultInt = new Intent();
        resultInt.putExtra("Result", "Done");

        int isNewList = getIntent().getIntExtra(ShoppingListsFragment.NEW_LIST_REQUEST_ID, 0);

        if (isNewList == ShoppingListsFragment.NEW_LIST_REQUEST) {
            UserData.get(this).addShoppingList(mShoppingList);
            setResult(RESULT_OK, resultInt);
        }

        super.onBackPressed();
    }

    public static Intent newIntent(Context packageContext, UUID shoppingListId) {
        Intent intent = new Intent(packageContext, ShoppingListActivity.class);
        intent.putExtra(EXTRA_SHOPPING_LIST_ID, shoppingListId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        UUID shoppingListId = (UUID) getIntent().getSerializableExtra(EXTRA_SHOPPING_LIST_ID);

        if (shoppingListId == null) {
            mShoppingList = new ShoppingList();
            mShoppingList.getShoppingListItems().add(new ShoppingListItem());
            setTitle("New Shopping List");
        } else {
            mShoppingList = UserData.get(this).getShoppingList(shoppingListId);
            setTitle(UserData.get(this).getShoppingList(shoppingListId).getName());
        }
        mShoppingList.setLastModified(System.currentTimeMillis());
        mRecyclerView = (RecyclerView) findViewById(R.id.shopping_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ShoppingListActivity.this));

        updateUI();
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new ShoppingListItemAdapter(mShoppingList.getShoppingListItems());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ShoppingListItemHolder extends RecyclerView.ViewHolder {
        private ShoppingListItem mShoppingListItem;
        private EditText mShoppingListItemEditText;
        private CheckBox mShoppingListItemCheckBox;
        private ImageButton mShoppingListItemDeleteButton;

        public ShoppingListItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.shopping_list_item_holder, parent, false));
            mShoppingListItemEditText = (EditText) itemView.findViewById(R.id.shopping_list_item_name);
            mShoppingListItemCheckBox = (CheckBox) itemView.findViewById(R.id.shopping_list_item_checkbox);
            mShoppingListItemDeleteButton = (ImageButton) itemView.findViewById(R.id.shopping_list_item_delete);

            if (mIsEnterPressed) {
                itemView.findViewById(R.id.shopping_list_item_name).requestFocus();
                mIsEnterPressed = false;
            }
        }

        public void bind(ShoppingListItem item) {
            mShoppingListItem = item;
            mShoppingListItemEditText.setText(mShoppingListItem.getName());
            mShoppingListItemCheckBox.setChecked(mShoppingListItem.isChecked());

            // Change the value of the check in the data
            mShoppingListItemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mShoppingListItem.setChecked(isChecked);
                }
            });

            // Change the value of the text in the data
            mShoppingListItemEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // This is intentionally blank
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mShoppingListItem.setName(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // this is also intentionally blank
                }
            });

            // Whenever the enter button is pressed, add an item under it
            mShoppingListItemEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == IME_ACTION_NEXT) {
                        mShoppingList.getShoppingListItems().add(getAdapterPosition() + 1, new ShoppingListItem());
                        mAdapter.notifyItemInserted(getAdapterPosition() + 1);
                        mRecyclerView.scrollToPosition(getAdapterPosition() + 1);
                        mIsEnterPressed = true;
                        return true;
                    }
                    return false;
                }
            });

            mShoppingListItemDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mShoppingList.getShoppingListItems().remove(getAdapterPosition());
                    mAdapter.notifyItemRemoved(getAdapterPosition());
                }
            });
        }

    }

    private class ShoppingListItemAdapter extends RecyclerView.Adapter<ShoppingListItemHolder> {

        private List<ShoppingListItem> mShoppingListItems;

        public ShoppingListItemAdapter(List<ShoppingListItem> items) {
            mShoppingListItems = items;
        }

        @Override
        public ShoppingListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(ShoppingListActivity.this);
            return new ShoppingListItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ShoppingListItemHolder holder, int position) {
            ShoppingListItem item = mShoppingListItems.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return mShoppingListItems.size();
        }

        @Override
        public void onViewAttachedToWindow(ShoppingListItemHolder holder) {
            super.onViewAttachedToWindow(holder);

            if (mIsEnterPressed) {
                holder.itemView.findViewById(R.id.shopping_list_item_name).requestFocus();
                mIsEnterPressed = false;
            }
        }

    }
}

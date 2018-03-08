package com.thecupboardapp.cupboard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;

public class SListActivity extends AppCompatActivity {
    private static final String EXTRA_SHOPPING_LIST_ID =
            "com.thecupboardapp.cupboard.shopping_list_id";
    private String TAG = "SListActivity";
    private SList mSList;
    private RecyclerView mRecyclerView;
    private ShoppingListItemAdapter mAdapter;

    private boolean mIsEnterPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        UUID shoppingListId = (UUID) getIntent().getSerializableExtra(EXTRA_SHOPPING_LIST_ID);

        if (shoppingListId == null) {
            mSList = new SList();
            mSList.getShoppingListItems().add(new SListItem());
            setTitle("New Shopping List");
        } else {
            mSList = UserData.get(this).getShoppingList(shoppingListId);
            setTitle(UserData.get(this).getShoppingList(shoppingListId).getName());
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.shopping_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SListActivity.this));

        updateUI();
    }

    @Override
    public boolean shouldUpRecreateTask(Intent targetIntent) {
        return false;
    }

    @Override
    public void onPause() {
        Intent resultInt = new Intent();
        resultInt.putExtra("Result", "Done");

        int isNewList = getIntent().getIntExtra(SListsFragment.NEW_LIST_REQUEST_ID, 0);

        if (isNewList == SListsFragment.NEW_LIST_REQUEST) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("lists")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .push();


            ref.child("name").setValue(mSList.getName());
            ref.child("lastModified").setValue(System.currentTimeMillis());
            for (SListItem item: mSList.getShoppingListItems()) {
                DatabaseReference listref = ref.child("items").push();
                item.setFirebaseId(listref.getKey());
                listref.child("name").setValue(item.getName());
                listref.child("checked").setValue(item.isChecked());
            }

            mSList.setFirebaseId(ref.getKey());
            mSList.setLastModified(System.currentTimeMillis());

            UserData.get(this).addShoppingList(mSList);
            setResult(RESULT_OK, resultInt);
            finish();
            Log.d(TAG, "onStop: settting result ok");

        } else {
            Map<String, Object> list = new HashMap<String, Object>();
            list.put("name", mSList.getName());
            list.put("lastModified", System.currentTimeMillis());
            mSList.setLastModified(System.currentTimeMillis());

            Map<String, Object> items = new HashMap<String, Object>();
            for (SListItem item: mSList.getShoppingListItems()) {
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("name", item.getName());
                values.put("checked", item.isChecked());
                if (item.getFirebaseId() == null) {
                    String id = FirebaseDatabase.getInstance().getReference().child("lists")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(mSList.getFirebaseId()).push().getKey();
                    items.put(id, values);
                } else {
                    items.put(item.getFirebaseId(), values);
                }

            }

            list.put("items", items);
            Log.d(TAG, list.toString());
            FirebaseDatabase.getInstance().getReference().child("lists")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(mSList.getFirebaseId()).updateChildren(list);
        }

        super.onPause();
    }

    public static Intent newIntent(Context packageContext, UUID shoppingListId) {
        Intent intent = new Intent(packageContext, SListActivity.class);
        intent.putExtra(EXTRA_SHOPPING_LIST_ID, shoppingListId);
        return intent;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        if (item.getItemId() == R.id.edit_title) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText edittext = new EditText(this);
            edittext.setText(mSList.getName());
            alert.setTitle("Title");
            alert.setView(edittext);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    SListActivity.this.setTitle(edittext.getText().toString());
                    mSList.setName(edittext.getText().toString());
                }
            });

            alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            alert.show();
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Make the navigate up button act as a back press
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new ShoppingListItemAdapter(mSList.getShoppingListItems());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ShoppingListItemHolder extends RecyclerView.ViewHolder {
        private SListItem mSListItem;
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

        public void bind(SListItem item) {
            mSListItem = item;
            mShoppingListItemEditText.setText(mSListItem.getName());
            mShoppingListItemCheckBox.setChecked(mSListItem.isChecked());

            // Change the value of the check in the data
            mShoppingListItemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mSListItem.setChecked(isChecked);
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
                    mSListItem.setName(s.toString());
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
                        SListItem item = new SListItem();
                        item.setFirebaseId(FirebaseDatabase.getInstance().getReference().push().getKey());

                        mSList.getShoppingListItems().add(getAdapterPosition() + 1, item);
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
                    mSList.getShoppingListItems().remove(getAdapterPosition());
                    mAdapter.notifyItemRemoved(getAdapterPosition());
                }
            });
        }

    }

    private class ShoppingListItemAdapter extends RecyclerView.Adapter<ShoppingListItemHolder> {

        private List<SListItem> mSListItems;

        public ShoppingListItemAdapter(List<SListItem> items) {
            mSListItems = items;
        }

        @Override
        public ShoppingListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(SListActivity.this);
            return new ShoppingListItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ShoppingListItemHolder holder, int position) {
            SListItem item = mSListItems.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return mSListItems.size();
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

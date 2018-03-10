package com.thecupboardapp.cupboard.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;
import com.thecupboardapp.cupboard.models.SListItemAdapter;
import com.thecupboardapp.cupboard.models.SListItemViewModel;
import com.thecupboardapp.cupboard.models.SListViewModel;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SListEditActivity extends AppCompatActivity {
    private String TAG = "SListEditActivity";
    private List<SListItem> mSListItems;
    private RecyclerView mRecyclerView;
    private SListItemAdapter mAdapter;
    private int extra;

    private Disposable mDisposable;

    public static final String EXTRA_ID = "com.thecupboardapp.cupboard.EXTRA_ID";

    public static boolean mIsEnterPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.shopping_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SListEditActivity.this));

        extra = getIntent().getIntExtra(EXTRA_ID, -1);

        Log.d(TAG, "onCreate: " + extra);

        SListItemViewModel itemViewModel = ViewModelProviders.of(this).get(SListItemViewModel.class);
        itemViewModel.SListViewModelFactory(this);
        mDisposable = itemViewModel.getListItemsById(extra)
                .subscribeOn(Schedulers.io())
                .subscribe(sListItems -> {
                    Log.d(TAG, "onCreate: " + sListItems.toString());
                    mAdapter = new SListItemAdapter(sListItems);
                    mRecyclerView.setAdapter(mAdapter);
                });
    }

    @Override
    protected void onStop() {
        if (extra == -1) {
            SListViewModel mSListViewModel = ViewModelProviders.of(this).get(SListViewModel.class);
            mSListViewModel.SListViewModelFactory(this);
            mSListViewModel.newList(new SList("activity list", 99));
        }

        mDisposable.dispose();
        super.onStop();
    }

    // @Override
    // public void onPause() {
    //     Intent resultInt = new Intent();
    //     resultInt.putExtra("Result", "Done");
    //
    //     int isNewList = getIntent().getIntExtra(SListsFragment.NEW_LIST_REQUEST_ID, 0);
    //
    //     if (isNewList == SListsFragment.NEW_LIST_REQUEST) {
    //
    //         DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("lists")
    //                 .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
    //                 .push();
    //
    //
    //         ref.child("name").setValue(mSList.getName());
    //         ref.child("lastModified").setValue(System.currentTimeMillis());
    //         for (SListItem item: mSList.getShoppingListItems()) {
    //             DatabaseReference listref = ref.child("items").push();
    //             item.setFirebaseId(listref.getKey());
    //             listref.child("name").setValue(item.getName());
    //             listref.child("checked").setValue(item.isChecked());
    //         }
    //
    //         mSList.setFirebaseId(ref.getKey());
    //         mSList.setLastModified(System.currentTimeMillis());
    //
    //         UserData.get(this).addShoppingList(mSList);
    //         setResult(RESULT_OK, resultInt);
    //         finish();
    //         Log.d(TAG, "onStop: settting result ok");
    //
    //     } else {
    //         Map<String, Object> list = new HashMap<String, Object>();
    //         list.put("name", mSList.getName());
    //         list.put("lastModified", System.currentTimeMillis());
    //         mSList.setLastModified(System.currentTimeMillis());
    //
    //         Map<String, Object> items = new HashMap<String, Object>();
    //         for (SListItem item: mSList.getShoppingListItems()) {
    //             Map<String, Object> values = new HashMap<String, Object>();
    //             values.put("name", item.getName());
    //             values.put("checked", item.isChecked());
    //             if (item.getFirebaseId() == null) {
    //                 String id = FirebaseDatabase.getInstance().getReference().child("lists")
    //                         .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
    //                         .child(mSList.getFirebaseId()).push().getKey();
    //                 items.put(id, values);
    //             } else {
    //                 items.put(item.getFirebaseId(), values);
    //             }
    //
    //         }
    //
    //         list.put("items", items);
    //         Log.d(TAG, list.toString());
    //         FirebaseDatabase.getInstance().getReference().child("lists")
    //                 .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
    //                 .child(mSList.getFirebaseId()).updateChildren(list);
    //     }
    //
    //     super.onPause();
    // }
    //

    public static Intent newIntent(Context packageContext, int id) {
        Intent intent = new Intent(packageContext, SListEditActivity.class);
        intent.putExtra(EXTRA_ID, id);
        return intent;
    }
    //
    // public boolean onCreateOptionsMenu(Menu menu) {
    //     MenuInflater inflater = getMenuInflater();
    //     inflater.inflate(R.menu.list_menu, menu);
    //     return true;
    // }
    //
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    //     AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    //
    //     if (item.getItemId() == R.id.edit_title) {
    //         AlertDialog.Builder alert = new AlertDialog.Builder(this);
    //         final EditText edittext = new EditText(this);
    //         edittext.setText(mSList.getName());
    //         alert.setTitle("Title");
    //         alert.setView(edittext);
    //
    //         alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    //             public void onClick(DialogInterface dialog, int whichButton) {
    //                 SListEditActivity.this.setTitle(edittext.getText().toString());
    //                 mSList.setName(edittext.getText().toString());
    //             }
    //         });
    //
    //         alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
    //             public void onClick(DialogInterface dialog, int whichButton) {
    //             }
    //         });
    //
    //         alert.show();
    //     } else if (item.getItemId() == android.R.id.home) {
    //         onBackPressed(); // Make the navigate up button act as a back press
    //         return true;
    //     }
    //
    //     return super.onOptionsItemSelected(item);
    // }
    //
    // private void updateUI() {
    //     if (mAdapter == null) {
    //         mAdapter = new SListItemAdapter(mSList.getShoppingListItems());
    //         mRecyclerView.setAdapter(mAdapter);
    //     } else {
    //         mAdapter.notifyDataSetChanged();
    //     }
    // }
    //
}

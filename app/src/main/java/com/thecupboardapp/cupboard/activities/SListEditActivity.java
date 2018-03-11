package com.thecupboardapp.cupboard.activities;

import android.arch.lifecycle.ViewModelProviders;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;
import com.thecupboardapp.cupboard.models.SListItemAdapter;
import com.thecupboardapp.cupboard.models.SListItemViewModel;
import com.thecupboardapp.cupboard.models.SListViewModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SListEditActivity extends AppCompatActivity {
    private String TAG = "SListEditActivity";
    private RecyclerView mRecyclerView;
    private SListItemAdapter mAdapter;
    private int extra;

    private CompositeDisposable mDisposables = new CompositeDisposable();

    public static final String EXTRA_ID = "com.thecupboardapp.cupboard.EXTRA_ID";

    public static boolean mIsEnterPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        mRecyclerView = findViewById(R.id.shopping_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SListEditActivity.this));

        extra = getIntent().getIntExtra(EXTRA_ID, -1);

        Log.d(TAG, "onCreate: " + extra);

        SListItemViewModel itemViewModel = ViewModelProviders.of(this).get(SListItemViewModel.class);
        itemViewModel.SListViewModelFactory(this);
        Disposable disposableItems = itemViewModel.getListItemsById(extra)
                .subscribeOn(Schedulers.io())
                .subscribe(sListItems -> {
                    if (mAdapter == null) {
                        Log.d(TAG, "onCreate: null adapter");
                        mAdapter = new SListItemAdapter(sListItems);
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        Log.d(TAG, "onCreate: not null adapter");
                        mAdapter.updateList(sListItems);
                        mAdapter.notifyDataSetChanged();
                    }
                });

        SListViewModel sListViewModel = ViewModelProviders.of(this).get(SListViewModel.class);
        sListViewModel.SListViewModelFactory(this);

        Disposable disposableTitle = sListViewModel.getListById(extra)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sList -> {
                    Log.d(TAG, "disposableTitle sub");
                    SListEditActivity.this.setTitle(sList.getName());
                });

        mDisposables.addAll(disposableItems, disposableTitle);
    }

    @Override
    protected void onStop() {
        if (extra == -1 ) {
            SListViewModel mSListViewModel = ViewModelProviders.of(this).get(SListViewModel.class);
            mSListViewModel.newList(new SList("activity list", 99));
        }

        mDisposables.dispose();
        super.onStop();
    }

    public static Intent newIntent(Context packageContext, int id) {
        Intent intent = new Intent(packageContext, SListEditActivity.class);
        intent.putExtra(EXTRA_ID, id);
        return intent;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_title: {
                editTitle();
                break;
            }
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            default: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void editTitle(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        View v = getLayoutInflater().inflate(R.layout.dialog_slist_edit_title,null);
        EditText text = v.findViewById(R.id.slist_edit_title);

        alert.setTitle("Title");
        alert.setView(v);

        alert.setPositiveButton("OK", (dialog, whichButton) -> {
            ViewModelProviders.of(this).get(SListViewModel.class).updateListTitle(extra, text.getText().toString());
        });

        alert.setNegativeButton("Back", (dialog, whichButton) -> {});

        alert.show();
    }
}
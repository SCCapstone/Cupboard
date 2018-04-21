package com.thecupboardapp.cupboard.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.activities.SListEditActivity;
import com.thecupboardapp.cupboard.adapters.SListAdapter;
import com.thecupboardapp.cupboard.decorations.SimpleDividerItemDecoration;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.viewmodels.SListViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Kyle on 1/12/2018.
 */

public class SListsFragment extends Fragment{
    private final String TAG = "SListsFragment";

    public static final int NEW_LIST_REQUEST_CODE = 1;

    private FloatingActionButton mNewListFAB;
    private RecyclerView mSListsRecyclerView;
    private SListAdapter mAdapter;
    private SListViewModel mSListViewModel;
    private Disposable mDisposableSList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSListViewModel = ViewModelProviders.of(getActivity()).get(SListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View v = inflater.inflate(R.layout.fragment_slists, container, false);
        getActivity().setTitle(R.string.title_lists);
        setHasOptionsMenu(true);

        mNewListFAB = v.findViewById(R.id.add_list_fab);
        mSListsRecyclerView = v.findViewById(R.id.shopping_lists_recycler_view);

        mNewListFAB.setOnClickListener(v1 -> createNewList());
        mSListsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSListsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        createItemTouchHelper().attachToRecyclerView(mSListsRecyclerView);

        return v;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        if (mDisposableSList == null) {
            mDisposableSList = mSListViewModel.getLists()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(sLists -> {
                        if (mAdapter == null) {
                            mAdapter = new SListAdapter(sLists);
                            mSListsRecyclerView.setAdapter(mAdapter);
                        } else {
                            mAdapter.swap(sLists);
                        }
                    });
        }
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.slist_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_alphabetically:
                mSListViewModel.sort(SListViewModel.SORT_ALPHABETICAL, mAdapter.getSLists());
                return true;
            case R.id.menu_sort_last_modified:
                mSListViewModel.sort(SListViewModel.SORT_LAST_MODIFIED, mAdapter.getSLists());
                return true;
            case R.id.menu_sort_date_created:
                mSListViewModel.sort(SListViewModel.SORT_DATE_CREATED, mAdapter.getSLists());
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        mDisposableSList.dispose();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_LIST_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "onActivityResult: New List Result OK!");
                    mAdapter.notifyDataSetChanged();
                }
            }
            default: {
                Log.d(TAG, "onActivityResult: Default");
            }
        }
    }

    private void createNewList() {
        Intent intent = new Intent(getActivity(), SListEditActivity.class);
        startActivity(intent);
    }

    private ItemTouchHelper createItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int pos = viewHolder.getAdapterPosition();
                SList list = mAdapter.getSLists().get(pos);
                ViewModelProviders.of(getActivity()).get(SListViewModel.class).removeList(list);

                Toast.makeText(getActivity(), "List Removed", Toast.LENGTH_SHORT).show();
            }
        };

        return new ItemTouchHelper(simpleItemTouchCallback);
    }
}



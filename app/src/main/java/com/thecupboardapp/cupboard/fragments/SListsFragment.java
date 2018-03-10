package com.thecupboardapp.cupboard.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.adapters.SListAdapter;
import com.thecupboardapp.cupboard.models.SListViewModel;
import com.thecupboardapp.cupboard.activities.SListEditActivity;

import java.util.List;

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
    public static final String NEW_LIST_REQUEST_CODE_KEY = "com.thecupboardapp.cupboard.new_list";

    private FloatingActionButton mNewListFAB;
    private RecyclerView mSListsRecyclerView;
    private List<SList> mSLists;
    private SListAdapter mAdapter;
    private SListViewModel mSListViewModel;
    private Disposable mDisposableSList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View v = inflater.inflate(R.layout.shopping_lists_fragment, container, false);
        getActivity().setTitle(R.string.title_lists);

        mNewListFAB = v.findViewById(R.id.add_list_fab);
        mSListsRecyclerView = v.findViewById(R.id.shopping_lists_recycler_view);

        mNewListFAB.setOnClickListener(v1 -> createNewList());
        mSListsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        createItemTouchHelper().attachToRecyclerView(mSListsRecyclerView);

        return v;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        mSListViewModel = ViewModelProviders.of(getActivity()).get(SListViewModel.class);
        mSListViewModel.SListViewModelFactory(getContext());

        mDisposableSList = mSListViewModel.getLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sLists -> {
                    if (mSLists == null) {
                        Log.d(TAG, "onStart: first run null");
                        mSLists = sLists;
                        mAdapter = new SListAdapter(mSLists);
                        mSListsRecyclerView.setAdapter(mAdapter);
                    } else {
                        Log.d(TAG, "onStart: subscribed data changed");
                        mSLists.clear();
                        mSLists.addAll(sLists);
                        mAdapter.notifyDataSetChanged();
                    }
        });

        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: Dispose");
        mDisposableSList.dispose();
        super.onStop();
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
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int pos = viewHolder.getAdapterPosition();
                SList list = mSLists.get(pos);

                mSLists.remove(pos);
                mAdapter.notifyItemRemoved(pos);
                mSListViewModel.removeList(list);

                Toast.makeText(getActivity(), "List Removed", Toast.LENGTH_SHORT).show();
            }
        };

        return new ItemTouchHelper(simpleItemTouchCallback);
    }
}



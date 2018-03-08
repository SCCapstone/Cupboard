package com.thecupboardapp.cupboard;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Kyle on 1/12/2018.
 */

public class SListsFragment extends Fragment{
    private final String TAG = "SListsFragment";

    private FloatingActionButton mAddListFAB;
    private RecyclerView mShoppingListsRecyclerView;
    private List<SList> mSLists;
    private ShoppingListAdapter mAdapter;

    static final int NEW_LIST_REQUEST = 1;
    static final String NEW_LIST_REQUEST_ID =
            "com.thecupboardapp.cupboard.new_shopping_list_id";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_lists);

        mSLists = UserData.get(getActivity()).getSLists();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: resultcode: " + resultCode);
        if (requestCode == NEW_LIST_REQUEST) {
            if (resultCode == RESULT_OK) {
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "new list result OK!");
            } else {
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "new list result not ok");
            }
        }
    }

    @Override
    public void onResume() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mAdapter = new ShoppingListAdapter(mSLists);
            mShoppingListsRecyclerView.setAdapter(mAdapter);
        }
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            View v = inflater.inflate(R.layout.sign_in_fragment, container, false);
            return v;
        }

        View v = inflater.inflate(R.layout.shopping_lists_fragment, container, false);

        mAddListFAB = (FloatingActionButton) v.findViewById(R.id.add_list_fab);
        mAddListFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SListActivity.class);
                intent.putExtra(NEW_LIST_REQUEST_ID, NEW_LIST_REQUEST);
                startActivityForResult(intent, NEW_LIST_REQUEST);
            }
        });
        mShoppingListsRecyclerView = (RecyclerView) v.findViewById(R.id.shopping_lists_recycler_view);
        mShoppingListsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

                FirebaseDatabase.getInstance().getReference().child("lists")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(list.getFirebaseId()).removeValue();

                Toast.makeText(getActivity(), "List Removed", Toast.LENGTH_SHORT).show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(mShoppingListsRecyclerView);

        return v;
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new ShoppingListAdapter(mSLists);
            mShoppingListsRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ShoppingListHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private SList mSList;
        private TextView mTitleTextView;

        public ShoppingListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.shopping_list_title_holder, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.shopping_list_title);
        }

        public void bind(SList sList) {
            mSList = sList;
            mTitleTextView.setText(mSList.getName());
        }

        @Override
        public void onClick(View view) {
            Intent intent = SListActivity.newIntent(getActivity(), mSList.getId());
            startActivity(intent);
        }
    }

    private class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListHolder> {

        private List<SList> mSLists;

        public ShoppingListAdapter(List<SList> sLists) {
            mSLists = sLists;
        }

        @Override
        public ShoppingListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ShoppingListHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ShoppingListHolder holder, int position) {
            SList sList = mSLists.get(position);
            holder.bind(sList);
        }

        @Override
        public int getItemCount() {
            return mSLists.size();
        }
    }
}



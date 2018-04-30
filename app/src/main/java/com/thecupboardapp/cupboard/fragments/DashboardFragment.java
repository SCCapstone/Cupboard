package com.thecupboardapp.cupboard.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.activities.ManualEntryActivity;
import com.thecupboardapp.cupboard.activities.SListEditActivity;
import com.thecupboardapp.cupboard.database.Database;
import com.thecupboardapp.cupboard.models.FoodItem;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.utils.DateUtilities;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kyle on 1/13/2018.
 * Editied by Jacob Strom on 2/19/2018.
 */

public class DashboardFragment extends Fragment {
    private ExpandableListView mFoodExpandableView;
    private ExpandableListView mListExpandableView;

    private ExpandableFoodAdapter mExpandableFoodAdapter;
    private ExpandableSListAdapter mExpandableSListAdapter;

    private Disposable mFoodsDisposable;
    private Disposable mListsDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_dashboard);
    }

    // public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //     if (requestCode == NEW_ENTRY_REQUEST || requestCode == UPDATE_ENTRY_REQUEST) {
    //         if (resultCode == RESULT_OK) {
    //             prepareAccordion();
    //             mExpandableFoodAdapter = new DashboardFragment.ExpandableFoodAdapter(getContext(), mHeaders, mListChild);
    //             mFoodExpandableView.setAdapter(mExpandableFoodAdapter);
    //         }
    //     }
    //     else if(requestCode == SHOW_REQUEST){
    //         if (resultCode == RESULT_OK) {
    //             mExpandableFoodAdapter = new DashboardFragment.ExpandableFoodAdapter(getContext(), mHeaders, mListChild);
    //             mFoodExpandableView.setAdapter(mExpandableFoodAdapter);
    //         }
    //     }
    // }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mFoodExpandableView = v.findViewById(R.id.food_expandable_view);
        mListExpandableView = v.findViewById(R.id.slist_expandable_view);

        mFoodsDisposable = Database.getDatabase(getActivity()).foodItemDao().getAllFlowableByExpiration(System.currentTimeMillis(),604800000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(foodItems -> {
                    mExpandableFoodAdapter = new ExpandableFoodAdapter(getContext(), "Foods Expiring Soon", foodItems);
                    mFoodExpandableView.setAdapter(mExpandableFoodAdapter);
                    mFoodExpandableView.expandGroup(0);
                });

        mListsDisposable = Database.getDatabase(getActivity()).sListDao().getAllFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sLists -> {
                    mExpandableSListAdapter = new ExpandableSListAdapter(getContext(), "Shopping Lists", sLists);
                    mListExpandableView.setAdapter(mExpandableSListAdapter);
                    mListExpandableView.expandGroup(0);
                });

        return v;
    }

    public class ExpandableFoodAdapter extends BaseExpandableListAdapter {
        private Context context;
        private String header;
        private List<FoodItem> children;

        public ExpandableFoodAdapter(Context context, String header, List<FoodItem> children) {
            this.context = context;
            this.header = header;
            this.children = children;
        }

        @Override
        public int getGroupCount() {
            return 1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children.size();
        }

        @Override
        public String getGroup(int groupPosition) {
            return header;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            FoodItem item = children.get(childPosition);
            String expiration;
            if (item.getExpiration() == 0) {
                expiration = "Never";
            } else {
                expiration = DateUtilities.longToDate(item.getExpiration());
            }
            return item.getName() + "\nExpires on: " + expiration;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String mHeaderTitle = getGroup(groupPosition);

            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.home_group, null);
            }

            TextView mHomeGroupHeader = convertView.findViewById(R.id.home_group_header);
            mHomeGroupHeader.setText(mHeaderTitle);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final String mChildText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.home_item, null);
            }

            TextView mChildTextView = convertView.findViewById(R.id.home_item_list_item);
            mChildTextView.setText(mChildText);

            convertView.setOnClickListener(v -> {
                FoodItem item = children.get(childPosition);
                Intent intent = new Intent(getActivity(), ManualEntryActivity.class);
                intent.putExtra(ManualEntryActivity.FOOD_ID_EXTRA_KEY, item.getId());
                startActivityForResult(intent, ManualEntryActivity.EDIT_ENTRY_REQUEST);
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    public class ExpandableSListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private String header;
        private List<SList> children;

        public ExpandableSListAdapter(Context context, String header, List<SList> children) {
            this.context = context;
            this.header = header;
            this.children = children;
        }

        @Override
        public int getGroupCount() {
            return 1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children.size();
        }

        @Override
        public String getGroup(int groupPosition) {
            return header;
        }

        @Override
        public String getChild(int groupPosition, int childPosition) {
            return children.get(childPosition).getName();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String mHeaderTitle = getGroup(groupPosition);

            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.home_group, null);
            }
            TextView mHomeGroupHeader = convertView.findViewById(R.id.home_group_header);
            mHomeGroupHeader.setText(mHeaderTitle);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final String mChildText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.home_item, null);
            }

            TextView mChildTextView = convertView.findViewById(R.id.home_item_list_item);
            mChildTextView.setText(mChildText);

            convertView.setOnClickListener(v -> {
                SList item = children.get(childPosition);
                Intent intent = new Intent(getActivity(), SListEditActivity.class);
                intent.putExtra(SListEditActivity.SLIST_ID_EXTRA_KEY, item.getId());
                startActivity(intent);
            });


            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }
}





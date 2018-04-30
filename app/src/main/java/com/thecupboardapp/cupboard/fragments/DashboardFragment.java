package com.thecupboardapp.cupboard.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Kyle on 1/13/2018.
 * Editied by Jacob Strom on 2/19/2018.
 */

public class DashboardFragment extends Fragment{
    private List<FoodItem> mFoods;
    private List<SList> mLists;

    private TextView mNextExpiring;
    private TextView mLastModifiedList;
    private ExpandableListView mExpandableListView;

    private ExpandableListAdapter mAccordion;
    private List<String> mHeaders;
    private HashMap<String, List<String>> mListChild;

    private Disposable mFoodsDisposable;
    private Disposable mListsDisposable;

    private int NEW_ENTRY_REQUEST = 0;
    private int UPDATE_ENTRY_REQUEST = 1;
    private int SHOW_REQUEST = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_dashboard);
    }

    // public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //     if (requestCode == NEW_ENTRY_REQUEST || requestCode == UPDATE_ENTRY_REQUEST) {
    //         if (resultCode == RESULT_OK) {
    //             prepareAccordion();
    //             mAccordion = new DashboardFragment.ExpandableListAdapter(getContext(), mHeaders, mListChild);
    //             mExpandableListView.setAdapter(mAccordion);
    //         }
    //     }
    //     else if(requestCode == SHOW_REQUEST){
    //         if (resultCode == RESULT_OK) {
    //             mAccordion = new DashboardFragment.ExpandableListAdapter(getContext(), mHeaders, mListChild);
    //             mExpandableListView.setAdapter(mAccordion);
    //         }
    //     }
    // }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mExpandableListView = v.findViewById(R.id.home_fragment_expandable_list_view);

        mFoodsDisposable = Database.getDatabase(getActivity()).foodItemDao().getAllFlowable().subscribeOn(Schedulers.io())
                .subscribe(foodItems -> {
                    mFoods = foodItems;
                    prepareAccordion();
                    mAccordion = new ExpandableListAdapter(getContext(), mHeaders, mListChild);
                    mExpandableListView.setAdapter(mAccordion);

                    mExpandableListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
                        if (groupPosition == 0) {
                            FoodItem item = mFoods.get(childPosition);
                            Intent intent = new Intent(getActivity(), ManualEntryActivity.class);
                            intent.putExtra(ManualEntryActivity.FOOD_ID_EXTRA_KEY, item.getId());
                            startActivityForResult(intent, ManualEntryActivity.EDIT_ENTRY_REQUEST);
                            return true;
                        } else if (groupPosition == 2) {
                            Intent intent = SListEditActivity.newIntent(getActivity(), mLists.get(childPosition).getId());
                            startActivity(intent);
                            return true;
                        }

                        // prepareAccordion();

                        // mAccordion = new ExpandableListAdapter(getContext(), mHeaders, mListChild);
                        //
                        // mExpandableListView.setAdapter(mAccordion);
                        //
                        // mExpandableListView.expandGroup(0);
                        // mExpandableListView.expandGroup(1);
                        return false;
                    });
                });

        mListsDisposable = Database.getDatabase(getActivity()).sListDao().getAllFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sLists -> {
                    mLists = sLists;
                    prepareAccordion();
                    mAccordion = new ExpandableListAdapter(getContext(), mHeaders, mListChild);
                    mExpandableListView.setAdapter(mAccordion);

                    mExpandableListView.setOnChildClickListener((parent, view, groupPosition, childPosition, id) -> {
                        if (groupPosition == 0) {
                            FoodItem item = mFoods.get(childPosition);
                            Intent intent = new Intent(getActivity(), ManualEntryActivity.class);
                            intent.putExtra(ManualEntryActivity.FOOD_ID_EXTRA_KEY, item.getId());
                            startActivityForResult(intent, ManualEntryActivity.EDIT_ENTRY_REQUEST);
                            return true;
                        } else if (groupPosition == 1) {
                            Intent intent = SListEditActivity.newIntent(getActivity(), mLists.get(childPosition).getId());
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    });

                });

        return v;
    }


    private void prepareAccordion() {
        mHeaders = new ArrayList<>();
        mListChild = new HashMap<>();

        mHeaders.add("Food Expiring Soon");
        mHeaders.add("Shopping Lists");

        List<String> mExpiringSoon = setExpiringSoon(7);
        mListChild.put(mHeaders.get(0), mExpiringSoon);

        try {
            if (mLists.isEmpty()) {
                ArrayList<String> empty = new ArrayList<>();
                empty.add("Create a Shopping List by going to the Shopping Lists menu.");
                mListChild.put(mHeaders.get(1), empty);
            } else {
                Collections.sort(mLists);

                ArrayList<String> shoppingListNames = new ArrayList<String>();
                for (SList list : mLists) {
                    shoppingListNames.add(list.getName());
                }
                mListChild.put(mHeaders.get(1), shoppingListNames);

            }
        }
        catch (Exception e) {

        }
    }

    private List<String> setExpiringSoon(int days) {
        Collections.sort(mFoods);
        List<String> retVal = new ArrayList<String>();

        long millisPerDay = 86400 * 1000;
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long expiringTime = currentTime+(millisPerDay*days);

        try {
            if (!mFoods.isEmpty()) {
                int counter = 0;
                try {
                    while (mFoods.get(counter).getExpiration() < expiringTime) {
                        String s = formatFood(mFoods.get(counter));
                        retVal.add(s);
                        ++counter;
                    }
                }
                catch (Exception e){

                }

            }
            else {
                String s = "no foods";
                mNextExpiring.setText(s);
            }
        }
        catch (Exception e) {
            // mNextExpiring.setText("error in next expiring");
        }

        if (retVal.isEmpty()){
            retVal.add("You do not have any food about to expire :)");
        }
        return retVal;
    }

    private String formatFood(FoodItem foodItem) {
        String s = "";
        s += foodItem.getName();
        //s += ": ";
        s += "\n";
        s += "Expires on: ";
        s += DateUtilities.longToDate(foodItem.getExpiration());


        return s;
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context mContext;
        private List<String> mListHeaders;
        private HashMap<String, List<String>> mListChild;

        public ExpandableListAdapter(Context context, List<String> listHeaders, HashMap<String, List<String>> listChild) {
            this.mContext = context;
            this.mListHeaders = listHeaders;
            this.mListChild = listChild;
        }

        @Override
        public int getGroupCount() {
            return this.mListHeaders.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mListChild.get(getGroup(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mListHeaders.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mListChild.get(mListHeaders.get(groupPosition)).get(childPosition);
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
            String mHeaderTitle = (String) getGroup(groupPosition);

            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.home_group, null);
            }
            TextView mHomeGroupHeader = (TextView) convertView.findViewById(R.id.home_group_header);
            mHomeGroupHeader.setText(mHeaderTitle);

            return  convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final String mChildText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.home_item, null);
            }

            TextView mChildTextView = (TextView) convertView.findViewById(R.id.home_item_list_item);
            mChildTextView.setText(mChildText);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }



    }
}





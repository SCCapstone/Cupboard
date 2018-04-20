package com.thecupboardapp.cupboard.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.activities.ManualEntryActivity;
import com.thecupboardapp.cupboard.adapters.CupboardExpandableListAdapter;
import com.thecupboardapp.cupboard.models.FoodItem;
import com.thecupboardapp.cupboard.models.viewmodels.CupboardViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Kyle on 1/12/2018.
 */

public class CupboardFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    public static final String TAG = "CupboardFragment";

    ExpandableListView mExpandableListView;
    private CupboardExpandableListAdapter mAdapter;

    private String mCategoryShown = "";

    private String[] groups;
    private String[] fullGroups;
    private String[][] children;
    private String[][] fullChildren;

    private FloatingActionButton manEntFAB;

    private int NEW_ENTRY_REQUEST = 0;
    private int UPDATE_ENTRY_REQUEST = 1;
    private int SHOW_REQUEST = 2;
    long NO_EXP_DATE = 4133987474999L;

    private Disposable mFoodItemDisposable;

    private CupboardViewModel mCupboardViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mCupboardViewModel = ViewModelProviders.of(this).get(CupboardViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cupboard, container, false);

        mExpandableListView = v.findViewById(R.id.accordion);
        manEntFAB = v.findViewById(R.id.add_food_fab);

        mFoodItemDisposable = mCupboardViewModel.getFoodItemFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                    mCupboardViewModel.setFoodItems(items);
                    updateFoods(items);

                    mAdapter = new CupboardExpandableListAdapter(getActivity(), groups, fullGroups, children, fullChildren);

                    if (mExpandableListView != null){
                        mExpandableListView.setAdapter(mAdapter);
                        mExpandableListView.setGroupIndicator(null);
                    }
                });


        manEntFAB.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), ManualEntryActivity.class);
            intent.putExtra("requestCode", NEW_ENTRY_REQUEST);
            startActivityForResult(intent, NEW_ENTRY_REQUEST);
        });

        return v;
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.cupboard_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
    }

    @Override
    public boolean onClose() {
        mAdapter.filterData("");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mAdapter.filterData(query);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.filterData(query);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_sort_alphabetically:
                mCupboardViewModel.sort(CupboardViewModel.SORT_ALPHABETICAL);
                return true;
            case R.id.menu_sort_expires_soon:
                mCupboardViewModel.sort(CupboardViewModel.SORT_EXPIRATION);
                return true;
            case R.id.menu_show_pantry:
                showByCategory("Pantry");
                return true;
            case R.id.menu_show_fridge:
                showByCategory("Fridge");
                return true;
            case R.id.menu_show_freezer:
                showByCategory("Freezer");
                return true;
            case R.id.menu_show_all:
                showByCategory("All");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showByCategory(String aCategory){
        String[] newGroups;
        ArrayList<FoodItem> newGroupsList = new ArrayList<>();

        if (aCategory.equals("All")) {
            newGroupsList.addAll(mCupboardViewModel.getFoodItems());
            mCategoryShown = "";
        } else {
            for (FoodItem item: mCupboardViewModel.getFoodItems()){
                if(item.getCategory().equals(aCategory)){
                    newGroupsList.add(item);
                }
            }
            mCategoryShown = aCategory;
        }

        newGroups = new String[newGroupsList.size()];
        for (int i = 0; i < newGroupsList.size(); i++){
            newGroups[i] = newGroupsList.get(i).getName();
        }


        groups = newGroups;

        onActivityResult(SHOW_REQUEST, RESULT_OK,null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_ENTRY_REQUEST || requestCode == UPDATE_ENTRY_REQUEST) {
            if (resultCode == RESULT_OK) {
                // updateFoods();
                //mAdapter.notifyDataSetChanged();
                mAdapter = new CupboardExpandableListAdapter(getContext(), groups, fullGroups, children, fullChildren);
                mExpandableListView.setAdapter(mAdapter);
            }
        }
        else if(requestCode == SHOW_REQUEST){
            if (resultCode == RESULT_OK) {
                mAdapter = new CupboardExpandableListAdapter(getContext(), groups, fullGroups, children, fullChildren);
                mExpandableListView.setAdapter(mAdapter);
            }
        }
    }

    public void updateFoods(List<FoodItem> mFoodItems) {
        groups = new String[mFoodItems.size()];
        fullGroups = new String[mFoodItems.size()];
        children = new String[mFoodItems.size()][1];
        fullChildren = new String[mFoodItems.size()][1];

        for(int i = 0; i < mFoodItems.size(); i++){
            groups[i] = mFoodItems.get(i).getName();
            fullGroups[i] = groups[i];

            String info = "Expires: ";
            if (mFoodItems.get(i).getExpiration() == NO_EXP_DATE) {
                info = info.concat("Never");
            } else {
                info = info.concat(FoodItem.longToDate(mFoodItems.get(i).getExpiration()));
            }

            info = info.concat("\nDate Added: " + FoodItem.longToDate(mFoodItems.get(i).getDateAdded()));
            info = info.concat("\nDescription: " + mFoodItems.get(i).getDescription());
            children[i][0] = info;
            fullChildren[i][0] = children[i][0];
        }
        if(!mCategoryShown.equals("")) {
            showByCategory(mCategoryShown);
        }
    }
}


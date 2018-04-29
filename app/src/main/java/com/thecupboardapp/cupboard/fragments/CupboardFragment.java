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
import android.widget.Toast;

import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.activities.ManualEntryActivity;
import com.thecupboardapp.cupboard.adapters.CupboardExpandableListAdapter;
import com.thecupboardapp.cupboard.models.FoodItem;
import com.thecupboardapp.cupboard.models.viewmodels.CupboardViewModel;

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

    private ExpandableListView mExpandableListView;
    private CupboardExpandableListAdapter mAdapter;
    private FloatingActionButton manEntFAB;

    private Disposable mFoodItemDisposable;

    private CupboardViewModel mCupboardViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCupboardViewModel = ViewModelProviders.of(this).get(CupboardViewModel.class);
        getActivity().setTitle(R.string.title_cupboard);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cupboard, container, false);

        mExpandableListView = v.findViewById(R.id.accordion);
        manEntFAB = v.findViewById(R.id.add_food_fab);

        manEntFAB.setOnClickListener(v1 -> {
            Log.d(TAG, "onCreateView: entry thing clicked");
            Intent intent = new Intent(getContext(), ManualEntryActivity.class);
            intent.putExtra(ManualEntryActivity.FOOD_ID_REQUEST_KEY, ManualEntryActivity.NEW_ENTRY_REQUEST);
            startActivityForResult(intent, ManualEntryActivity.NEW_ENTRY_REQUEST);
        });

        if (mFoodItemDisposable == null) {
            mFoodItemDisposable = mCupboardViewModel.getFoodItemFlowable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(items -> {
                        mCupboardViewModel.setFoodItems(items);
                        updateFoods(items);
                    });
        }
        return v;
    }

    @Override
    public void onDestroy() {
        mFoodItemDisposable.dispose();
        super.onDestroy();
    }

    //Inflates the menu for My Cupboard, sets up the search bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.cupboard_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

    }

    //Method called after a food is added or edited
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + " , " + resultCode);
        switch (requestCode) {
            case ManualEntryActivity.NEW_ENTRY_REQUEST: {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getContext(), "Food added!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case ManualEntryActivity.EDIT_ENTRY_REQUEST: {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(getContext(), "Food edited!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Closing out of the search bar completely should return to showing whole list
    @Override
    public boolean onClose() {
        mAdapter.filterDataByQuery("");
        return false;
    }

    //Search bar method for changing the string being searched
    @Override
    public boolean onQueryTextChange(String query) {
        mAdapter.filterDataByQuery(query);
        return false;
    }

    //Do same thing as onQueryTextChange
    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.filterDataByQuery(query);
        return false;
    }

    //Method for user to select what foods My Cupboard displays with the menu list
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
                mAdapter.filterDataByCategory("Pantry");
                return true;
            case R.id.menu_show_fridge:
                mAdapter.filterDataByCategory("Fridge");
                return true;
            case R.id.menu_show_freezer:
                mAdapter.filterDataByCategory("Freezer");
                return true;
            case R.id.menu_show_all:
                mAdapter.filterDataByCategory("All");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //After a change is made to mFoodItems, this method called to update adapter
    public void updateFoods(List<FoodItem> mFoodItems) {
        mAdapter = new CupboardExpandableListAdapter(getActivity(), mFoodItems, this);

        mExpandableListView.setAdapter(mAdapter);
        mExpandableListView.setGroupIndicator(null);
    }
}


package com.thecupboardapp.cupboard.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.thecupboardapp.cupboard.database.Database;
import com.thecupboardapp.cupboard.database.FoodItemDao;
import com.thecupboardapp.cupboard.models.FoodItem;

import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

/**
 * Created by Kyle on 4/19/2018.
 */

public class CupboardViewModel extends AndroidViewModel {
    public static final int SORT_ALPHABETICAL = 0;
    public static final int SORT_EXPIRATION = 1;

    private FoodItemDao mFoodItemDao;
    private Flowable<List<FoodItem>> mFoodItemFlowable;
    private List<FoodItem> mFoodItems;


    public CupboardViewModel(@NonNull Application application) {
        super(application);

        mFoodItemDao = Database.getDatabase(application).foodItemDao();
        mFoodItemFlowable = mFoodItemDao.getAllFlowable();
    }

    public List<FoodItem> getFoodItems() {
        return mFoodItems;
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        mFoodItems = foodItems;
    }

    public Flowable<List<FoodItem>> getFoodItemFlowable() {
        return mFoodItemFlowable;
    }

    public Maybe<List<FoodItem>> foodsExist() {
        return mFoodItemDao.getAllMaybe();
    }

    public void sort(int method) {
        AsyncTask.execute(() -> {
            switch (method) {
                case SORT_ALPHABETICAL: {
                    Collections.sort(mFoodItems, (itemA, itemB) ->
                            itemA.getName().compareToIgnoreCase(itemB.getName()));
                    break;
                }
                case SORT_EXPIRATION: {
                    Collections.sort(mFoodItems, (itemA, itemB) ->
                            Long.compare(itemA.getExpiration(), itemB.getExpiration()));
                    break;
                }
                default :
                    break;
            }

            for (int i = 0; i < mFoodItems.size(); i++){
                mFoodItems.get(i).setIndex(i);
            }

            mFoodItemDao.updateAll(mFoodItems);
        });
    }
}

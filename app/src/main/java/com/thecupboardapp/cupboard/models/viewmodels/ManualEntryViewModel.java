package com.thecupboardapp.cupboard.models.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.thecupboardapp.cupboard.database.Database;
import com.thecupboardapp.cupboard.database.FoodItemDao;
import com.thecupboardapp.cupboard.models.FoodItem;

import io.reactivex.Flowable;

/**
 * Created by Kyle on 4/20/2018.
 */

public class ManualEntryViewModel extends AndroidViewModel {
    private FoodItemDao mFoodItemDao;
    private Flowable<FoodItem> mFoodItemFlowable;
    private FoodItem mFoodItem;

    public ManualEntryViewModel(@NonNull Application application) {
        super(application);

        mFoodItemDao = Database.getDatabase(application).foodItemDao();
    }

    public Flowable<FoodItem> getFoodItemFlowable() {
        return mFoodItemFlowable;
    }

    public void setFoodItemFlowable(long id) {
        mFoodItemFlowable = mFoodItemDao.getFoodItemFlowable(id);
    }

    public void updateFood(FoodItem foodItem) {
        AsyncTask.execute(() -> mFoodItemDao.update(foodItem));
    }

    public void insertFood(FoodItem foodItem) {
        AsyncTask.execute(() -> mFoodItemDao.insert(foodItem));
    }
}

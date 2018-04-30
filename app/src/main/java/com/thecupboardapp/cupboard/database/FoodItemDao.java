package com.thecupboardapp.cupboard.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.thecupboardapp.cupboard.models.FoodItem;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by Kyle on 4/19/2018.
 */

@Dao
public interface FoodItemDao {
    //Gets all FoodItems in Flowable
    @Query("SELECT * FROM food_items ORDER BY `index` ASC")
    Flowable<List<FoodItem>> getAllFlowable();

    //Gets all FoodItems in Maybe
    @Query("SELECT * FROM food_items ORDER BY `index` ASC")
    Maybe<List<FoodItem>> getAllMaybe();

    @Query("SELECT * FROM food_items")
    Single<List<FoodItem>> getAllSingle();

    //Gets 1 FoodItem with specified id
    @Query("SELECT * FROM food_items WHERE id = :id LIMIT 1")
    Flowable<FoodItem> getFoodItemFlowable(long id);

    @Insert
    long[] insertAll(FoodItem ... foodItems);

    @Insert
    long insert(FoodItem foodItem);

    @Delete
    void delete(FoodItem foodItem);

    @Update
    void update(FoodItem foodItem);

    @Update
    void updateAll(List<FoodItem> foodItems);
}

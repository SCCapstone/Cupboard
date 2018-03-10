package com.thecupboardapp.cupboard.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.thecupboardapp.cupboard.models.SList;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;


/**
 * Created by Kyle on 3/8/2018.
 */

@Dao
public interface SListDao {
    @Query("SELECT * FROM slists")
    Flowable<List<SList>> getAll();

    @Insert
    void insertAll(SList... sLists);

    @Delete
    void delete(SList slist);

    @Query("DELETE FROM slists")
    void deleteAll();

    @Update
    void update(SList slist);

    @Query("SELECT * FROM slists WHERE id = :id")
    Single<SList> getListById(int id);
}

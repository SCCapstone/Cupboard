package com.thecupboardapp.cupboard.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.thecupboardapp.cupboard.models.SListItem;

import java.util.List;

import io.reactivex.Flowable;


/**
 * Created by Kyle on 3/8/2018.
 */

@Dao
public interface SListItemDao {
    @Query("SELECT * FROM slist_items")
    Flowable<List<SListItem>> getAll();

    @Insert
    void insertAll(SListItem... sListItems);

    @Delete
    void delete(SListItem sListItem);

    @Query("DELETE FROM slist_items")
    void deleteAll();

    @Update
    void update(List<SListItem> sListItems);

    @Query("SELECT * FROM slist_items WHERE parent_id = :id")
    Flowable<List<SListItem>> getSListItemById(int id);
}

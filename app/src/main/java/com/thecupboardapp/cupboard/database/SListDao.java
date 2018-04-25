package com.thecupboardapp.cupboard.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;


/**
 * Created by Kyle on 3/8/2018.
 */

@Dao
public interface SListDao {
    @Query("SELECT * FROM slists ORDER BY `index` ASC")
    Flowable<List<SList>> getAllFlowable();

    @Query("SELECT * FROM slists")
    Single<List<SList>> getAllSingle();

    @Insert
    void insertAll(SList... sLists);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertList(SList sList);

    @Delete
    void delete(SList slist);

    @Query("DELETE FROM slists")
    void deleteAll();

    @Update
    void update(SList slist);

    @Update
    void update(SList... sLists);

    @Update
    void update(List<SList> sLists);

    @Query("UPDATE slists SET name = :name WHERE id = :id")
    void updateName(String name, long id);

    @Query("SELECT * FROM slists WHERE id = :id LIMIT 1")
    Flowable<SList> getFlowableListById(long id);

    @Query("SELECT * FROM slists WHERE id = :id LIMIT 1")
    Single<SList> getSingleListById(long id);

    @Query("UPDATE slists SET last_modified = :lastModified WHERE id = :id")
    void updateLastModified(long id, long lastModified);
}

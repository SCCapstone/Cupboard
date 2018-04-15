package com.thecupboardapp.cupboard.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;

/**
 * Created by Kyle on 3/8/2018.
 */

@Database(entities = {SList.class, SListItem.class}, version = 1)
public abstract class Repository extends RoomDatabase {

    public abstract SListDao sListDao();
    public abstract SListItemDao sListItemDao();
    public abstract CupboardDao mCupboardDao();

    private static Repository INSTANCE;

    public static Repository getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    Repository.class,
                    "cupboard_db").build();
        }
        return INSTANCE;
    }
}

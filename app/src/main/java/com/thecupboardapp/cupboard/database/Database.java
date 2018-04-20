package com.thecupboardapp.cupboard.database;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.thecupboardapp.cupboard.models.FoodItem;
import com.thecupboardapp.cupboard.models.SList;
import com.thecupboardapp.cupboard.models.SListItem;

/**
 * Created by Kyle on 3/8/2018.
 */

@android.arch.persistence.room.Database(entities = {SList.class, SListItem.class, FoodItem.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public static final String DATABASE_NAME = "cupboard";

    public abstract SListDao sListDao();
    public abstract SListItemDao sListItemDao();
    public abstract FoodItemDao foodItemDao();

    private static Database INSTANCE;

    public static synchronized Database getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = create(context);
        }
        return INSTANCE;
    }

    static Database create(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                Database.class,
                DATABASE_NAME).build();
    }
}

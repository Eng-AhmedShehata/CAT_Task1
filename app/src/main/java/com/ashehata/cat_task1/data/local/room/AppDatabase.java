package com.ashehata.cat_task1.data.local.room;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1,exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    public abstract UserDao userDao();


    public synchronized static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    private static AppDatabase buildDatabase(Context context) {

        return  Room.databaseBuilder(context,
                AppDatabase.class, "user_data").build();
    }
}
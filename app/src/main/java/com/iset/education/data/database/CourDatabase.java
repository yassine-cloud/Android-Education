package com.iset.education.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.iset.education.data.models.Cour;

@Database(entities = {Cour.class}, version = 1, exportSchema = true)
public abstract class CourDatabase extends RoomDatabase {
    private static CourDatabase instance;

    public abstract CourDao courseDao();

    public static synchronized CourDatabase getInstance(Context context) {
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            CourDatabase.class, "cour_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}

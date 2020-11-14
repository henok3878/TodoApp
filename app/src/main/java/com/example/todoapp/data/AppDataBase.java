package com.example.todoapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {TaskEntry.class},version = 1,exportSchema = false)
@TypeConverters({DateConvertor.class})
public abstract class AppDataBase extends RoomDatabase {

    private static final String DATABASE_NAME = "todo_database";
    public abstract TaskDoa getTaskDoa();
    private static AppDataBase sInstance;

    public static AppDataBase getInstance(Context context){
        if(sInstance == null){
            sInstance = Room.databaseBuilder(context.getApplicationContext(),AppDataBase.class,DATABASE_NAME)
                    .build();
        }
        return sInstance;
    }
}

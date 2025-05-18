package com.example.dictionaryapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dictionaryapp.DAO.WordDao;
import com.example.dictionaryapp.Entity.WordEntity;

@Database(entities = {WordEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract WordDao wordDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "dictionary.db") // Tên file giống file trong assets
                    .createFromAsset("dictionary.db") // Sử dụng file trong assets
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}

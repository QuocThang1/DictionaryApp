package com.example.dictionaryapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dictionaryapp.DAO.HistoryDao;
import com.example.dictionaryapp.DAO.WordDao;
import com.example.dictionaryapp.Entity.HistoryEntity;
import com.example.dictionaryapp.Entity.WordEntity;

@Database(entities = {WordEntity.class, HistoryEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    DatabaseCopyHelper DatabaseCopyHelper;

    public abstract WordDao wordDao();
    public abstract HistoryDao historyDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "dictionary1.db") // Tên file giống file trong assets
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}

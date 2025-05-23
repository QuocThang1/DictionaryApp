package com.example.dictionaryapp.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.dictionaryapp.Entity.HistoryEntity;

import java.util.List;

@Dao
public interface HistoryDao {
    @Insert
    void insert(HistoryEntity history);

    @Query("SELECT * FROM history_table ORDER BY timestamp DESC")
    List<HistoryEntity> getAllHistory();

    @Query("SELECT * FROM history_table WHERE word LIKE '%' || :keyword || '%' ORDER BY timestamp DESC")
    List<HistoryEntity> searchHistory(String keyword);

    @Query("DELETE FROM history_table WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM history_table")
    void deleteAll();
}
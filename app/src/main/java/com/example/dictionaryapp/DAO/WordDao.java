package com.example.dictionaryapp.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dictionaryapp.Entity.WordEntity;

import java.util.List;

@Dao
public interface WordDao {

    @Insert
    void insert(WordEntity word);

    @Update
    void update(WordEntity word);

    @Delete
    void delete(WordEntity word);

    @Query("SELECT * FROM word_table WHERE word LIKE :keyword || '%'")
    List<WordEntity> searchWords(String keyword);

    @Query("SELECT * FROM word_table WHERE isFavorite = 1")
    List<WordEntity> getFavorites();

    @Query("SELECT * FROM word_table")
    List<WordEntity> getAll();

    @Query("SELECT * FROM word_table ORDER BY word ASC")
    List<WordEntity> getAllWordsSorted();

}

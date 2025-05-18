package com.example.dictionaryapp.Controller;

import android.content.Context;

import com.example.dictionaryapp.DAO.WordDao;
import com.example.dictionaryapp.Database.AppDatabase;
import com.example.dictionaryapp.Entity.WordEntity;

import java.util.List;

public class DictionaryController {
    private final WordDao wordDao;

    public DictionaryController(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        wordDao = db.wordDao();
    }

    public List<WordEntity> search(String keyword) {
        return wordDao.searchWords("%" + keyword + "%");
    }

    public void addWord(String word, String meaning, String pronounce) {
        WordEntity w = new WordEntity(word, meaning, false, pronounce);
        wordDao.insert(w);
    }

    public void updateFavorite(WordEntity word, boolean isFavorite) {
        word.isFavorite = isFavorite;
        wordDao.update(word);
    }

    public List<WordEntity> getAllWords() {
        return wordDao.getAll();
    }

    public List<WordEntity> getAllWordsSorted() {
        return wordDao.getAllWordsSorted();
    }


}

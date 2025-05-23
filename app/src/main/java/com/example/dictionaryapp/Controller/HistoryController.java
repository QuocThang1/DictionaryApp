package com.example.dictionaryapp.Controller;

import android.content.Context;

import com.example.dictionaryapp.DAO.HistoryDao;
import com.example.dictionaryapp.Database.AppDatabase;
import com.example.dictionaryapp.Entity.HistoryEntity;

import java.util.List;

public class HistoryController {
    private final HistoryDao historyDao;

    public HistoryController(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        historyDao = db.historyDao();
    }

    public void addToHistory(String word) {
        HistoryEntity history = new HistoryEntity(word, System.currentTimeMillis());
        historyDao.insert(history);
    }

    public List<HistoryEntity> getAllHistory() {
        return historyDao.getAllHistory();
    }

    public List<HistoryEntity> searchHistory(String keyword) {
        return historyDao.searchHistory(keyword);
    }

    public void deleteHistoryItem(int id) {
        historyDao.deleteById(id);
    }

    public void clearAllHistory() {
        historyDao.deleteAll();
    }
}
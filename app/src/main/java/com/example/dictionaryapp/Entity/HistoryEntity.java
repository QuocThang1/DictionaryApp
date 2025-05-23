package com.example.dictionaryapp.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history_table")
public class HistoryEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String word;
    public long timestamp;

    public HistoryEntity(String word, long timestamp) {
        this.word = word;
        this.timestamp = timestamp;
    }
}
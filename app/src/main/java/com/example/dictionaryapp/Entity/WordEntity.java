package com.example.dictionaryapp.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "word_table")
public class WordEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String word;
    public String meaning;
    public boolean isFavorite;

    public String pronounce;

    public WordEntity(String word, String meaning, boolean isFavorite, String pronounce) {
        this.word = word;
        this.meaning = meaning;
        this.isFavorite = isFavorite;
        this.pronounce = pronounce;
    }
}

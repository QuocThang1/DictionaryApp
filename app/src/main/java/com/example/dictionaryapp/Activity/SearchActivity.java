package com.example.dictionaryapp.Activity;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import com.example.dictionaryapp.Adapter.WordAdapter;
import com.example.dictionaryapp.Controller.DictionaryController;
import com.example.dictionaryapp.Entity.WordEntity;
import com.example.dictionaryapp.R;

public class SearchActivity extends AppCompatActivity {

    private DictionaryController dictionaryController;
    private WordAdapter wordAdapter;

    private ImageButton buttonBack;
    private RecyclerView recyclerView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dictionaryController = new DictionaryController(this);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.searchResultsRecyclerView);
        buttonBack = findViewById(R.id.buttonBackSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        wordAdapter = new WordAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(wordAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        buttonBack.setOnClickListener(v -> finish());
    }

    private void performSearch(String keyword) {
        List<WordEntity> results = dictionaryController.search(keyword);
        wordAdapter.updateData(results);
    }
}

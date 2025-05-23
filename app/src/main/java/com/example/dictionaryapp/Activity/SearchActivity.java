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
import com.example.dictionaryapp.Controller.HistoryController;
import com.example.dictionaryapp.Entity.WordEntity;
import com.example.dictionaryapp.R;

public class SearchActivity extends AppCompatActivity {

    private DictionaryController dictionaryController;
    private HistoryController historyController;
    private WordAdapter wordAdapter;

    private ImageButton buttonBack;
    private RecyclerView recyclerView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dictionaryController = new DictionaryController(this);
        historyController = new HistoryController(this);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.searchResultsRecyclerView);
        buttonBack = findViewById(R.id.buttonBackSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        wordAdapter = new WordAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(wordAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Chỉ lưu lịch sử khi người dùng nhấn Enter hoặc nút tìm kiếm
                performSearch(query, true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Không lưu lịch sử khi người dùng đang nhập
                performSearch(newText, false);
                return true;
            }
        });

        buttonBack.setOnClickListener(v -> finish());
    }

    private void performSearch(String keyword, boolean saveToHistory) {
        List<WordEntity> results = dictionaryController.search(keyword);
        wordAdapter.updateData(results);
        
        // Chỉ lưu vào lịch sử nếu saveToHistory = true và có kết quả tìm kiếm
        if (saveToHistory && !results.isEmpty() && !keyword.trim().isEmpty()) {
            historyController.addToHistory(keyword);
        }
    }
}

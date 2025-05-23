package com.example.dictionaryapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionaryapp.Adapter.HistoryAdapter;
import com.example.dictionaryapp.Controller.HistoryController;
import com.example.dictionaryapp.Entity.HistoryEntity;
import com.example.dictionaryapp.R;

import java.util.List;

public class HistoryFragment extends Fragment {
    private HistoryController historyController;
    private HistoryAdapter historyAdapter;
    private RecyclerView recyclerView;
    private EditText editTextSearch;
    private Button buttonClearAll;
    private ImageButton buttonSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewHistory);
        editTextSearch = view.findViewById(R.id.editTextSearchHistory);
        buttonClearAll = view.findViewById(R.id.buttonClearAllHistory);
        buttonSearch = view.findViewById(R.id.buttonSearchHistory);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Initialize controller
        historyController = new HistoryController(getContext());
        
        // Load history data
        List<HistoryEntity> historyList = historyController.getAllHistory();
        
        // Setup adapter
        historyAdapter = new HistoryAdapter(getContext(), historyList);
        recyclerView.setAdapter(historyAdapter);
        
        // Setup search button
        buttonSearch.setOnClickListener(v -> {
            String keyword = editTextSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                List<HistoryEntity> filteredList = historyController.searchHistory(keyword);
                historyAdapter.updateData(filteredList);
            } else {
                // If search field is empty, show all history
                historyAdapter.updateData(historyController.getAllHistory());
            }
        });
        
        // Setup clear all button
        buttonClearAll.setOnClickListener(v -> {
            historyController.clearAllHistory();
            historyAdapter.updateData(historyController.getAllHistory());
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh history list when fragment becomes visible
        if (historyAdapter != null) {
            historyAdapter.updateData(historyController.getAllHistory());
        }
    }
}
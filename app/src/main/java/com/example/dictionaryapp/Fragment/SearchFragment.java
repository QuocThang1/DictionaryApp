// SearchFragment.java
package com.example.dictionaryapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionaryapp.Adapter.AlphabetAdapter;
import com.example.dictionaryapp.Adapter.WordAdapter;
import com.example.dictionaryapp.Controller.DictionaryController;
import com.example.dictionaryapp.Entity.WordEntity;
import com.example.dictionaryapp.R;

import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private WordAdapter adapter;
    private DictionaryController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewWords);
        ListView alphabetList = view.findViewById(R.id.alphabetListView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        controller = new DictionaryController(getContext());
        List<WordEntity> wordList = controller.getAllWords();

        adapter = new WordAdapter(wordList);
        recyclerView.setAdapter(adapter);

        AlphabetAdapter alphabetAdapter = new AlphabetAdapter();
        alphabetList.setAdapter(alphabetAdapter);

        alphabetList.setOnItemClickListener((parent, view1, position, id) -> {
            String letter = (String) alphabetAdapter.getItem(position);
            // Scroll đến từ bắt đầu với letter
            for (int i = 0; i < wordList.size(); i++) {
                if (wordList.get(i).word.toUpperCase().startsWith(letter)) {
                    recyclerView.scrollToPosition(i);
                    break;
                }
            }
        });

        return view;
    }
}

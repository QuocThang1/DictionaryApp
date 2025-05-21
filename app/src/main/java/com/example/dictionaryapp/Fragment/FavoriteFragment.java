package com.example.dictionaryapp.Fragment;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionaryapp.Adapter.FavoriteAdapter;
import com.example.dictionaryapp.Controller.DictionaryController;
import com.example.dictionaryapp.Entity.WordEntity;
import com.example.dictionaryapp.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private DictionaryController controller;
    private List<WordEntity> favoriteWords;
    private Button buttonSortAZ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        buttonSortAZ = view.findViewById(R.id.buttonSortAZ);

        controller = new DictionaryController(requireContext());
        favoriteWords = controller.getFavoriteWords();

        adapter = new FavoriteAdapter(favoriteWords, controller, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        buttonSortAZ.setOnClickListener(v -> {
            Collections.sort(favoriteWords, Comparator.comparing(w -> w.word.toLowerCase()));
            adapter.notifyDataSetChanged();
        });

        return view;
    }
}

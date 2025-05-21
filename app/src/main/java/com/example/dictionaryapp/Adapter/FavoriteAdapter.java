package com.example.dictionaryapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionaryapp.Activity.WordDetailActivity;
import com.example.dictionaryapp.Controller.DictionaryController;
import com.example.dictionaryapp.Entity.WordEntity;
import com.example.dictionaryapp.R;

import java.util.List;
import java.util.Locale;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.WordViewHolder> {
    private final List<WordEntity> wordList;
    private final DictionaryController controller;

    private Context context;
    private TextToSpeech tts ;

    public FavoriteAdapter(List<WordEntity> wordList, DictionaryController controller, Context context) {
        this.context = context;
        this.wordList = wordList;
        this.controller = controller;

        this.tts = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        WordEntity word = wordList.get(position);
        holder.wordText.setText(word.word);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WordDetailActivity.class);
            intent.putExtra("word", word.word);
            intent.putExtra("pronounce", word.pronounce);
            intent.putExtra("meaning", word.meaning);
            context.startActivity(intent);
        });

        holder.speakerIcon.setOnClickListener(v -> {
            if (tts != null) {
                tts.speak(word.word, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        holder.deleteIcon.setOnClickListener(v -> {
            word.isFavorite = false;
            controller.updateFavorite(word, false);
            wordList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView wordText;
        ImageView speakerIcon, deleteIcon;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            wordText = itemView.findViewById(R.id.textViewFavoriteWord);
            speakerIcon = itemView.findViewById(R.id.buttonSpeak);
            deleteIcon = itemView.findViewById(R.id.buttonRemoveFavorite);
        }
    }
}

// WordAdapter.java
package com.example.dictionaryapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<WordEntity> wordList;
    private Context context;

    private DictionaryController controller;

    private TextToSpeech tts;


    public WordAdapter(Context context, List<WordEntity> wordList) {
        this.context = context;
        this.wordList = wordList;
        controller = new DictionaryController(context);

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
                .inflate(R.layout.word_item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        WordEntity word = wordList.get(position);
        holder.wordText.setText(word.word);

        holder.buttonFavorite.setImageResource(
                word.isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_border);
        holder.buttonFavorite.setColorFilter(context.getColor(
                word.isFavorite ? R.color.favorite_active : R.color.favorite_inactive));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WordDetailActivity.class);
            intent.putExtra("word", word.word);
            intent.putExtra("pronounce", word.pronounce);
            intent.putExtra("meaning", word.meaning);
            context.startActivity(intent);
        });

        holder.buttonSpeaker.setOnClickListener(v -> {
            if (tts != null) {
                tts.speak(word.word, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        holder.buttonFavorite.setOnClickListener(v -> {
            boolean newStatus = !word.isFavorite;
            word.isFavorite = newStatus;
            controller.updateFavorite(word, newStatus);
            notifyItemChanged(position);
        });
    }


    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView wordText;
        ImageView buttonSpeaker, buttonFavorite;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            wordText = itemView.findViewById(R.id.textViewWord);
            buttonSpeaker = itemView.findViewById(R.id.imageViewSpeaker);
            buttonFavorite = itemView.findViewById(R.id.imageViewFavorite);
        }
    }

    public void updateData(List<WordEntity> newWordList) {
        this.wordList = newWordList;
        notifyDataSetChanged();
    }

}

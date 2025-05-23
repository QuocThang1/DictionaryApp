package com.example.dictionaryapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionaryapp.Activity.WordDetailActivity;
import com.example.dictionaryapp.Controller.DictionaryController;
import com.example.dictionaryapp.Controller.HistoryController;
import com.example.dictionaryapp.Entity.HistoryEntity;
import com.example.dictionaryapp.Entity.WordEntity;
import com.example.dictionaryapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<HistoryEntity> historyList;
    private final Context context;
    private final HistoryController historyController;
    private final DictionaryController dictionaryController;

    public HistoryAdapter(Context context, List<HistoryEntity> historyList) {
        this.context = context;
        this.historyList = historyList;
        this.historyController = new HistoryController(context);
        this.dictionaryController = new DictionaryController(context);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryEntity history = historyList.get(position);
        holder.textWord.setText(history.word);
        
        // Format timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(history.timestamp));
        holder.textTimestamp.setText(formattedDate);
        
        // Set click listener to view word details
        holder.itemView.setOnClickListener(v -> {
            List<WordEntity> results = dictionaryController.search(history.word);
            if (!results.isEmpty()) {
                WordEntity word = results.get(0);
                Intent intent = new Intent(context, WordDetailActivity.class);
                intent.putExtra("word", word.word);
                intent.putExtra("meaning", word.meaning);
                intent.putExtra("pronounce", word.pronounce);
                context.startActivity(intent);
            }
        });
        
        // Set delete button click listener
        holder.buttonDelete.setOnClickListener(v -> {
            historyController.deleteHistoryItem(history.id);
            removeItem(position);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateData(List<HistoryEntity> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }
    
    public void removeItem(int position) {
        historyList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, historyList.size());
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textWord, textTimestamp;
        ImageButton buttonDelete;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textWord = itemView.findViewById(R.id.textHistoryWord);
            textTimestamp = itemView.findViewById(R.id.textHistoryTimestamp);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteHistory);
        }
    }
}
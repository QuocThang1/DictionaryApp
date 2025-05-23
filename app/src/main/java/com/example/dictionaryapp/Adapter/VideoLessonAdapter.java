package com.example.dictionaryapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionaryapp.Model.VideoLesson;
import com.example.dictionaryapp.R;

import java.util.List;

public class VideoLessonAdapter extends RecyclerView.Adapter<VideoLessonAdapter.LessonViewHolder> {
    
    private Context context;
    private List<VideoLesson> lessons;
    private OnLessonClickListener listener;
    
    public interface OnLessonClickListener {
        void onLessonClick(VideoLesson lesson);
    }
    
    public VideoLessonAdapter(Context context, List<VideoLesson> lessons, OnLessonClickListener listener) {
        this.context = context;
        this.lessons = lessons;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_lesson, parent, false);
        return new LessonViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        VideoLesson lesson = lessons.get(position);
        
        holder.textTitle.setText(lesson.getTitle());
        holder.imageThumbnail.setImageResource(lesson.getThumbnailResource());
        
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLessonClick(lesson);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return lessons.size();
    }
    
    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageThumbnail;
        TextView textTitle;
        
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            imageThumbnail = itemView.findViewById(R.id.imageThumbnail);
            textTitle = itemView.findViewById(R.id.textTitle);
        }
    }
}
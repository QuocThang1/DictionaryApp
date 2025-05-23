package com.example.dictionaryapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dictionaryapp.R;

public class VideoLessonDetailActivity extends AppCompatActivity {

    private TextView textTitle;
    private ImageButton buttonBack;
    private ImageButton buttonPlayVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_lesson_detail);

        textTitle = findViewById(R.id.textTitle);
        buttonBack = findViewById(R.id.buttonBack);
        buttonPlayVideo = findViewById(R.id.buttonPlayVideo);

        String title = getIntent().getStringExtra("lesson_title");
        String videoUrl = getIntent().getStringExtra("lesson_url");

        textTitle.setText(title);

        // Set up play button to open YouTube app or browser
        buttonPlayVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(videoUrl));
            startActivity(intent);
        });

        buttonBack.setOnClickListener(v -> finish());
    }
}
package com.example.dictionaryapp.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dictionaryapp.R;

import java.util.Locale;

public class WordDetailActivity extends Activity {

    TextView textWord, textPronounce, textMeaning;
    ImageButton buttonSpeaker, buttonBack;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        textWord = findViewById(R.id.textWord);
        textPronounce = findViewById(R.id.textPronounce);
        textMeaning = findViewById(R.id.textMeaning);
        buttonSpeaker = findViewById(R.id.buttonSpeaker);
        buttonBack = findViewById(R.id.buttonBack);

        String word = getIntent().getStringExtra("word");
        String pronounce = getIntent().getStringExtra("pronounce");
        String meaning = getIntent().getStringExtra("meaning");

        textWord.setText(word);
        textPronounce.setText("[" + pronounce + "]");
        textMeaning.setText(meaning);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        buttonSpeaker.setOnClickListener(v -> {
            if (tts != null) {
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        buttonBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}

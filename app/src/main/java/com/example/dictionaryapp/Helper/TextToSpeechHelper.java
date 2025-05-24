package com.example.dictionaryapp.Helper;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

public class TextToSpeechHelper {
    private TextToSpeech tts;
    private boolean isSpeaking;
    private ImageButton speakButton;
    private Context context;
    
    public TextToSpeechHelper(Context context, ImageButton button, Locale locale) {
        this.context = context;
        this.speakButton = button;
        initializeTTS(locale);
    }
    
    private void initializeTTS(Locale locale) {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(locale);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    speakButton.setEnabled(false);
                    Toast.makeText(context, "Không hỗ trợ đọc ngôn ngữ này", Toast.LENGTH_SHORT).show();
                }
                
                // Thiết lập UtteranceProgressListener để theo dõi trạng thái phát âm
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        isSpeaking = true;
                        speakButton.post(() -> speakButton.setImageResource(android.R.drawable.ic_media_pause));
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        isSpeaking = false;
                        speakButton.post(() -> speakButton.setImageResource(android.R.drawable.ic_btn_speak_now));
                    }

                    @Override
                    public void onError(String utteranceId) {
                        isSpeaking = false;
                        speakButton.post(() -> speakButton.setImageResource(android.R.drawable.ic_btn_speak_now));
                    }
                });
            } else {
                speakButton.setEnabled(false);
            }
        });
    }
    
    public void setLanguage(Locale locale) {
        int result = tts.setLanguage(locale);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            speakButton.setEnabled(false);
            Toast.makeText(context, "Không hỗ trợ đọc ngôn ngữ này", Toast.LENGTH_SHORT).show();
        } else {
            speakButton.setEnabled(true);
        }
    }
    
    public void speak(String text) {
        if (!text.isEmpty()) {
            if (isSpeaking) {
                // Nếu đang phát âm, dừng lại
                stop();
            } else {
                // Nếu chưa phát âm, bắt đầu phát
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            }
        }
    }
    
    public void stop() {
        if (tts != null) {
            tts.stop();
            isSpeaking = false;
            speakButton.setImageResource(android.R.drawable.ic_btn_speak_now);
        }
    }
    
    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
    
    public boolean isInitialized() {
        return tts != null;
    }
    
    public boolean isSpeaking() {
        return isSpeaking;
    }
    
    public void setEnabled(boolean enabled) {
        speakButton.setEnabled(enabled);
    }
}
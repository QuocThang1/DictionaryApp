package com.example.dictionaryapp.Helper;

import android.content.Context;
import android.widget.Toast;

import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.HashMap;
import java.util.Map;

public class TranslatorManager {
    private Map<String, Translator> translators = new HashMap<>();
    private Context context;
    
    public TranslatorManager(Context context) {
        this.context = context;
    }
    
    public Translator getTranslator(String sourceLanguage, String targetLanguage) {
        String translatorKey = sourceLanguage + "-" + targetLanguage;
        
        // Kiểm tra xem đã có translator cho cặp ngôn ngữ này chưa
        if (!translators.containsKey(translatorKey)) {
            createTranslator(sourceLanguage, targetLanguage);
        }
        
        return translators.get(translatorKey);
    }
    
    private void createTranslator(String sourceLanguage, String targetLanguage) {
        String translatorKey = sourceLanguage + "-" + targetLanguage;
        
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build();
        Translator translator = Translation.getClient(options);
        
        // Tải mô hình dịch về thiết bị
        translator.downloadModelIfNeeded()
                .addOnSuccessListener(unused -> {
                    // Mô hình đã được tải xuống thành công
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi tải mô hình
                    Toast.makeText(context, 
                            "Không thể tải mô hình dịch: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
        
        translators.put(translatorKey, translator);
    }
    
    public void translate(String sourceLanguage, String targetLanguage, String text, TranslationCallback callback) {
        Translator translator = getTranslator(sourceLanguage, targetLanguage);
        
        if (translator != null) {
            translator.translate(text)
                    .addOnSuccessListener(translatedString -> {
                        callback.onTranslationSuccess(translatedString);
                    })
                    .addOnFailureListener(e -> {
                        callback.onTranslationFailure(e.getMessage());
                    });
        } else {
            callback.onTranslationFailure("Không thể khởi tạo translator");
        }
    }
    
    public void closeAll() {
        for (Translator translator : translators.values()) {
            translator.close();
        }
        translators.clear();
    }
    
    public interface TranslationCallback {
        void onTranslationSuccess(String translatedText);
        void onTranslationFailure(String errorMessage);
    }
}
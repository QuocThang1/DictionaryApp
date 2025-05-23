package com.example.dictionaryapp.Controller;

import android.content.Context;
import android.util.Log;

import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.HashMap;
import java.util.Map;

public class TranslationController {
    private static final String TAG = "TranslationController";
    private Context context;
    private Map<String, String> languageCodes;
    private Map<String, Translator> translators;

    public TranslationController(Context context) {
        this.context = context;
        this.translators = new HashMap<>();
        setupLanguageCodes();
    }

    private void setupLanguageCodes() {
        languageCodes = new HashMap<>();
        // Thêm các ngôn ngữ được hỗ trợ bởi ML Kit
        languageCodes.put("en", "Tiếng Anh");
        languageCodes.put("vi", "Tiếng Việt");
        languageCodes.put("fr", "Tiếng Pháp");
        languageCodes.put("de", "Tiếng Đức");
        languageCodes.put("it", "Tiếng Ý");
        languageCodes.put("ja", "Tiếng Nhật");
        languageCodes.put("ko", "Tiếng Hàn");
        languageCodes.put("zh", "Tiếng Trung");
        languageCodes.put("ru", "Tiếng Nga");
        languageCodes.put("es", "Tiếng Tây Ban Nha");
        languageCodes.put("pt", "Tiếng Bồ Đào Nha");
        languageCodes.put("ar", "Tiếng Ả Rập");
        languageCodes.put("hi", "Tiếng Hindi");
        languageCodes.put("th", "Tiếng Thái");
    }

    public Map<String, String> getLanguageCodes() {
        return languageCodes;
    }

    public void translateText(String text, String sourceLanguage, String targetLanguage, TranslationCallback callback) {
        if (text == null || text.isEmpty()) {
            callback.onTranslationError("Văn bản nguồn trống");
            return;
        }

        // Tạo key cho translator
        String translatorKey = sourceLanguage + "-" + targetLanguage;

        // Kiểm tra xem đã có translator cho cặp ngôn ngữ này chưa
        Translator translator = translators.get(translatorKey);

        if (translator == null) {
            // Tạo translator mới nếu chưa có
            TranslatorOptions options = new TranslatorOptions.Builder()
                    .setSourceLanguage(getMLKitLanguageCode(sourceLanguage))
                    .setTargetLanguage(getMLKitLanguageCode(targetLanguage))
                    .build();

            translator = Translation.getClient(options);
            translators.put(translatorKey, translator);

            // Lưu trữ translator và text trong biến final
            final Translator finalTranslator = translator;

            // Tải mô hình ngôn ngữ (nếu cần)
            translator.downloadModelIfNeeded()
                    .addOnSuccessListener(unused -> {
                        // Mô hình đã được tải, thực hiện dịch
                        performTranslation(finalTranslator, text, callback);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Lỗi tải mô hình ngôn ngữ", e);
                        callback.onTranslationError("Không thể tải mô hình ngôn ngữ: " + e.getMessage());
                    });
        } else {
            // Đã có translator, thực hiện dịch ngay
            performTranslation(translator, text, callback);
        }
    }

    private void performTranslation(Translator translator, String text, TranslationCallback callback) {
        translator.translate(text)
                .addOnSuccessListener(translatedText -> {
                    callback.onTranslationSuccess(translatedText);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi dịch văn bản", e);
                    callback.onTranslationError("Lỗi dịch văn bản: " + e.getMessage());
                });
    }

    private String getMLKitLanguageCode(String languageCode) {
        // ML Kit sử dụng mã ngôn ngữ riêng, cần chuyển đổi nếu cần
        switch (languageCode) {
            case "zh-CN": return TranslateLanguage.CHINESE;
            case "zh-TW": return TranslateLanguage.CHINESE;
            default: return languageCode;
        }
    }

    public void detectLanguage(String text, LanguageDetectionCallback callback) {
        if (text == null || text.isEmpty()) {
            callback.onError("Văn bản trống");
            return;
        }

        LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(text)
                .addOnSuccessListener(languageCode -> {
                    if (languageCode.equals("und")) {
                        callback.onError("Không thể xác định ngôn ngữ");
                    } else {
                        String languageName = languageCodes.containsKey(languageCode)
                                ? languageCodes.get(languageCode)
                                : languageCode;
                        callback.onLanguageDetected(languageCode, languageName);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi phát hiện ngôn ngữ", e);
                    callback.onError("Lỗi phát hiện ngôn ngữ: " + e.getMessage());
                });
    }

    public void closeTranslators() {
        for (Translator translator : translators.values()) {
            translator.close();
        }
        translators.clear();
    }

    public interface TranslationCallback {
        void onTranslationSuccess(String translatedText);
        void onTranslationError(String errorMessage);
    }

    public interface LanguageDetectionCallback {
        void onLanguageDetected(String languageCode, String languageName);
        void onError(String errorMessage);
    }
}
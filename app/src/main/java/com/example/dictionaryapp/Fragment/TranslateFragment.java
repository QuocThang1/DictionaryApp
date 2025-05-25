package com.example.dictionaryapp.Fragment;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dictionaryapp.Controller.TranslationController;
import com.example.dictionaryapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TranslateFragment extends Fragment implements TextToSpeech.OnInitListener {

    private EditText inputText;
    private TextView outputText;
    private Button translateButton;
    private ImageButton detectLanguageButton;
    private ImageButton speakSourceButton;
    private ImageButton speakTargetButton;
    private ImageButton copyOutputButton;
    private Spinner sourceLanguageSpinner;
    private Spinner targetLanguageSpinner;
    private ProgressBar progressBar;
    private TextView detectedLanguageText;

    private TranslationController translationController;
    private TextToSpeech textToSpeech;
    private Map<String, String> languageCodes;
    private List<String> languageNames;
    private List<String> languageCodesArray;
    private String currentSourceLanguageCode = "en";
    private String currentTargetLanguageCode = "vi";
    private Locale currentTtsLocale = Locale.US;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.translate_fragment, container, false);

        // Khởi tạo các view
        inputText = view.findViewById(R.id.inputText);
        outputText = view.findViewById(R.id.outputText);
        translateButton = view.findViewById(R.id.translateButton);
        detectLanguageButton = view.findViewById(R.id.detectLanguageButton);
        speakSourceButton = view.findViewById(R.id.speakSourceButton);
        speakTargetButton = view.findViewById(R.id.speakTargetButton);
        copyOutputButton = view.findViewById(R.id.copyOutputButton);
        sourceLanguageSpinner = view.findViewById(R.id.sourceLanguageSpinner);
        targetLanguageSpinner = view.findViewById(R.id.targetLanguageSpinner);
        progressBar = view.findViewById(R.id.progressBar);
        detectedLanguageText = view.findViewById(R.id.detectedLanguageText);

        // Khởi tạo controller
        translationController = new TranslationController(getContext());

        // Khởi tạo TextToSpeech
        textToSpeech = new TextToSpeech(getContext(), this);

        // Lấy danh sách ngôn ngữ
        languageCodes = translationController.getLanguageCodes();
        setupLanguageSpinners();

        // Thiết lập sự kiện
        translateButton.setOnClickListener(v -> translateText());
        detectLanguageButton.setOnClickListener(v -> detectLanguage());
        speakSourceButton.setOnClickListener(v -> speakText(inputText.getText().toString(), getLocaleForLanguage(currentSourceLanguageCode)));
        speakTargetButton.setOnClickListener(v -> speakText(outputText.getText().toString(), getLocaleForLanguage(currentTargetLanguageCode)));
        copyOutputButton.setOnClickListener(v -> copyTranslatedText());
        return view;
    }

    private void setupLanguageSpinners() {
        languageNames = new ArrayList<>();
        languageCodesArray = new ArrayList<>();

        for (Map.Entry<String, String> entry : languageCodes.entrySet()) {
            languageCodesArray.add(entry.getKey());
            languageNames.add(entry.getValue());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, languageNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sourceLanguageSpinner.setAdapter(adapter);
        targetLanguageSpinner.setAdapter(adapter);

        // Thiết lập giá trị mặc định
        int englishIndex = languageCodesArray.indexOf("en");
        int vietnameseIndex = languageCodesArray.indexOf("vi");

        if (englishIndex >= 0) {
            sourceLanguageSpinner.setSelection(englishIndex);
        }

        if (vietnameseIndex >= 0) {
            targetLanguageSpinner.setSelection(vietnameseIndex);
        }

        sourceLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSourceLanguageCode = languageCodesArray.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        targetLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentTargetLanguageCode = languageCodesArray.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void translateText() {
        String text = inputText.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập văn bản cần dịch", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        translationController.translateText(text, currentSourceLanguageCode, currentTargetLanguageCode, new TranslationController.TranslationCallback() {
            @Override
            public void onTranslationSuccess(String translatedText) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        outputText.setText(translatedText);
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }

            @Override
            public void onTranslationError(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }
        });
    }

    private void detectLanguage() {
        String text = inputText.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập văn bản để phát hiện ngôn ngữ", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        translationController.detectLanguage(text, new TranslationController.LanguageDetectionCallback() {
            @Override
            public void onLanguageDetected(String languageCode, String languageName) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Hiển thị ngôn ngữ đã phát hiện
                        detectedLanguageText.setText("Đã phát hiện: " + languageName);
                        detectedLanguageText.setVisibility(View.VISIBLE);

                        // Cập nhật spinner
                        int index = languageCodesArray.indexOf(languageCode);
                        if (index >= 0) {
                            sourceLanguageSpinner.setSelection(index);
                        }

                        progressBar.setVisibility(View.GONE);
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }
        });
    }

    private Locale getLocaleForLanguage(String languageCode) {
        switch (languageCode) {
            case "en": return Locale.US;
            case "vi": return new Locale("vi", "VN");
            case "fr": return Locale.FRANCE;
            case "de": return Locale.GERMANY;
            case "it": return Locale.ITALY;
            case "ja": return Locale.JAPAN;
            case "ko": return Locale.KOREA;
            case "zh-CN": return Locale.SIMPLIFIED_CHINESE;
            case "zh-TW": return Locale.TRADITIONAL_CHINESE;
            default: return Locale.US;
        }
    }

    private void copyTranslatedText() {
        String text = outputText.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(getContext(), "Không có văn bản để sao chép", Toast.LENGTH_SHORT).show();
            return;
        }

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                getContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Văn bản đã dịch", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getContext(), "Đã sao chép văn bản", Toast.LENGTH_SHORT).show();
    }

    private void speakText(String text, Locale locale) {
        if (text.isEmpty()) {
            Toast.makeText(getContext(), "Không có văn bản để phát âm", Toast.LENGTH_SHORT).show();
            return;
        }

        if (textToSpeech != null) {
            if (locale != null && !locale.equals(currentTtsLocale)) {
                currentTtsLocale = locale;
                textToSpeech.setLanguage(locale);
            }
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Thiết lập ngôn ngữ mặc định
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getContext(), "Ngôn ngữ này không được hỗ trợ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Khởi tạo TextToSpeech thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        // Giải phóng tài nguyên translator
        if (translationController != null) {
            translationController.closeTranslators();
        }

        super.onDestroy();
    }
}
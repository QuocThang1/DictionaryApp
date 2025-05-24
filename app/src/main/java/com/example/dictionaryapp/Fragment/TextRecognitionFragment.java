package com.example.dictionaryapp.Fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dictionaryapp.Helper.ImageHelper;
import com.example.dictionaryapp.Helper.TextToSpeechHelper;
import com.example.dictionaryapp.Helper.TranslatorManager;
import com.example.dictionaryapp.Helper.TranslatorManager.TranslationCallback;
import com.example.dictionaryapp.R;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Locale;

public class TextRecognitionFragment extends Fragment {
    // UI components
    private ImageView imageView;
    private EditText recognizedText;
    private TextView translatedText;
    private Spinner sourceLanguageSpinner;
    private Spinner targetLanguageSpinner;
    private Button captureButton;
    private Button galleryButton;
    private Button translateButton;
    private ImageButton speakSourceButton;
    private ImageButton speakTargetButton;
    private ImageButton copySourceButton;
    private ImageButton copyTargetButton;
    
    // Helper classes
    private TextRecognizer recognizer;
    private TextToSpeechHelper sourceTextToSpeech;
    private TextToSpeechHelper targetTextToSpeech;
    private TranslatorManager translatorManager;
    private ImageHelper imageHelper;
    private ClipboardManager clipboardManager;
    
    // Data
    private String[] sourceLanguages;
    private String[] targetLanguages;
    private String[] sourceLanguageCodes;
    private String[] targetLanguageCodes;
    
    // Locale tương ứng cho TextToSpeech
    private final Locale[] sourceLocales = {
            Locale.US,
            new Locale("vi", "VN"),
            Locale.FRANCE,
            Locale.CHINA
    };
    
    private final Locale[] targetLocales = {
            new Locale("vi", "VN"),
            Locale.US
    };
    
    private int selectedSourceLanguage = 0;
    private int selectedTargetLanguage = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_recognition_fragment, container, false);

        initializeResources();
        initializeViews(view);
        setupSpinners();
        setupTextToSpeech();
        setupClickListeners();
        
        // Ban đầu vô hiệu hóa nút đọc và sao chép
        speakSourceButton.setEnabled(false);
        speakTargetButton.setEnabled(false);
        copySourceButton.setEnabled(false);
        copyTargetButton.setEnabled(false);

        return view;
    }
    
    private void initializeResources() {
        // Lấy danh sách ngôn ngữ từ resources
        sourceLanguages = getResources().getStringArray(R.array.source_languages);
        targetLanguages = getResources().getStringArray(R.array.target_languages);
        sourceLanguageCodes = getResources().getStringArray(R.array.source_language_codes);
        targetLanguageCodes = getResources().getStringArray(R.array.target_language_codes);

        // Khởi tạo các helper
        clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        translatorManager = new TranslatorManager(requireContext());
        imageHelper = new ImageHelper(this);
    }

    private void initializeViews(View view) {
        // Khởi tạo các thành phần UI
        imageView = view.findViewById(R.id.capturedImage);
        recognizedText = view.findViewById(R.id.recognizedText);
        translatedText = view.findViewById(R.id.translatedText);
        sourceLanguageSpinner = view.findViewById(R.id.sourceLanguageSpinner);
        targetLanguageSpinner = view.findViewById(R.id.targetLanguageSpinner);
        captureButton = view.findViewById(R.id.captureButton);
        galleryButton = view.findViewById(R.id.galleryButton);
        translateButton = view.findViewById(R.id.translateButton);
        speakSourceButton = view.findViewById(R.id.speakSourceButton);
        speakTargetButton = view.findViewById(R.id.speakTargetButton);
        copySourceButton = view.findViewById(R.id.copySourceButton);
        copyTargetButton = view.findViewById(R.id.copyTargetButton);
    }

    private void setupSpinners() {
        // Thiết lập adapter cho spinner ngôn ngữ nguồn
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, sourceLanguages);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceLanguageSpinner.setAdapter(sourceAdapter);
        
        // Thiết lập adapter cho spinner ngôn ngữ đích
        ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, targetLanguages);
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetLanguageSpinner.setAdapter(targetAdapter);
        
        // Thiết lập sự kiện khi chọn ngôn ngữ nguồn
        sourceLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSourceLanguage = position;
                updateSourceLanguage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        
        // Thiết lập sự kiện khi chọn ngôn ngữ đích
        targetLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTargetLanguage = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupTextToSpeech() {
        sourceTextToSpeech = new TextToSpeechHelper(requireContext(), speakSourceButton, sourceLocales[selectedSourceLanguage]);
        targetTextToSpeech = new TextToSpeechHelper(requireContext(), speakTargetButton, targetLocales[selectedTargetLanguage]);
    }

    private void setupClickListeners() {
        // Thiết lập các sự kiện click
        captureButton.setOnClickListener(v -> imageHelper.checkCameraPermissionAndOpenCamera());
        galleryButton.setOnClickListener(v -> imageHelper.checkStoragePermissionAndOpenGallery());
        translateButton.setOnClickListener(v -> translateRecognizedText());
        
        // Thiết lập sự kiện đọc văn bản nguồn
        speakSourceButton.setOnClickListener(v -> handleSpeakSource());
        
        // Thiết lập sự kiện đọc văn bản đích
        speakTargetButton.setOnClickListener(v -> handleSpeakTarget());
        
        // Thiết lập sự kiện sao chép văn bản nguồn
        copySourceButton.setOnClickListener(v -> handleCopySource());
        
        // Thiết lập sự kiện sao chép văn bản đích
        copyTargetButton.setOnClickListener(v -> handleCopyTarget());
    }
    
    private void handleSpeakSource() {
        String content = recognizedText.getText().toString();
        if (!content.isEmpty()) {
            sourceTextToSpeech.speak(content);
        }
    }

    private void handleSpeakTarget() {
        String translatedContent = translatedText.getText().toString();
        if (!translatedContent.isEmpty() && !translatedContent.equals("Đang dịch...")) {
            targetTextToSpeech.speak(translatedContent);
        }
    }

    private void handleCopySource() {
        String content = recognizedText.getText().toString();
        if (!content.isEmpty()) {
            copyToClipboard(content, "Văn bản nguồn");
            Toast.makeText(requireContext(), "Đã sao chép văn bản nguồn", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCopyTarget() {
        String translatedContent = translatedText.getText().toString();
        if (!translatedContent.isEmpty() && !translatedContent.equals("Đang dịch...")) {
            copyToClipboard(translatedContent, "Văn bản dịch");
            Toast.makeText(requireContext(), "Đã sao chép bản dịch", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void copyToClipboard(String text, String label) {
        ClipData clip = ClipData.newPlainText(label, text);
        clipboardManager.setPrimaryClip(clip);
    }
    
    private void updateSourceLanguage() {
        if (sourceTextToSpeech != null) {
            sourceTextToSpeech.setLanguage(sourceLocales[selectedSourceLanguage]);
            sourceTextToSpeech.setEnabled(!recognizedText.getText().toString().isEmpty());
        }
    }

    private void translateRecognizedText() {
        String textToTranslate = recognizedText.getText().toString();
        if (textToTranslate.isEmpty()) {
            Toast.makeText(requireContext(), "Không có văn bản để dịch", Toast.LENGTH_SHORT).show();
            return;
        }
        
        translatedText.setText("Đang dịch...");
        targetTextToSpeech.setEnabled(false);
        copyTargetButton.setEnabled(false);
        
        String sourceLanguage = sourceLanguageCodes[selectedSourceLanguage];
        String targetLanguage = targetLanguageCodes[selectedTargetLanguage];
        
        translatorManager.translate(sourceLanguage, targetLanguage, textToTranslate, new TranslationCallback() {
            @Override
            public void onTranslationSuccess(String translatedText) {
                TextRecognitionFragment.this.translatedText.setText(translatedText);
                targetTextToSpeech.setEnabled(true);
                copyTargetButton.setEnabled(true);
            }

            @Override
            public void onTranslationFailure(String errorMessage) {
                TextRecognitionFragment.this.translatedText.setText("Lỗi dịch: " + errorMessage);
                targetTextToSpeech.setEnabled(false);
                copyTargetButton.setEnabled(false);
                Toast.makeText(requireContext(), 
                        "Lỗi khi dịch văn bản: " + errorMessage, 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ImageHelper.getCameraPermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imageHelper.checkCameraPermissionAndOpenCamera();
            } else {
                Toast.makeText(requireContext(), "Cần quyền camera để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == ImageHelper.getStoragePermissionCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imageHelper.checkStoragePermissionAndOpenGallery();
            } else {
                Toast.makeText(requireContext(), "Cần quyền truy cập bộ nhớ để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageHelper.getCameraRequestCode() && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                imageView.setImageBitmap(photo);
                recognizeText(photo);
            }
        } else if (requestCode == ImageHelper.getGalleryRequestCode() && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = imageHelper.getBitmapFromUri(selectedImageUri);
                    imageView.setImageBitmap(bitmap);
                    recognizeText(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Lỗi khi đọc ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void recognizeText(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        recognizer.process(image)
                .addOnSuccessListener(text -> {
                    if (text.getText().isEmpty()) {
                        recognizedText.setText("");
                        translateButton.setEnabled(false);
                        sourceTextToSpeech.setEnabled(false);
                        copySourceButton.setEnabled(false);
                        Toast.makeText(requireContext(), "Không tìm thấy văn bản trong ảnh", Toast.LENGTH_SHORT).show();
                    } else {
                        recognizedText.setText(text.getText());
                        translateButton.setEnabled(true);
                        sourceTextToSpeech.setEnabled(true);
                        copySourceButton.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    recognizedText.setText("");
                    translateButton.setEnabled(false);
                    sourceTextToSpeech.setEnabled(false);
                    copySourceButton.setEnabled(false);
                    Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    @Override
    public void onDestroy() {
        if (sourceTextToSpeech != null) {
            sourceTextToSpeech.shutdown();
        }
        if (targetTextToSpeech != null) {
            targetTextToSpeech.shutdown();
        }
        
        if (translatorManager != null) {
            translatorManager.closeAll();
        }
        
        super.onDestroy();
    }
}
package com.example.dictionaryapp.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.dictionaryapp.R;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.io.IOException;
import java.util.Locale;

public class TextRecognitionFragment extends Fragment {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int GALLERY_REQUEST_CODE = 103;

    private ImageView imageView;
    private TextView resultText;
    private TextView translatedText;
    private TextView sourceLanguageText;
    private TextView targetLanguageText;
    private Button captureButton;
    private Button galleryButton;
    private Button translateButton;
    private ImageButton speakSourceButton;
    private ImageButton speakTargetButton;
    private TextRecognizer recognizer;
    private Translator englishVietnameseTranslator;
    private String recognizedTextContent = "";
    private String sourceLanguage = "Tiếng Anh";
    private String targetLanguage = "Tiếng Việt";
    private TextToSpeech ttsSource;
    private TextToSpeech ttsTarget;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_recognition_fragment, container, false);

        // Khởi tạo các thành phần UI
        imageView = view.findViewById(R.id.capturedImage);
        resultText = view.findViewById(R.id.recognizedText);
        translatedText = view.findViewById(R.id.translatedText);
        sourceLanguageText = view.findViewById(R.id.sourceLanguageText);
        targetLanguageText = view.findViewById(R.id.targetLanguageText);
        captureButton = view.findViewById(R.id.captureButton);
        galleryButton = view.findViewById(R.id.galleryButton);
        translateButton = view.findViewById(R.id.translateButton);
        speakSourceButton = view.findViewById(R.id.speakSourceButton);
        speakTargetButton = view.findViewById(R.id.speakTargetButton);

        // Thiết lập hiển thị ngôn ngữ
        sourceLanguageText.setText(sourceLanguage);
        targetLanguageText.setText(targetLanguage);

        // Khởi tạo TextToSpeech cho ngôn ngữ nguồn (tiếng Anh)
        ttsSource = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = ttsSource.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    speakSourceButton.setEnabled(false);
                    Toast.makeText(requireContext(), "Không hỗ trợ đọc tiếng Anh", Toast.LENGTH_SHORT).show();
                }
            } else {
                speakSourceButton.setEnabled(false);
            }
        });

        // Khởi tạo TextToSpeech cho ngôn ngữ đích (tiếng Việt)
        ttsTarget = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = ttsTarget.setLanguage(new Locale("vi", "VN"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    speakTargetButton.setEnabled(false);
                    Toast.makeText(requireContext(), "Không hỗ trợ đọc tiếng Việt", Toast.LENGTH_SHORT).show();
                }
            } else {
                speakTargetButton.setEnabled(false);
            }
        });

        // Khởi tạo TextRecognizer
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        
        // Khởi tạo translator từ tiếng Anh sang tiếng Việt
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                .build();
        englishVietnameseTranslator = Translation.getClient(options);
        
        // Tải mô hình dịch về thiết bị (có thể thực hiện offline)
        downloadTranslationModel();

        // Thiết lập các sự kiện click
        captureButton.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());
        galleryButton.setOnClickListener(v -> checkStoragePermissionAndOpenGallery());
        translateButton.setOnClickListener(v -> translateRecognizedText());
        
        // Thiết lập sự kiện đọc văn bản
        speakSourceButton.setOnClickListener(v -> {
            if (!recognizedTextContent.isEmpty()) {
                ttsSource.speak(recognizedTextContent, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
        
        speakTargetButton.setOnClickListener(v -> {
            String translatedContent = translatedText.getText().toString();
            if (!translatedContent.isEmpty() && !translatedContent.equals("Đang dịch...")) {
                ttsTarget.speak(translatedContent, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        // Ban đầu vô hiệu hóa nút đọc
        speakSourceButton.setEnabled(false);
        speakTargetButton.setEnabled(false);

        return view;
    }
    
    private void downloadTranslationModel() {
        englishVietnameseTranslator.downloadModelIfNeeded()
                .addOnSuccessListener(unused -> {
                    // Mô hình đã được tải xuống thành công
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi tải mô hình
                    Toast.makeText(requireContext(), 
                            "Không thể tải mô hình dịch: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }
    
    private void checkStoragePermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ sử dụng READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
            } else {
                openGallery();
            }
        } else {
            // Android 12 trở xuống sử dụng READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                openGallery();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
    
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Cần quyền camera để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(requireContext(), "Cần quyền truy cập bộ nhớ để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                imageView.setImageBitmap(photo);
                recognizeText(photo);
            }
        } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap;
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                    } else {
                        ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(), selectedImageUri);
                        bitmap = ImageDecoder.decodeBitmap(source);
                    }
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
                        resultText.setText("Không tìm thấy văn bản trong ảnh");
                        translateButton.setEnabled(false);
                        speakSourceButton.setEnabled(false);
                        recognizedTextContent = "";
                    } else {
                        recognizedTextContent = text.getText();
                        resultText.setText(recognizedTextContent);
                        translateButton.setEnabled(true);
                        speakSourceButton.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    resultText.setText("Lỗi: " + e.getMessage());
                    translateButton.setEnabled(false);
                    speakSourceButton.setEnabled(false);
                    recognizedTextContent = "";
                });
    }
    
    private void translateRecognizedText() {
        if (recognizedTextContent.isEmpty()) {
            Toast.makeText(requireContext(), "Không có văn bản để dịch", Toast.LENGTH_SHORT).show();
            return;
        }
        
        translatedText.setText("Đang dịch...");
        speakTargetButton.setEnabled(false);
        
        englishVietnameseTranslator.translate(recognizedTextContent)
                .addOnSuccessListener(translatedString -> {
                    translatedText.setText(translatedString);
                    speakTargetButton.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    translatedText.setText("Lỗi dịch: " + e.getMessage());
                    speakTargetButton.setEnabled(false);
                    Toast.makeText(requireContext(), 
                            "Lỗi khi dịch văn bản: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }
    
    @Override
    public void onDestroy() {
        if (ttsSource != null) {
            ttsSource.stop();
            ttsSource.shutdown();
        }
        if (ttsTarget != null) {
            ttsTarget.stop();
            ttsTarget.shutdown();
        }
        englishVietnameseTranslator.close();
        super.onDestroy();
    }
}
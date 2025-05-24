package com.example.dictionaryapp.Helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class ImageHelper {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int GALLERY_REQUEST_CODE = 103;
    
    private Fragment fragment;
    
    public ImageHelper(Fragment fragment) {
        this.fragment = fragment;
    }
    
    public void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(fragment.requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }
    
    public void checkStoragePermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ sử dụng READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(fragment.requireActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
            } else {
                openGallery();
            }
        } else {
            // Android 12 trở xuống sử dụng READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(fragment.requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                openGallery();
            }
        }
    }
    
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fragment.startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
    
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fragment.startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }
    
    public Bitmap getBitmapFromUri(Uri uri) throws IOException {
        if (Build.VERSION.SDK_INT < 28) {
            return MediaStore.Images.Media.getBitmap(fragment.requireActivity().getContentResolver(), uri);
        } else {
            ImageDecoder.Source source = ImageDecoder.createSource(fragment.requireActivity().getContentResolver(), uri);
            return ImageDecoder.decodeBitmap(source);
        }
    }
    
    public static int getCameraPermissionCode() {
        return CAMERA_PERMISSION_CODE;
    }
    
    public static int getStoragePermissionCode() {
        return STORAGE_PERMISSION_CODE;
    }
    
    public static int getCameraRequestCode() {
        return CAMERA_REQUEST_CODE;
    }
    
    public static int getGalleryRequestCode() {
        return GALLERY_REQUEST_CODE;
    }
}
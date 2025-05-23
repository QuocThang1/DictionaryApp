package com.example.dictionaryapp.Model;

public class VideoLesson {
    private String title;
    private String videoUrl;
    private int thumbnailResource;
    
    public VideoLesson(String title, String videoUrl, int thumbnailResource) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.thumbnailResource = thumbnailResource;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public int getThumbnailResource() {
        return thumbnailResource;
    }
}
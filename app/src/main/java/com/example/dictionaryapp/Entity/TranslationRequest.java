package com.example.dictionaryapp.Entity;

public class TranslationRequest {
    private String q;
    private String source;
    private String target;
    private String format = "text";

    public TranslationRequest(String text, String sourceLanguage, String targetLanguage) {
        this.q = text;
        this.source = sourceLanguage;
        this.target = targetLanguage;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
package com.ashomok.imagetotext.ocr.ocr_task;

/**
 * Created by iuliia on 9/28/17.
 */
public class OcrRequestBean {
    private String[] languages;
    private String gcsImageUri;

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public void setGcsImageUri(String gcsImageUri) {
        this.gcsImageUri = gcsImageUri;
    }
}

package com.ashomok.ocrme.ocr.ocr_task;

/**
 * Created by iuliia on 9/28/17.
 */
public class OcrRequestBean {
    private String[] languages;
    private String gcsImageUri; //example gs://imagetotext-149919.appspot.com/ocr_request_images/659d2a80-f1fa-4b93-80fb-a83c534fc289cropped.jpg
    private String idTokenString;

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public void setGcsImageUri(String gcsImageUri) {
        this.gcsImageUri = gcsImageUri;
    }

    public void setIdTokenString(String idTokenString) {
        this.idTokenString = idTokenString;
    }
}

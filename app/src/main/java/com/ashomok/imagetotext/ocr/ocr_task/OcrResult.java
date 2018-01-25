package com.ashomok.imagetotext.ocr.ocr_task;

import java.io.Serializable;

/**
 * Created by iuliia on 1/22/18.
 */
public class OcrResult implements Serializable{
    private String sourceImageUrl; //example gs://imagetotext-149919.appspot.com/ocr_request_images/659d2a80-f1fa-4b93-80fb-a83c534fc289cropped.jpg
    private String[] languages;
    private String textResult;
    private String pdfResultGsUrl;
    private String pdfResultMediaUrl;
    private Long id;
    private String timeStamp;

    public String getSourceImageUrl() {
        return sourceImageUrl;
    }

    public String[] getLanguages() {
        return languages;
    }

    public String getTextResult() {
        return textResult;
    }

    public String getPdfResultGsUrl() {
        return pdfResultGsUrl;
    }

    public String getPdfResultMediaUrl() {
        return pdfResultMediaUrl;
    }

    public Long getId() {
        return id;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}

package com.ashomok.ocrme.ocr.ocr_task;

import java.io.Serializable;

/**
 * Created by iuliia on 1/22/18.
 */

public class OcrResult implements Serializable {
    private String sourceImageUrl; //example gs://imagetotext-149919.appspot.com/ocr_request_images/659d2a80-f1fa-4b93-80fb-a83c534fc289cropped.jpg
    private String[] languages;
    private String textResult;
    private String pdfResultGsUrl;
    private String pdfResultMediaUrl;
    private Long id;
    private String timeStamp;

    private OcrResult(Builder builder) {
        this.sourceImageUrl = builder.sourceImageUrl;
        this.languages = builder.languages;
        this.textResult = builder.textResult;
        this.pdfResultGsUrl = builder.pdfResultGsUrl;
        this.pdfResultMediaUrl = builder.pdfResultMediaUrl;
        this.id = builder.id;
        this.timeStamp = builder.timeStamp;
    }

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

    public static class Builder {
        private String sourceImageUrl; //example gs://imagetotext-149919.appspot.com/ocr_request_images/659d2a80-f1fa-4b93-80fb-a83c534fc289cropped.jpg
        private String[] languages;
        private String textResult;
        private String pdfResultGsUrl;
        private String pdfResultMediaUrl;
        private Long id;
        private String timeStamp;

        public Builder sourceImageUrl(String sourceImageUrl) {
            this.sourceImageUrl = sourceImageUrl;
            return this;
        }

        public Builder languages(String[] languages) {
            this.languages = languages;
            return this;
        }


        public Builder textResult(String textResult) {
            this.textResult = textResult;
            return this;
        }

        public Builder pdfResultGsUrl(String pdfResultGsUrl) {
            this.pdfResultGsUrl = pdfResultGsUrl;
            return this;
        }


        public Builder pdfResultMediaUrl(String pdfResultMediaUrl) {
            this.pdfResultMediaUrl = pdfResultMediaUrl;
            return this;
        }


        public Builder id(Long id) {
            this.id = id;
            return this;
        }


        public Builder timeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public OcrResult build() {
            return new OcrResult(this);
        }

    }
}

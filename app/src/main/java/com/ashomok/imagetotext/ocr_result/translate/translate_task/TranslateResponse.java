package com.ashomok.imagetotext.ocr_result.translate.translate_task;

/**
 * Created by iuliia on 9/5/17.
 */

public class TranslateResponse {

    private String sourceLanguageCode;
    private String targetLanguageCode;
    private Status status;
    private String textResult;

    public enum Status {
        OK,
        UNKNOWN_ERROR
    }

    public String getSourceLanguageCode() {
        return sourceLanguageCode;
    }
    public String getTargetLanguageCode() {
        return targetLanguageCode;
    }
    public Status getStatus() {
        return status;
    }
    public String getTextResult() {
        return textResult;
    }
}
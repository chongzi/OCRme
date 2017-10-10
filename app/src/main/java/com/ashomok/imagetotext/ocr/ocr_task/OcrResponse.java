package com.ashomok.imagetotext.ocr.ocr_task;

import java.io.Serializable;

/**
 * Created by iuliia on 9/17/17.
 */

public class OcrResponse implements Serializable{

    private String textResult;
    private String pdfResultGsUrl;
    private String pdfResultMediaUrl;
    private Status status;

    public OcrResponse(
            String textResult, String pdfResultGsUrl, String pdfResultMediaUrl, Status status){
        this.textResult = textResult;
        this.pdfResultGsUrl = pdfResultGsUrl;
        this.pdfResultMediaUrl = pdfResultMediaUrl;
        this.status = status;
    }

    public enum Status implements Serializable{
        OK,
        PDF_CAN_NOT_BE_CREATED_LANGUAGE_NOT_SUPPORTED,
        TEXT_NOT_FOUND,
        INVALID_LANGUAGE_HINTS,
        UNKNOWN_ERROR
    }
    public String getTextResult() {
        return textResult;
    }
    public Status getStatus() {
        return status;
    }
    public String getPdfResultGsUrl() {
        return pdfResultGsUrl;
    }
    public String getPdfResultMediaUrl() {
        return pdfResultMediaUrl;
    }

    @Override
    public String toString() {
        return "OcrResponse{" +
                "textResult='" + textResult + '\'' +
                ", pdfResultGsUrl='" + pdfResultGsUrl + '\'' +
                ", pdfResultMediaUrl='" + pdfResultMediaUrl + '\'' +
                ", status=" + status +
                '}';
    }
}

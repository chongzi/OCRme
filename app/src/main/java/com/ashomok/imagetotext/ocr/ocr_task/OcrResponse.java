package com.ashomok.imagetotext.ocr.ocr_task;

import java.io.Serializable;

/**
 * Created by iuliia on 9/17/17.
 */

public class OcrResponse implements Serializable{

    private String textResult;
    private String pdfResultUrl;
    private Status status;

    public enum Status implements Serializable{
        OK,
        PDF_CAN_NOT_BE_CREATED_LANGUAGE_NOT_SUPPORTED,
        TEXT_NOT_FOUND,
        UNKNOWN_ERROR
    }
    public String getTextResult() {
        return textResult;
    }
    public String getPdfResultUrl() {
        return pdfResultUrl;
    }
    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "OcrResponse{" +
                "textResult='" + textResult + '\'' +
                ", pdfResultUrl='" + pdfResultUrl + '\'' +
                ", status=" + status +
                '}';
    }
}

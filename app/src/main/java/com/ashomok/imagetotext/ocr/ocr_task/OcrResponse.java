package com.ashomok.imagetotext.ocr.ocr_task;

import java.io.Serializable;

/**
 * Created by iuliia on 9/17/17.
 */

public class OcrResponse implements Serializable {

    private Status status;
    private OcrResult ocrResult;

    public OcrResponse(OcrResult ocrResult, Status status) {
        this.ocrResult = ocrResult;
        this.status = status;
    }

    @Override
    public String toString() {
        return "OcrResponse{" +
                "status=" + status +
                ", ocrResult=" + ocrResult +
                '}';
    }

    public enum Status implements Serializable {
        OK,
        PDF_CAN_NOT_BE_CREATED_LANGUAGE_NOT_SUPPORTED,
        TEXT_NOT_FOUND,
        INVALID_LANGUAGE_HINTS,
        UNKNOWN_ERROR
    }

    public Status getStatus() {
        return status;
    }

    public OcrResult getOcrResult() {
        return ocrResult;
    }
}

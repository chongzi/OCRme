package com.ashomok.ocrme.ocr_result.translate.translate_task;

import java.io.Serializable;

/**
 * Created by iuliia on 8/31/17.
 */
public class TranslateResponse implements Serializable {
    private Status status;
    private TranslateResult translateResult;

    public TranslateResult getTranslateResult() {
        return translateResult;
    }
    public Status getStatus() {
        return status;
    }

    public enum Status {
        OK,
        UNKNOWN_ERROR
    }
}

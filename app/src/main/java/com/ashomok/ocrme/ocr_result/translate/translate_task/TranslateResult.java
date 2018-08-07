package com.ashomok.ocrme.ocr_result.translate.translate_task;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TranslateResult implements Serializable {
    private String sourceLanguageCode;
    private String targetLanguageCode;
    private String textResult;
    private String timeStamp;
    private Long id;


    public String getSourceLanguageCode() {
        return sourceLanguageCode;
    }

    public String getTargetLanguageCode() {
        return targetLanguageCode;
    }

    public String getTextResult() {
        return textResult;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Long getId() {
        return id;
    }

}

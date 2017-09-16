package com.ashomok.imagetotext.ocr_result.translate.translate_task;

/**
 * Created by iuliia on 5/22/17.
 */

//TODO make as builder - use buildre pattern

public class TranslateRequestBean {
    private String sourceLang;
    private String targetLang;
    private String sourceText;

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }
}

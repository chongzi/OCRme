package com.ashomok.imagetotext.translate_task;

/**
 * Created by iuliia on 5/22/17.
 */
public class TranslateRequestBean {
    private String deviceLang;
    private String sourceLang;
    private String targetLang;
    private String sourceText;

    public void setDeviceLang(String deviceLang) {
        this.deviceLang = deviceLang;
    }

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

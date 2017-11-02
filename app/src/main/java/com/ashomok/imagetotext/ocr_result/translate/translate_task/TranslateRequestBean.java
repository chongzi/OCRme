package com.ashomok.imagetotext.ocr_result.translate.translate_task;

/**
 * Created by iuliia on 5/22/17.
 */

public class TranslateRequestBean {
    private String sourceLang;
    private String targetLang;
    private String sourceText;

    private TranslateRequestBean(Builder builder) {
        this.sourceLang = builder.sourceLang;
        this.targetLang = builder.targetLang;
        this.sourceText = builder.sourceText;
    }

    public static class Builder {
        private String sourceLang;
        private String targetLang;
        private String sourceText;

        public Builder sourceLang(String sourceLang) {
            this.sourceLang = sourceLang;
            return this;
        }

        public Builder targetLang(String targetLang) {
            this.targetLang = targetLang;
            return this;
        }

        public Builder sourceText(String sourceText) {
            this.sourceText = sourceText;
            return this;
        }

        public TranslateRequestBean build() {
            return new TranslateRequestBean(this);
        }
    }
}

package com.ashomok.ocrme.ocr_result.translate.translate_task.translate_task;

import javax.annotation.Nullable;

/**
 * Created by iuliia on 5/22/17.
 */

//todo updated - use new
public class TranslateRequestBean {
    private String sourceLang;
    private String targetLang;
    private String sourceText;
    private @Nullable
    String idTokenString;

    private TranslateRequestBean(Builder builder) {
        this.sourceLang = builder.sourceLang;
        this.targetLang = builder.targetLang;
        this.sourceText = builder.sourceText;
        this.idTokenString = builder.idTokenString;
    }

    public static class Builder {
        private String sourceLang;
        private String targetLang;
        private String sourceText;
        private @Nullable String idTokenString;

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

        public Builder idTokenString(String idTokenString) {
            this.idTokenString = idTokenString;
            return this;
        }

        public TranslateRequestBean build() {
            return new TranslateRequestBean(this);
        }
    }
}

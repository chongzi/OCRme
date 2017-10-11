package com.ashomok.imagetotext.ocr_result.translate.translate_task;

import java.io.Serializable;
import java.util.List;

/**
 * Created by iuliia on 9/5/17.
 */

public class SupportedLanguagesResponse implements Serializable {

    private List<Language> supportedLanguages;
    private Status status;

    public enum Status {
        OK,
        UNKNOWN_ERROR
    }

    public List<Language> getSupportedLanguages() {
        return supportedLanguages;
    }
    public Status getStatus() {
        return status;
    }

    public class Language implements Serializable{
        private final String code;
        private final String name;

        private Language(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return this.code;
        }

        public String getName() {
            return this.name;
        }
    }
}

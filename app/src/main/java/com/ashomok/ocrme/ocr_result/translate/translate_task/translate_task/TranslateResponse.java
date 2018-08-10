package com.ashomok.ocrme.ocr_result.translate.translate_task.translate_task;

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

    @Override
    public String toString() {
        return "TranslateResponse{" +
                "status=" + status +
                ", translateResult=" + translateResult +
                '}';
    }

    public enum Status {
        OK,
        UNKNOWN_ERROR
    }

    public static class TranslateResult implements Serializable {
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

        @Override
        public String toString() {
            return "TranslateResult{" +
                    "sourceLanguageCode='" + sourceLanguageCode + '\'' +
                    ", targetLanguageCode='" + targetLanguageCode + '\'' +
                    ", textResult='" + textResult + '\'' +
                    ", timeStamp='" + timeStamp + '\'' +
                    ", id=" + id +
                    '}';
        }
    }
}

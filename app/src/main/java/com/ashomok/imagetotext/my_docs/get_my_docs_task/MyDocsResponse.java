package com.ashomok.imagetotext.my_docs.get_my_docs_task;
import java.util.List;

/**
 * Created by iuliia on 12/19/17.
 */

public class MyDocsResponse {

    private String endCursor;
    private List<MyDoc> requestList;
    private Status status;

    public enum Status {
        OK,
        USER_NOT_FOUND,
        UNKNOWN_ERROR
    }

    public String getEndCursor() {
        return endCursor;
    }

    public List<MyDoc> getRequestList() {
        return requestList;
    }

    public Status getStatus() {
        return status;
    }

    public static class MyDoc {
        private String sourceImageUrl; //example gs://imagetotext-149919.appspot.com/ocr_request_images/659d2a80-f1fa-4b93-80fb-a83c534fc289cropped.jpg
        private String[] languages;
        private String textResult;
        private String pdfResultGsUrl;
        private String pdfResultMediaUrl;
        private Long id;
        private String timeStamp;

        public String getSourceImageUrl() {
            return sourceImageUrl;
        }

        public String[] getLanguages() {
            return languages;
        }

        public String getTextResult() {
            return textResult;
        }

        public String getPdfResultGsUrl() {
            return pdfResultGsUrl;
        }

        public String getPdfResultMediaUrl() {
            return pdfResultMediaUrl;
        }

        public Long getId() {
            return id;
        }

        public String getTimeStamp() {
            return timeStamp;
        }
    }
}

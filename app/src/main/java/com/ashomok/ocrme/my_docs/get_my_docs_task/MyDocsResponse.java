package com.ashomok.ocrme.my_docs.get_my_docs_task;

import com.ashomok.ocrme.ocr.ocr_task.OcrResult;

import java.util.List;

/**
 * Created by iuliia on 12/19/17.
 */

public class MyDocsResponse {

    private String endCursor;
    private List<OcrResult> requestList;
    private Status status;

    public String getEndCursor() {
        return endCursor;
    }

    public List<OcrResult> getRequestList() {
        return requestList;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        OK,
        USER_NOT_FOUND,
        UNKNOWN_ERROR
    }

}

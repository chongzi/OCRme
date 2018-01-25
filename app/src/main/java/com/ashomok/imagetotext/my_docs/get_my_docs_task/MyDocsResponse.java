package com.ashomok.imagetotext.my_docs.get_my_docs_task;
import com.ashomok.imagetotext.ocr.ocr_task.OcrResult;

import java.util.List;

/**
 * Created by iuliia on 12/19/17.
 */

public class MyDocsResponse {

    private String endCursor;
    private List<OcrResult> requestList;
    private Status status;

    public enum Status {
        OK,
        USER_NOT_FOUND,
        UNKNOWN_ERROR
    }

    public String getEndCursor() {
        return endCursor;
    }

    public List<OcrResult> getRequestList() {
        return requestList;
    }

    public Status getStatus() {
        return status;
    }

}

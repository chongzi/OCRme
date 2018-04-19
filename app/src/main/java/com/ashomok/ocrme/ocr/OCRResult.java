package com.ashomok.ocrme.ocr;

import android.support.annotation.Nullable;

/**
 * Created by iuliia on 5/28/17.
 */

class OCRResult {
    @Nullable
    public String getError() {
        return error;
    }

    public void setError(@Nullable String error) {
        this.error = error;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private @Nullable String error;
    private String text;


}

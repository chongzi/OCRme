package com.ashomok.ocrme.ocr_result;

import com.ashomok.ocrme.di_dagger.BasePresenter;

public interface OcrResultContract {
    interface View {
        void showError(String message);
    }

    interface Presenter extends BasePresenter<View> { }
}


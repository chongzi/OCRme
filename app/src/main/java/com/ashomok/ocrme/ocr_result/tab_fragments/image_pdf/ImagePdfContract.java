package com.ashomok.ocrme.ocr_result.tab_fragments.image_pdf;

import com.ashomok.ocrme.di_dagger.BasePresenter;

import java.io.File;

import io.reactivex.Completable;

public interface ImagePdfContract {
    interface View {

        void showProgress(boolean show);

        void showError(Throwable error);

        void initBottomPanel();

        void setToPDFView(File mPdfFile);

        void showError(int errorMessageRes);

        void showWarning(int message);

        void showInfo(int infoMessageRes);

        void showInfo(String message);

        void runPDFIntent(File pdfFile);
    }

    interface Presenter extends BasePresenter<View> {

        Completable initPdfView(String mGsUrl);

        void runPdfIntent();

        void saveFileOnDevice();
    }
}

package com.ashomok.imagetotext.my_docs;

/**
 * Created by iuliia on 1/10/18.
 */

import android.content.Intent;
import android.support.annotation.StringRes;

import com.ashomok.imagetotext.di_dagger.BasePresenter;
import com.ashomok.imagetotext.ocr.ocr_task.OcrResult;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface MyDocsContract {
    interface View {

        void showError(@StringRes int errorMessageRes);

        void showInfo (@StringRes int infoMessageRes);

        void addNewLoadedDocs(List<OcrResult> newLoadedDocs);

        void clearDocsList();

        void showProgress(final boolean show);

        void startActivity(Intent intent);

        void showAds();
    }

    interface Presenter extends BasePresenter<MyDocsContract.View> {
        void loadMoreDocs();

        void onShareTextClicked(String textResult);

        void onSharePdfClicked(String mDownloadURL);

        void showAdsIfNeeded();
    }
}

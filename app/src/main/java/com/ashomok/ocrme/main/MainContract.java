package com.ashomok.ocrme.main;

/**
 * Created by iuliia on 2/14/18.
 */

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.annimon.stream.Optional;
import com.ashomok.ocrme.di_dagger.BasePresenter;
import com.ashomok.ocrme.billing.BillingProviderCallback;
import com.tbruyelle.rxpermissions2.Permission;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public class MainContract {
    interface View {

        void showError(@StringRes int errorMessageRes);

        void showWarning(@StringRes int message);

        void showInfo (@StringRes int infoMessageRes);
        void showInfo(String message);

        void updateLanguageString(String languageString);

        void updateView(boolean isPremium);

        void updateRequestsCounter(boolean isVisible);

        void initRequestsCounter(int requestCount);

        void startCamera();

        void showRequestsCounterDialog(int requestsCount);

        void startGalleryChooser();

        void showAds();
    }

    interface Presenter extends BasePresenter<MainContract.View> {
        void onCheckedLanguageCodesObtained(@Nullable List<String> checkedLanguageCodes);

        void showAdsIfNeeded();

        Optional<List<String>> getLanguageCodes();

        boolean isRequestsAvailable();

        int getRequestsCount();

        void consumeRequest();

        void onPhotoBtnClicked(Permission permission);

        void onGalleryChooserClicked();

        String getUserEmail();
    }
}

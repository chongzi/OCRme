package com.ashomok.imagetotext.main;

/**
 * Created by iuliia on 2/14/18.
 */

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.annimon.stream.Optional;
import com.ashomok.imagetotext.di_dagger.BasePresenter;
import com.ashomok.imagetotext.billing.BillingProviderCallback;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public class MainContract {
    interface View {

        void showError(@StringRes int errorMessageRes);

        void showInfo (@StringRes int infoMessageRes);
        void showInfo(String message);

        void updateLanguageString(String languageString);

        void updateView(boolean isPremium);

        void updateRequestsCounter(boolean isVisible);

        void initRequestsCounter(int requestCount);
    }

    interface Presenter extends BasePresenter<MainContract.View> {
        void onCheckedLanguageCodesObtained(@Nullable List<String> checkedLanguageCodes);

        Optional<List<String>> getLanguageCodes();

        boolean isRequestsAvailable();

        int getRequestsCount();

        void consumeRequest();
    }
}

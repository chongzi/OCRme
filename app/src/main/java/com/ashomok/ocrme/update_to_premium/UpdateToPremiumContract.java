package com.ashomok.ocrme.update_to_premium;

import android.support.annotation.StringRes;

import com.ashomok.ocrme.billing.model.SkuRowData;
import com.ashomok.ocrme.di_dagger.BasePresenter;

import java.util.List;

/**
 * Created by iuliia on 1/29/18.
 */

/**
 * This specifies the contract between the view and the presenter.
 */
public interface UpdateToPremiumContract {
    interface View {

        void showError(@StringRes int errorMessageRes);

        void showInfo (@StringRes int infoMessageRes);

        void updateView(boolean isPremium);

        void initPremiumMonthRow(SkuRowData item);

        void initPremiumYearRow(SkuRowData item);

        void showInfo(String message);
    }

    interface Presenter extends BasePresenter<UpdateToPremiumContract.View> {
        void onOneYearClicked(SkuRowData item);

        void onOneMonthClicked(SkuRowData item);
    }
}

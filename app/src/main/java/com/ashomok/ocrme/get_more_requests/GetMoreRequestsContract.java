package com.ashomok.ocrme.get_more_requests;

/**
 * Created by iuliia on 3/2/18.
 */

import android.support.annotation.StringRes;

import com.ashomok.ocrme.billing.model.SkuRowData;
import com.ashomok.ocrme.di_dagger.BasePresenter;
import com.ashomok.ocrme.update_to_premium.UpdateToPremiumContract;

/**
 * This specifies the contract between the view and the presenter.
 */
public class GetMoreRequestsContract {
    interface View {

        void showError(@StringRes int errorMessageRes);

        void showInfo (@StringRes int infoMessageRes);

        void initBuyRequestsRow(SkuRowData item);

        void showInfo(String message);

        void updateToolbarText();
    }

    interface Presenter extends BasePresenter<GetMoreRequestsContract.View> {
        void onBuyRequestsClicked(SkuRowData item);
    }
}

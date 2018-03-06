package com.ashomok.imagetotext.get_more_requests;

import android.content.Context;
import android.support.annotation.Nullable;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.billing.BillingProviderCallback;
import com.ashomok.imagetotext.billing.BillingProviderImpl;
import com.ashomok.imagetotext.billing.model.SkuRowData;
import com.ashomok.imagetotext.utils.NetworkUtils;

import java.util.List;

import javax.inject.Inject;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/2/18.
 */

public class GetMoreRequestsPresenter implements GetMoreRequestsContract.Presenter {

    public static final String TAG = DEV_TAG + GetMoreRequestsPresenter.class.getSimpleName();
    @Nullable
    private GetMoreRequestsContract.View view;

    @Inject
    BillingProviderImpl billingProvider;

    @Inject
    Context context;

    private BillingProviderCallback billingProviderCallback = new BillingProviderCallback() {
        @Override
        public void onPurchasesUpdated() {
        }

        @Override
        public void showError(int stringResId) {
            if (view != null) {
                view.showError(stringResId);
            }
        }

        @Override
        public void showInfo(String message) {
            if (view != null) {
                view.showInfo(message);
            }
        }

        @Override
        public void onSkuRowDataUpdated() {
            updatePaidOption(billingProvider.getSkuRowDataListForInAppPurchases());
        }
    };

    @Inject
    GetMoreRequestsPresenter() {}

    private void updatePaidOption(List<SkuRowData> skuRowDataListForInAppPurchases) {
        //todo
//        if (view != null) {
//            if (skuRowDataListForSubscriptions.size() == 2) {
//                for (SkuRowData item : skuRowDataListForSubscriptions) {
//                    switch (item.getSku()) {
//                        case PREMIUM_MONTHLY_SKU_ID:
//                            view.initPremiumMonthRow(item);
//                            break;
//                        case PREMIUM_YEARLY_SKU_ID:
//                            view.initPremiumYearRow(item);
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            }
//        }
    }


    @Override
    public void onBuyRequestsClicked(SkuRowData item) {
//todo
    }

    @Override
    public void takeView(GetMoreRequestsContract.View getMoreRequestsActivity) {
        view = getMoreRequestsActivity;
        init();
    }

    private void init() {
        billingProvider.setCallback(billingProviderCallback);
        billingProvider.init();

        if (view != null) {
            checkConnection();
        }
    }

    @Override
    public void dropView() {
        view = null;
        billingProvider.destroy();
    }


    private boolean isOnline() {
        return NetworkUtils.isOnline(context);
    }

    private void checkConnection() {
        if (view != null) {
            if (!isOnline()) {
                view.showError(R.string.no_internet_connection);
            }
        }
    }
}

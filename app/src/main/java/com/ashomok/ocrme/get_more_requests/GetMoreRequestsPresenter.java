package com.ashomok.ocrme.get_more_requests;

import android.content.Context;
import android.support.annotation.Nullable;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.billing.BillingProviderCallback;
import com.ashomok.ocrme.billing.BillingProviderImpl;
import com.ashomok.ocrme.billing.model.SkuRowData;
import com.ashomok.ocrme.utils.NetworkUtils;

import java.util.List;

import javax.inject.Inject;

import static com.ashomok.ocrme.billing.BillingProviderImpl.SCAN_IMAGE_REQUESTS_SKU_ID;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/2/18.
 */

public class GetMoreRequestsPresenter implements GetMoreRequestsContract.Presenter {

    public static final String TAG = DEV_TAG + GetMoreRequestsPresenter.class.getSimpleName();
    @Nullable
    private GetMoreRequestsContract.View view;

    BillingProviderImpl billingProvider;

    Context context;

    @Inject
    GetMoreRequestsPresenter(BillingProviderImpl billingProvider,  Context context) {
        this.billingProvider = billingProvider;
        this.context = context;
    }

    private BillingProviderCallback billingProviderCallback = new BillingProviderCallback() {
        @Override
        public void onPurchasesUpdated() {
            if (view != null) {
                view.updateToolbarText();
            }
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

    private void updatePaidOption(List<SkuRowData> skuRowDataListForInAppPurchases) {
        if (view != null) {
            if (skuRowDataListForInAppPurchases.size() == 1) {
                for (SkuRowData item : skuRowDataListForInAppPurchases) {
                    switch (item.getSku()) {
                        case SCAN_IMAGE_REQUESTS_SKU_ID:
                            view.initBuyRequestsRow(item);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onBuyRequestsClicked(SkuRowData data) {
      billingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                data.getSkuType());
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

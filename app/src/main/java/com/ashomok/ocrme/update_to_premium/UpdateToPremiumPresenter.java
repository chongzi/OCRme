package com.ashomok.ocrme.update_to_premium;

import android.content.Context;
import android.support.annotation.Nullable;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.billing.BillingProviderCallback;
import com.ashomok.ocrme.billing.BillingProviderImpl;
import com.ashomok.ocrme.billing.model.SkuRowData;
import com.ashomok.ocrme.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.ashomok.ocrme.billing.BillingProviderImpl.PREMIUM_MONTHLY_SKU_ID;
import static com.ashomok.ocrme.billing.BillingProviderImpl.PREMIUM_YEARLY_SKU_ID;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 1/29/18.
 */

public class UpdateToPremiumPresenter implements UpdateToPremiumContract.Presenter {

    public static final String TAG = DEV_TAG + UpdateToPremiumPresenter.class.getSimpleName();
    @Nullable
    private UpdateToPremiumContract.View view;

    private BillingProviderImpl billingProvider;

    private Context context;

    private BillingProviderCallback billingProviderCallback = new BillingProviderCallback() {
        @Override
        public void onPurchasesUpdated() {
            if (view != null) {
                boolean isPremium = billingProvider.isPremiumMonthlySubscribed()
                        || billingProvider.isPremiumYearlySubscribed();

                view.updateView(isPremium);
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
            updateSkuRows(billingProvider.getSkuRowDataListForSubscriptions());
        }
    };


    @Inject
    UpdateToPremiumPresenter(BillingProviderImpl billingProvider, Context context) {
        this.billingProvider = billingProvider;
        this.context = context;
    }

    /**
     * update sku rows for subscriptions
     *
     * @param skuRowDataListForSubscriptions
     */
    private void updateSkuRows(List<SkuRowData> skuRowDataListForSubscriptions) {
        if (view != null) {
            if (skuRowDataListForSubscriptions.size() == 2) {
                for (SkuRowData item : skuRowDataListForSubscriptions) {
                    switch (item.getSku()) {
                        case PREMIUM_MONTHLY_SKU_ID:
                            view.initPremiumMonthRow(item);
                            break;
                        case PREMIUM_YEARLY_SKU_ID:
                            view.initPremiumYearRow(item);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }


    @Override
    public void takeView(UpdateToPremiumContract.View updateToPremiumActivity) {
        view = updateToPremiumActivity;
        init();
    }

    private void init() {
        billingProvider.setCallback(billingProviderCallback);

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

    @Override
    public void onOneYearClicked(SkuRowData data) {
        if (data != null) {
            if (billingProvider.isPremiumMonthlySubscribed()) {
                // If we already subscribed to monthly premium, launch replace flow
                ArrayList<String> currentSubscriptionSku = new ArrayList<>();
                currentSubscriptionSku.add(PREMIUM_MONTHLY_SKU_ID);
                billingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                        currentSubscriptionSku, data.getSkuType());
            } else {
                billingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                        data.getSkuType());
            }
        }
    }

    @Override
    public void onOneMonthClicked(SkuRowData data) {
        if (data != null) {
            if (billingProvider.isPremiumYearlySubscribed()) {
                // If we already subscribed to yearly premium, launch replace flow
                ArrayList<String> currentSubscriptionSku = new ArrayList<>();
                currentSubscriptionSku.add(PREMIUM_YEARLY_SKU_ID);
                billingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                        currentSubscriptionSku, data.getSkuType());
            } else {
                billingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                        data.getSkuType());
            }
        }
    }
}

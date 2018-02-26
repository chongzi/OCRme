package com.ashomok.imagetotext.update_to_premium;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.billing.BillingProviderCallback;
import com.ashomok.imagetotext.billing.BillingProviderImpl;
import com.ashomok.imagetotext.billing.model.SkuRowData;
import com.ashomok.imagetotext.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.ashomok.imagetotext.billing.BillingProviderImpl.Premium_Monthly_SKU_ID;
import static com.ashomok.imagetotext.billing.BillingProviderImpl.Premium_Yearly_SKU_ID;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 1/29/18.
 */

public class UpdateToPremiumPresenter implements UpdateToPremiumContract.Presenter {

    public static final String TAG = DEV_TAG + UpdateToPremiumPresenter.class.getSimpleName();
    @Nullable
    private UpdateToPremiumContract.View view;

    @Inject
    BillingProviderImpl billingProvider;

    @Inject
    Context context;

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
        public void onSkuRowDataUpdated() {
            updateSkuRows(billingProvider.getSkuRowDataListForSubscriptions());
        }
    };


    @Inject
    UpdateToPremiumPresenter() {
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
                        case Premium_Monthly_SKU_ID:
                            view.initPremiumMonthRow(item);
                            break;
                        case Premium_Yearly_SKU_ID:
                            view.initPremiumYearRow(item);
                            break;
                        default:
                            view.showError(R.string.unknown_error);
                            break;
                    }
                }
            }
        }
    }


    @Override
    public void takeView(UpdateToPremiumContract.View updateToPremiumActivity) {
        view = updateToPremiumActivity;
//        loadData(); //load amount of free requests //todo reduntant
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


    /**
     * Save current amount of free requests
     * <p>
     * Note: In a real application, we recommend you save data in a secure way to
     * prevent tampering.
     * For simplicity in this sample, we simply store the data using a
     * SharedPreferences.
     */
    private void saveData() {
        //todo reduntant
//        SharedPreferences.Editor spe = mActivity.getPreferences(MODE_PRIVATE).edit();
//        spe.putInt("tank", mTank);
//        spe.apply();
//        Log.d(TAG, "Saved data: tank = " + String.valueOf(mTank));
    }

    private void loadData() {
        //todo reduntant
//        SharedPreferences sp = mActivity.getPreferences(MODE_PRIVATE);
//        mTank = sp.getInt("tank", 2);
//        Log.d(TAG, "Loaded data: tank = " + String.valueOf(mTank));
    }

    @Override
    public void onOneYearClicked(SkuRowData data) {
        if (data != null) {
            if (billingProvider.isPremiumMonthlySubscribed()) {
                // If we already subscribed to monthly premium, launch replace flow
                ArrayList<String> currentSubscriptionSku = new ArrayList<>();
                currentSubscriptionSku.add(Premium_Monthly_SKU_ID);
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
                currentSubscriptionSku.add(Premium_Yearly_SKU_ID);
                billingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                        currentSubscriptionSku, data.getSkuType());
            } else {
                billingProvider.getBillingManager().initiatePurchaseFlow(data.getSku(),
                        data.getSkuType());
            }
        }
    }
}

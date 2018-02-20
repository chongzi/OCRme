package com.ashomok.imagetotext.main.billing;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.update_to_premium.billing.BillingManager;
import com.ashomok.imagetotext.update_to_premium.billing.BillingProvider;
import com.ashomok.imagetotext.update_to_premium.model.SkuRowData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

import static com.ashomok.imagetotext.update_to_premium.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 2/14/18.
 */

public class BillingProviderImpl implements BillingProvider {

    private BillingManager mBillingManager;
    private boolean mGoldMonthly;
    private boolean mGoldYearly;
    public static final String goldMonthly_SKU_ID = "one_month_subscription";
    public static final String goldYearly_SKU_ID = "one_year_subscription";
    public static final String TAG = DEV_TAG + BillingProviderImpl.class.getSimpleName();

    private Lazy<BillingProviderCallback> callback;

    @NonNull
    private Activity activity;

    @Inject
    public BillingProviderImpl(@NonNull Activity activity, Lazy<BillingProviderCallback> callback) {
        this.activity = activity;
        this.callback = callback;
        init();
    }

    private void init() {
        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(activity, new UpdateListener());

        if (mBillingManager.getBillingClientResponseCode() > BILLING_MANAGER_NOT_INITIALIZED) {
            onManagerReady(this);
        }

        handleManagerAndUiReady();
    }

    /**
     * Executes query for SKU details at the background thread
     */
    private void handleManagerAndUiReady() {
        // If Billing Manager was successfully initialized - start querying for SKUs
        querySkuDetails();
    }

    //todo do nothing reduntant method?
    /**
     * Queries for in-app and subscriptions SKU details and updates an adapter with new data
     */
    private void querySkuDetails() {
        final List<SkuRowData> dataList = new ArrayList<>();

        // Filling the list with all the data to render subscription rows
        List<String> subscriptionsSkus = new ArrayList<>();
        subscriptionsSkus.add(goldMonthly_SKU_ID);
        subscriptionsSkus.add(goldYearly_SKU_ID);
        addSkuRows(dataList, subscriptionsSkus, BillingClient.SkuType.SUBS, null);
    }

    private void addSkuRows(final List<SkuRowData> inList, List<String> skusList,
                            final @BillingClient.SkuType String billingType,
                            final Runnable executeWhenFinished) {

        getBillingManager().querySkuDetailsAsync(billingType, skusList,
                (responseCode, skuDetailsList) -> {

                    if (responseCode != BillingClient.BillingResponse.OK) {
                        Log.w(TAG, "Unsuccessful query for type: " + billingType
                                + ". Error code: " + responseCode);
                    } else if (skuDetailsList != null && skuDetailsList.size() > 0) {
                        // If we successfully got SKUs - fill all rows
                        for (SkuDetails details : skuDetailsList) {
                            Log.i(TAG, "Adding sku: " + details);
                            inList.add(new SkuRowData(details, billingType));
                        }

                        if (inList.size() == 0) {
                            onBillingError();
                        } else if (inList.size() == 2) {
                            for (SkuRowData item : inList) {
                                //todo do nothing reduntant method?

//                                switch (item.getSku()) {
//                                    case SKU_ID_PREMIUM_MONTHLY:
//                                        initPremiumMonthRow(item);
//                                        break;
//                                    case SKU_ID_PREMIUM_YEARLY:
//                                        initPremiumYearRow(item);
//                                        break;
//                                    default:
//                                        showError(R.string.unknown_error);
//                                        break;
//                                }
                            }
                        }

                    } else {
                        Log.e(TAG, "skuDetailsList is empty");
                        showError(R.string.unknown_error);
                    }

                    if (executeWhenFinished != null) {
                        executeWhenFinished.run();
                    }
                });
    }

    private void onBillingError() {
        int billingResponseCode = getBillingManager()
                .getBillingClientResponseCode();

        switch (billingResponseCode) {
            case BillingClient.BillingResponse.OK:
                // If manager was connected successfully, then show no SKUs error
                showError(R.string.error_no_skus);
                break;
            case BillingClient.BillingResponse.BILLING_UNAVAILABLE:
                showError(R.string.error_billing_unavailable);
                break;
            default:
                showError(R.string.error_billing_unavailable);
        }
    }

    private void showError(@StringRes int stringResId) {
        //todo use lisnener to return result
    }

    //todo simplyfy-refactoring needed

    /**
     * Notifies the fragment that billing manager is ready and provides a BillingProviders
     * instance to access it
     */
    public void onManagerReady(BillingProvider billingProvider) {
        handleManagerAndUiReady();
    }

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    @Override
    public boolean isPremiumMonthlySubscribed() {
        return mGoldMonthly;
    }

    @Override
    public boolean isPremiumYearlySubscribed() {
        return mGoldYearly;
    }

    /**
     * Handler to billing updates
     */
    public class UpdateListener implements BillingManager.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
            onBillingManagerSetupFinished();
        }

        @Override
        public void onConsumeFinished(String token, @BillingClient.BillingResponse int result) {
            Log.d(TAG, "Consumption finished. Purchase token: " + token + ", result: " + result);
            //todo redintant - we consume nothing

//            // Note: We know this is the SKU_GAS, because it's the only one we consume, so we don't
//            // check if token corresponding to the expected sku was consumed.
//            // If you have more than one sku, you probably need to validate that the token matches
//            // the SKU you expect.
//            // It could be done by maintaining a map (updating it every time you call consumeAsync)
//            // of all tokens into SKUs which were scheduled to be consumed and then looking through
//            // it here to check which SKU corresponds to a consumed token.
//            if (result == BillingClient.BillingResponse.OK) {
//                // Successfully consumed, so we apply the effects of the item in our
//                // game world's logic, which in our case means filling the gas tank a bit
//                Log.d(TAG, "Consumption successful. Provisioning.");
//                mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
//                saveData();
//                mActivity.alert(R.string.alert_fill_gas, mTank);
//            } else {
//                mActivity.alert(R.string.alert_error_consuming, result);
//            }
//
//            mActivity.showRefreshedUi();
//            Log.d(TAG, "End consumption flow.");
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {
            mGoldMonthly = false;
            mGoldYearly = false;

            for (Purchase purchase : purchaseList) {
                switch (purchase.getSku()) {
                    case goldMonthly_SKU_ID:
                        mGoldMonthly = true;
                        break;
                    case goldYearly_SKU_ID:
                        mGoldYearly = true;
                        break;
                }
            }

            callback.get().onPurchasesUpdated();
        }
    }

    private void onBillingManagerSetupFinished() {
        querySkuDetails();
    }
}

package com.ashomok.imagetotext.billing;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.annimon.stream.Stream;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.billing.model.SkuRowData;
import com.ashomok.imagetotext.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 2/14/18.
 */

public class BillingProviderImpl implements BillingProvider {

    public static final String AVAILABLE_OCR_REQUESTS_COUNT_TAG = "availableOcrRequestsCount";
    private BillingManager mBillingManager;
    private boolean mGoldMonthly;
    private boolean mGoldYearly;
    public static final String Premium_Monthly_SKU_ID = "one_month_subscription";
    public static final String Premium_Yearly_SKU_ID = "one_year_subscription";
    public static final String Scan_Image_requests_SKU_ID = "5_scan_image_requests";
    private static final int Scan_Image_Requests_MAX = 5;
    public static final String TAG = DEV_TAG + BillingProviderImpl.class.getSimpleName();

    private List<SkuRowData> skuRowDataList = new ArrayList<>();

    @Nullable
    BillingProviderCallback callback;

    @NonNull
    private Activity activity;

    private int availableOcrRequests;

    @Inject
    SharedPreferences sharedPreferences;


    @Inject
    public BillingProviderImpl(@NonNull Activity activity) {
        this.activity = activity;
    }

    public void init() {
        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(activity, new UpdateListener());
        loadData();
    }

    public void setCallback(@Nullable BillingProviderCallback callback) {
        this.callback = callback;
    }
    public int getAvailableOcrRequests() {
        return availableOcrRequests;
    }

    public List<SkuRowData> getSkuRowDataList() {
        return skuRowDataList;
    }

    public List<SkuRowData> getSkuRowDataListForSubscriptions() {
        Log.d(TAG, "");
        return Stream.of(skuRowDataList)
                .filter(i -> i.getSkuType().equals(BillingClient.SkuType.SUBS))
                .toList();
    }

    private void updatePurchaseData() {
        List<String> subscriptionsSkus = new ArrayList<>();
        subscriptionsSkus.add(Premium_Monthly_SKU_ID);
        subscriptionsSkus.add(Premium_Yearly_SKU_ID);

        processSkuRows(
                skuRowDataList, subscriptionsSkus, BillingClient.SkuType.SUBS,
                new Runnable() {
                    @Override
                    public void run() {
                        List<String> inAppSkus = new ArrayList<>();
                        inAppSkus.add(Scan_Image_requests_SKU_ID);
                        processSkuRows(skuRowDataList, inAppSkus, BillingClient.SkuType.INAPP, null);
                    }
                });
    }

    private void processSkuRows(List<SkuRowData> inList, List<String> skusList,
                                final @BillingClient.SkuType String billingType,
                                final Runnable executeWhenFinished) {
        getBillingManager().querySkuDetailsAsync(billingType, skusList,
                (responseCode, skuDetailsList) -> {

                    if (responseCode != BillingClient.BillingResponse.OK) {
                        Log.e(TAG, "Unsuccessful query for type: " + billingType
                                + ". Error code: " + responseCode);
                        onBillingError();
                    } else if (skuDetailsList == null || skuDetailsList.size() == 0) {
                        Log.e(TAG, "skuDetailsList is empty");
                        onBillingError();
                    } else {
                        // If we successfully got SKUs - fill all rows
                        for (SkuDetails details : skuDetailsList) {
                            Log.i(TAG, "Adding sku: " + details);
                            inList.add(new SkuRowData(details, billingType));
                        }
                        if (inList.size() == 0) {
                            onBillingError();
                        }
                        if (callback != null) {
                            callback.onSkuRowDataUpdated();
                        }
                    }

                    if (executeWhenFinished != null) {
                        executeWhenFinished.run();
                    }
                });
    }

    public void useOcrRequest() {
        availableOcrRequests--;
        saveData();
        Log.d(TAG, "ocr requests count: " + availableOcrRequests);
    }

    private void saveData() {
        sharedPreferences.edit().putInt(AVAILABLE_OCR_REQUESTS_COUNT_TAG, availableOcrRequests)
                .apply();
    }

    private void loadData() {
       availableOcrRequests =
               sharedPreferences.getInt(AVAILABLE_OCR_REQUESTS_COUNT_TAG, Scan_Image_Requests_MAX);
    }

    public void destroy() {
        if (mBillingManager != null) {
            mBillingManager.destroy();
        }
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
                showError(R.string.unknown_error);
        }
    }

    private void showError(@StringRes int stringResId) {
        if (callback != null) {
            callback.showError(stringResId);
        }
    }

    private void showInfo(String message) {
        if (callback != null) {
            callback.showInfo(message);
        }
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
    public boolean isOcrRequestsAvailable() {
        return availableOcrRequests > 0;
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
            updatePurchaseData();
        }

        @Override
        public void onConsumeFinished(String token, @BillingClient.BillingResponse int result) {
            Log.d(TAG, "Consumption finished. Purchase token: " + token + ", result: " + result);
            // Note: We know this is the Scan_Image_requests_SKU_ID, because it's the only one we
            // consume, so we don't
            // check if token corresponding to the expected sku was consumed.
            // If you have more than one sku, you probably need to validate that the token matches
            // the SKU you expect.
            // It could be done by maintaining a map (updating it every time you call consumeAsync)
            // of all tokens into SKUs which were scheduled to be consumed and then looking through
            // it here to check which SKU corresponds to a consumed token.
            if (result == BillingClient.BillingResponse.OK) {
                // Successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                availableOcrRequests = Scan_Image_Requests_MAX;
                saveData();

                String message = activity.getString(R.string.you_get_ocr_requests,
                        String.valueOf(Scan_Image_Requests_MAX));
                showInfo(message);
            } else {
                showError(R.string.unknown_error);
            }

            if (callback != null) {
                callback.onPurchasesUpdated();
            }
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {
            mGoldMonthly = false;
            mGoldYearly = false;

            for (Purchase purchase : purchaseList) {
                switch (purchase.getSku()) {
                    case Premium_Monthly_SKU_ID:
                        mGoldMonthly = true;
                        break;
                    case Premium_Yearly_SKU_ID:
                        mGoldYearly = true;
                        break;
                    case Scan_Image_requests_SKU_ID:
                        // We should consume the purchase and fill up the requests once it was consumed
                        getBillingManager().consumeAsync(purchase.getPurchaseToken());
                        break;
                }
            }
            if (callback != null) {
                callback.onPurchasesUpdated();
            }
        }
    }
}

package com.ashomok.ocrme.billing;

/**
 * Created by iuliia on 2/19/18.
 */

public interface BillingProviderCallback {
    void onPurchasesUpdated();
    void showError(int stringResId);
    void showInfo(String message);
    void onSkuRowDataUpdated();
}

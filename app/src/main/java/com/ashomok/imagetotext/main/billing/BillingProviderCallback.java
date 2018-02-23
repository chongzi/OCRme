package com.ashomok.imagetotext.main.billing;

/**
 * Created by iuliia on 2/19/18.
 */

public interface BillingProviderCallback {
    void onPurchasesUpdated();
    void showError(int stringResId);
}

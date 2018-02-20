package com.ashomok.imagetotext.update_to_premium;

import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.ashomok.imagetotext.update_to_premium.billing.BillingManager;

import java.util.List;

import javax.inject.Inject;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 1/29/18.
 */

public class UpdateToPremiumPresenter implements UpdateToPremiumContract.Presenter {

    public static final String TAG = DEV_TAG + UpdateToPremiumPresenter.class.getSimpleName();
    @Nullable
    private UpdateToPremiumContract.View view;

    private UpdateListener mUpdateListener;

    // Tracks if we currently own subscriptions SKUs
    private boolean mGoldMonthly;
    private boolean mGoldYearly;
    public static final String goldMonthly_SKU_ID = "one_month_subscription";
    public static final String goldYearly_SKU_ID = "one_year_subscription";

    @Inject
    UpdateToPremiumPresenter() {
    }

    @Override
    public void takeView(UpdateToPremiumContract.View updateToPremiumActivity) {
        view = updateToPremiumActivity;
        mUpdateListener = new UpdateListener();
//        loadData(); //load amount of free requests //todo reduntant
    }

    @Override
    public void dropView() {
        view = null;
    }

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public boolean isGoldMonthlySubscribed() {
        return mGoldMonthly;
    }

    public boolean isGoldYearlySubscribed() {
        return mGoldYearly;
    }

    /**
     * Handler to billing updates
     */
    public class UpdateListener implements BillingManager.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
            if (view != null) {
                view.onBillingManagerSetupFinished();
            }
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

            view.showRefreshedUi();
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
}

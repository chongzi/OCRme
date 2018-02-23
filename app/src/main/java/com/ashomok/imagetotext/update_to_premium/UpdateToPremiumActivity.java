package com.ashomok.imagetotext.update_to_premium;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetails;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.billing.BillingManager;
import com.ashomok.imagetotext.billing.BillingProvider;
import com.ashomok.imagetotext.billing.model.SkuRowData;
import com.ashomok.imagetotext.utils.InfoSnackbarUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.ashomok.imagetotext.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 1/29/18.
 */

public class UpdateToPremiumActivity extends RxAppCompatActivity
        implements View.OnClickListener, UpdateToPremiumContract.View, BillingProvider {

    private static final String TAG = DEV_TAG + UpdateToPremiumActivity.class.getSimpleName();
    @Inject
    UpdateToPremiumPresenter mPresenter;

    @Inject
    FeaturesListAdapter featuresListAdapter;

    private BillingManager mBillingManager;
    private View mRootView;
    public static final String SKU_ID_PREMIUM_MONTHLY = "one_month_subscription";
    public static final String SKU_ID_PREMIUM_YEARLY = "one_year_subscription";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this); //todo or extends daggerappcompat activity instaead
        setContentView(R.layout.activity_update_to_premium);
        mRootView = findViewById(android.R.id.content);
        if (mRootView == null) {
            mRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        }
        initToolbar();
        initFeaturesList();
        mPresenter.takeView(this);

        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, mPresenter.getUpdateListener()); //todo inject

        if (mBillingManager.getBillingClientResponseCode() > BILLING_MANAGER_NOT_INITIALIZED) {
            onManagerReady(this);
        }

        handleManagerAndUiReady();
    }

    /**
     * Executes query for SKU details at the background thread
     */
    private void handleManagerAndUiReady() {
        //todo called tvice - fix it
        Log.d(TAG, "handleManagerAndUiReady called");
        // If Billing Manager was successfully initialized - start querying for SKUs
//        setWaitScreen(true);
        querySkuDetails();
    }

    /**
     * Queries for in-app and subscriptions SKU details and updates an adapter with new data
     */
    private void querySkuDetails() {
        final List<SkuRowData> dataList = new ArrayList<>();

        // Filling the list with all the data to render subscription rows
        List<String> subscriptionsSkus = new ArrayList<>();
        subscriptionsSkus.add(SKU_ID_PREMIUM_MONTHLY);
        subscriptionsSkus.add(SKU_ID_PREMIUM_YEARLY);
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
                            displayBillingError();
                        } else if (inList.size() == 2) {
                            for (SkuRowData item : inList) {
                                switch (item.getSku()) {
                                    case SKU_ID_PREMIUM_MONTHLY:
                                        initPremiumMonthRow(item);
                                        break;
                                    case SKU_ID_PREMIUM_YEARLY:
                                        initPremiumYearRow(item);
                                        break;
                                    default:
                                        showError(R.string.unknown_error);
                                        break;
                                }
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

    @SuppressLint("DefaultLocale")
    private void initPremiumYearRow(SkuRowData item) {
        View oneYearLayout = findViewById(R.id.one_year_subscription);
        TextView oneYearPrice = findViewById(R.id.one_year_price);
        oneYearPrice.setText(item.getPrice());
        TextView pricePerMonth = findViewById(R.id.price_per_month);

        String subTitle = getString(R.string.price_per_month,
                item.getPriceCurrencyCode(),
                String.format("%.2f", (double) item.getPriceAmountMicros() / 12000000));
        pricePerMonth.setText(subTitle);
        oneYearLayout.setOnClickListener(view -> onOneYearClicked(item));
    }

    private void initPremiumMonthRow(SkuRowData item) {
        View oneMonthLayout = findViewById(R.id.one_month_subscription);
        TextView oneMonthPrice = findViewById(R.id.one_month_price);
        oneMonthPrice.setText(item.getPrice());
        oneMonthLayout.setOnClickListener(view -> onOneMonthClicked(item));
    }

    private void onOneYearClicked(SkuRowData data) {
        if (data != null) {
            if (isPremiumMonthlySubscribed()) {
                // If we already subscribed to monthly premium, launch replace flow
                ArrayList<String> currentSubscriptionSku = new ArrayList<>();
                currentSubscriptionSku.add(SKU_ID_PREMIUM_MONTHLY);
                getBillingManager().initiatePurchaseFlow(data.getSku(),
                        currentSubscriptionSku, data.getSkuType());
            } else {
                getBillingManager().initiatePurchaseFlow(data.getSku(),
                        data.getSkuType());
            }
        }
    }

    private void onOneMonthClicked(SkuRowData data) {
        if (data != null) {
            if (isPremiumYearlySubscribed()) {
                // If we already subscribed to yearly premium, launch replace flow
                ArrayList<String> currentSubscriptionSku = new ArrayList<>();
                currentSubscriptionSku.add(SKU_ID_PREMIUM_YEARLY);
                getBillingManager().initiatePurchaseFlow(data.getSku(),
                        currentSubscriptionSku, data.getSkuType());
            } else {
                getBillingManager().initiatePurchaseFlow(data.getSku(),
                        data.getSkuType());
            }
        }
    }

    private void displayBillingError() {
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


    /**
     * Notifies the fragment that billing manager is ready and provides a BillingProviders
     * instance to access it
     */
    public void onManagerReady(BillingProvider billingProvider) {
        handleManagerAndUiReady();
    }

    public void onBillingManagerSetupFinished() {
        onManagerReady(this);
    }

    @Override
    public void showRefreshedUi() {
        Log.d(TAG, "Looks like purchases list might have been updated - refreshing the UI");
        //todo
//        if (mAdapter != null) {
//            mAdapter.notifyDataSetChanged();
//        }

    }

    /**
     * Update UI to reflect model
     */
    @UiThread
    private void updateUi() {
        Log.d(TAG, "Updating the UI. Thread: " + Thread.currentThread().getName());

        //todo
//        // Update car's color to reflect premium status or lack thereof
//        setImageResourceWithTestTag(mCarImageView, isPremiumPurchased() ? R.drawable.premium
//                : R.drawable.free);
//
//        if (isPremiumMonthlySubscribed() || isPremiumYearlySubscribed()) {
//            mCarImageView.setBackgroundColor(ContextCompat.getColor(this, R.color.gold));
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Note: We query purchases in onResume() to handle purchases completed while the activity
        // is inactive. For example, this can happen if the activity is destroyed during the
        // purchase flow. This ensures that when the activity is resumed it reflects the user's
        // current purchases.
        if (mBillingManager != null
                && mBillingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK) {
            mBillingManager.queryPurchases();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying");
        if (mBillingManager != null) {
            mBillingManager.destroy();
        }
        super.onDestroy();
    }

//    private void initBillingClient() {
//        mBillingClient = BillingClient.newBuilder(this).setListener(this).build();
//        mBillingClient.startConnection(new BillingClientStateListener() {
//            @Override
//            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
//                if (billingResponseCode == BillingClient.BillingResponse.OK) {
//                    // The billing client is ready. You can query purchases here.
//                    //todo
//                }
//            }
//            @Override
//            public void onBillingServiceDisconnected() {
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//                //todo
//            }
//        });
//    }

    private void initFeaturesList() {
        RecyclerView recyclerView = findViewById(R.id.premium_features_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager recentlyChosenLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recentlyChosenLayoutManager);

        recyclerView.setAdapter(featuresListAdapter);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Show CollapsingToolbarLayout Title ONLY when collapsed
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getResources().getString(R.string.update_to_premium));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
//todo
    }

    @Override
    public void showError(int errorMessageRes) {
        InfoSnackbarUtil.showError(errorMessageRes, mRootView);
    }

    @Override
    public void showInfo(int infoMessageRes) {
        InfoSnackbarUtil.showInfo(infoMessageRes, mRootView);
    }

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }


    @Override
    public boolean isPremiumMonthlySubscribed() {
        return mPresenter.isGoldMonthlySubscribed();
    }

    @Override
    public boolean isPremiumYearlySubscribed() {
        return mPresenter.isGoldYearlySubscribed();
    }
}


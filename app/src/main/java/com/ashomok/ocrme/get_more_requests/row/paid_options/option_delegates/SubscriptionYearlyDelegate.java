package com.ashomok.ocrme.get_more_requests.row.paid_options.option_delegates;

import android.content.Context;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.billing.BillingProviderImpl;
import com.ashomok.ocrme.billing.model.SkuRowData;
import com.ashomok.ocrme.get_more_requests.row.paid_options.PaidOptionRowViewHolder;
import com.ashomok.ocrme.get_more_requests.row.paid_options.UiManagingDelegate;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.ashomok.ocrme.billing.BillingProviderImpl.PREMIUM_MONTHLY_SKU_ID;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

public class SubscriptionYearlyDelegate extends UiManagingDelegate {
    public static final String TAG = DEV_TAG + SubscriptionYearlyDelegate.class.getSimpleName();

    @Inject
    public SubscriptionYearlyDelegate(BillingProviderImpl billingProvider, Context context) {
        super(billingProvider, context);
    }

    @Override
    public void onRowClicked(SkuRowData data) {
        if (data != null) {
            if (getBillingProvider().isPremiumMonthlySubscribed()) {
                // If we already subscribed to monthly premium, launch replace flow
                ArrayList<String> currentSubscriptionSku = new ArrayList<>();
                currentSubscriptionSku.add(PREMIUM_MONTHLY_SKU_ID);
                getBillingProvider().getBillingManager().initiatePurchaseFlow(data.getSku(),
                        currentSubscriptionSku, data.getSkuType());
            } else {
                super.onRowClicked(data);
            }
        }
    }

    @Override
    public void onBindViewHolder(SkuRowData data, PaidOptionRowViewHolder holder) {
        super.onBindViewHolder(data, holder);

        holder.getTitle().setText(getContext().getResources().getString(R.string.one_year_premium));
        holder.getSubtitleTop().setText(getContext().getResources().getString(R.string.unlimited_requests));
    }
}

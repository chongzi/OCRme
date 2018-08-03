package com.ashomok.ocrme.get_more_requests.row.paid_options.option_delegates;

import android.content.Context;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.billing.BillingProviderImpl;
import com.ashomok.ocrme.billing.model.SkuRowData;
import com.ashomok.ocrme.get_more_requests.row.paid_options.PaidOptionRowViewHolder;
import com.ashomok.ocrme.get_more_requests.row.paid_options.UiPaidOptionManagingDelegate;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.ashomok.ocrme.billing.BillingProviderImpl.PREMIUM_YEARLY_SKU_ID;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

public class SubscriptionMonthlyDelegate extends UiPaidOptionManagingDelegate {
    public static final String TAG = DEV_TAG + SubscriptionMonthlyDelegate.class.getSimpleName();

    @Inject
    public SubscriptionMonthlyDelegate(BillingProviderImpl billingProvider, Context context) {
        super(billingProvider, context);
    }

    @Override
    public void onRowClicked(SkuRowData data) {
        if (data != null) {
            if (getBillingProvider().isPremiumYearlySubscribed()) {
                // If we already subscribed to yearly premium, launch replace flow
                ArrayList<String> currentSubscriptionSku = new ArrayList<>();
                currentSubscriptionSku.add(PREMIUM_YEARLY_SKU_ID);
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

        holder.getTitle().setText(getContext().getResources().getString(R.string.one_month_premium));
        holder.getSubtitleTop().setText(getContext().getResources().getString(R.string.unlimited_requests));
    }
}

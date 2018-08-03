package com.ashomok.ocrme.get_more_requests.row.paid_options.option_delegates;

import android.content.Context;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.billing.BillingProviderImpl;
import com.ashomok.ocrme.billing.model.SkuRowData;
import com.ashomok.ocrme.get_more_requests.row.paid_options.PaidOptionRowViewHolder;
import com.ashomok.ocrme.get_more_requests.row.paid_options.UiPaidOptionManagingDelegate;

import javax.inject.Inject;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

public class Batch5Delegate extends UiPaidOptionManagingDelegate {
    public static final String TAG = DEV_TAG + Batch5Delegate.class.getSimpleName();

    @Inject
    public Batch5Delegate(BillingProviderImpl billingProvider, Context context) {
        super(billingProvider, context);
    }

    @Override
    public void onBindViewHolder(SkuRowData data, PaidOptionRowViewHolder holder) {
        super.onBindViewHolder(data, holder);

        holder.getTitle().setText(getContext().getResources().getString(R.string.buy_5_requests));
    }
}
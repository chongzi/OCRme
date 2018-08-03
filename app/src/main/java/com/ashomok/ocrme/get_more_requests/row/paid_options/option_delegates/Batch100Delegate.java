package com.ashomok.ocrme.get_more_requests.row.paid_options.option_delegates;

import android.content.Context;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.billing.BillingProviderImpl;
import com.ashomok.ocrme.billing.model.SkuRowData;
import com.ashomok.ocrme.get_more_requests.row.paid_options.PaidOptionRowViewHolder;
import com.ashomok.ocrme.get_more_requests.row.paid_options.UiPaidOptionManagingDelegate;

import javax.inject.Inject;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

public class Batch100Delegate extends UiPaidOptionManagingDelegate {
    public static final String TAG = DEV_TAG + Batch100Delegate.class.getSimpleName();

    @Inject
    public Batch100Delegate(BillingProviderImpl billingProvider, Context context) {
        super(billingProvider, context);
    }

    @Override
    public void onBindViewHolder(SkuRowData data, PaidOptionRowViewHolder holder) {
        super.onBindViewHolder(data, holder);

        holder.getTitle().setText(getContext().getResources().getString(R.string.buy_100_requests));

        String subTitle = getContext().getResources().getString(R.string.price_per_1_in_100,
                data.getPriceCurrencyCode(),
                String.format("%.2f", (double) data.getPriceAmountMicros() / 100000000));

        holder.getSubtitleBottom().setText(subTitle);

    }
}
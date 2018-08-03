// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.ashomok.ocrme.get_more_requests.row.paid_options;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.ashomok.ocrme.OcrRequestsCounter;
import com.ashomok.ocrme.R;
import com.ashomok.ocrme.billing.BillingProviderCallback;
import com.ashomok.ocrme.billing.BillingProviderImpl;
import com.ashomok.ocrme.billing.model.SkuRowData;
import com.ashomok.ocrme.get_more_requests.GetMoreRequestsActivity;
import com.ashomok.ocrme.get_more_requests.GetMoreRequestsContract;
import com.ashomok.ocrme.get_more_requests.row.free_options.PromoRowFreeOptionData;
import com.ashomok.ocrme.get_more_requests.row.free_options.option_delegates.FreeOptionRowViewHolder;

import java.util.List;

/**
 * Implementations of this abstract class are responsible to render UI and handle user actions for
 * promo rows to render RecyclerView
 */

public abstract class UiManagingDelegate {

    private BillingProviderImpl billingProvider;

    private Context context;

    public UiManagingDelegate(BillingProviderImpl billingProvider,
                              Context context) {
        this.billingProvider = billingProvider;
        this.context = context;
    }

    protected BillingProviderImpl getBillingProvider() {
        return billingProvider;
    }

    public Context getContext() {
        return context;
    }

    public void onRowClicked(SkuRowData data){
        getBillingProvider().getBillingManager().initiatePurchaseFlow(data.getSku(),
                data.getSkuType());
    }

    public void onBindViewHolder(SkuRowData data, PaidOptionRowViewHolder holder) {
        holder.getPrice().setText(data.getPrice());
    }
}

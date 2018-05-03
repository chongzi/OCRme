/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ashomok.ocrme.billing.model;

import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.SkuDetails;
/**
 * A model for Sku row
 */
public class SkuRowData {
    private String sku;
    private String title;
    private String price;
    private String description;

    private String priceCurrencyCode;

    private long priceAmountMicros;
    private @SkuType
    String billingType;

    public SkuRowData(SkuDetails details,
                      @SkuType String billingType) {
        this.sku = details.getSku();
        this.title = details.getTitle();
        this.price = details.getPrice();
        this.description = details.getDescription();
        this.priceCurrencyCode = details.getPriceCurrencyCode();
        priceAmountMicros = details.getPriceAmountMicros();

        this.billingType = billingType;
    }

    public SkuRowData(String title) {
        this.title = title;
    }

    public String getSku() {
        return sku;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public long getPriceAmountMicros() {
        return priceAmountMicros;
    }

    public String getPriceCurrencyCode() {
        return priceCurrencyCode;
    }

    public @SkuType
    String getSkuType() {
        return billingType;
    }
}

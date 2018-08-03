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
package com.ashomok.ocrme.get_more_requests.row.paid_options;


import com.ashomok.ocrme.billing.model.SkuRowData;

import java.util.Map;

import javax.inject.Inject;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * This factory is responsible to finding the appropriate delegate for Ui rendering and calling
 * corresponding method on it.
 */
public class UiDelegatesFactory {

    public static final String TAG = DEV_TAG + UiDelegatesFactory.class.getSimpleName();

    private final Map<String, UiPaidOptionManagingDelegate> uiDelegates;

    @Inject
    public UiDelegatesFactory(Map<String, UiPaidOptionManagingDelegate> uiDelegates) {
        this.uiDelegates = uiDelegates;
    }

    public void onButtonClicked(SkuRowData data) {
        uiDelegates.get(data.getSku()).onRowClicked(data);
    }

    public void onBindViewHolder(SkuRowData data, PaidOptionRowViewHolder holder) {
        uiDelegates.get(data.getSku()).onBindViewHolder(data, holder);
    }

}

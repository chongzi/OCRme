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
package com.ashomok.ocrme.get_more_requests.row.free_options;

import java.util.Map;

import javax.inject.Inject;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * This factory is responsible to finding the appropriate delegate for Ui rendering and calling
 * corresponding method on it.
 */
public class UiDelegatesFactory {

    public static final String TAG = DEV_TAG + UiDelegatesFactory.class.getSimpleName();

    private final Map<String, UiFreeOptionManagingDelegate> uiDelegates;

    @Inject
    public UiDelegatesFactory(Map<String, UiFreeOptionManagingDelegate> uiDelegates) {
        this.uiDelegates = uiDelegates;
    }

    public void onBindViewHolder(PromoRowFreeOptionData data, FreeOptionRowViewHolder holder) {
        uiDelegates.get(data.getId()).onBindViewHolder(data, holder);
    }

    public void onButtonClicked(PromoRowFreeOptionData data) {
        uiDelegates.get(data.getId()).onRowClicked();
    }
}

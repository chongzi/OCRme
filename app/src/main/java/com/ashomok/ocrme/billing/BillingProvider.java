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
package com.ashomok.ocrme.billing;


import com.ashomok.ocrme.billing.BillingManager;
import com.ashomok.ocrme.billing.model.SkuRowData;

import java.util.List;

/**
 * An interface that provides an access to BillingLibrary methods
 */
public interface BillingProvider {
    BillingManager getBillingManager();
    boolean isPremiumMonthlySubscribed();
    boolean isPremiumYearlySubscribed();

    List<SkuRowData> getSkuRowDataList();
    List<SkuRowData> getSkuRowDataListForSubscriptions();
    List<SkuRowData> getSkuRowDataListForInAppPurchases();
}


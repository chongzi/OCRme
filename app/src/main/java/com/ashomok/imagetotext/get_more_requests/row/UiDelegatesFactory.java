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
package com.ashomok.imagetotext.get_more_requests.row;

import com.ashomok.imagetotext.get_more_requests.row.task_delegates.FollowUsOnFbDelegate;
import com.ashomok.imagetotext.get_more_requests.row.task_delegates.LoginToSystemDelegate;
import com.ashomok.imagetotext.get_more_requests.row.task_delegates.WatchVideoDelegate;
import com.ashomok.imagetotext.get_more_requests.row.task_delegates.WriteFeedbackDelegate;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * This factory is responsible to finding the appropriate delegate for Ui rendering and calling
 * corresponding method on it.
 */
public class UiDelegatesFactory {
    private final Map<String, UiManagingDelegate> uiDelegates;

    @Inject
    public UiDelegatesFactory() {
        uiDelegates = new HashMap<>();
        uiDelegates.put(WatchVideoDelegate.ID, new WatchVideoDelegate());
        uiDelegates.put(LoginToSystemDelegate.ID, new LoginToSystemDelegate());
        uiDelegates.put(WriteFeedbackDelegate.ID, new WriteFeedbackDelegate());
        uiDelegates.put(FollowUsOnFbDelegate.ID, new FollowUsOnFbDelegate());
    }

    public void onBindViewHolder(PromoRowData data, RowViewHolder holder) {
        uiDelegates.get(data.getId()).onBindViewHolder(data, holder);
    }

    public void onButtonClicked(PromoRowData data) {
        uiDelegates.get(data.getId()).onRowClicked(data);
    }
}

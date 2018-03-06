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
package com.ashomok.imagetotext.get_more_requests.row;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.ashomok.imagetotext.R;

import javax.inject.Inject;

/**
 * Implementations of this abstract class are responsible to render UI and handle user actions for
 * promo rows to render RecyclerView
 */
public abstract class UiManagingDelegate {

    @Inject
    Context context;

    @Inject
    public UiManagingDelegate() {}

    public void onBindViewHolder(PromoRowData item, RowViewHolder holder) {
        holder.icon.setImageDrawable(context.getResources().getDrawable(item.getDrawableIconId()));
        holder.title.setText(item.getTitleStringId());
        if (item.isSubtitleExists()) {
            holder.subtitle.setVisibility(View.VISIBLE);
            holder.subtitle.setText(item.getSubtitleStringId());
        } else {
            holder.subtitle.setVisibility(View.INVISIBLE);
        }

        holder.isDone.setVisibility(item.isDone ? View.VISIBLE : View.GONE);
        holder.requestsCost.setText(context.getString(
                R.string.requests_cost, String.valueOf(item.getRequestsCost())));
    }

    //todo replase by sneakbar
    protected void showTaskIsDoneToast() {
        Toast.makeText(context,
                R.string.task_is_done, Toast.LENGTH_SHORT).show();
    }

    /**
     * task may be done and not available any more
     * @return
     */
    public boolean isTaskAvailable() {
        return true;
    }

    public void onRowClicked(PromoRowData data){
        if (isTaskAvailable()){
            startTask();
        } else {
            showTaskIsDoneToast();
        }
    }

    protected abstract void startTask();
}

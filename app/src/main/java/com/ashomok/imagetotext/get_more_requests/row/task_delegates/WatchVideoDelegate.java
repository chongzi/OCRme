package com.ashomok.imagetotext.get_more_requests.row.task_delegates;

import android.util.Log;

import com.ashomok.imagetotext.get_more_requests.row.PromoRowData;
import com.ashomok.imagetotext.get_more_requests.row.RowViewHolder;
import com.ashomok.imagetotext.get_more_requests.row.UiManagingDelegate;

import javax.inject.Inject;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/5/18.
 */

public class WatchVideoDelegate extends UiManagingDelegate {
    public static final String TAG = DEV_TAG + WatchVideoDelegate.class.getSimpleName();
    public static final String ID = "watch_video";

    @Inject
    public WatchVideoDelegate(){}

    @Override
    public void onBindViewHolder(PromoRowData data, RowViewHolder holder) {
        super.onBindViewHolder(data, holder);
        //todo update view if not avalible
    }


    @Override
    protected void startTask() {
        Log.d(TAG, "onStartTask");
        //todo run video ads
    }
}

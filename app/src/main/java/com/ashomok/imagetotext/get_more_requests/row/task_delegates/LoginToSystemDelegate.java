package com.ashomok.imagetotext.get_more_requests.row.task_delegates;

import android.content.Context;
import android.util.Log;

import com.ashomok.imagetotext.get_more_requests.row.PromoRowData;
import com.ashomok.imagetotext.get_more_requests.row.RowViewHolder;
import com.ashomok.imagetotext.get_more_requests.row.UiManagingDelegate;

import javax.inject.Inject;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/6/18.
 */

public class LoginToSystemDelegate extends UiManagingDelegate {
    public static final String TAG = DEV_TAG + LoginToSystemDelegate.class.getSimpleName();
    public static final String ID = "login_to_system";

    public LoginToSystemDelegate(Context context){
        super(context);
    }

    @Override
    public void onBindViewHolder(PromoRowData data, RowViewHolder holder) {
        super.onBindViewHolder(data, holder);
        //todo update view if not avalible
    }

    @Override
    protected void startTask() {
        Log.d(TAG, "onstartTask");
        //todo run video ads
    }

    @Override
    public boolean isTaskAvailable() {
        //todo
        return true;
    }

}

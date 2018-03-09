package com.ashomok.imagetotext.get_more_requests.row.task_delegates;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ashomok.imagetotext.OcrRequestsCounter;
import com.ashomok.imagetotext.Settings;
import com.ashomok.imagetotext.firebaseUiAuth.BaseLoginActivity;
import com.ashomok.imagetotext.get_more_requests.GetMoreRequestsActivity;
import com.ashomok.imagetotext.get_more_requests.row.PromoRowData;
import com.ashomok.imagetotext.get_more_requests.row.RowViewHolder;
import com.ashomok.imagetotext.get_more_requests.row.UiManagingDelegate;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/6/18.
 */

public class LoginToSystemDelegate extends UiManagingDelegate {
    public static final String TAG = DEV_TAG + LoginToSystemDelegate.class.getSimpleName();
    public static final String ID = "login_to_system";
    private static final String LOGIN_TO_SYSTEM_DONE_TAG = "LOGIN_TO_SYSTEM_DONE0";
    private final OcrRequestsCounter ocrRequestsCounter;

    private BaseLoginActivity activity;
    private SharedPreferences sharedPreferences;

    @Inject
    public LoginToSystemDelegate(GetMoreRequestsActivity activity, OcrRequestsCounter ocrRequestsCounter,
                                 SharedPreferences sharedPreferences){
        super(activity);
        this.ocrRequestsCounter = ocrRequestsCounter;
        this.activity = activity;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void onBindViewHolder(PromoRowData data, RowViewHolder holder) {
        super.onBindViewHolder(data, holder);
    }

    @Override
    protected void startTask() {
        Log.d(TAG, "onstartTask");
        saveData();
        activity.signIn();

        onTaskDone(ocrRequestsCounter);
    }

    @Override
    public boolean isTaskAvailable() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        boolean loggedIn = auth.getCurrentUser() != null;
        boolean isAlreadyDone = sharedPreferences.getBoolean(LOGIN_TO_SYSTEM_DONE_TAG, false);
        return (!loggedIn) && (!isAlreadyDone);
    }

    private void saveData() {
        sharedPreferences.edit().putBoolean(LOGIN_TO_SYSTEM_DONE_TAG, true).apply();
    }
}

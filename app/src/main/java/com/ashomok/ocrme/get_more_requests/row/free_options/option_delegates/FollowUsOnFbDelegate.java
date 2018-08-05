package com.ashomok.ocrme.get_more_requests.row.free_options.option_delegates;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.ashomok.ocrme.OcrRequestsCounter;
import com.ashomok.ocrme.get_more_requests.GetMoreRequestsActivity;
import com.ashomok.ocrme.get_more_requests.row.free_options.UiFreeOptionManagingDelegate;

import javax.inject.Inject;

import static com.ashomok.ocrme.Settings.facebookPageUrl;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/6/18.
 */

public class FollowUsOnFbDelegate extends UiFreeOptionManagingDelegate {
    public static final String TAG = DEV_TAG + FollowUsOnFbDelegate.class.getSimpleName();
    public static final String ID = "follow_us_on_fb";
    private static final String FOLLOW_US_ON_FB_DONE_TAG = "FOLLOW_US_ON_FB_DONE";
    private final GetMoreRequestsActivity activity;
    private final OcrRequestsCounter ocrRequestsCounter;
    private final SharedPreferences sharedPreferences;

    @Inject
    public FollowUsOnFbDelegate(GetMoreRequestsActivity activity, OcrRequestsCounter ocrRequestsCounter, SharedPreferences sharedPreferences) {
        super(activity);
        this.activity = activity;
        this.ocrRequestsCounter = ocrRequestsCounter;
        this.sharedPreferences = sharedPreferences;
    }

    /**
     * <p>Intent to open the official Facebook app. If the Facebook app is not installed then the
     * default web browser will be used.</p>
     * <p>
     * <p>Example usage:</p>
     * <p>
     * {@code newFacebookIntent(ctx.getPackageManager(), "https://www.facebook.com/JRummyApps");}
     *
     * @param pm  The {@link PackageManager}. You can find this class through {@link
     *            Context#getPackageManager()}.
     * @param url The full URL to the Facebook page or profile.
     * @return An intent that will open the Facebook page/profile.
     */
    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    @Override
    protected void startTask() {
        Log.d(TAG, "onstartTask");
        saveData();
        runFollowUs();

        onTaskDone(ocrRequestsCounter, activity);
    }

    private void runFollowUs() {
        activity.startActivity(newFacebookIntent(activity.getPackageManager(), facebookPageUrl));
    }

    @Override
    public boolean isTaskAvailable() {
        boolean isAlreadyDone = sharedPreferences.getBoolean(FOLLOW_US_ON_FB_DONE_TAG, false);
        return !isAlreadyDone;
    }

    private void saveData() {
        sharedPreferences.edit().putBoolean(FOLLOW_US_ON_FB_DONE_TAG, true).apply();
    }
}

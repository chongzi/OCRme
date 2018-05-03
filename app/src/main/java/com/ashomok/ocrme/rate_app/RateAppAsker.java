package com.ashomok.ocrme.rate_app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.ashomok.ocrme.R;


/**
 * Created by iuliia on 10/5/16.
 */

//todo never used
public class RateAppAsker {

    /**
     * Ask to rate app if the app was used UsingCountForRateApp times
     */
    public static final int UsingCountForRateApp = 5;
    public static final int UsingCountForNeverAsk = -1;

    public static void init(final Activity activity) {

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        int timesAppWasUsed = sharedPref.getInt(activity.getString(R.string.times_app_was_used), 0);

        if (timesAppWasUsed == UsingCountForNeverAsk) {
            return;
        } else {
            SharedPreferences.Editor editor = sharedPref.edit();
            if (timesAppWasUsed >= UsingCountForRateApp) {
                askToRate(activity);
                editor.putInt(activity.getString(R.string.times_app_was_used), 0);
            } else {

                editor.putInt(activity.getString(R.string.times_app_was_used), ++timesAppWasUsed);
            }
            editor.apply();
        }
    }

    private static void askToRate(final Activity activity) {
        RateAppDialogFragment rateAppDialogFragment = RateAppDialogFragment.newInstance(R.string.rate_app_dialog_title);
        rateAppDialogFragment.setOnStopAskListener(new OnNeverAskReachedListener() {
            @Override
            public void onStopAsk() {
                //set default
                SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(activity.getString(R.string.times_app_was_used), UsingCountForNeverAsk);
                editor.apply();
            }
        });
        rateAppDialogFragment.show(activity.getFragmentManager(), "rate_app_dialog");
    }
}

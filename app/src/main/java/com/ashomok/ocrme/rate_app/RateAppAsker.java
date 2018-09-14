package com.ashomok.ocrme.rate_app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.main.RequestsCounterDialogFragment;

import javax.inject.Inject;


/**
 * Created by iuliia on 10/5/16.
 */

public class RateAppAsker {

    /**
     * Ask to rate app if the app was used UsingCountForRateApp times
     */
    public static final int UsingCountForRateApp = 100;
    public static final int UsingCountForNeverAsk = -1;
    private Activity activity;
    private SharedPreferences sharedPreferences;

    @Inject
    public RateAppAsker(SharedPreferences sharedPreferences, Activity activity){
        this.sharedPreferences = sharedPreferences;
        this.activity = activity;
    }

    public void init() {

        SharedPreferences sharedPref = sharedPreferences;
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
        RateAppDialogFragment rateAppDialogFragment = RateAppDialogFragment.newInstance();
        rateAppDialogFragment.setOnStopAskListener(() -> {
            //set default
            SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(activity.getString(R.string.times_app_was_used), UsingCountForNeverAsk);
            editor.apply();
        });
        rateAppDialogFragment.show(activity.getFragmentManager(), "rate_app_dialog");
    }
}

package com.ashomok.ocrme.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.ashomok.ocrme.R;

public class RateAppUtils {
    public void rate(Activity activity) {
        Toast.makeText(activity, R.string.thank_you_for_your_support, Toast.LENGTH_SHORT).show();
        String appPackageName = activity.getPackageName();
        openPackageInMarket(appPackageName, activity);
    }

    private void openPackageInMarket(String appPackageName, Activity activity) {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        try {
            activity.startActivity(marketIntent);
        } catch (ActivityNotFoundException exception) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}

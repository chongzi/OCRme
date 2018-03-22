package com.ashomok.imagetotext.ad;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ashomok.imagetotext.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import javax.inject.Inject;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 7/26/16.
 */


public class AdMobContainerImpl implements AdContainer {

    private static final String TAG = DEV_TAG + AdMobContainerImpl.class.getSimpleName();
    private final Context context;
    private final int adid;

    @Inject
    public AdMobContainerImpl(Context context, @StringRes int adid) {
        this.context = context;
        this.adid = adid;
        String appId = context.getResources().getString(R.string.app_id);
        MobileAds.initialize(context, appId);
    }

    /**
     * add bottom banner on the parent view. Note: It may overlay some views.
     *
     * @param parent
     */
    private void addBottomBanner(ViewGroup parent) {
        if (parent instanceof RelativeLayout || parent instanceof LinearLayout) {
            AdView adView = new AdView(context);
            if (parent instanceof RelativeLayout) {

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                adView.setLayoutParams(lp);
            } else if (parent instanceof LinearLayout) {
                adView.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f));

            }

            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId(context.getResources().getString(adid));
            adView.setId(R.id.ad_banner);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            parent.addView(adView);
        } else {
            Log.e(TAG, "Ads can not been loaded programmaticaly. " +
                    "RelativeLayout and LinearLayout are supported as parent.");
        }
    }

    /**
     * init ad with bottom banner. Note: It may overlay some view.
     *
     * @param parentLayout
     */
    @Override
    public void initBottomBannerAd(ViewGroup parentLayout) {
        if (context.getResources().getConfiguration().orientation ==
                android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            //init banner
            addBottomBanner(parentLayout);
        }
    }
}

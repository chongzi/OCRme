package com.ashomok.ocrme.get_more_requests.row.task_delegates;

import android.util.Log;

import com.ashomok.ocrme.OcrRequestsCounter;
import com.ashomok.ocrme.R;
import com.ashomok.ocrme.get_more_requests.GetMoreRequestsActivity;
import com.ashomok.ocrme.get_more_requests.row.PromoRowData;
import com.ashomok.ocrme.get_more_requests.row.RowViewHolder;
import com.ashomok.ocrme.get_more_requests.row.UiManagingDelegate;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import javax.inject.Inject;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/5/18.
 */

//todo Forward lifecycle events https://developers.google.com/admob/android/rewarded-video -
// impossible now due to no delegate from activity - minor bug
public class WatchVideoDelegate extends UiManagingDelegate implements RewardedVideoAdListener {
    public static final String TAG = DEV_TAG + WatchVideoDelegate.class.getSimpleName();
    public static final String ID = "watch_video";
    private final GetMoreRequestsActivity activity;
    private final OcrRequestsCounter ocrRequestsCounter;
    private RewardedVideoAd mRewardedVideoAd;

    @Inject
    public WatchVideoDelegate(GetMoreRequestsActivity activity, OcrRequestsCounter ocrRequestsCounter) {
        super(activity);
        this.activity = activity;
        this.ocrRequestsCounter = ocrRequestsCounter;
        Log.d(TAG, "in constructor");
        String appId = activity.getResources().getString(R.string.app_id);
        MobileAds.initialize(activity, appId);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
    }

    private void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            String rewardedVideoAdAppId =
                    activity.getResources().getString(R.string.rewarded_video_ad_unit_id);
            mRewardedVideoAd.loadAd(rewardedVideoAdAppId, new AdRequest.Builder().build());
        }
    }

    @Override
    public void onBindViewHolder(PromoRowData data, RowViewHolder holder) {
        super.onBindViewHolder(data, holder);
    }

    @Override
    protected void startTask() {
        showRewardedVideo();
    }

    private void showRewardedVideo() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        // Preload the next video ad.
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        addRequests(rewardItem.getAmount());
    }

    private void addRequests(int amount) {
       onTaskDone(ocrRequestsCounter, activity);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {}

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        activity.showError(R.string.failed_to_load_video_ad);
    }
}

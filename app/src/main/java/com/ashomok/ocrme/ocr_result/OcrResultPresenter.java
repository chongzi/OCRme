package com.ashomok.ocrme.ocr_result;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.ashomok.ocrme.rate_app.RateAppAsker;
import com.ashomok.ocrme.rate_app.RateAppAskerCallback;

import javax.inject.Inject;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

public class OcrResultPresenter implements OcrResultContract.Presenter, RateAppAskerCallback {
    public static final String TAG = DEV_TAG + OcrResultPresenter.class.getSimpleName();
    @Nullable
    private OcrResultContract.View view;
    private RateAppAsker rateAppAsker;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    OcrResultPresenter(RateAppAsker rateAppAsker) {
        this.rateAppAsker = rateAppAsker;
    }

    @Override
    public void takeView(OcrResultContract.View view) {
        this.view = view;
        init();
    }

    @Override
    public void dropView() {
        view = null;
    }

    private void init() {
        rateAppAsker.init(this);
    }

    @Override
    public void showRateAppDialog(DialogFragment rateAppDialogFragment) {

        // Show after 3 seconds
        final Handler handler = new Handler();
        final Runnable runnable = () -> {
            Activity activity = (Activity) view;
            if (activity != null) {
                rateAppDialogFragment.show(activity.getFragmentManager(), "rate_app_dialog");
            }
        };

        handler.postDelayed(runnable, 3000);
    }
}

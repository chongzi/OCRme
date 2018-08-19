package com.ashomok.ocrme.ocr_result;

import android.content.Context;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

public class OcrResultPresenter implements OcrResultContract.Presenter {
    public static final String TAG = DEV_TAG + OcrResultPresenter.class.getSimpleName();
    @Nullable
    private OcrResultContract.View view;
    private Context context;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    OcrResultPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void takeView(OcrResultContract.View view) {
        this.view = view;
    }

    @Override
    public void dropView() {
        view = null;
    }


}

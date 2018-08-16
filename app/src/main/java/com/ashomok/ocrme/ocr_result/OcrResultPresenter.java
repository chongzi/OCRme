package com.ashomok.ocrme.ocr_result;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import com.ashomok.ocrme.R;

import javax.inject.Inject;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.ashomok.ocrme.Settings.appPackageName;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;
import static dagger.internal.Preconditions.checkNotNull;

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

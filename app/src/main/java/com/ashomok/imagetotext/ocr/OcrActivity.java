package com.ashomok.imagetotext.ocr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.Settings;
import com.ashomok.imagetotext.ocr.ocr_task.OcrHttpClient;
import com.ashomok.imagetotext.ocr.ocr_task.OcrResponse;
import com.ashomok.imagetotext.ocr_result.OcrResultActivity;
import com.ashomok.imagetotext.utils.FileUtils;
import com.ashomok.imagetotext.utils.NetworkUtils;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.ashomok.imagetotext.ocr_result.OcrResultActivity.EXTRA_ERROR_MESSAGE;
import static com.ashomok.imagetotext.ocr_result.OcrResultActivity.EXTRA_OCR_RESPONSE;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;


/**
 * Created by Iuliia on 13.12.2015.
 */

public class OcrActivity extends RxAppCompatActivity {

    private Uri imageUri;
    private ArrayList<String> sourceLanguageCodes;
    public static final String EXTRA_LANGUAGES = "com.ashomokdev.imagetotext.LANGUAGES";
    public static final String TAG = DEV_TAG + OcrActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ocr_animation_layout);

        imageUri = getIntent().getData();
        sourceLanguageCodes = getIntent().getStringArrayListExtra(EXTRA_LANGUAGES);

        initCancelBtn();
        initImage();
        initAnimatedScanBand(Settings.isTestMode);

        OcrHttpClient httpClient = OcrHttpClient.getInstance();
        callOcr(httpClient);
    }

    private void callOcr(OcrHttpClient httpClient) {
        if (NetworkUtils.isOnline(this)) {
            String filePath = getImagePath(imageUri);
            if (filePath != null) {
                Single<OcrResponse> ocrResponseSingle =
                        httpClient.ocr(filePath, sourceLanguageCodes);

                ocrResponseSingle.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .compose(bindToLifecycle())
                        .subscribe(
                                myData -> {
                                    Log.d(TAG, "ocr returns " + myData.toString());
                                    startOcrResultActivity(myData);
                                },
                                throwable -> {
                                    String errorMessage = throwable.getMessage();
                                    startOcrResultActivity(errorMessage);
                                });
            } else {
                startOcrResultActivity(getString(R.string.file_not_found));
            }
        } else {
            startOcrResultActivity(getString(R.string.network_error));
        }
    }

    private void startOcrResultActivity(OcrResponse data) {
        Intent intent = new Intent(this, OcrResultActivity.class);
        intent.putExtra(EXTRA_OCR_RESPONSE, data);
        startActivity(intent);
    }

    private void startOcrResultActivity(String errorMessage) {
        Log.e(TAG, "ERROR: " + errorMessage);
        Intent intent = new Intent(this, OcrResultActivity.class);
        intent.putExtra(EXTRA_ERROR_MESSAGE, errorMessage);
        startActivity(intent);
    }

    @Nullable
    private String getImagePath(Uri uri) {
        String path = null;
        try {
            path = FileUtils.getRealPath(this, uri);
        } catch (IOException e) {
            e.printStackTrace();
            startOcrResultActivity(getString(R.string.file_not_found));
        }
        return path;
    }

    public void initAnimatedScanBand(boolean isTestMode) {
        if (isTestMode) {
            return;
        }
        ImageView scan_band = (ImageView) findViewById(R.id.scan_band);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        TranslateAnimation animation = new TranslateAnimation(0.0f, width,
                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(5000);  // animation duration
        animation.setRepeatCount(Animation.INFINITE);  // animation repeat count
        animation.setRepeatMode(2);   // repeat animation (left to right, right to left )

        scan_band.startAnimation(animation);  // start animation
    }

    //todo use rx here
    private void initCancelBtn() {
        Button cancel = (Button) findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void initImage() {
        ImageView image = (ImageView) findViewById(R.id.image);
        Picasso.with(this)
                .load(imageUri)
                .into(image);
    }
}

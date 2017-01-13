package com.ashomok.imagetotext.ocr_task;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.Settings;
import com.squareup.picasso.Picasso;

import java.io.File;

import static com.ashomok.imagetotext.MainActivity.IMAGE_PATH_EXTRA;


/**
 * Created by Iuliia on 13.12.2015.
 */
public class OCRAnimationActivity extends AppCompatActivity {

    private String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ocr_animation_layout);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            path = bundle.getString(IMAGE_PATH_EXTRA);
        }

        initCancelBtn();

        initImage();

        initScanBand(Settings.isTestMode);
    }

    public void initScanBand(boolean isTestMode) {
        if (isTestMode) {
            return;
        }
        ImageView scan_band = (ImageView) findViewById(R.id.scan_band);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        TranslateAnimation animation = new TranslateAnimation(0.0f, width,
                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(10000);  // animation duration
        animation.setRepeatCount(Animation.INFINITE);  // animation repeat count
        animation.setRepeatMode(2);   // repeat animation (left to right, right to left )
        //animation.setFillAfter(true);

        scan_band.startAnimation(animation);  // start animation
    }

    private void initCancelBtn() {
        Button cancel = (Button) findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void initImage() {
        ImageView image = (ImageView) findViewById(R.id.image);

        //todo what if image is very large?
        File file = new File(path);
        Picasso.with(this)
                .load(file)
                .into(image);
    }
}

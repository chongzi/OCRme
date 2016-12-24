package com.ashomok.imagetotext.ocr_task;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ashomok.imagetotext.R;
import com.squareup.picasso.Picasso;

import static com.ashomok.imagetotext.MainActivity.IMAGE_PATH_EXTRA;


/**
 * Created by Iuliia on 13.12.2015.
 */
public class OCRAnimationActivity extends AppCompatActivity {

    private String imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ocr_animation_layout);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            imageUri = bundle.getString(IMAGE_PATH_EXTRA);
        }

        initCancelBtn();

        initImage();
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

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels / 2;
        int width = displayMetrics.widthPixels /2;

        Picasso.with(this)
                .load(imageUri)
                .resize(width, height)
                .into(image);
    }
}

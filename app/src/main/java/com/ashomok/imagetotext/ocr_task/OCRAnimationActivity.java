package com.ashomok.imagetotext.ocr_task;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ashomok.imagetotext.R;
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
//todo get sizes of original image

        File file = new File(path);
        Picasso.with(this)
                .load(file)
//                .resize(width, height)
                .into(image);
    }
}

package com.ashomok.imagetotext.ocr_task;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ashomok.imagetotext.R;

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

        Button cancel = (Button) findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setResult(RESULT_CANCELED);
                finish();
            }
        });

        OCRAnimationView ocrAnimationView = (OCRAnimationView) findViewById(R.id.ocr_animation_view);
        ocrAnimationView.setImageUri(imageUri);

    }
}

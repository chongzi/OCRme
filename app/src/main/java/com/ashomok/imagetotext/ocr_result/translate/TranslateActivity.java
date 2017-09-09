package com.ashomok.imagetotext.ocr_result.translate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.ocr_result.tab_fragments.TextFragment;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 8/27/17.
 */

public class TranslateActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = DEV_TAG + TranslateActivity.class.getSimpleName();
    private String mInputLanguage;
    private String mOutputLanguage;
    private String mInputText ="dummu input text";
    private String mOutputText = "dummu output text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        Intent intent = getIntent();
        mInputText = intent.getStringExtra(TextFragment.EXTRA_TEXT);

        initToolbar();
        initTopLayout();
    }

    private void initTopLayout() {
        //https://stackoverflow.com/questions/9476665/how-to-change-spinner-text-size-and-text-color
        //// TODO: 8/27/17
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {

    }
}

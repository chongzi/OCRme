package com.ashomok.imagetotext.ocr_result;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.ocr.ocr_task.OcrResponse;

import java.util.ArrayList;

import static com.ashomok.imagetotext.language_choser.LanguageActivity.CHECKED_LANGUAGES;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 5/30/17.
 */

public class OcrResultActivity extends AppCompatActivity {
    private static final String TAG = DEV_TAG + OcrResultActivity.class.getSimpleName();
    public static final int LANGUAGE_CHANGED_REQUEST_CODE = 1;
    public static final String EXTRA_OCR_RESPONSE = "com.ashomokdev.imagetotext.OCR_RESPONCE";
    public static final String EXTRA_ERROR_MESSAGE = "com.ashomokdev.imagetotext.ERROR_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_result);
        initToolbar();

        Intent intent = getIntent();
        String errorMessage = intent.getStringExtra(EXTRA_ERROR_MESSAGE);
        OcrResponse ocrData = (OcrResponse) intent.getExtras().getSerializable(EXTRA_OCR_RESPONSE);

        if (errorMessage != null && errorMessage.length() > 0) {
            showError(errorMessage);
        } else if (ocrData != null) {
            if (ocrData.getStatus().equals(OcrResponse.Status.OK)) {
                initTabLayout(ocrData);
            } else {
                showError(ocrData.getStatus());
            }
        }
    }

    private void showError(OcrResponse.Status status) {
        String errorMessage = getString(R.string.unknown_error);
        switch (status) {
            case UNKNOWN_ERROR:
                break;
            case PDF_CAN_NOT_BE_CREATED_LANGUAGE_NOT_SUPPORTED:
                errorMessage = getString(R.string.language_not_supported);
                break;
            case TEXT_NOT_FOUND:
                errorMessage = getString(R.string.text_not_found);
                break;
            default:
                break;
        }
        showError(errorMessage);
    }

    private void showError(String errorMessage) {
        //// TODO: 9/20/17
        //show beautiful empty view here
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LANGUAGE_CHANGED_REQUEST_CODE && resultCode == RESULT_OK) {
            //language was changed - run ocr again for the same image
            Bundle bundle = data.getExtras();
            ArrayList<String> updatedLanguages = bundle.getStringArrayList(CHECKED_LANGUAGES);
            updateOcrResult(updatedLanguages);
        }
    }

    private void updateOcrResult(ArrayList<String> languages) {
        //// TODO: 8/23/17
    }

    private void initTabLayout(OcrResponse ocrData) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.text)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.PDF)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final MyPagerAdapter adapter = new MyPagerAdapter(getFragmentManager(), ocrData);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //back btn pressed

                //// TODO: 5/30/17 save data if you need here
                finish();
            }
        });
    }
}

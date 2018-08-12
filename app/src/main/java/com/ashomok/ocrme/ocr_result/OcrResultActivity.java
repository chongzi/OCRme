package com.ashomok.ocrme.ocr_result;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.ocr.ocr_task.OcrResponse;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 5/30/17.
 */
public class OcrResultActivity extends AppCompatActivity {
    public static final String EXTRA_OCR_RESPONSE = "com.ashomokdev.imagetotext.OCR_RESPONCE";
    public static final String EXTRA_ERROR_MESSAGE = "com.ashomokdev.imagetotext.ERROR_MESSAGE";
    private static final String TAG = DEV_TAG + OcrResultActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_result);
        initToolbar();

        Intent intent = getIntent();
        String errorMessage = intent.getStringExtra(EXTRA_ERROR_MESSAGE);

        if (errorMessage != null && errorMessage.length() > 0) {
            showError(errorMessage);
        } else {
            OcrResponse ocrData = null;
            if (intent.getExtras() != null) {
                ocrData = (OcrResponse) intent.getExtras().getSerializable(EXTRA_OCR_RESPONSE);
            }
            if (ocrData != null) {
                if (ocrData.getStatus().equals(OcrResponse.Status.OK)) {
                    initTabLayout(ocrData);
                } else {
                    showError(ocrData.getStatus());
                }
            }
            fixCoordinatorLayout();
        }
    }

    /**
     * fix of issue - Android - footer scrolls off screen when used in CoordinatorLayout
     * https://stackoverflow.com/questions/30777698/android-footer-scrolls-off-screen-when-used-in-coordinatorlayout
     */

    private void fixCoordinatorLayout() {
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        ViewPager contentLayout = findViewById(R.id.pager);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) contentLayout.getLayoutParams();
            layoutParams.setMargins(
                    0, 0, 0, appBarLayout1.getMeasuredHeight() / 2 + verticalOffset);
            contentLayout.requestLayout();
        });
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
            case INVALID_LANGUAGE_HINTS:
                errorMessage = getString(R.string.invalid_language);
                break;
            default:
                break;
        }
        showError(errorMessage);
    }

    private void showError(String errorMessage) {
        View emptyResult = findViewById(R.id.empty_result_layout);
        emptyResult.setVisibility(View.VISIBLE);

        View resultView = findViewById(R.id.pager);
        resultView.setVisibility(View.GONE);

        TextView errorMessageView = emptyResult.findViewById(R.id.error_message);
        errorMessageView.setText(errorMessage);
    }

    private void initTabLayout(OcrResponse ocrData) {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.text)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.PDF)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pager);
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            //back btn pressed

            //save data if you need here
            finish();
        });
    }
}

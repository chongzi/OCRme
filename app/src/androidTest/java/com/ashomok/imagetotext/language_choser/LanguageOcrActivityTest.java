package com.ashomok.imagetotext.language_choser;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.app.ActionBar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

/**
 * Created by iuliia on 10/31/17.
 */
public class LanguageOcrActivityTest {
    @Rule
    public ActivityTestRule<LanguageOcrActivity> mActivityRule = new ActivityTestRule<>(
            LanguageOcrActivity.class, true, false);

    @Before
    public void launchActivityWithPredefinedData() {
        final Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, LanguageOcrActivity.class);
        ArrayList<String> languageCodes = new ArrayList<String>() {{
            add("en");
        }};
        intent.putStringArrayListExtra(LanguageOcrActivity.CHECKED_LANGUAGE_CODES, languageCodes);
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void toolbarBackBtn() throws InterruptedException {
        ActionBar actionBar = mActivityRule.getActivity().getSupportActionBar();
        assertNotNull(actionBar);
        Thread.sleep(300000);
    }
}
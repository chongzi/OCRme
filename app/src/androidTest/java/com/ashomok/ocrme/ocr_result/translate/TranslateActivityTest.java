package com.ashomok.ocrme.ocr_result.translate;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.ashomok.ocrme.ocr_result.tab_fragments.TextFragment.EXTRA_TEXT;

/**
 * Created by iuliia on 9/16/17.
 */

// run slow emulator /home/iuliia/Android/Sdk/tools/emulator -avd Nexus_5X_API_24 -netspeed gsm
// /home/iuliia/Android/Sdk/tools/emulator -avd Nexus_5X_API_24 -netdelay gprs

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TranslateActivityTest {

    @Rule
    public ActivityTestRule<TranslateActivity> mActivityRule = new ActivityTestRule<>(
            TranslateActivity.class, true, false);

    @Test
    public void seeHowToLook() throws InterruptedException {
        final Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, TranslateActivity.class);
        String sourceText = "русский длинный текст й длинный текст русскирусский текструсский длинный текструсский дй длинный текст русскирусский текструсский длинный текструсский дй длинный текст русскирусский текструсский длинный текструсский дй длинный текст русскирусский текструсский длинный текструсский друсскирусский текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текструсский длинный текст";
        intent.putExtra(EXTRA_TEXT, sourceText);
        mActivityRule.launchActivity(intent);
        Thread.sleep(10000);
    }

    @Test
    public void seeHowToLook2() throws InterruptedException {
        final Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, TranslateActivity.class);

        String sourceText = "руский длинный";

        intent.putExtra(EXTRA_TEXT, sourceText);
        mActivityRule.launchActivity(intent);
        Thread.sleep(10000);
    }
}
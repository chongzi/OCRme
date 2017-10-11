package com.ashomok.imagetotext.ocr_result;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.language_choser.LanguageActivity;
import com.ashomok.imagetotext.ocr.ocr_task.OcrResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.ashomok.imagetotext.ocr_result.OcrResultActivity.EXTRA_OCR_RESPONSE;

/**
 * Created by iuliia on 5/31/17.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class OcrResultActivityTest {
    private String longText = "long long long long long long long long long long long long long long long  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long long\";\n    private String longText = \"long long long long long long long long long  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long long\";\nong long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong  long long long long long long long long longlong longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long longlong long long long long";
    private String shortText ="short text";

    private String pdfResultUrl = "gs://imagetotext-149919.appspot.com/ru.pdf";
    private OcrResponse.Status status = OcrResponse.Status.OK;

    @Rule
    public ActivityTestRule<OcrResultActivity> mActivityRule = new ActivityTestRule<>(
            OcrResultActivity.class, true, false);


    public void launchActivityWithLongText() {
        final Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, OcrResultActivity.class);

        OcrResponse response = new OcrResponse(longText, pdfResultUrl, "media url", status);
        intent.putExtra(EXTRA_OCR_RESPONSE, response);
        mActivityRule.launchActivity(intent);
    }

    public void launchActivityWithShortText() {
        final Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, OcrResultActivity.class);

        OcrResponse response = new OcrResponse(shortText, pdfResultUrl, "media url", status);
        intent.putExtra(EXTRA_OCR_RESPONSE, response);
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void tabSwap() throws InterruptedException {
        launchActivityWithLongText();
        onView(withId(R.id.pager)).perform(swipeLeft());
        Thread.sleep(40000);
        onView(withId(R.id.pager)).perform(swipeRight());
    }

    @Test
    public void testLongText() throws InterruptedException {
        launchActivityWithLongText();
        Thread.sleep(4000);
    }

    @Test
    public void testShortText() throws InterruptedException {
        launchActivityWithShortText();
        Thread.sleep(4000);
    }
}
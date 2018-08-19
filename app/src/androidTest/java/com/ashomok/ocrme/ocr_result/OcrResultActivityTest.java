package com.ashomok.ocrme.ocr_result;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.ocr.ocr_task.OcrResponse;
import com.ashomok.ocrme.ocr.ocr_task.OcrResult;
import com.ashomok.ocrme.utils.Repeat;
import com.ashomok.ocrme.utils.RepeatRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.ashomok.ocrme.ocr_result.OcrResultActivity.EXTRA_OCR_RESPONSE;
import static com.ashomok.ocrme.utils.FilesProvider.getGcsImagePdfUri;
import static com.ashomok.ocrme.utils.FilesProvider.getGcsImageUri;
import static com.ashomok.ocrme.utils.FilesProvider.getGcsSearchablePdfUri;

/**
 * Created by iuliia on 5/31/17.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class OcrResultActivityTest {
    private String longText = "long long long long long long long long long long long long long long" +
            " long  long long long long long long long long longlong  long long long long long long" +
            " long long longlong  long long long long long long long long longlong  long long long " +
            "long long long long long longlong  long long long long long long long long longlong  " +
            "long long long long long long long long longlong  long long long long long long long" +
            " long longlong  long long long long long long long long longlong longlong long long " +
            "long longlong long long long longlong long long long longlong long long long longlong" +
            " long long long longlong long long long longlong long long long longlong long long long" +
            " longlong long long long longlong long long long longlong long long long longlong long " +
            "long long longlong long long long longlong long long long longlong long long long long" +
            "long long long long longlong long long long longlong long long long longlong long long" +
            " long longlong long long long longlong long long long longlong long long long longlong long" +
            " long long longlong long long long longlong long long long long\";\n    private String " +
            "longText = \"long long long long long long long long long  long long long long long lo" +
            "ng long long longlong  long long long long long long long long longlong  long long lo" +
            "ng long long long long long longlong  long long long long long long long long longlo" +
            "ng  long long long long long long long long longlong  long long long long long long" +
            " long long longlong  long long long long long long long long longlong  long long l" +
            "ong long long long long long longlong longlong long long long longlong long long lo" +
            "ng longlong long long long longlong long long long longlong long long long longlong" +
            " long long long longlong long long long longlong long long long longlong long long" +
            " long longlong long long long longlong long long long longlong long long long lon" +
            "glong long long long longlong long long long longlong long long long longlong lon" +
            "g long long longlong long long long longlong long long long longlong long long lo" +
            "ng longlong long long long longlong long long long longlong long long long longlo" +
            "ng long long long longlong long long long longlong long long long long\";\nong lo" +
            "ng long long long long long long longlong  long long long long long long long long" +
            " longlong  long long long long long long long long longlong  long long long long " +
            "long long long long longlong  long long long long long long long long longlong  lo" +
            "ng long long long long long long long longlong  long long long long long long long" +
            " long longlong  long long long long long long long long longlong longlong long lon" +
            "g long longlong long long long longlong long long long longlong long long long lon" +
            "glong long long long longlong long long long longlong long long long longlong long lo" +
            "ng long longlong long long long longlong long long long longlong long long long lon" +
            "glong long long long longlong long long long longlong long long long longlong long lo" +
            "ng long longlong long long long longlong long long long longlong long long long longlo" +
            "ng long long long longlong long long long longlong long long long longlong long long l" +
            "ong longlong long long long longlong long long long longlong long long long long";
    private String shortText ="short text";
    private String gcsSearchablePdfUri = getGcsSearchablePdfUri();
    private String gcsImagePdfUri = getGcsImagePdfUri();
    private String imageUrl = getGcsImageUri();
    private OcrResponse.Status status = OcrResponse.Status.OK;

    @Rule
    public RepeatRule rule = new RepeatRule();

    @Rule
    public ActivityTestRule<OcrResultActivity> mActivityRule = new ActivityTestRule<>(
            OcrResultActivity.class, true, false);


    public void launchActivityWithLongText() {
        final Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, OcrResultActivity.class);
        OcrResult ocrResult =new OcrResult.Builder()
                .textResult(longText)
                .pdfResultGsUrl(gcsSearchablePdfUri)
                .pdfImageResultGsUrl(gcsImagePdfUri)
                .pdfResultMediaUrl( "media url")
                .pdfImageResultMediaUrl( "media url")
                .sourceImageUrl(imageUrl)
                .build();
        OcrResponse response = new OcrResponse(ocrResult, status);
        intent.putExtra(EXTRA_OCR_RESPONSE, response);
        mActivityRule.launchActivity(intent);
    }

    public void launchActivityWithShortText() {
        final Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, OcrResultActivity.class);
        OcrResult ocrResult =new OcrResult.Builder()
                .textResult(shortText)
                .pdfResultGsUrl(gcsSearchablePdfUri)
                .pdfImageResultGsUrl(gcsImagePdfUri)
                .pdfResultMediaUrl( "media url")
                .pdfImageResultMediaUrl( "media url")
                .sourceImageUrl(imageUrl)
                .build();
        OcrResponse response = new OcrResponse(ocrResult, status);
        intent.putExtra(EXTRA_OCR_RESPONSE, response);
        mActivityRule.launchActivity(intent);
    }

    @Repeat(2)
    @Test
    public void tabSwap() throws InterruptedException {
        launchActivityWithLongText();
        onView(withId(R.id.pager)).perform(swipeLeft());
        Thread.sleep(4000);
        onView(withId(R.id.pager)).perform(swipeRight());

    }

    @Test
    public void testLongText() throws InterruptedException {
        launchActivityWithLongText();
        Thread.sleep(40000);
    }

    @Test
    public void testShortText() throws InterruptedException {
        launchActivityWithShortText();
        Thread.sleep(4000);
    }
}
package com.ashomok.imagetotext.ocr;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ImageView;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.Settings;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.ashomok.imagetotext.utils.FilesProvider.getTestImages;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 12/25/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OcrActivityTest {

    private static final String TAG = DEV_TAG + OcrActivityTest.class.getSimpleName();

    @Rule
    public ActivityTestRule<OcrActivity> mActivityRule = new ActivityTestRule<>(
            OcrActivity.class, true, false);

    @Before
    public void setTestMode() {
        Settings.isTestMode = true;
    }

    @Test
    public void testOcr() throws InterruptedException {
        String path = getTestImages().get(0);
        launchActivityWithPath(Uri.fromFile(new File(path)));
        Thread.sleep(10000);//waiting for ocr finished
    }

    @Test
    public void backgroundSettedProperly() throws InterruptedException {
        String path = getTestImages().get(0);
        launchActivityWithPath(Uri.fromFile(new File(path)));

        onView(withId(R.id.image)).check(matches(isDisplayed()));
        Thread.sleep(3000); //waiting for image setted
        onView(withId(R.id.image)).check(matches(hasDrawable()));
        Thread.sleep(10000); //waiting for ocr finished
        onView(withId(R.id.source_image)).check(matches(hasDrawable()));
        onView(withId(R.id.source_image)).check(matches(isDisplayed()));
    }

    private void launchActivityWithPath(Uri uri) {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, OcrActivity.class);
        intent.putExtra(OcrActivity.EXTRA_IMAGE_URI, uri);

        ArrayList<String> languages = new ArrayList<>();
        languages.add("ru");
        intent.putExtra(OcrActivity.EXTRA_LANGUAGES, languages);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        mActivityRule.launchActivity(intent);
    }

    public static Matcher<View> hasDrawable() {
        return new DrawableMatcher(DrawableMatcher.ANY);
    }

    public static class DrawableMatcher extends TypeSafeMatcher<View> {

        private final int expectedId;
        String resourceName;
        static final int EMPTY = -1;
        static final int ANY = -2;

        public DrawableMatcher(int expectedId) {
            super(View.class);
            this.expectedId = expectedId;
        }

        @Override
        protected boolean matchesSafely(View target) {
            if (!(target instanceof ImageView)) {
                return false;
            }
            ImageView imageView = (ImageView) target;
            if (expectedId == EMPTY) {
                return imageView.getDrawable() == null;
            }
            if (expectedId == ANY) {
                return imageView.getDrawable() != null;
            }
            Resources resources = target.getContext().getResources();
            Drawable expectedDrawable = resources.getDrawable(expectedId);
            resourceName = resources.getResourceEntryName(expectedId);

            if (expectedDrawable == null) {
                return false;
            }

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            Bitmap otherBitmap = ((BitmapDrawable) expectedDrawable).getBitmap();
            return bitmap.sameAs(otherBitmap);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("with drawable from resource id: ");
            description.appendValue(expectedId);
            if (resourceName != null) {
                description.appendText("[");
                description.appendText(resourceName);
                description.appendText("]");
            }
        }
    }
}
package com.ashomok.imagetotext.ocr_task;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ashomok.imagetotext.MainActivity;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.Settings;

import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static java.lang.Thread.sleep;

/**
 * Created by iuliia on 12/25/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OCRAnimationActivityTest {

    private static final String TAG = OCRAnimationActivityTest.class.getSimpleName();
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/ImageToText/";
    private static final String TEST_IMGS = "test_imgs";


    @Rule
    public ActivityTestRule<OCRAnimationActivity> mActivityRule = new ActivityTestRule<>(
            OCRAnimationActivity.class, true, false);

    @Before
    public void setTestMode() {
        Settings.isTestMode = true;
    }

    @Test
    public void backgroundSettedProperly() throws InterruptedException {
        ArrayList<String> imagePaths = getTestImages();
        for (String path : imagePaths) {
            launchActivityWithPath(Uri.fromFile(new File(path)));

            onView(withId(R.id.image)).check(matches(isDisplayed()));
            //wait while image was loaded by Picasso
            sleep(5000);
            onView(withId(R.id.image)).check(matches(hasDrawable()));

            mActivityRule.getActivity().finish();
        }
    }

    private void launchActivityWithPath(Uri uri) {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, OCRAnimationActivity.class);
        intent.setData(uri);
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

    private void prepareDirectories(String[] paths) {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();

        if (ContextCompat.checkSelfPermission(targetContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            throw new AssertionError("Test not failed, but needs permission");
        } else {
            for (String path : paths) {
                File dir = new File(path);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.e(TAG, "ERROR: Creation of directory " + path
                                + " on sdcard failed");
                        Assert.fail("ERROR: Creation of directory " + path
                                + " on sdcard failed");
                    } else {
                        Log.v(TAG, "Created directory " + path + " on sdcard");
                    }
                }
            }
        }

    }

    private ArrayList<String> getTestImages() {
        //create folders for tessdata files
        prepareDirectories(
                new String[]{DATA_PATH + TEST_IMGS});

        ArrayList<String> files = new ArrayList<String>();

        try {

            AssetManager assetManager = getInstrumentation().getContext().getAssets();
            String fileList[] = assetManager.list(TEST_IMGS);

            for (String fileName : fileList) {

                // open file within the assets folder
                // if it is not already there copy it to the sdcard
                String pathToDataFile = DATA_PATH + TEST_IMGS + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {

                    InputStream in = assetManager.open(TEST_IMGS + "/" + fileName);

                    OutputStream out = new FileOutputStream(pathToDataFile);

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    Log.v(TAG, "Copied " + fileName + "to test_imgs");
                }
                if (!(new File(pathToDataFile)).exists()) {

                    throw new AssertionError("Can not copy file.");
                }
                files.add(pathToDataFile);
            }
        } catch (IOException e) {
            Log.e(TAG, "Was unable to copy files to test_imgs " + e.toString());
            Assert.fail("Was unable to copy files to test_imgs " + e.toString());

        }
        return files;
    }

}
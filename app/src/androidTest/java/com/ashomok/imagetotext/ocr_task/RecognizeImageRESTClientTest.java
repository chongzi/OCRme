package com.ashomok.imagetotext.ocr_task;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ashomok.imagetotext.MainActivity;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.utils.PermissionUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static java.lang.Thread.sleep;

/**
 * Created by iuliia on 1/19/17.
 */
public class RecognizeImageRESTClientTest {

    private static final String TAG = RecognizeImageRESTClientTest.class.getSimpleName();
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/ImageToText/";
    private static final String TEST_IMGS = "test_imgs";
    private static final String EN = "eng";
    private static final String WRONG_ORIENTATION = "wrong_orientation";
    private static final String VERTICAL_ORIENTATION = "vertical_orientation";
    private static final String RU = "rus";
    private static final String RU_EN = "ru-en";

    private Context context;
    private ArrayList<String> files;
    private static final String PL = "pl";
    private static final String EMPTY = "empty";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void init() {
        context = mActivityRule.getActivity();
        files = getTestImages();
    }

    /**
     * empty result expected
     */
    @Test
    public void recognizeImageWithoutText() {
        recognizeImageTest(EMPTY);
    }

    @Test
    public void recognizeEnglish() {
        recognizeImageTest(EN);
    }

    @Test
    public void recognizeWrongOrientation() {
        recognizeImageTest(WRONG_ORIENTATION);
    }

    @Test
    public void recognizeVerticalOrientation() {
        recognizeImageTest(VERTICAL_ORIENTATION);
    }

    /**
     * recognize image with uni characters
     * ć, ń, ó, ś, ź, ł, ę, ą for Polish
     */
    @Test
    public void recognizeUniCharacters() {
        ArrayList<String> languages = new ArrayList<>();
        languages.add("pl");
        recognizeLanguageTest(languages, PL);
    }

    @Test
    public void recognizeRussian() {
        ArrayList<String> languages = new ArrayList<>();
        languages.add("ru");
        recognizeLanguageTest(languages, RU);
    }

    @Test
    public void recognizeEnglishRussian() {
        ArrayList<String> languages = new ArrayList<>();
        languages.add("ru");
        languages.add("en");
        recognizeLanguageTest(languages, RU_EN);
    }

    /**
     * measure duration
     */
    @Test
    public void measureDuration() {
        long startTime = System.nanoTime();
        String path = files.get(0);

        final CountDownLatch signal = new CountDownLatch(1);
        TaskDelegateImpl delegate = new TaskDelegateImpl(signal, null);

        RecognizeImageAsyncTask task = new RecognizeImageRESTClient(Uri.fromFile(new File(path)), null);
        task.setOnTaskCompletedListener(delegate);
        executeTask(signal, task);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        Log.v(TAG, "duration: " + duration + "/n");
    }

    /**
     * @param tag of file name of image from assets
     */
    private void recognizeImageTest(@Nullable String tag) {
        for (String path : files) {
            if (path.contains(tag)) {
                final CountDownLatch signal = new CountDownLatch(1);

                Uri uri = Uri.fromFile(new File(path));

                RecognizeImageAsyncTask task = new RecognizeImageRESTClient(uri, null);

                TaskDelegateImpl delegate = new TaskDelegateImpl(signal, tag);
                task.setOnTaskCompletedListener(delegate);

                executeTask(signal, task);
            }
        }
    }

    /**
     * Test image recognition with language list specified.
     *
     * @param languages - target languages
     * @param tag       - tag for image file
     */
    private void recognizeLanguageTest(List<String> languages, @Nullable String tag) {
        for (String path : files) {
            if (path.contains(tag)) {
                final CountDownLatch signal = new CountDownLatch(1);

                Uri uri = Uri.fromFile(new File(path));

                RecognizeImageAsyncTask task = new RecognizeImageRESTClient(uri, languages);

                TaskDelegateImpl delegate = new TaskDelegateImpl(signal, tag);
                task.setOnTaskCompletedListener(delegate);

                executeTask(signal, task);
            }
        }
    }

    private ArrayList<String> getTestImages() {
        //create folders for tessdata files
        prepareDirectories(
                new String[]{DATA_PATH + TEST_IMGS});

        ArrayList<String> files = new ArrayList<>();

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

    private class TaskDelegateImpl implements RecognizeImageAsyncTask.OnTaskCompletedListener {
        private CountDownLatch signal;
        private
        @Nullable
        String tag;

        public TaskDelegateImpl(CountDownLatch signal, String tag) {
            this.signal = signal;
            this.tag = tag;
        }

        @Override
        public void onTaskCompleted(String result) {
            Log.d(TAG, " result: " + result);
            signal.countDown();// notify the count down latch
            if (tag == null) {
                Assert.assertTrue(result.length() > 50);
            } else {
                if (tag.equals(PL)) {
                    Assert.assertTrue(result.contains("ś") || result.contains("ł"));
                } else if (tag.equals(EMPTY)) {
                    Assert.assertTrue(result.equals(context.getResources().getString(R.string.text_not_found)));
                } else if (tag.equals(EN)) {
                    Assert.assertTrue(result.contains("right"));
                } else if (tag.equals(RU)) {
                    Assert.assertTrue(result.contains("Барышня"));
                } else if (tag.equals(RU_EN)) {
                    Assert.assertTrue(result.contains("Dictionary") && result.contains("Обычно"));
                } else if (tag.equals(VERTICAL_ORIENTATION) || tag.equals(WRONG_ORIENTATION)) {
                    Assert.assertTrue(result.length() > 50);
                }
            }
        }

        @Override
        public void onError(String message) {
           throw new AssertionError(message);
        }
    }

    private void executeTask(CountDownLatch signal, RecognizeImageAsyncTask task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
        try {
            signal.await();// wait for callback
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void prepareDirectories(String[] paths) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            PermissionUtils.requestPermission(mActivityRule.getActivity(), 0, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);


            final long PERMISSIONS_DIALOG_DELAY = 10000;
            try {
                sleep(PERMISSIONS_DIALOG_DELAY);
            } catch (Exception e) {
                throw new AssertionError("Unexpected error");
            }
        }

        if (ContextCompat.checkSelfPermission(mActivityRule.getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

}
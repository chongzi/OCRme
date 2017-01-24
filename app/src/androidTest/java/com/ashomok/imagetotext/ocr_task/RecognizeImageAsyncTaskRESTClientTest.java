package com.ashomok.imagetotext.ocr_task;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ashomok.imagetotext.MainActivity;
import com.ashomok.imagetotext.tools.PermissionUtils;

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
import java.util.concurrent.CountDownLatch;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static java.lang.Thread.sleep;

/**
 * Created by iuliia on 1/19/17.
 */
public class RecognizeImageAsyncTaskRESTClientTest {

    private static final String TAG = RecognizeImageAsyncTaskRESTClientTest.class.getSimpleName();
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/ImageToText/";
    private static final String TEST_IMGS = "test_imgs";
    private Context context;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void init() {
        context = getContext();
    }

    /**
     * recognize image with uni characters
     * ć, ń, ó, ś, ź, ł, ę, ą for Polish
     */
    @Test
    public void recognizePolish() {
        ArrayList<String> files = getTestImages();


        long startTime = System.nanoTime();
        for (String path : files) {

            final CountDownLatch signal = new CountDownLatch(1);
//            TaskDelegateImpl delegate = new TaskDelegateImpl(signal);

            ArrayList<String> languages = new ArrayList<>();
            languages.add("pl");
            Uri uri = Uri.fromFile(new File(path));


//            RecognizeImageAsyncTask.OnTaskCompletedListener onTaskCompletedListener = new RecognizeImageAsyncTask.OnTaskCompletedListener() {
//                @Override
//                public void onTaskCompleted(String result) {
//                    finishActivity(OCRAnimationActivity_REQUEST_CODE);
//                    Log.d(TAG, result);
//                    //// TODO: 12/22/16
//                    //open new activity and show result
//                }
//            };
//            recognizeImageAsyncTask.setOnTaskCompletedListener(onTaskCompletedListener);

            RecognizeImageAsyncTask task = new RecognizeImageAsyncTaskRESTClient(context, uri, languages);
            executeTask(signal, task);
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        Log.v(TAG, "RecognizeImageAsyncTaskRESTClient: " + duration + "/n");
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

    private class TaskDelegateImpl implements TaskDelegate {
        private CountDownLatch signal;

        public TaskDelegateImpl(CountDownLatch signal) {
            this.signal = signal;
        }

        @Override
        public void TaskCompletionResult(String[] result) {
            StringBuilder builder = new StringBuilder();
            for (String s : result) {
                builder.append(s);
            }
            Log.d(TAG, " result: " + builder.toString());

            signal.countDown();// notify the count down latch
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

            PermissionUtils.requestPermission(mActivityRule.getActivity(),0, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);


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


    public interface TaskDelegate {
        void TaskCompletionResult(String[] result);
    }

}
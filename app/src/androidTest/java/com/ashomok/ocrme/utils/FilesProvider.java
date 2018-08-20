package com.ashomok.ocrme.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import junit.framework.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 9/19/17.
 */

public class FilesProvider {

    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/ImageToText/";
    private static final String TEST_IMGS = "test_imgs";
    private static final String TAG = DEV_TAG + FilesProvider.class.getSimpleName();

    private static final String gcsImageUri = "gs://ocrme-77a2b.appspot.com/test/ru.jpg";

    private static final String gcsSearchablePdfUri = "gs://ocrme-77a2b.appspot.com/test/ru.pdf";

    private static final String gcsImagePdfUri = "gs://ocrme-77a2b.appspot.com/test/pdf-results%2F2018-08-19-14-51-16-841-file.pdf";


    public static ArrayList<String> getTestImages() {
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

    private static void prepareDirectories(String[] paths) {
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

    public static String getGcsImageUri() {
        return gcsImageUri;
    }

    public static String getGcsSearchablePdfUri() { return gcsSearchablePdfUri; }

    public static String getGcsImagePdfUri() { return gcsImagePdfUri; }
}

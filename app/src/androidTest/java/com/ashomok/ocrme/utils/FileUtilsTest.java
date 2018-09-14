package com.ashomok.ocrme.utils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.annimon.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static com.ashomok.ocrme.utils.FilesProvider.getTestImages;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class FileUtilsTest {

    private Bitmap bitmap;
    private Bitmap bitmapScaled;

    public static final String TAG = DEV_TAG + FileUtils.class.getSimpleName();

    private static Bitmap getBitmap() {
        String path = Stream.of(getTestImages()).filter(s -> s.contains("vertical")).single();
        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
    }

    @Before
    public void setup() {
        bitmap = getBitmap();
    }

    @Test
    public void bitmapToBytesTest() {
        Assert.assertTrue(bitmap != null);
        byte[] scaled = FileUtils.toBytes(bitmap, Bitmap.CompressFormat.JPEG);
        bitmapScaled = BitmapFactory.decodeByteArray(scaled, 0, scaled.length);
        Log.d(TAG, "maxImgSize = " + FileUtils.maxImageSizeBytes);
        Assert.assertTrue(bitmapScaled != null);
    }
}
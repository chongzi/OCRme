package com.ashomok.ocrme.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.annimon.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;

import static com.ashomok.ocrme.utils.FilesProvider.getTestImages;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;
import static org.junit.Assert.*;

//todo ove to robolectric
@RunWith(AndroidJUnit4.class)
@SmallTest
public class FileUtilsTest {

    private Bitmap bitmap;
    public static final String TAG = DEV_TAG + FileUtils.class.getSimpleName();


    private static Bitmap getBitmap() {
        String path = Stream.of(getTestImages()).filter(s->s.contains("vertical")).single();
        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
    }

    @Before
    public void setup() {
        bitmap = getBitmap();
    }

    @Test
    public void scaleBitmapDown() {
        Assert.assertTrue(bitmap != null);

//        FileUtils.scaleBitmapDown()

    }
}
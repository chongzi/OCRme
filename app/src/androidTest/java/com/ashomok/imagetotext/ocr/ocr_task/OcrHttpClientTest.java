package com.ashomok.imagetotext.ocr.ocr_task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

import static com.ashomok.imagetotext.utils.FilesProvider.getTestImages;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 9/19/17.
 */
public class OcrHttpClientTest {

    private OcrHttpClient client;
    private static final String TAG = DEV_TAG + OcrHttpClientTest.class.getSimpleName();

    @Before
    public void init() {
        client = OcrHttpClient.getInstance();
    }

    @Test
    public void ocr() {
        String imagePath = getImagePath();
        List<String> languages = new ArrayList<>();
        languages.add("en");

        Single<OcrResponse> response = client.ocr(imagePath, languages);

        OcrResponse ocrResponse = response.blockingGet();

        Assert.assertEquals(ocrResponse.getStatus(), OcrResponse.Status.OK);
        Assert.assertTrue(ocrResponse.getTextResult().length() > 5);
        Assert.assertTrue(ocrResponse.getPdfResultUrl().length() > 5);
    }

    private String getImagePath() {
        ArrayList<String> imagePaths = getTestImages();
        return imagePaths.get(0);
    }
}
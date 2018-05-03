package com.ashomok.ocrme.ocr.ocr_task;

import com.annimon.stream.Optional;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.ashomok.ocrme.utils.FirebaseAuthUtil.getIdToken;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 9/19/17.
 */
public class OcrHttpClientTest {

    private OcrHttpClient client;
    private static final String TAG = DEV_TAG + OcrHttpClientTest.class.getSimpleName();
    private static final String gcsImageUri =
            "gs://bucket-for-requests-test/2017-07-26-12-37-36-806-2017-07-26-12-37-36-806-ru.jpg";

    @Before
    public void init() {
        client = OcrHttpClient.getInstance();
    }

    @Test
    public void ocrWithToken() {
        List<String> languages = new ArrayList<>();
        languages.add("en");

        String token = getIdToken().blockingGet().get();
        Single<OcrResponse> response = client.ocr(gcsImageUri, Optional.of(languages), Optional.of(token));

        OcrResponse ocrResponse = response.blockingGet();

        Assert.assertEquals(ocrResponse.getStatus(), OcrResponse.Status.OK);
        Assert.assertTrue(ocrResponse.getOcrResult().getTextResult().length() > 5);
        Assert.assertTrue(ocrResponse.getOcrResult().getPdfResultGsUrl().length() > 5);
    }

    @Test
    public void ocr() {
        List<String> languages = new ArrayList<>();
        languages.add("en");

        Single<OcrResponse> response = client.ocr(gcsImageUri, Optional.of(languages), Optional.empty());

        OcrResponse ocrResponse = response.blockingGet();

        Assert.assertEquals(ocrResponse.getStatus(), OcrResponse.Status.OK);
        Assert.assertTrue(ocrResponse.getOcrResult().getTextResult().length() > 5);
        Assert.assertTrue(ocrResponse.getOcrResult().getPdfResultGsUrl().length() > 5);
    }
}
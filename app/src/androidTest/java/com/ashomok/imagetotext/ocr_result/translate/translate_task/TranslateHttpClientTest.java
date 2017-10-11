package com.ashomok.imagetotext.ocr_result.translate.translate_task;

import android.util.Log;
import android.util.Pair;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 9/6/17.
 */
public class TranslateHttpClientTest {
    private TranslateHttpClient client;
    private static final String TAG = DEV_TAG + TranslateHttpClientTest.class.getSimpleName();

    @Before
    public void init() {
        client = TranslateHttpClient.getInstance();
    }

    @Test
    public void getSupportedLanguages() {
        SupportedLanguagesResponse response =
                client.getSupportedLanguages("en").blockingGet();

        Assert.assertTrue(response.getSupportedLanguages().size() > 0);
        Assert.assertTrue(response.getStatus().equals(SupportedLanguagesResponse.Status.OK));
    }

    @Test
    public void translate() {
        TranslateResponse responce =
                client.translate("de", "наша мама добрая").blockingGet();

        Assert.assertTrue(responce.getStatus().equals(TranslateResponse.Status.OK));
        Assert.assertTrue(responce.getSourceLanguageCode().equals("ru"));
        Assert.assertTrue(responce.getTargetLanguageCode().equals("de"));
        Assert.assertTrue(responce.getTextResult().length() > 0);
    }

    @Test
    public void callInParallel() throws InterruptedException {

        Single<SupportedLanguagesResponse> supportedLanguagesResponceSingle =
                client.getSupportedLanguages("en")
                        .doOnEvent((supportedLanguagesResponce, throwable) -> {
                                    Log.d(TAG, "getSupportedLanguages thread = "
                                            + Thread.currentThread().getName());
                                }
                        ).subscribeOn(Schedulers.io());

        Single<TranslateResponse> translateResponseSingle =
                client.translate("de", "наша мама добрая")
                        .doOnEvent((supportedLanguagesResponce, throwable) -> {
                                    Log.d(TAG, "getSupportedLanguages thread = "
                                            + Thread.currentThread().getName());
                                }
                        ).subscribeOn(Schedulers.io());

        Single<Pair<SupportedLanguagesResponse, TranslateResponse>> zipped =
                Single.zip(
                        supportedLanguagesResponceSingle,
                        translateResponseSingle,
                        (a, b) -> new Pair<>(a, b))
                        .observeOn(AndroidSchedulers.mainThread());// Will switch to Main-Thread when finished

        zipped.subscribe(myData -> {
            Log.d(TAG, "zipped called with " + myData.toString()
                    + "in thread " + Thread.currentThread().getName());
        }, throwable -> {
            Log.e(TAG, throwable.getMessage());
        });

        Thread.sleep(3000);
        Pair<SupportedLanguagesResponse, TranslateResponse> result = zipped.blockingGet();

        SupportedLanguagesResponse supportedLanguagesResponse = result.first;
        TranslateResponse translateResponse = result.second;

        Assert.assertEquals(supportedLanguagesResponse.getStatus(), SupportedLanguagesResponse.Status.OK);
        Assert.assertEquals(translateResponse.getStatus(), TranslateResponse.Status.OK);
    }
}
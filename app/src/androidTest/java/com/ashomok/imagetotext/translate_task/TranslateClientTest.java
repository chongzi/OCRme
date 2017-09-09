package com.ashomok.imagetotext.translate_task;

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
public class TranslateClientTest {
    private TranslateClient client;
    private static final String TAG = DEV_TAG + TranslateClientTest.class.getSimpleName();

    @Before
    public void init() {
        client = TranslateClient.getInstance();
    }

    @Test
    public void getSupportedLanguages() {
        SupportedLanguagesResponce response =
                client.getSupportedLanguages("en").blockingGet();

        Assert.assertTrue(response.getSupportedLanguages().size() > 0);
        Assert.assertTrue(response.getStatus().equals(SupportedLanguagesResponce.Status.OK));
    }

    @Test
    public void translate() {
        TranslateRequestBean translateRequest = new TranslateRequestBean();
        translateRequest.setDeviceLang("de");
        translateRequest.setSourceText("наша мама добрая");

        TranslateResponse responce =
                client.translate(translateRequest).blockingGet();

        Assert.assertTrue(responce.getStatus().equals(TranslateResponse.Status.OK));
        Assert.assertTrue(responce.getSourceLanguageCode().equals("ru"));
        Assert.assertTrue(responce.getTargetLanguageCode().equals("de"));
        Assert.assertTrue(responce.getTextResult().length() > 0);
    }

    @Test
    public void callInParallel() {

        TranslateRequestBean translateRequest = new TranslateRequestBean();
        translateRequest.setDeviceLang("de");
        translateRequest.setSourceText("наша мама добрая");

        Single<SupportedLanguagesResponce> supportedLanguagesResponceSingle =
                client.getSupportedLanguages("en")
                        .doOnEvent((supportedLanguagesResponce, throwable) -> {
                                    Log.d(TAG, "getSupportedLanguages thread = "
                                            + Thread.currentThread().getName());
                                }
                        ).subscribeOn(Schedulers.io());

        Single<TranslateResponse> translateResponseSingle =
                client.translate(translateRequest)
                        .doOnEvent((supportedLanguagesResponce, throwable) -> {
                                    Log.d(TAG, "getSupportedLanguages thread = "
                                            + Thread.currentThread().getName());
                                }
                        ).subscribeOn(Schedulers.io());


        Single<Pair<SupportedLanguagesResponce, TranslateResponse>> zipped =
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

        Pair<SupportedLanguagesResponce, TranslateResponse> result = zipped.blockingGet();

        SupportedLanguagesResponce supportedLanguagesResponce = result.first;
        TranslateResponse translateResponse = result.second;

        Assert.assertEquals(supportedLanguagesResponce.getStatus(), SupportedLanguagesResponce.Status.OK);
        Assert.assertEquals(translateResponse.getStatus(), TranslateResponse.Status.OK);
    }
}
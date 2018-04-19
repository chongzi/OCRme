package com.ashomok.ocrme.ocr_result.translate.translate_task;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 9/5/17.
 */

//singleton
public class TranslateHttpClient {
    private static final String TAG = DEV_TAG + TranslateHttpClient.class.getSimpleName();
    private static TranslateHttpClient instance;
    private TranslateAPI translateAPI;
    private static final int CONNECTION_TIMEOUT_SEC = 90;

    public static TranslateHttpClient getInstance() {
        if (instance == null) {
            instance = new TranslateHttpClient();
        }
        return instance;
    }

    private TranslateHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                .writeTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(TranslateAPI.ENDPOINT)
                .build();

        translateAPI = retrofit.create(TranslateAPI.class);
    }

    public Single<SupportedLanguagesResponse> getSupportedLanguages(@NonNull String deviceLanguageCode) {
        return translateAPI.getSupportedLanguages(deviceLanguageCode);
    }

    /**
     * @param deviceLanguageCode device Language Code
     * @param sourceText input text
     * @return
     */
    public Single<TranslateResponse> translate(String deviceLanguageCode, String sourceText) {

        TranslateRequestBean translateRequest = new TranslateRequestBean.Builder()
                .targetLang(deviceLanguageCode)
                .sourceText(sourceText)
                .build();

        return translateAPI.translate(translateRequest);
    }

    /**
     * @param sourceLanguageCode source language code
     * @param targetLanguageCode target language code
     * @param sourceText source text
     * @return
     */
    public Single<TranslateResponse> translate(String sourceLanguageCode, String targetLanguageCode, String sourceText) {
        TranslateRequestBean translateRequest = new TranslateRequestBean.Builder()
                .sourceText(sourceText)
                .sourceLang(sourceLanguageCode)
                .targetLang(targetLanguageCode)
                .build();
        return translateAPI.translate(translateRequest);
    }
}

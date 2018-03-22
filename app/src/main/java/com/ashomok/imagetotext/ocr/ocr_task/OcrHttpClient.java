package com.ashomok.imagetotext.ocr.ocr_task;

import com.annimon.stream.Optional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 9/17/17.
 */

//singleton
public class OcrHttpClient {
    private static final String TAG =
            DEV_TAG + OcrHttpClient.class.getSimpleName();
    private static OcrHttpClient instance;
    private OcrAPI ocrAPI;
    private static final int CONNECTION_TIMEOUT_SEC = 90;

    public static OcrHttpClient getInstance() {
        if (instance == null) {
            instance = new OcrHttpClient();
        }
        return instance;
    }

    private OcrHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                .writeTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(OcrAPI.ENDPOINT)
                .build();

        ocrAPI = retrofit.create(OcrAPI.class);
    }

    /**
     * @param gcsImageUri Google cloud storage image uri,
     *                    example "gs://bucket-for-requests-test/2017-07-26-12-37-36-806-2017-07-26-12-37-36-806-ru.jpg";
     * @param languages
     * @param idToken firebase user token, docs: https://firebase.google.com/docs/auth/admin/verify-id-tokens
     * @return
     */
    public Single<OcrResponse> ocr(
            String gcsImageUri, Optional<List<String>> languages, Optional<String> idToken) {

        if (gcsImageUri != null && gcsImageUri.contains("gs://")) {
            OcrRequestBean ocrRequest = new OcrRequestBean();
            ocrRequest.setGcsImageUri(gcsImageUri);
            languages.ifPresent(l -> ocrRequest.setLanguages(l.toArray(new String[l.size()])));
            idToken.ifPresent(ocrRequest::setIdTokenString);
            return ocrAPI.ocr(ocrRequest);
        } else {
            return Single.error(new Exception("Wrong path or file not exists."));
        }
    }
}


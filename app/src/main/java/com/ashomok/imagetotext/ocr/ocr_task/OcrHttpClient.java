package com.ashomok.imagetotext.ocr.ocr_task;

import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
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

    public Single<OcrResponse> ocr(String filePath, List<String> languages) {
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            if (file.exists()) {

                // creates RequestBody instance from file
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                // MultipartBody.Part is used to send also the actual filename
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                return ocrAPI.ocr(body, languages);
            } else {
                Log.e(TAG, "file not exists");
            }
        }
        return Single.error(new Exception("file not exists"));
    }
}


package com.ashomok.imagetotext.translate_task;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 9/5/17.
 */

//singleton
public class TranslateClient {
    private static final String TAG = DEV_TAG + TranslateClient.class.getSimpleName();
    private static TranslateClient instance;
    private TranslateAPI translateAPI;

    public static TranslateClient getInstance() {
        if (instance == null) {
            instance = new TranslateClient();
        }
        return instance;
    }

    private TranslateClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(TranslateAPI.ENDPOINT)
                .build();

        translateAPI = retrofit.create(TranslateAPI.class);
    }

    public Single<SupportedLanguagesResponce> getSupportedLanguages(@NonNull String deviceLanguageCode) {
        return translateAPI.getSupportedLanguages(deviceLanguageCode);
    }

    public Single<TranslateResponse> translate(@NonNull TranslateRequestBean translateRequest) {
        return translateAPI.translate(translateRequest);
    }


//       supportedLanguages.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(supportedLanguagesResponceConsumer -> {
//                    Log.d(TAG, "Supported languages count = " + supportedLanguagesResponceConsumer.getSupportedLanguages().size());
//                });

}

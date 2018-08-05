package com.ashomok.ocrme.my_docs.get_my_docs_task;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 12/19/17.
 */

//singleton
public class MyDocsHttpClient {
    private static final String TAG = DEV_TAG + MyDocsHttpClient.class.getSimpleName();
    private static final int CONNECTION_TIMEOUT_SEC = 90;
    private static MyDocsHttpClient instance;
    private MyDocsAPI myDocsAPI;

    private MyDocsHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                .writeTimeout(CONNECTION_TIMEOUT_SEC, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MyDocsAPI.ENDPOINT)
                .build();

        myDocsAPI = retrofit.create(MyDocsAPI.class);

    }

    public static MyDocsHttpClient getInstance() {
        if (instance == null) {
            instance = new MyDocsHttpClient();
        }
        return instance;
    }

    public Single<MyDocsResponse> getMyDocs(String userToken, @Nullable String startCursor) {
        MyDocsRequestBean myDocsRequest = new MyDocsRequestBean.Builder()
                .userToken(userToken)
                .startCursor(startCursor)
                .build();

        return myDocsAPI.getMyDocs(myDocsRequest);
    }

    public Completable deleteMyDocs(List<Long> ids) {
        List<String> list = new ArrayList<>();
        for (Long id : ids) {
            list.add(String.valueOf(id));
        }
        return myDocsAPI.delete(list.toArray(new String[list.size()]));
    }
}

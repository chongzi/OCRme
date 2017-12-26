package com.ashomok.imagetotext.my_docs.get_my_docs_task;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by iuliia on 12/19/17.
 */

public interface MyDocsAPI {
    String ENDPOINT = "https://imagetotext-149919.appspot.com";

    @POST("list_ocr_requests")
    Single<MyDocsResponse> getMyDocs(@Body MyDocsRequestBean myDocsRequestBean);
}

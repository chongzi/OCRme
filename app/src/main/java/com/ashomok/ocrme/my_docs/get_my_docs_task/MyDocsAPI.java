package com.ashomok.ocrme.my_docs.get_my_docs_task;

import com.ashomok.ocrme.ocr_result.translate.translate_task.SupportedLanguagesResponse;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by iuliia on 12/19/17.
 */

public interface MyDocsAPI {
    String ENDPOINT = "https://ocrme-77a2b.appspot.com";

    @POST("list_ocr_requests")
    Single<MyDocsResponse> getMyDocs(@Body MyDocsRequestBean myDocsRequestBean);

    @DELETE("list_ocr_requests")
    Completable delete(@Query("ocr_request_ids") String[] ocr_request_ids);
}

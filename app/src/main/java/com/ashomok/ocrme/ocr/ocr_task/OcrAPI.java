package com.ashomok.ocrme.ocr.ocr_task;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by iuliia on 9/17/17.
 */

public interface OcrAPI {

    @POST("ocr_request")
    Single<OcrResponse> ocr(@Body OcrRequestBean ocrRequest);
}

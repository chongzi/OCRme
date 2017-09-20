package com.ashomok.imagetotext.ocr.ocr_task;

import com.ashomok.imagetotext.ocr_result.translate.translate_task.SupportedLanguagesResponce;
import com.ashomok.imagetotext.ocr_result.translate.translate_task.TranslateRequestBean;
import com.ashomok.imagetotext.ocr_result.translate.translate_task.TranslateResponse;

import java.util.List;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by iuliia on 9/17/17.
 */

public interface OcrAPI {
    String ENDPOINT = "https://imagetotext-149919.appspot.com";

    //curl -i -X POST -H "Content-Type: multipart/form-data" -F "file=@/home/iuliia/Documents/idea_projects/ocr_me/ocrmeGVisionAppEngine/src/test/resources/test_imgs/rus.jpg" https://imagetotext-149919.appspot.com/ocr_request?language=ru
    @Multipart
    @POST("ocr_request")
    Single<OcrResponse> ocr(@Part MultipartBody.Part file,  @Query("language") List<String> languages);

}

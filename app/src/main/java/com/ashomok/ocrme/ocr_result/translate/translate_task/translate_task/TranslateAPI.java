package com.ashomok.ocrme.ocr_result.translate.translate_task.translate_task;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by iuliia on 9/5/17.
 */


public interface TranslateAPI {

    //curl https://ocrme-77a2b.appspot.com/supported_languages?device_language_code=de
    @GET("supported_languages?")
    Single<SupportedLanguagesResponse> getSupportedLanguages(
            @Query("device_language_code") String device_language_code);


    //curl -X POST -d '{"deviceLang":"de", sourceLang":"de","targetLang":"es","sourceText":"Mit Macht kommt große Verantwortung."}' https://ocrme-77a2bgit .appspot.com/translate
    @POST("translate")
    Single<TranslateResponse> translate(@Body TranslateRequestBean translateRequest);
}

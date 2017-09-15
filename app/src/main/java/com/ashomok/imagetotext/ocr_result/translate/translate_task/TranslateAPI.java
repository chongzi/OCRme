package com.ashomok.imagetotext.ocr_result.translate.translate_task;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by iuliia on 9/5/17.
 */


public interface TranslateAPI {

    String ENDPOINT = "https://imagetotext-149919.appspot.com";

    //curl https://imagetotext-149919.appspot.com/supported_languages?device_language_code=de
    @GET("supported_languages?")
    Single<SupportedLanguagesResponce> getSupportedLanguages(
            @Query("device_language_code") String device_language_code);


    //curl -X POST -d '{"deviceLang":"de", sourceLang":"de","targetLang":"es","sourceText":"Mit Macht kommt große Verantwortung."}' https://imagetotext-149919.appspot.com/translate
    @POST("translate")
    Single<TranslateResponse> translate(@Body TranslateRequestBean translateRequest);
}

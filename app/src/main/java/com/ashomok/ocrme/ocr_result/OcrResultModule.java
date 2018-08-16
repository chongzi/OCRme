package com.ashomok.ocrme.ocr_result;

import com.ashomok.ocrme.ocr_result.translate.TranslateActivity;
import com.ashomok.ocrme.ocr_result.translate.TranslateContract;
import com.ashomok.ocrme.ocr_result.translate.TranslatePresenter;
import com.ashomok.ocrme.ocr_result.translate.translate_task.translate_task.TranslateHttpClient;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class OcrResultModule {

    @Binds
    abstract OcrResultContract.Presenter ocrResultPresenter(OcrResultPresenter presenter);
}

package com.ashomok.ocrme.ocr_result;

import com.ashomok.ocrme.ocr_result.tab_fragments.searchable_pdf.SearchablePdfFragment;
import com.ashomok.ocrme.ocr_result.tab_fragments.text.TextFragment;
import com.ashomok.ocrme.ocr_result.translate.TranslateActivity;
import com.ashomok.ocrme.ocr_result.translate.TranslateContract;
import com.ashomok.ocrme.ocr_result.translate.TranslatePresenter;
import com.ashomok.ocrme.ocr_result.translate.translate_task.translate_task.TranslateHttpClient;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class OcrResultModule {

    @Binds
    abstract OcrResultContract.Presenter ocrResultPresenter(OcrResultPresenter presenter);

    @ContributesAndroidInjector
    abstract TextFragment textFragment();

    @ContributesAndroidInjector
    abstract SearchablePdfFragment searchablePdfFragment();
}

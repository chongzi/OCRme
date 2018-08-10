package com.ashomok.ocrme.ocr_result.translate;

import android.support.annotation.StringRes;

import com.ashomok.ocrme.di_dagger.BasePresenter;
import com.ashomok.ocrme.ocr_result.translate.translate_task.translate_task.SupportedLanguagesResponse;

import java.util.List;

public interface TranslateContract  {
    interface View {

        void showError(String message);

        void showError(@StringRes int errorMessageRes);

        void showProgress(final boolean show);

        void updateTargetText(String targetText);

        void initSourceLanguagesSpinner(List<SupportedLanguagesResponse.Language> languages, String sourceLanguageCode);

        void initTargetLanguagesSpinner(List<SupportedLanguagesResponse.Language> languages, String targetLanguageCode);

        String getSourceText();


//        void showAds(); //todo add
    }

    interface Presenter extends BasePresenter<View> {
        void callTranslate();

        void updateSourceText(String sourceText);

        void updateSourceLanguageCode(int index);

        void updateTargetLanguageCode(int index);

//        void showAdsIfNeeded();  //todo add
    }
}

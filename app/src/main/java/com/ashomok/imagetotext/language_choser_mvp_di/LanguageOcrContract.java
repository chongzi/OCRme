package com.ashomok.imagetotext.language_choser_mvp_di;

/**
 * Created by iuliia on 11/15/17.
 */

/**
 * This specifies the contract between the view and the presenter.
 */
public interface LanguageOcrContract {

    interface View {

        void showCheckLanguage(String languageCode);

        void showUncheckLanguage(String languageCode);

        void setLoadingIndicator(boolean active);

    }

    interface Presenter {

        void checkLanguage(String languageCode);

        void uncheckLanguage(String languageCode);

        void takeView(View languageOcrActivity);

        void dropView();
    }
}

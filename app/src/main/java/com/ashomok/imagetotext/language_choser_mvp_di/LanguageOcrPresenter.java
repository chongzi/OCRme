package com.ashomok.imagetotext.language_choser_mvp_di;

/**
 * Created by iuliia on 11/15/17.
 */

import android.support.annotation.Nullable;

import javax.inject.Inject;

/**
 * Listens to user actions from the UI ({@link LanguageOcrActivity}), retrieves the data and updates
 * the UI as required.
 * <p>
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the TaskDetailPresenter (if it fails, it emits a compiler error). It uses

 * <p>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */

public class LanguageOcrPresenter implements LanguageOcrContract.Presenter {

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    LanguageOcrPresenter() {
    }

    @Override
    public void checkLanguage(String languageCode) {

    }

    @Override
    public void uncheckLanguage(String languageCode) {

    }

    @Override
    public void takeView(LanguageOcrContract.View languageOcrActivity) {

    }

    @Override
    public void dropView() {

    }
}

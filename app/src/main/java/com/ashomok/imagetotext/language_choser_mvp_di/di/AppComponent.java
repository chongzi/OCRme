package com.ashomok.imagetotext.language_choser_mvp_di.di;

import com.ashomok.imagetotext.language_choser_mvp_di.LanguageOcrActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by iuliia on 11/14/17.
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    LanguageOcrActivity inject(LanguageOcrActivity activity);
}

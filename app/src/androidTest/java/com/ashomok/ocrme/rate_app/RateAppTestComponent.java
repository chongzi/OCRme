package com.ashomok.ocrme.rate_app;

import com.ashomok.ocrme.di_dagger.AppComponent;
import com.ashomok.ocrme.di_dagger.ApplicationTestModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RateAppTestModule.class})
public interface RateAppTestComponent {
    void inject(RateAppAskerTest test);
}

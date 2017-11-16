package com.ashomok.imagetotext.language_choser_mvp_di.di;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by iuliia on 11/14/17.
 */

@Module
public class AppModule {
    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

//    @Provides @Singleton HelloService provideHelloService() {
//        return new HelloService();
//    }
//
//    @Provides @Singleton SchedulerProvider provideSchedulerProvider() {
//        return SchedulerProvider.DEFAULT;
//    }
}

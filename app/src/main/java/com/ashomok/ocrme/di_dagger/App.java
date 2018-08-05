package com.ashomok.ocrme.di_dagger;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.ashomok.ocrme.R;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * Created by iuliia on 11/14/17.
 */

/**
 * We create a custom {@link Application} class that extends  {@link DaggerApplication}.
 * We then override applicationInjector() which tells Dagger how to make our @Singleton Component
 * We never have to call `component.inject(this)` as {@link DaggerApplication} will do that for us.
 */
public class App extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.AppTheme);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
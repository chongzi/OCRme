package com.ashomok.imagetotext.main;

/**
 * Created by iuliia on 2/14/18.
 */

import android.app.Activity;

import com.ashomok.imagetotext.di_dagger.ActivityScoped;
import com.ashomok.imagetotext.main.billing.BillingProviderCallback;
import com.ashomok.imagetotext.my_docs.MyDocsPresenter;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link MyDocsPresenter}.
 */
@Module
public abstract class MainModule {
    @ActivityScoped
    @Binds
    abstract MainContract.Presenter mainPresenter(MainPresenter presenter);

    @Provides
    @ActivityScoped
    static Activity provideActivity(MainActivity activity) {
        return activity;
    }

    @Provides
    @ActivityScoped
    static BillingProviderCallback provideBillingProviderCallback(
            MainContract.Presenter mainPresenter) {
        return mainPresenter.getBillingProviderCallback();
    }
}

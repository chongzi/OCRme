package com.ashomok.ocrme.my_docs;

/**
 * Created by iuliia on 12/27/17.
 */

import android.support.annotation.StringRes;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.Settings;
import com.ashomok.ocrme.di_dagger.ActivityScoped;
import com.ashomok.ocrme.my_docs.get_my_docs_task.MyDocsHttpClient;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link MyDocsPresenter}.
 */
@Module
public abstract class MyDocsModule {
    @ActivityScoped
    @Binds
    abstract MyDocsContract.Presenter myDocsPresenter(MyDocsPresenter presenter);

    @Provides
    static MyDocsHttpClient provideMyDocsHttpClient() {
        return MyDocsHttpClient.getInstance();
    }

    @Provides
    @ActivityScoped
    static @StringRes
    int provideAdBannerId() {
        if (Settings.isTestMode) {
            return R.string.test_banner;
        }
        else {
            return R.string.my_docs_banner;
        }
    }
}

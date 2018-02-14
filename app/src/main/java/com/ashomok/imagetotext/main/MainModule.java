package com.ashomok.imagetotext.main;

/**
 * Created by iuliia on 2/14/18.
 */

import com.ashomok.imagetotext.di_dagger.ActivityScoped;
import com.ashomok.imagetotext.my_docs.MyDocsContract;
import com.ashomok.imagetotext.my_docs.MyDocsPresenter;

import dagger.Binds;
import dagger.Module;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link MyDocsPresenter}.
 */
@Module
public abstract class MainModule {
    @ActivityScoped
    @Binds
    abstract MainContract.Presenter mainPresenter(MainPresenter presenter);
}

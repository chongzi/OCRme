package com.ashomok.imagetotext.language_choser_mvp_di;

/**
 * Created by iuliia on 11/21/17.
 */

import android.content.Context;

import com.ashomok.imagetotext.Settings;
import com.ashomok.imagetotext.di_dagger.ActivityScoped;
import com.ashomok.imagetotext.language_choser_mvp_di.di.AllLanguageCodes;
import com.ashomok.imagetotext.language_choser_mvp_di.di.RecentlyChosenLanguageCodes;

import java.util.ArrayList;
import java.util.List;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link LanguageOcrPresenter}.
 */
@Module
public abstract class LanguageOcrModule {

    // Rather than having the activity deal with getting the intent extra and passing it to the presenter
    // we will provide the checkedLanguageCodes directly into the LanguageOcrActivitySubcomponent
    // which is what gets generated for us by Dagger.Android.
    // We can then inject our checkedLanguageCodes into our Presenter without having pass through dependency from
    // the Activity. Each UI object gets the dependency it needs and nothing else.

    @Provides
    @ActivityScoped
    static LanguageOcrActivity.LanguagesListAdapter.ResponsableList<String> provideCheckedLanguageCodes(
            LanguageOcrActivity activity) {
        return activity.getCheckedLanguageCodes();
    }

    @Provides
    @RecentlyChosenLanguageCodes
    @ActivityScoped
    static List<String> provideRecentlyChosenLanguageCodes(LanguageOcrActivity activity) {
        return activity.getRecentlyChosenLanguageCodes();
    }

    @Provides
    @AllLanguageCodes
    @ActivityScoped
    static List<String> provideAllLanguageCodes(Context context) {
        return new ArrayList<>(Settings.getOcrLanguageSupportList(context).keySet());
    }

    @ActivityScoped
    @Binds
    abstract LanguageOcrContract.Presenter languageOcrPresenter(LanguageOcrPresenter presenter);
}

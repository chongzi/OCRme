package com.ashomok.imagetotext.language_choser_mvp_di;

/**
 * Created by iuliia on 11/21/17.
 */

import com.ashomok.imagetotext.language_choser_mvp_di.di.ActivityScoped;

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
    @ActivityScoped
    static List<String> provideRecentlyChosenLanguageCodes(LanguageOcrActivity activity) {
        return activity.getRecentlyChosenLanguageCodes();
    }

    @ActivityScoped
    @Binds
    abstract LanguageOcrContract.Presenter languageOcrPresenter(LanguageOcrPresenter presenter);

}

package com.ashomok.imagetotext.di_dagger;

import com.ashomok.imagetotext.get_more_requests.GetMoreRequestsActivity;
import com.ashomok.imagetotext.get_more_requests.GetMoreRequestsModule;
import com.ashomok.imagetotext.language_choser.LanguageOcrActivity;
import com.ashomok.imagetotext.language_choser.LanguageOcrModule;
import com.ashomok.imagetotext.main.MainActivity;
import com.ashomok.imagetotext.main.MainModule;
import com.ashomok.imagetotext.my_docs.MyDocsActivity;
import com.ashomok.imagetotext.my_docs.MyDocsModule;
import com.ashomok.imagetotext.update_to_premium.UpdateToPremiumActivity;
import com.ashomok.imagetotext.update_to_premium.UpdateToPremiumModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module ActivityBindingModule is on,
 * in our case that will be AppComponent. The beautiful part about this setup is that you never need to tell AppComponent that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that AppComponent exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the specified modules and be aware of a scope annotation @ActivityScoped
 * When Dagger.Android annotation processor runs it will create 4 subcomponents for us.
 */
@Module
public abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity mainActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = LanguageOcrModule.class)
    abstract LanguageOcrActivity languageOcrActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = MyDocsModule.class)
    abstract MyDocsActivity myDocsActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = UpdateToPremiumModule.class)
    abstract UpdateToPremiumActivity updateToPremiumActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = GetMoreRequestsModule.class)
    abstract GetMoreRequestsActivity getMoreRequestsActivity();
}

package com.ashomok.ocrme.di_dagger;

import com.ashomok.ocrme.get_more_requests.GetMoreRequestsActivity;
import com.ashomok.ocrme.get_more_requests.GetMoreRequestsModule;
import com.ashomok.ocrme.language_choser.LanguageOcrActivity;
import com.ashomok.ocrme.language_choser.LanguageOcrModule;
import com.ashomok.ocrme.main.MainActivity;
import com.ashomok.ocrme.main.MainModule;
import com.ashomok.ocrme.my_docs.MyDocsActivity;
import com.ashomok.ocrme.my_docs.MyDocsModule;
import com.ashomok.ocrme.update_to_premium.UpdateToPremiumActivity;
import com.ashomok.ocrme.update_to_premium.UpdateToPremiumModule;

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

    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity mainActivity();


    @ContributesAndroidInjector(modules = LanguageOcrModule.class)
    abstract LanguageOcrActivity languageOcrActivity();


    @ContributesAndroidInjector(modules = MyDocsModule.class)
    abstract MyDocsActivity myDocsActivity();


    @ContributesAndroidInjector(modules = UpdateToPremiumModule.class)
    abstract UpdateToPremiumActivity updateToPremiumActivity();


    @ContributesAndroidInjector(modules = GetMoreRequestsModule.class)
    abstract GetMoreRequestsActivity getMoreRequestsActivity();
}

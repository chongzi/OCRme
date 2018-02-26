package com.ashomok.imagetotext.update_to_premium;

/**
 * Created by iuliia on 1/29/18.
 */

import android.app.Activity;
import android.content.Context;

import com.ashomok.imagetotext.di_dagger.ActivityScoped;
import com.ashomok.imagetotext.main.MainActivity;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link UpdateToPremiumPresenter}.
 */
@Module
public abstract class UpdateToPremiumModule {
    @ActivityScoped
    @Binds
    abstract UpdateToPremiumContract.Presenter updateToPremiumPresenter(
            UpdateToPremiumPresenter presenter);


    @Provides
    static FeaturesList provideFeaturesList(){
        return new FeaturesList();
    }

    @Provides
    static FeaturesListAdapter provideFeaturesListAdapter(FeaturesList featuresList, Context context){
        return new FeaturesListAdapter(featuresList, context);
    }

    @Provides
    @ActivityScoped
    static Activity provideActivity(UpdateToPremiumActivity activity) {
        return activity;
    }
}

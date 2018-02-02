package com.ashomok.imagetotext.update_to_premium;

/**
 * Created by iuliia on 1/29/18.
 */

import android.app.Application;
import android.content.Context;

import com.ashomok.imagetotext.di_dagger.ActivityScoped;
import com.ashomok.imagetotext.my_docs.MyDocsContract;
import com.ashomok.imagetotext.my_docs.MyDocsPresenter;
import com.ashomok.imagetotext.update_to_premium.model.FeaturesList;

import java.util.List;

import javax.inject.Singleton;

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
}

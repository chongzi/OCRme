package com.ashomok.imagetotext.get_more_requests;

/**
 * Created by iuliia on 3/2/18.
 */

import android.app.Activity;
import android.content.Context;

import com.ashomok.imagetotext.di_dagger.ActivityScoped;
import com.ashomok.imagetotext.get_more_requests.row.PromoListAdapter;
import com.ashomok.imagetotext.get_more_requests.row.PromoRowData;
import com.ashomok.imagetotext.update_to_premium.UpdateToPremiumPresenter;

import java.util.List;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link UpdateToPremiumPresenter}.
 */
@Module
public abstract class GetMoreRequestsModule {
    @ActivityScoped
    @Binds
    abstract GetMoreRequestsContract.Presenter getMoreRequestsPresenter(
            GetMoreRequestsPresenter presenter);


    @Provides
    static List<PromoRowData> providePromoList(){
        return PromoList.getList();
    }

//    @Provides
//    static PromoListAdapter providePromoListAdapter(List<PromoRowData> promoList){
//        return new PromoListAdapter(promoList);
//    }

    @Provides
    @ActivityScoped
    static Activity provideActivity(GetMoreRequestsActivity activity) {
        return activity;
    }
}

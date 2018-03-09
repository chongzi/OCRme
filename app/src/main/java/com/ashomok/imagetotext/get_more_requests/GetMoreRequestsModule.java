package com.ashomok.imagetotext.get_more_requests;

/**
 * Created by iuliia on 3/2/18.
 */

import android.app.Activity;

import com.ashomok.imagetotext.di_dagger.ActivityScoped;
import com.ashomok.imagetotext.get_more_requests.row.PromoRowData;
import com.ashomok.imagetotext.get_more_requests.row.UiManagingDelegate;
import com.ashomok.imagetotext.get_more_requests.row.task_delegates.FollowUsOnFbDelegate;
import com.ashomok.imagetotext.get_more_requests.row.task_delegates.LoginToSystemDelegate;
import com.ashomok.imagetotext.get_more_requests.row.task_delegates.RateAppDelegate;
import com.ashomok.imagetotext.get_more_requests.row.task_delegates.WatchVideoDelegate;
import com.ashomok.imagetotext.update_to_premium.UpdateToPremiumPresenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Provides
    @ActivityScoped
    static Activity provideActivity(GetMoreRequestsActivity activity) {
        return activity;
    }

    @Provides
    @ActivityScoped
    static Map<String, UiManagingDelegate> provideUiDelegates(
            LoginToSystemDelegate loginToSystemDelegate,
            WatchVideoDelegate watchVideoDelegate,
            RateAppDelegate rateAppDelegate,
            FollowUsOnFbDelegate followUsOnFbDelegate){
        Map<String, UiManagingDelegate> uiDelegates = new HashMap<>();
        uiDelegates.put(WatchVideoDelegate.ID, watchVideoDelegate);
        uiDelegates.put(LoginToSystemDelegate.ID, loginToSystemDelegate);
        uiDelegates.put(RateAppDelegate.ID, rateAppDelegate);
        uiDelegates.put(FollowUsOnFbDelegate.ID, followUsOnFbDelegate);
        return uiDelegates;
    }

}

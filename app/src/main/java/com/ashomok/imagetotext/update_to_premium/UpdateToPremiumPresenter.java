package com.ashomok.imagetotext.update_to_premium;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ashomok.imagetotext.my_docs.get_my_docs_task.MyDocsHttpClient;

import javax.inject.Inject;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;
import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by iuliia on 1/29/18.
 */

public class UpdateToPremiumPresenter implements UpdateToPremiumContract.Presenter {

    public static final String TAG = DEV_TAG + UpdateToPremiumPresenter.class.getSimpleName();
    @Nullable
    private UpdateToPremiumContract.View view;


    @Inject
    UpdateToPremiumPresenter() {
    }

    @Override
    public void takeView(UpdateToPremiumContract.View updateToPremiumActivity) {
        view = updateToPremiumActivity;
    }

    @Override
    public void dropView() {
        view = null;
    }
}

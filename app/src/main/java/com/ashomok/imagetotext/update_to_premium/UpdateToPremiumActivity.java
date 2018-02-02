package com.ashomok.imagetotext.update_to_premium;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ashomok.imagetotext.R;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 1/29/18.
 */

public class UpdateToPremiumActivity extends RxAppCompatActivity
        implements View.OnClickListener, UpdateToPremiumContract.View {

    private static final String TAG = DEV_TAG + UpdateToPremiumActivity.class.getSimpleName();
    @Inject
    UpdateToPremiumPresenter mPresenter;

    @Inject
    FeaturesListAdapter featuresListAdapter;

    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this); //todo or extends daggerappcompat activity instaead
        setContentView(R.layout.activity_update_to_premium);
        initToolbar();
        initFeaturesList();
        mPresenter.takeView(this);
    }

    private void initFeaturesList() {
        recyclerView = findViewById(R.id.premium_features_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager recentlyChosenLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recentlyChosenLayoutManager);

        recyclerView.setAdapter(featuresListAdapter);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Show CollapsingToolbarLayout Title ONLY when collapsed
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getResources().getString(R.string.update_to_premium));
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void showError(int errorMessageRes) {

    }

    @Override
    public void showInfo(int infoMessageRes) {

    }
}


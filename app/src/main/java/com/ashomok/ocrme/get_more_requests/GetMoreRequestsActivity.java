package com.ashomok.ocrme.get_more_requests;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ashomok.ocrme.OcrRequestsCounter;
import com.ashomok.ocrme.R;
import com.ashomok.ocrme.Settings;
import com.ashomok.ocrme.billing.model.SkuRowData;
import com.ashomok.ocrme.firebaseUiAuth.BaseLoginActivity;
import com.ashomok.ocrme.get_more_requests.row.PromoListAdapter;
import com.ashomok.ocrme.utils.InfoSnackbarUtil;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/2/18.
 */

public class GetMoreRequestsActivity extends BaseLoginActivity
        implements GetMoreRequestsContract.View {

    private static final String TAG = DEV_TAG + GetMoreRequestsActivity.class.getSimpleName();
    @Inject
    GetMoreRequestsPresenter mPresenter;

    @Inject
    PromoListAdapter promoListAdapter;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    OcrRequestsCounter ocrRequestsCounter;

    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this); //todo or extends daggerappcompat activity instaead
        setContentView(R.layout.activity_get_more_requests);
        mRootView = findViewById(android.R.id.content);
        if (mRootView == null) {
            mRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        }
        initToolbar();
        updateToolbarText();
        initPromoList();
        mPresenter.takeView(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        promoListAdapter.notifyDataSetChanged();
        updateToolbarText();
    }

    @Override
    public void updateUi(boolean isUserSignedIn) {
        //ignore
    }

    @Override
    public void updateToolbarText() {
        TextView youHaveRequestsTextView = findViewById(R.id.you_have_requests_text);
        youHaveRequestsTextView.setText(
                getString(R.string.you_have_n_requests,
                        String.valueOf(ocrRequestsCounter.getAvailableOcrRequests())
                ));
    }

    private void initPromoList() {
        RecyclerView recyclerView = findViewById(R.id.promo_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(promoListAdapter);
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
                    collapsingToolbar.setTitle(getResources().getString(R.string.get_free_requests));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void showError(int errorMessageRes) {
        InfoSnackbarUtil.showError(errorMessageRes, mRootView);
    }

    @Override
    public void showInfo(int infoMessageRes) {
        InfoSnackbarUtil.showInfo(infoMessageRes, mRootView);
    }

    @Override
    public void showInfo(String message) {
        InfoSnackbarUtil.showInfo(message, mRootView);
    }

    @Override
    public void initBuyRequestsRow(SkuRowData item) {
        View paidOptionLayout = findViewById(R.id.paid_option);
        TextView price = findViewById(R.id.price);
        price.setText(item.getPrice());
        paidOptionLayout.setOnClickListener(view -> mPresenter.onBuyRequestsClicked(item));
    }
}

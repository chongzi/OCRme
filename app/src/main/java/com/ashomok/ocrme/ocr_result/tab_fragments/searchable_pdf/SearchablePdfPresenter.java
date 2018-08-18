package com.ashomok.ocrme.ocr_result.tab_fragments.searchable_pdf;

import android.support.annotation.Nullable;

import com.ashomok.ocrme.ocr_result.tab_fragments.text.TextContract;
import com.ashomok.ocrme.ocr_result.tab_fragments.text.TextPresenter;

import javax.inject.Inject;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

public class SearchablePdfPresenter implements SearchablePdfContract.Presenter{
    public static final String TAG = DEV_TAG + TextPresenter.class.getSimpleName();

    @Nullable
    private SearchablePdfContract.View view;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    SearchablePdfPresenter() { }

    @Override
    public void takeView(SearchablePdfContract.View view) {
        this.view = view;
    }

    @Override
    public void dropView() {
        view = null;
    }
}

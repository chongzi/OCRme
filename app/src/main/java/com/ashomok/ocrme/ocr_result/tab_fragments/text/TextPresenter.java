package com.ashomok.ocrme.ocr_result.tab_fragments.text;

import android.support.annotation.Nullable;

import javax.inject.Inject;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;


//currently redundant - exist for clean architecture
public class TextPresenter implements TextContract.Presenter {

    public static final String TAG = DEV_TAG + TextPresenter.class.getSimpleName();

    @Nullable
    private TextContract.View view;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    TextPresenter() { }

    @Override
    public void takeView(TextContract.View view) {
        this.view = view;
    }

    @Override
    public void dropView() {
        view = null;
    }
}
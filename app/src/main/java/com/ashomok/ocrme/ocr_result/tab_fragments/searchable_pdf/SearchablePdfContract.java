package com.ashomok.ocrme.ocr_result.tab_fragments.searchable_pdf;

import com.ashomok.ocrme.di_dagger.BasePresenter;
import com.ashomok.ocrme.ocr_result.tab_fragments.text.TextContract;

public interface SearchablePdfContract {
    interface View { }

    interface Presenter extends BasePresenter<SearchablePdfContract.View> { }
}

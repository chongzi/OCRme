package com.ashomok.ocrme.ocr_result.tab_fragments.text;

import com.ashomok.ocrme.di_dagger.BasePresenter;
import com.ashomok.ocrme.ocr_result.OcrResultContract;

public interface TextContract {
    interface View { }
    interface Presenter extends BasePresenter<TextContract.View> { }
}

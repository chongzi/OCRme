package com.ashomok.imagetotext.update_to_premium;

import android.support.annotation.StringRes;

import com.ashomok.imagetotext.di_dagger.BasePresenter;
import com.ashomok.imagetotext.my_docs.MyDocsContract;
import com.ashomok.imagetotext.ocr.ocr_task.OcrResult;

import java.util.List;

/**
 * Created by iuliia on 1/29/18.
 */

/**
 * This specifies the contract between the view and the presenter.
 */
public interface UpdateToPremiumContract {
    interface View {

        void showError(@StringRes int errorMessageRes);

        void showInfo (@StringRes int infoMessageRes);
    }

    interface Presenter extends BasePresenter<UpdateToPremiumContract.View> {
    }
}

package com.ashomok.imagetotext.language_choser_mvp_di;

/**
 * Created by iuliia on 11/15/17.
 */

import java.util.List;

import com.ashomok.imagetotext.di_dagger.BasePresenter;
import com.ashomok.imagetotext.language_choser_mvp_di.LanguageOcrActivity.LanguagesListAdapter.ResponsableList;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface LanguageOcrContract {

    interface View {

        void showRecentlyChosenLanguages(List<String> recentlyChosenLanguageCodes,
                ResponsableList<String> checkedLanguageCodes);

        void showAllLanguages(List<String> allLanguageCodes,
                              ResponsableList<String> checkedLanguageCodes);

        void updateAutoView(boolean isAuto);

        void initAutoBtn();
    }

    interface Presenter extends BasePresenter<View> {
    }
}

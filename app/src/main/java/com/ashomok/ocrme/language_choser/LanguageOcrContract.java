package com.ashomok.ocrme.language_choser;

/**
 * Created by iuliia on 11/15/17.
 */

import com.ashomok.ocrme.di_dagger.BasePresenter;
import com.ashomok.ocrme.language_choser.LanguagesListAdapter.ResponsableList;

import java.util.List;

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

package com.ashomok.imagetotext.my_docs;

/**
 * Created by iuliia on 1/10/18.
 */

import android.support.annotation.StringRes;

import com.ashomok.imagetotext.di_dagger.BasePresenter;
import com.ashomok.imagetotext.language_choser_mvp_di.LanguageOcrContract;
import com.ashomok.imagetotext.language_choser_mvp_di.LanguagesListAdapter;
import com.ashomok.imagetotext.my_docs.get_my_docs_task.MyDocsResponse;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface MyDocsContract {
    interface View {

        void showAllDocs(List<MyDocsResponse.MyDoc> data);

        void choseDocs(List<MyDocsResponse.MyDoc> choseDocs);

        void deleteDocs(List<MyDocsResponse.MyDoc> deleteDocs);

        void showError(@StringRes int errorMessageRes);

        void addNewLoadedDocs(List<MyDocsResponse.MyDoc> newLoadedDocs);
    }

    interface Presenter extends BasePresenter<MyDocsContract.View> {
        void loadMoreDocs();
    }
}

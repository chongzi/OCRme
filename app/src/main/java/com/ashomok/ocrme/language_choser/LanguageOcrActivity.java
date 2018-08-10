package com.ashomok.ocrme.language_choser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.ashomok.ocrme.R;
import com.ashomok.ocrme.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

import static com.ashomok.ocrme.utils.InfoSnackbarUtil.showInfo;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 10/22/17.
 */

//todo add tests
//MINOR todo add search view https://developer.android.com/training/search/search.html (add add async loader firstly because of technical reasons)
//MINOR todo add async loader for fill recyclerviews LoaderManager.LoaderCallbacks<List<String>>
public class LanguageOcrActivity extends DaggerAppCompatActivity implements LanguageOcrContract.View {
    public static final String CHECKED_LANGUAGE_CODES = "checked_languages_set";
    private static final String TAG = DEV_TAG + LanguageOcrActivity.class.getSimpleName();
    @Inject
    LanguageOcrPresenter mPresenter;
    @Inject
    SharedPreferences mSharedPreferences;
    private List<String> recentlyChosenLanguageCodes;
    private boolean isAuto;
    private LanguagesListAdapter.ResponsableList<String> checkedLanguageCodes;
    private LanguagesListAdapter allLangAdapter;
    private LanguagesListAdapter recentlyChosenLangAdapter;
    private StateChangedNotifier notifier = isAutoChecked -> {
        if (!isAutoChecked) {
            isAuto = false;
            updateAutoView(isAuto);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_ocr);
        initToolbar();

        @Nullable List<String> list = obtainCheckedLanguageCodes();
        checkedLanguageCodes = (list == null) ?
                new LanguagesListAdapter.ResponsableList<>(new ArrayList<>())
                : new LanguagesListAdapter.ResponsableList<>(list);

        checkedLanguageCodes.addOnListChangedListener(o -> showInfo(
                getString(R.string.languages_selected, String.valueOf(checkedLanguageCodes.size())),
                findViewById(R.id.base_view)));
        recentlyChosenLanguageCodes = obtainRecentlyChosenLanguageCodes();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.takeView(this); //because presenter use Lazy - so need to wait while activity created
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();  //prevent leaking activity in
        // case presenter is orchestrating a long running task
    }

    /**
     * obtain recently chosen Languages from SharedPreferences in order: first - the most recently chosen.
     * Max 5 recently chosen Languages allowed.
     *
     * @return recently chosen Languages
     */
    @NonNull
    private List<String> obtainRecentlyChosenLanguageCodes() {

        String tag = getString(R.string.ocr_recently_chosen_languge_codes);
        List<String> recentlyChosenLanguageCodes = SharedPreferencesUtil.pullStringList(
                mSharedPreferences, tag);
        if (recentlyChosenLanguageCodes == null) {
            recentlyChosenLanguageCodes = new ArrayList<>();
        }
        return recentlyChosenLanguageCodes;
    }


    /**
     * if returns null - auto detection is checked
     *
     * @return checked language keys or null, which means auto detection is checked
     */
    private @Nullable
    List<String> obtainCheckedLanguageCodes() {
        Intent intent = getIntent();
        ArrayList<String> extra = intent.getStringArrayListExtra(CHECKED_LANGUAGE_CODES);
        if (extra != null) {
            return extra;
        } else {
            return null;
        }
    }

    /**
     * call before finish activity
     */
    private void saveRecentlyChosenLanguages() {
        LinkedHashSet<String> languagesSet = new LinkedHashSet<>();
        languagesSet.addAll(checkedLanguageCodes);
        languagesSet.addAll(recentlyChosenLanguageCodes);

        List<String> languagesSubList =
                Stream.of(languagesSet).limit(5).collect(Collectors.toList());

        SharedPreferencesUtil.pushStringList(mSharedPreferences,
                languagesSubList, getString(R.string.ocr_recently_chosen_languge_codes));
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            //back btn pressed
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        saveRecentlyChosenLanguages();
        if (checkedLanguageCodes != null) {
            intent.putExtra(CHECKED_LANGUAGE_CODES, checkedLanguageCodes);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    @Override
    public void showRecentlyChosenLanguages(
            List<String> recentlyChosenLanguageCodes,
            LanguagesListAdapter.ResponsableList<String> checkedLanguageCodes) {

        //init recently chosen language list

        View recentlyChosen = findViewById(R.id.recently_chosen);
        recentlyChosen.setVisibility(View.VISIBLE);

        RecyclerView recyclerViewRecentlyChosen = findViewById(R.id.recently_chosen_list);
        recyclerViewRecentlyChosen.setHasFixedSize(true);
        LinearLayoutManager recentlyChosenLayoutManager = new LinearLayoutManager(this);
        recyclerViewRecentlyChosen.setLayoutManager(recentlyChosenLayoutManager);

        recentlyChosenLangAdapter = new LanguagesListAdapter(
                recentlyChosenLanguageCodes, checkedLanguageCodes, notifier);
        recyclerViewRecentlyChosen.setAdapter(recentlyChosenLangAdapter);
    }

    @Override
    public void showAllLanguages(
            List<String> allLanguageCodes,
            LanguagesListAdapter.ResponsableList<String> checkedLanguageCodes) {

        //init all languages list
        RecyclerView recyclerViewAllLanguages = findViewById(R.id.all_languages_list);
        recyclerViewAllLanguages.setHasFixedSize(true);
        LinearLayoutManager allLanguagesLayoutManager = new LinearLayoutManager(this);
        recyclerViewAllLanguages.setLayoutManager(allLanguagesLayoutManager);
        allLangAdapter = new LanguagesListAdapter(
                allLanguageCodes, checkedLanguageCodes, notifier);
        recyclerViewAllLanguages.setAdapter(allLangAdapter);
    }

    @Override
    public void updateAutoView(boolean isAuto) {
        View checkedIcon = findViewById(R.id.checked_icon);
        checkedIcon.setVisibility(isAuto ? View.VISIBLE : View.GONE);
        View autoIcon = findViewById(R.id.auto_icon);
        autoIcon.setVisibility(isAuto ? View.GONE : View.VISIBLE);
    }

    @Override
    public void initAutoBtn() {
        LinearLayout autoBtn = findViewById(R.id.auto);

        autoBtn.setOnClickListener(view -> {
            isAuto = !isAuto;
            updateAutoView(isAuto);

            if (recentlyChosenLangAdapter != null) {
                recentlyChosenLangAdapter.onAutoStateChanged(isAuto);
            }
            if (allLangAdapter != null) {
                allLangAdapter.onAutoStateChanged(isAuto);
            }
        });
    }

    public LanguagesListAdapter.ResponsableList<String> getCheckedLanguageCodes() {
        return checkedLanguageCodes;
    }

    public List<String> getRecentlyChosenLanguageCodes() {
        return recentlyChosenLanguageCodes;
    }
}

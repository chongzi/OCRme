package com.ashomok.imagetotext.language_choser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.Settings;
import com.ashomok.imagetotext.utils.LogUtil;
import com.ashomok.imagetotext.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by iuliia on 10/22/17.
 */

//todo add search view
//https://developer.android.com/training/search/search.html

    //todo add async loader for fill recyclerviews LoaderManager.LoaderCallbacks<List<EN>>
public class LanguageOcrActivity extends AppCompatActivity {
    private static final String TAG = LogUtil.DEV_TAG + LanguageOcrActivity.class.getSimpleName();
    public static final String CHECKED_LANGUAGE_CODES = "checked_languages_set";
    private List<String> recentlyChosenLanguages;
    private boolean isAuto;
    private LanguagesListAdapter.ResponsableList<String> checkedLanguages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_ocr);

        initToolbar();

        @Nullable List<String> list = obtainCheckedLanguagesList();
        checkedLanguages = (list == null) ?
                new LanguagesListAdapter.ResponsableList<>(new ArrayList<>())
                : new LanguagesListAdapter.ResponsableList<>(list);

        StateChangedNotifier notifier = isAutoChecked -> {
            if (!isAutoChecked) {
                isAuto = false;
                updateAutoUi(isAuto);
            }
        };

        //init recently chosen language list
        recentlyChosenLanguages = obtainRecentlyChosenLanguagesList();
        LanguagesListAdapter recentlyChosenLangAdapter = null;
        if ( recentlyChosenLanguages.size()>0) {
            View recentlyChosen = findViewById(R.id.recently_chosen);
            recentlyChosen.setVisibility(View.VISIBLE);
            RecyclerView recyclerViewRecentlyChosen = findViewById(R.id.recently_chosen_list);
            recyclerViewRecentlyChosen.setHasFixedSize(true);
            LinearLayoutManager recentlyChosenLayoutManager = new LinearLayoutManager(this);
            recyclerViewRecentlyChosen.setLayoutManager(recentlyChosenLayoutManager);

           recentlyChosenLangAdapter = new LanguagesListAdapter(
                    recentlyChosenLanguages, checkedLanguages, notifier);
            recyclerViewRecentlyChosen.setAdapter(recentlyChosenLangAdapter);
        }

        //init all languages list
        RecyclerView recyclerViewAllLanguages = findViewById(R.id.all_languages_list);
        recyclerViewAllLanguages.setHasFixedSize(true);
        LinearLayoutManager allLanguagesLayoutManager = new LinearLayoutManager(this);
        recyclerViewAllLanguages.setLayoutManager(allLanguagesLayoutManager);
        List<String> allLanguages = obtainAllLanguagesList();
        LanguagesListAdapter allLangAdapter = new LanguagesListAdapter(
                allLanguages, checkedLanguages, notifier);
        recyclerViewAllLanguages.setAdapter(allLangAdapter);

        //init auto btn
        if (checkedLanguages.size() < 1) {
            //check auto btn
            isAuto = true;
        }
        LinearLayout autoBtn = findViewById(R.id.auto);
        updateAutoUi(isAuto);

        LanguagesListAdapter finalRecentlyChosenLangAdapter = recentlyChosenLangAdapter;
        autoBtn.setOnClickListener(view -> {
            isAuto = !isAuto;
            updateAutoUi(isAuto);

            if (finalRecentlyChosenLangAdapter != null) {
                finalRecentlyChosenLangAdapter.onAutoStateChanged(isAuto);
            }
            allLangAdapter.onAutoStateChanged(isAuto);
        });
    }

    private List<String> obtainAllLanguagesList() {
        return new ArrayList<>(Settings.getOcrLanguageSupportList(this).values());
    }

    /**
     * obtain recently chosen Languages from SharedPreferences in order: first - the most recently chosen.
     * Max 5 recently chosen Languages allowed.
     *
     * @return recently chosen Languages
     */
    private
    List<String> obtainRecentlyChosenLanguagesList() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return SharedPreferencesUtil.pullStringList(
                sharedPref, getString(R.string.recently_chosen_languges_list));
    }

    private @Nullable
    List<String> obtainCheckedLanguagesList() {
        Set<String> checkedLanguageKeys = obtainCheckedLanguageKeys();
        if (checkedLanguageKeys == null) {
            return null;
        } else {
            Map<String, String> allLanguages = Settings.getOcrLanguageSupportList(this);

            return Stream.of(checkedLanguageKeys)
                    .filter(allLanguages::containsKey)
                    .map(allLanguages::get)
                    .collect(Collectors.toList());
        }
    }

    /**
     * if returns null - auto detection is checked
     *
     * @return checked language keys or null, which means auto detection is checked
     */
    private @Nullable
    Set<String> obtainCheckedLanguageKeys() {
        Intent intent = getIntent();
        ArrayList<String> extra = intent.getStringArrayListExtra(CHECKED_LANGUAGE_CODES);
        if (extra != null) {
            return new HashSet<>(extra);
        } else {
            return null;
        }
    }

    /**
     * call before finish activity
     */
    private void saveRecentlyChosenLanguages() {
        LinkedHashSet<String> languagesSet = new LinkedHashSet<>();
        languagesSet.addAll(checkedLanguages);
        languagesSet.addAll(recentlyChosenLanguages);

        List<String> languagesSubList =
                Stream.of(languagesSet).limit(5).collect(Collectors.toList());

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferencesUtil.pushStringList(sharedPref,
                languagesSubList, getString(R.string.recently_chosen_languges_list));
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            //back btn pressed
            Intent intent = new Intent();

            ArrayList<String> languageCodes = new ArrayList<>();
            saveRecentlyChosenLanguages();
//            languageCodes.addAll(adapter.getCheckedLanguages()); //// TODO: 10/22/17
            intent.putExtra(CHECKED_LANGUAGE_CODES, languageCodes);
            setResult(RESULT_OK, intent); //todo reduntant?

            finish();
        });
    }

    void updateAutoUi(boolean checked) {
        View checkedIcon = findViewById(R.id.checked_icon);
        checkedIcon.setVisibility(checked ? View.VISIBLE : View.GONE);
        View autoIcon = findViewById(R.id.auto_icon);
        autoIcon.setVisibility(checked ? View.GONE : View.VISIBLE);
    }

    public interface StateChangedNotifier {
        void changeAutoState(boolean isAutoChecked);
    }
}

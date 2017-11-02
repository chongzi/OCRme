package com.ashomok.imagetotext.language_choser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.Settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by iuliia on 10/22/17.
 */

//todo add search view
//https://developer.android.com/training/search/search.html

//todo use recyclerview instead
public class LanguageOcrActivity extends AppCompatActivity {
    private static final String TAG = LanguageOcrActivity.class.getSimpleName();
    public static final String CHECKED_LANGUAGE_CODES = "checked_languages_set";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_ocr);

        initToolbar();

        List<String> checkedLanguages = obtainCheckedLanguagesList();

        //init recently chosen language list
        ListView listViewRecentlyChosen = findViewById(R.id.recently_chosen_list);
        List<String> recentlyChosenLanguagesList = obtainRecentlyChosenLanguagesList();
        LanguagesListAdapter recentlyChosenLangAdapter = new LanguagesListAdapter(
                recentlyChosenLanguagesList, checkedLanguages);
        listViewRecentlyChosen.setAdapter(recentlyChosenLangAdapter);

        //init all languages list
        ListView listViewAllLanguages = findViewById(R.id.all_languages_list);
        List<String> allLanguages = obtainAllLanguagesList();
        LanguagesListAdapter allLangAdapter = new LanguagesListAdapter(
                allLanguages, checkedLanguages);
        listViewAllLanguages.setAdapter(allLangAdapter);
    }

    //todo order may nor garantee? check it
    private List<String> obtainAllLanguagesList() {
        return new ArrayList<>(Settings.getOcrLanguageSupportList(this).values());
    }

    //todo order may nor garantee? check it
    private List<String> obtainRecentlyChosenLanguagesList() {
        LinkedList<String> recentlyChosenLanguageKeys = obtainRecentlyChosenLanguageKeys();
        if (recentlyChosenLanguageKeys == null) {
            return null;
        } else {
            Map<String, String> allLanguages = Settings.getOcrLanguageSupportList(this);

            List<String> result = Stream.of(recentlyChosenLanguageKeys)
                    .filter(allLanguages::containsKey)
                    .collect(Collectors.toList());

            return result;
        }
    }

    private LinkedHashMap<String, String> obtainAllLanguages() {
        return Settings.getOcrLanguageSupportList(this);
    }

    /**
     * obtain recently chosen Languages from SharedPreferences in order: first - the most recently chosen.
     * Max 5 recently chosen Languages allowed.
     *
     * @return
     */
    private @Nullable
    LinkedHashMap<String, String> obtainRecentlyChosenLanguageMap() {
        LinkedList<String> recentlyChosenLanguageKeys = obtainRecentlyChosenLanguageKeys();
        if (recentlyChosenLanguageKeys == null) {
            return null;
        } else {
            Map<String, String> allLanguages = Settings.getOcrLanguageSupportList(this);

            LinkedHashMap<String, String> result = Stream.of(recentlyChosenLanguageKeys)
                    .filter(allLanguages::containsKey)
                    .collect(
                            Collectors.toMap(i -> i, allLanguages::get, LinkedHashMap::new));

            return result;
        }
    }

    private @Nullable
    List<String> obtainCheckedLanguagesList() {
        Set<String> checkedLanguageKeys = obtainCheckedLanguageKeys();
        if (checkedLanguageKeys == null) {
            return null;
        } else {
            Map<String, String> allLanguages = Settings.getOcrLanguageSupportList(this);

            List<String> result = Stream.of(checkedLanguageKeys)
                    .filter(allLanguages::containsKey)
                    .collect(Collectors.toList());

            return result;
        }
    }

    /**
     * @return
     */
    private @Nullable
    Map<String, String> obtainCheckedLanguages() {
        Set<String> checkedLanguageKeys = obtainCheckedLanguageKeys();
        if (checkedLanguageKeys == null) {
            return null;
        } else {
            Map<String, String> allLanguages = Settings.getOcrLanguageSupportList(this);

            Map<String, String> result = Stream.of(checkedLanguageKeys)
                    .filter(allLanguages::containsKey)
                    .collect(
                            Collectors.toMap(i -> i, allLanguages::get));

            return result;
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
        HashSet<String> checkedLanguages =
                new HashSet<>(intent.getStringArrayListExtra(CHECKED_LANGUAGE_CODES));
        return checkedLanguages;
    }

    /**
     * obtain recently chosen Language Keys from SharedPreferences in order: first - the most recently chosen.
     * Max 5 recently chosen Language Keys allowed.
     *
     * @return recently chosen Language Keys
     */
    private LinkedList<String> obtainRecentlyChosenLanguageKeys() {

        //todo
//        There's actually a Stack class: http://java.sun.com/j2se/1.5.0/docs/api/java/util/Stack.html
//        If you don't want to use that, the LinkedList class (http://java.sun.com/j2se/1.5.0/docs/api/java/util/LinkedList.html) has addFirst and addLast and  removeFirst and removeLast methods, making it perfect for use as a stack or queue class.

        LinkedList<String> recentlyChosenLanguageKeys = new LinkedList<>();
        recentlyChosenLanguageKeys.addFirst("ru");
        recentlyChosenLanguageKeys.addFirst("en");
        return recentlyChosenLanguageKeys;
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
//            languageCodes.addAll(adapter.getCheckedLanguages()); //// TODO: 10/22/17
            intent.putExtra(CHECKED_LANGUAGE_CODES, languageCodes);
            setResult(RESULT_OK, intent); //todo reduntant?

            finish();
        });
    }

}

package com.ashomok.imagetotext.language;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ashomok.imagetotext.App;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.ashomok.imagetotext.language.LanguageList.CHECKED_LANGUAGES;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by iuliia on 12/20/16.
 * <p>
 * how to add coverage metrics
 * ./gradlew createDebugCoverageReport
 * http://stackoverflow.com/questions/29133761/jacoco-code-coverage-in-android-studio
 * report path - app/build/reports/coverage/debug/index.html
 */
@RunWith(AndroidJUnit4.class)
public class LanguageListTest {

    private LanguageList instance;

    @Before
    public void setup() {
        instance = LanguageList.getInstance();
    }


    @Test
    public void getChecked() throws Exception {
        LinkedHashSet<Language> languages = instance.getLanguages();
        for (Language language : languages) {
            language.setChecked(true);
        }

        LinkedHashSet<Language> checked = instance.getChecked();
        for (Language language : checked) {
            assertTrue(language.isChecked());
        }

        assertTrue(languages.size() == (checked.size() + 1)); //all except auto

    }

    @Test
    public void obtainDataFromSharedPreferances() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        assertNotNull(context);

        //test1
        LinkedHashSet<Language> data1 = new LinkedHashSet<>();
        data1.add(new Language("German", "de"));
        data1.add(new Language("Greek", "el"));

        instance.putDataToSharedPreferances(data1);
        LinkedHashSet<Language> result = instance.obtainDataFromSharedPreferances();
        assertTrue(result.size() == data1.size());

        //test2
        LinkedHashSet<Language> empty = new LinkedHashSet<>();
        instance.putDataToSharedPreferances(empty);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> checkedLanguagesNames = sharedPref.getStringSet(CHECKED_LANGUAGES, null);
        assertTrue(checkedLanguagesNames.size() == 0);

        //test3
        LinkedHashSet<Language> data2 = new LinkedHashSet<>();
        data2.add(instance.getDefaultLanguage());
        instance.putDataToSharedPreferances(data2);
        LinkedHashSet<Language> result2 = instance.obtainDataFromSharedPreferances();
        assertTrue(result2.size() == data2.size());

        //test4
        data2.add(new Language("Greek", "el"));
        instance.putDataToSharedPreferances(data2);
        LinkedHashSet<Language> result3 = instance.obtainDataFromSharedPreferances();
        assertTrue(result3.size() == 2);
    }

    @Test
    public void putDataToSharedPreferences() throws Exception {
        LinkedHashSet<Language> data = new LinkedHashSet<>();
        data.add(new Language("German", "de"));
        data.add(new Language("Greek", "el"));

        instance.putDataToSharedPreferances(data);

        Context context = InstrumentationRegistry.getTargetContext();
        assertNotNull(context);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> checkedLanguagesNames = sharedPref.getStringSet(CHECKED_LANGUAGES, null);
        assertNotNull(checkedLanguagesNames);
        assertTrue(checkedLanguagesNames.size() == data.size());
    }

}

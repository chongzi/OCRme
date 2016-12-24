package com.ashomok.imagetotext.language;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.VisibleForTesting;

import com.ashomok.imagetotext.App;
import com.ashomok.imagetotext.R;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by iuliia on 12/17/16.
 */

//singleton
public class LanguageList {

    public static final String CHECKED_LANGUAGES = "checked_languages";
    private static LanguageList instance;
    private LinkedHashSet<Language> languages;

    public LinkedHashSet<Language> getLanguages() {
        return languages;
    }

    public static LanguageList getInstance() {
        if (instance == null) {
            instance = new LanguageList();
        }
        return instance;
    }

    public Language getDefaultLanguage() {
        return new Language(App.getContext().getString(R.string.auto), null);
    }

    public LinkedHashSet<Language> getChecked() {
        LinkedHashSet<Language> result = new LinkedHashSet<>();
        for (Language l : languages) {
            if (l.isChecked()) {
                result.add(l);
            }
        }
        if (result.size() > 1 && result.contains(getDefaultLanguage())) {
            result.remove(getDefaultLanguage());
            putDataToSharedPreferances(result);
        } else if (result.size() <= 1) {
            //try to get value from shared preferences
            result = obtainDataFromSharedPreferances();
        }
        return result;
    }

    @VisibleForTesting
    protected void putDataToSharedPreferances(Set<Language> data) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> checkedLanguages = new HashSet<>();
        for (Language l : data) {
            checkedLanguages.add(l.getName());
        }
        editor.putStringSet(CHECKED_LANGUAGES, checkedLanguages);
        editor.apply();
    }

    /**
     * Obtain data from shared preferances of provide default value
     * @return
     */
    @VisibleForTesting
    protected LinkedHashSet<Language> obtainDataFromSharedPreferances() {
        LinkedHashSet<Language> result = new LinkedHashSet<>();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        Set<String> checkedLanguagesNames = sharedPref.getStringSet(CHECKED_LANGUAGES, null);

        if (checkedLanguagesNames != null) {

            Iterator<Language> languagesIterator = languages.iterator();
            while (languagesIterator.hasNext()) {
                Language language = languagesIterator.next();
                Iterator<String> checkedLanguagesNamesIterator = checkedLanguagesNames.iterator();
                while (checkedLanguagesNamesIterator.hasNext()) {
                    String checkedLanguageName = checkedLanguagesNamesIterator.next();
                    if (checkedLanguageName.equals(language.getName())) {
                        result.add(language);
                    }
                }
            }
        }

        //if empty - provide default value
        if (result.size() < 1) {
            result.add(getDefaultLanguage());
        }
        return result;
    }

    //todo add english language equals auto
    private LanguageList() {
        final Context context = App.getContext();
        languages = new LinkedHashSet<Language>() {{
            add(new Language(context.getString(R.string.auto), null, true));
            add(new Language(context.getString(R.string.afrikaans), "af"));
            add(new Language(context.getString(R.string.arabic), "ar"));
            add(new Language(context.getString(R.string.assamese), "as"));
            add(new Language(context.getString(R.string.azarbaijani), "az"));
            add(new Language(context.getString(R.string.belarusian), "be"));
            add(new Language(context.getString(R.string.bengali), "bn"));
            add(new Language(context.getString(R.string.bulgarian), "bg"));
            add(new Language(context.getString(R.string.catalan), "ca"));
            add(new Language(context.getString(R.string.chinese), "zh"));
            add(new Language(context.getString(R.string.croatin), "hr"));
            add(new Language(context.getString(R.string.czech), "cs"));
            add(new Language(context.getString(R.string.danish), "da"));
            add(new Language(context.getString(R.string.dutch), "nl"));
            add(new Language(context.getString(R.string.estonian), "et"));
            add(new Language("Finnish", "fi"));
            add(new Language("French", "fr"));
            add(new Language("German", "de"));
            add(new Language("Greek", "el"));
            add(new Language("Hebrew", "he"));
            add(new Language("Hindi", "hi"));
            add(new Language("Hungarian", "hu"));
            add(new Language("Icelandic", "is"));
            add(new Language("Indonesian", "id"));
            add(new Language("Italian", "it"));
            add(new Language("Japanese", "ja"));
            add(new Language("Kazakh", "kk"));
            add(new Language("Korean", "ko"));
            add(new Language("Kyrgyz", "ky"));
            add(new Language("Latvian", "lv"));
            add(new Language("Lithuanian", "lt"));
            add(new Language("Macedonian", "mk"));
            add(new Language("Marathi", "mr"));
            add(new Language("Mongolian", "mn"));
            add(new Language("Nepali", "ne"));
            add(new Language("Norwegian", "no"));
            add(new Language("Pashtu", "ps"));
            add(new Language("Persian", "fa"));
            add(new Language("Polish", "pl"));
            add(new Language("Portuguese", "pt"));
            add(new Language("Romanian", "ro"));
            add(new Language("Russian", "ru"));
            add(new Language("Sanskrit", "sa"));
            add(new Language("Serbian", "sr"));
            add(new Language("Slovak", "sk"));
            add(new Language("Slovenian", "sl"));
            add(new Language("Spanish", "es"));
            add(new Language("Swedish", "sv"));
            add(new Language("Tagalog", "tl"));
            add(new Language("Tamil", "ta"));
            add(new Language("Thai", "th"));
            add(new Language("Turkish", "tr"));
            add(new Language("Ukrainian", "uk"));
            add(new Language("Urdu", "ur"));
            add(new Language("Uzbek", "uz"));
            add(new Language("Vietnamese", "vi"));
        }};
    }
}

package com.ashomok.imagetotext.language_choser;

import android.content.Context;
import com.ashomok.imagetotext.R;
import java.util.LinkedHashMap;

/**
 * Created by iuliia on 12/17/16.
 */

public class LanguageList {


    private LinkedHashMap<String, String> languages;

    public LinkedHashMap<String, String> getLanguages() {
        return languages;
    }

    public LanguageList(final Context context) {
        languages = new LinkedHashMap<String, String>() {{
            put(context.getString(R.string.auto), null);
            put(context.getString(R.string.afrikaans), "af");
            put(context.getString(R.string.arabic), "ar");
            put(context.getString(R.string.assamese), "as");
            put(context.getString(R.string.azarbaijani), "az");
            put(context.getString(R.string.belarusian), "be");
            put(context.getString(R.string.bengali), "bn");
            put(context.getString(R.string.bulgarian), "bg");
            put(context.getString(R.string.catalan), "ca");
            put(context.getString(R.string.chinese), "zh");
            put(context.getString(R.string.croatin), "hr");
            put(context.getString(R.string.czech), "cs");
            put(context.getString(R.string.danish), "da");
            put(context.getString(R.string.dutch), "nl");
            put(context.getString(R.string.estonian), "et");
            put(context.getString(R.string.english), null);
            put("Finnish", "fi");
            put("French", "fr");
            put("German", "de");
            put("Greek", "el");
            put("Hebrew", "he");
            put("Hindi", "hi");
            put("Hungarian", "hu");
            put("Icelandic", "is");
            put("Indonesian", "id");
            put("Italian", "it");
            put("Japanese", "ja");
            put("Kazakh", "kk");
            put("Korean", "ko");
            put("Kyrgyz", "ky");
            put("Latvian", "lv");
            put("Lithuanian", "lt");
            put("Macedonian", "mk");
            put("Marathi", "mr");
            put("Mongolian", "mn");
            put("Nepali", "ne");
            put("Norwegian", "no");
            put("Pashtu", "ps");
            put("Persian", "fa");
            put("Polish", "pl");
            put("Portuguese", "pt");
            put("Romanian", "ro");
            put("Russian", "ru");
            put("Sanskrit", "sa");
            put("Serbian", "sr");
            put("Slovak", "sk");
            put("Slovenian", "sl");
            put("Spanish", "es");
            put("Swedish", "sv");
            put("Tagalog", "tl");
            put("Tamil", "ta");
            put("Thai", "th");
            put("Turkish", "tr");
            put("Ukrainian", "uk");
            put("Urdu", "ur");
            put("Uzbek", "uz");
            put("Vietnamese", "vi");
        }};
    }
}

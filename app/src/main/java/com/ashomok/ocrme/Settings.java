package com.ashomok.ocrme;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedHashMap;

/**
 * Created by iuliia on 12/12/16.
 */

public class Settings {

    public static String appPackageName = "com.ashomok.ocrme";

    public static boolean isAdsActive = true; //will be set in MainActivity
    public static boolean isPremium = false;//will be set in MainActivity

    public static final String facebookPageUrl = "https://www.facebook.com/OCRmePhotoScaner"; //for follow us on fb button

    public static LinkedHashMap<String, String> getOcrLanguageSupportList(final Context context) {
        return new LinkedHashMap<String, String>() {{
            put("af", context.getString(R.string.afrikaans));
            put("ar", context.getString(R.string.arabic));
            put("as", context.getString(R.string.assamese));
            put("az", context.getString(R.string.azarbaijani));
            put("be", context.getString(R.string.belorussian));
            put("bn", context.getString(R.string.bengali));
            put("bg", context.getString(R.string.bulgarian));
            put("ca", context.getString(R.string.catalan));
            put("zh", context.getString(R.string.chinese));
            put("hr", context.getString(R.string.croatin));
            put("cs", context.getString(R.string.czech));
            put("da", context.getString(R.string.danish));
            put("nl", context.getString(R.string.dutch));
            put("et", context.getString(R.string.estonian));
            put("en", context.getString(R.string.english));
            put("fi", context.getString(R.string.finnish));
            put("fr", context.getString(R.string.french));
            put("de", context.getString(R.string.german));
            put("el", context.getString(R.string.greek));
            put("he", context.getString(R.string.hebrew));
            put("hi", context.getString(R.string.hindi));
            put("hu", context.getString(R.string.hungarian));
            put("is", context.getString(R.string.icelandic));
            put("id", context.getString(R.string.indonesian));
            put("it", context.getString(R.string.italian));
            put("ja", context.getString(R.string.japanese));
            put("kk", context.getString(R.string.kazakh));
            put("ko", context.getString(R.string.korean));
            put("ky", context.getString(R.string.kyrgyz));
            put("lv", context.getString(R.string.latvian));
            put("lt", context.getString(R.string.lithuanian));
            put("mk", context.getString(R.string.macedonian));
            put("mr", context.getString(R.string.marathi));
            put("mn", context.getString(R.string.mongolian));
            put("ne", context.getString(R.string.nepali));
            put("no", context.getString(R.string.norwegian));
            put("ps", context.getString(R.string.pashtu));
            put("fa", context.getString(R.string.persian));
            put("pl", context.getString(R.string.polish));
            put("pt", context.getString(R.string.portuguese));
            put("ro", context.getString(R.string.romanian));
            put("ru", context.getString(R.string.russian));
            put("sa", context.getString(R.string.sanskrit));
            put("sr", context.getString(R.string.serbian));
            put("sk", context.getString(R.string.slovak));
            put("sl", context.getString(R.string.slovenian));
            put("es", context.getString(R.string.spanish));
            put("sv", context.getString(R.string.swedish));
            put("tl", context.getString(R.string.tagalog));
            put("ta", context.getString(R.string.tamil));
            put("th", context.getString(R.string.thai));
            put("tr", context.getString(R.string.turkish));
            put("uk", context.getString(R.string.ukrainian));
            put("ur", context.getString(R.string.urdu));
            put("uz", context.getString(R.string.uzbek));
            put("vi", context.getString(R.string.vietnamese));
        }};
    }
}

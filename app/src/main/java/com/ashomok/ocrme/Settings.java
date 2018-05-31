package com.ashomok.ocrme;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedHashMap;

/**
 * Created by iuliia on 12/12/16.
 */

public class Settings {

    public static String appPackageName = "com.ashomok.ocrme";

    //todo write tests for this
    public static boolean isTestMode = false; //todo undo in prod

    public static boolean isAdsActive = true; //will be set in MainActivity
    public static boolean isPremium = false;//will be set in MainActivity

    public static final String facebookPageUrl = "https://www.facebook.com/OCRmePhotoScaner"; //for follow us on fb button

    //todo add all to string
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
            put("fi", "Finnish");
            put("fr", "French");
            put("de", "German");
            put("el", "Greek");
            put("he", "Hebrew");
            put("hi", "Hindi");
            put("hu", "Hungarian");
            put("is", "Icelandic");
            put("id", "Indonesian");
            put("it", "Italian");
            put("ja", "Japanese");
            put("kk", "Kazakh");
            put("ko", "Korean");
            put("ky", "Kyrgyz");
            put("lv", "Latvian");
            put("lt", "Lithuanian");
            put("mk", "Macedonian");
            put("mr", "Marathi");
            put("mn", "Mongolian");
            put("ne", "Nepali");
            put("no", "Norwegian");
            put("ps", "Pashtu");
            put("fa", "Persian");
            put("pl", "Polish");
            put("pt", "Portuguese");
            put("ro", "Romanian");
            put("ru", "Russian");
            put("sa", "Sanskrit");
            put("sr", "Serbian");
            put("sk", "Slovak");
            put("sl", "Slovenian");
            put("es", "Spanish");
            put("sv", "Swedish");
            put("tl", "Tagalog");
            put("ta", "Tamil");
            put("th", "Thai");
            put("tr", "Turkish");
            put("uk", "Ukrainian");
            put("ur", "Urdu");
            put("uz", "Uzbek");
            put("vi", "Vietnamese");
        }};
    }
}

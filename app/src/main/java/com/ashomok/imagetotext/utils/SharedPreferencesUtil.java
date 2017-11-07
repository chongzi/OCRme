package com.ashomok.imagetotext.utils;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iuliia on 11/6/17.
 */

public class SharedPreferencesUtil {

    public static void pushStringList(SharedPreferences sharedPref,
                                      List<String> list, String uniqueListName) {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(uniqueListName + "_size", list.size());

        for (int i = 0; i < list.size(); i++) {
            editor.remove(uniqueListName + i);
            editor.putString(uniqueListName + i, list.get(i));
        }
        editor.apply();
    }

    public static List<String> pullStringList(SharedPreferences sharedPref,
                                              String uniqueListName) {

        List<String> result = new ArrayList<>();
        int size = sharedPref.getInt(uniqueListName + "_size", 0);

        for (int i = 0; i < size; i++) {
            result.add(sharedPref.getString(uniqueListName + i, null));
        }
        return result;
    }
}

package com.ashomok.imagetotext.language;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ashomok.imagetotext.App;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySetOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by iuliia on 1/6/17.
 */
public class LanguageListTest {

//    SharedPreferences sharedPreferences;
//
//    Context context;
//
//    @Before
//    public void before() throws Exception {
//        this.sharedPreferences = Mockito.mock(SharedPreferences.class);
//        this.context = Mockito.mock(Context.class);
//
//
//        Mockito.when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
//    }
//
//    @Test
//    public void constructorTest() throws Exception {
//        Set<String> checkedLanguagesNames = new HashSet<>();
//        checkedLanguagesNames.add("Arabic");
//        checkedLanguagesNames.add("Dutch");
//
//        App app = Mockito.mock(App.class);
//        Mockito.when(sharedPreferences.getStringSet(anyString(), anySetOf(String.class))).thenReturn(checkedLanguagesNames);
//        Mockito.when(context.getString(anyInt())).thenReturn("Language");
//
//
//        LanguageList languageList = new LanguageList();
//
//        LinkedHashSet<Language> languages = languageList.getLanguages();
//
//    }
//
//
//    @Test
//    public void obtainDataFromSharedPreferances() throws Exception {
//
//    }

}
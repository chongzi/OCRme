package com.ashomok.imagetotext.language_choser_mvp_di;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by iuliia on 1/11/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class LanguagesListAdapterTest {

    private LanguageOcrActivity.LanguagesListAdapter languagesListAdapter;

    @Before
    public void before() throws Exception {
        List<String> allLanguages = new ArrayList<>();
        allLanguages.add("1 language");
        allLanguages.add("2 language");
        allLanguages.add("3 language");
        allLanguages.add("4 language");
        allLanguages.add("5 language");

        List<String> checked = new ArrayList<>();
        checked.add("1 language");

        Context context = Mockito.mock(Context.class);
        languagesListAdapter = new LanguageOcrActivity.LanguagesListAdapter(allLanguages, checked, notifier);
    }


    @Test
    public void getCheckedLanguages() throws Exception {
        assertTrue(languagesListAdapter.getCheckedLanguageCodes().size() > 0);

    }

    @Test
    public void getCount() throws Exception {
        assertTrue(languagesListAdapter.getCount() > 0);
    }

    @Test
    public void getItem() throws Exception {
        assertEquals(languagesListAdapter.getItem(0), "1 language");
    }
}
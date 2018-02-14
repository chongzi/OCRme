package com.ashomok.imagetotext.language_choser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

/**
 * Created by iuliia on 1/11/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class LanguagesListAdapterTest {

    private LanguagesListAdapter languagesListAdapter;

    @Before
    public void before() throws Exception {
        List<String> allLanguages = new ArrayList<>();
        allLanguages.add("1 language");
        allLanguages.add("2 language");
        allLanguages.add("3 language");
        allLanguages.add("4 language");
        allLanguages.add("5 language");

        LanguagesListAdapter.ResponsableList<String> checked =
                new LanguagesListAdapter.ResponsableList<>(new ArrayList<>());
        checked.add("1 language");

        languagesListAdapter = spy(new LanguagesListAdapter(allLanguages, checked, isAutoChecked -> {
            //auto checked
        }));
    }

    @Test
    public void getCheckedLanguages() throws Exception {
        assertTrue(languagesListAdapter.getCheckedLanguageCodes().size() > 0);
    }

    @Test
    public void getItem() throws Exception {
        assertEquals(languagesListAdapter.getItem(0), "1 language");
    }

    @Test
    public void getCheckedLanguageCodes() throws Exception {
        LanguagesListAdapter.ResponsableList<String> checked =
                new LanguagesListAdapter.ResponsableList<>(new ArrayList<>());
        checked.add("1 language");
        assertEquals(languagesListAdapter.getCheckedLanguageCodes(), checked);
    }

    @Test
    public void getItemCount() throws Exception {
        assertTrue(languagesListAdapter.getItemCount() > 0);
    }
}
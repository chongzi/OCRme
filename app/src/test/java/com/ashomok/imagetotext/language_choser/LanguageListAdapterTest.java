package com.ashomok.imagetotext.language_choser;

import android.content.Context;

import com.ashomok.imagetotext.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by iuliia on 1/11/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class LanguageListAdapterTest {

    private LanguageListAdapter languageListAdapter;

    @Before
    public void before() throws Exception {
        LinkedHashSet<String> data = new LinkedHashSet<>();
        data.add("1 language");
        data.add("2 language");
        data.add("3 language");
        data.add("4 language");
        data.add("5 language");

        LinkedHashSet<String> checked = new LinkedHashSet<>();
        checked.add("1 language");

        Context context = Mockito.mock(Context.class);
        languageListAdapter = new LanguageListAdapter(context, data, checked);
    }


    @Test
    public void getCheckedLanguages() throws Exception {
        assertTrue(languageListAdapter.getCheckedLanguages().size() > 0);

    }

    @Test
    public void getCount() throws Exception {
        assertTrue(languageListAdapter.getCount() > 0);
    }

    @Test
    public void getItem() throws Exception {
        assertEquals(languageListAdapter.getItem(0), "1 language");
    }

}
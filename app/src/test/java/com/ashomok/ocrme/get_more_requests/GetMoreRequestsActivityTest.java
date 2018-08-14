package com.ashomok.ocrme.get_more_requests;

import android.widget.TextView;

import com.ashomok.ocrme.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

//todo failed becuase not injected ads - fix
@RunWith(RobolectricTestRunner.class)
public class GetMoreRequestsActivityTest {
    private GetMoreRequestsActivity activity;

    @Before
    public void setUp() {
        // Convenience method to run MainActivity through the Activity Lifecycle methods:
        // onCreate(...) => onStart() => onPostCreate(...) => onResume()
        activity = Robolectric.setupActivity(GetMoreRequestsActivity.class);
    }

    @Test
    @Config(qualifiers = "ru")
    public void localizedRussian() {
        TextView textView = activity.findViewById(R.id.you_have_requests_text);
        assertTrue(textView.getText().toString().contains("есть"));
    }
}


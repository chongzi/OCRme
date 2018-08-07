package com.ashomok.ocrme.main;

import android.content.Intent;
import android.widget.TextView;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.my_docs.MyDocsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    private MainActivity activity;

    @Before
    public void setUp() {
        // Convenience method to run MainActivity through the Activity Lifecycle methods:
        // onCreate(...) => onStart() => onPostCreate(...) => onResume()
        activity = Robolectric.setupActivity(MainActivity.class);
    }


//    //todo remove failed
//    @Test
//    public void clickingLogin_shouldStartMyDocsActivity() {
//
//        activity.findViewById(R.id.sign_in_btn).performClick();
//        activity.findViewById(R.id.my_docs_btn).performClick();
//
//        Intent expectedIntent = new Intent(activity, MyDocsActivity.class);
//        Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
//        assertEquals(expectedIntent.getComponent(), actual.getComponent());
//    }

    @Test
    public void validateLanguageTextViewContent() {
        TextView tvLanguage = activity.findViewById(R.id.language);
        assertNotNull("TextView could not be found", tvLanguage);
        assertTrue("TextView contains incorrect text",
                "auto".equals(tvLanguage.getText().toString()));
    }

    //todo write similar
//    @Test
//    public void testButtonClickShouldShowToast() throws Exception {
//        RobolectricActivity activity = Robolectric.buildActivity(RobolectricActivity.class).create().get();
//        Button view = (Button) activity.findViewById(R.id.showToast);
//        assertNotNull(view);
//        view.performClick();
//        assertThat(ShadowToast.getTextOfLatestToast(), equalTo("Lala") );
//    }
//
//    @Test
//    @Config(qualifiers = "es")
//    public void localizedSpanishHelloWorld() {
//        TextView tvHelloWorld = (TextView)activity.findViewById(R.id.tvHelloWorld);
//        assertEquals(tvHelloWorld.getText().toString(), "Hola Mundo!");
//    }
//
//    @Test
//    @Config(qualifiers = "fr")
//    public void localizedFrenchHelloWorld() {
//        TextView tvHelloWorld = (TextView)activity.findViewById(R.id.tvHelloWorld);
//        assertEquals(tvHelloWorld.getText().toString(), "Bonjour le monde!");
//    }
}
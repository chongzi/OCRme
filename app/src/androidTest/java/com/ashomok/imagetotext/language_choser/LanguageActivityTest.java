package com.ashomok.imagetotext.language_choser;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.ActionBar;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.ocr_task.OCRAnimationActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

/**
 * espresso UI test
 * Created by iuliia on 12/23/16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class LanguageActivityTest {

    @Rule
    public ActivityTestRule<LanguageActivity> mActivityRule = new ActivityTestRule<>(
            LanguageActivity.class, true, false);

    @Before
    public void launchActivityWithPredefinedData() {
        final Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, OCRAnimationActivity.class);
        ArrayList<String> data = new ArrayList<String>(){{
            add(targetContext.getString(R.string.auto));
        }};
        intent.putExtra(LanguageActivity.CHECKED_LANGUAGES, data);
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void toolbarBackBtn() {
        ActionBar actionBar = mActivityRule.getActivity().getSupportActionBar();
        assertNotNull(actionBar);
    }

    @Test
    public void listViewBehaviour() {
        String language = mActivityRule.getActivity().getResources().getString(R.string.azarbaijani);
        
        String auto = mActivityRule.getActivity().getResources().getString(R.string.auto);
        
        //auto checked by default
        onData(withName(equalTo(auto)))
                .onChildView(withId(R.id.checkbox))
                .check(matches(isChecked()));

        // Click on some another language
        onData(withName(equalTo(language)))
                .onChildView(withId(R.id.checkbox))
                .perform(click());

        // Check that the another language is checked
        onData(withName(equalTo(language)))
                .onChildView(withId(R.id.checkbox))
                .check(matches(isChecked()));
        
        //check that auto is not checked
        onData(withName(equalTo(auto)))
                .onChildView(withId(R.id.checkbox))
                .check(matches(isNotChecked()));
    }

    public static Matcher<String> withName(final Matcher<String> nameMatcher) {
        return new TypeSafeMatcher<String>() {

            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected boolean matchesSafely(String item) {
                return nameMatcher.matches(item);
            }
        };
    }
}
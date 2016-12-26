package com.ashomok.imagetotext.language;

import android.support.test.filters.LargeTest;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.ActionBar;

import com.ashomok.imagetotext.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
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
            LanguageActivity.class);

    @Test
    public void toolbarBackBtn() {
        ActionBar actionBar = mActivityRule.getActivity().getSupportActionBar();
        assertNotNull(actionBar);
    }

    @Test
    public void listViewBehaviour() {
        String language = mActivityRule.getActivity().getResources().getString(R.string.azarbaijani);

        //auto checked by default
        onData(withName(equalTo(LanguageList.getInstance().getDefaultLanguage().getName())))
                .onChildView(withId(R.id.checkbox))
                .check(matches(isChecked()));

        // Click on some item
        onData(withName(equalTo(language)))
                .onChildView(withId(R.id.checkbox))
                .perform(click());

        // Check that the checkbox is checked.
        onData(withName(equalTo(language)))
                .onChildView(withId(R.id.checkbox))
                .check(matches(isChecked()));
    }

    public static Matcher<Language> withName(final Matcher<String> nameMatcher) {
        return new TypeSafeMatcher<Language>() {

            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected boolean matchesSafely(Language item) {
                return nameMatcher.matches(item.getName());
            }
        };
    }
}
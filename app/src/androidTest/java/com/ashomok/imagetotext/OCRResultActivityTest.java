package com.ashomok.imagetotext;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ashomok.imagetotext.language.LanguageActivity;
import com.ashomok.imagetotext.ocr_result.OCRResultActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by iuliia on 5/31/17.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class OCRResultActivityTest {

    @Rule
    public ActivityTestRule<OCRResultActivity> mActivityRule = new ActivityTestRule<>(
            OCRResultActivity.class, true, false);
    
//// TODO: 5/31/17
    @Before
    public void launchActivityWithPredefinedData() {
        final Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, OCRResultActivity.class);

        ArrayList<String> data = new ArrayList<String>(){{
            add(targetContext.getString(R.string.auto));
        }};
        intent.putExtra(LanguageActivity.CHECKED_LANGUAGES, data);
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void tabSwap()
    {
        onView(withId(R.id.pager)).perform(swipeLeft());
//        onView(allOf(
//                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
//                withId(R.id.container_weather)))
//                .check(matches(isDisplayed()));
        onView(withId(R.id.pager)).perform(swipeRight());
    }
}
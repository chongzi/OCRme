package com.ashomok.imagetotext.sign_in;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by iuliia on 8/9/17.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class, true, true);

    @Test
    public void emptyTest() {

    }

}
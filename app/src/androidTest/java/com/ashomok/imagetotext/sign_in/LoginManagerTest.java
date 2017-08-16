package com.ashomok.imagetotext.sign_in;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.FragmentActivity;

import com.ashomok.imagetotext.MainActivity;
import com.ashomok.imagetotext.language_choser.LanguageActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by iuliia on 8/16/17.
 */
public class LoginManagerTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class, true, true);

    private LoginManager testInstance;

    @Before
    public void init()
    {
        final Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        testInstance = new LoginManager(targetContext,
                MainActivity.LoginsProvider.getLogins(mActivityRule.getActivity()));
    }

//error here java.lang.IllegalStateException: Already managing a GoogleApiClient with id 0 -
// test shoud be rewritten
//    @Test
//    public void trySignInAutomatically() throws Exception {
//        testInstance.trySignInAutomatically();
//
//    }

}
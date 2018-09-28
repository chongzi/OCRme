package com.ashomok.ocrme.rate_app;



import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.di_dagger.ApplicationTestModule;
import com.ashomok.ocrme.main.MainActivity;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;


@Module(includes = ApplicationTestModule.class)
public class RateAppTestModule {

//    @Provides
//    RateAppAsker provideRateAppAsker(SharedPreferences sharedPreferences, Context context) {
//        return new RateAppAsker(sharedPreferences, context);
//
//    }
}

package com.ashomok.ocrme.di_dagger;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.rate_app.RateAppAsker;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationTestModule {

    @Provides
    static Context provideContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    @Provides
    static SharedPreferences provideSharedPrefs(Context context) {
        return context.getSharedPreferences(
                context.getString(R.string.preferences), Context.MODE_PRIVATE);
    }
}


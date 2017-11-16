package com.ashomok.imagetotext.language_choser_mvp_di.di;

import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.support.multidex.MultiDex;

/**
 * Created by iuliia on 11/14/17.
 */

public class App extends Application {
    private AppComponent component;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @VisibleForTesting
    protected AppComponent createComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent getAppComponent(Context context) {
        App app = (App) context.getApplicationContext();
        if (app.component == null) {
            app.component = app.createComponent();
        }
        return app.component;
    }
}
package com.ashomok.imagetotext;

import android.app.Application;
import android.content.Context;

/**
 * Created by iuliia on 12/18/16.
 */

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}

package com.ashomok.imagetotext.sign_in;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Created by iuliia on 8/10/17.
 */

public class LogoutAsyncTask extends AsyncTask<Void, Void, Void> {

    // Weak references will still allow the ApplicationContext in LoginManager to be garbage-collected
    private final WeakReference<LoginManager> weakLoginManager;

    public LogoutAsyncTask(LoginManager loginManager) {
        this.weakLoginManager = new WeakReference<LoginManager>(loginManager);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        weakLoginManager.get().logout();
        return null;
    }
}

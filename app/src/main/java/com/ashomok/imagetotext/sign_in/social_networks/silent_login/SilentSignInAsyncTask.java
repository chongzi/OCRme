package com.ashomok.imagetotext.sign_in.social_networks.silent_login;

import android.os.AsyncTask;

import com.ashomok.imagetotext.sign_in.LoginManager;

import java.lang.ref.WeakReference;

/**
 * Created by iuliia on 8/10/17.
 */

public class SilentSignInAsyncTask extends AsyncTask<Void, Void, Void> {

    // Weak references will still allow the ApplicationContext in LoginManager to be garbage-collected
    private final WeakReference<LoginManager> weakLoginManager;

    public SilentSignInAsyncTask(LoginManager loginManager) {
        this.weakLoginManager = new WeakReference<>(loginManager);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        weakLoginManager.get().trySignInAutomatically();
        return null;
    }
}

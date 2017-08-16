package com.ashomok.imagetotext.sign_in.social_networks.silent_login;

import android.os.AsyncTask;
import android.util.Log;

import com.ashomok.imagetotext.sign_in.LoginManager;

import java.lang.ref.WeakReference;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 8/10/17.
 */

public class SilentSignInAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = DEV_TAG + SilentSignInAsyncTask.class.getSimpleName();
    // Weak references will still allow the ApplicationContext in LoginManager to be garbage-collected
    private final WeakReference<LoginManager> weakLoginManager;

    public SilentSignInAsyncTask(LoginManager loginManager) {
        this.weakLoginManager = new WeakReference<>(loginManager);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "SilentSignInAsyncTask called");
        weakLoginManager.get().trySignInAutomatically();
        return null;
    }
}

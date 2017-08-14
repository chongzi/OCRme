package com.ashomok.imagetotext.sign_in;

import android.os.AsyncTask;

import com.ashomok.imagetotext.MainActivity;

import java.lang.ref.WeakReference;

/**
 * Created by iuliia on 8/10/17.
 */

public class AutoSignInAsyncTask extends AsyncTask<Void, Void, Boolean> {

    // Weak references will still allow the ApplicationContext in LoginManager to be garbage-collected
    private final WeakReference<LoginManager> weakLoginManager;
    private final AutoSignInListener listener;

    public AutoSignInAsyncTask(LoginManager loginManager, AutoSignInListener listener) {
        this.weakLoginManager = new WeakReference<>(loginManager);
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return weakLoginManager.get().trySignInAutomatically();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (listener != null)
            listener.signedIn(result);
    }

    public interface AutoSignInListener{
        void signedIn(boolean isSignedIn);
    }
}

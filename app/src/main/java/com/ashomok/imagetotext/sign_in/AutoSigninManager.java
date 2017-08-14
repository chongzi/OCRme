package com.ashomok.imagetotext.sign_in;

/**
 * Created by iuliia on 8/10/17.
 */

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.ashomok.imagetotext.sign_in.social_networks.LoginProcessor;
import com.ashomok.imagetotext.sign_in.social_networks.LoginProcessorFacebook;
import com.ashomok.imagetotext.sign_in.social_networks.LoginProcessorGoogle;
import com.facebook.CallbackManager;

import java.util.ArrayList;

/**
 * Login manager for autologin
 * Currently works with social networks logins only
 */
public class AutoSigninManager {

    private boolean isSignedIn;

    //user email
    private String mSignedAs;
    public String getSignedAs() {
        return mSignedAs;
    }

    /**
     * try sign in without user interaction using previous sign in data
     *
     * @return is sign in
     */
    public boolean trySignInAutomatically() {
        if (loginsInUseArray.size() < 1) {
            Log.e(TAG, "Error. You need to add login modes firstly. Call addLogin before.");
            return false;
        } else {
            if (!isSignedIn) {
                for (LoginProcessor loginProcessor : loginsInUseArray) {
                    loginProcessor.trySignIn();
                }

                //checking if user already signed in
                for (LoginProcessor loginProcessor : loginsInUseArray) {
                    if (loginProcessor.isSignedIn()) {
                        isSignedIn = true;
                        @LoginManager.LoginMode int loginMode = getLoginMode(loginProcessor);
                        @LoginManager.LoginMode int mLastLoginMode = getLastLoginMode();
                        if (loginMode == mLastLoginMode || mLoginMode == LOGIN_MODE_UNDEFINED) {
                            mLoginMode = loginMode;
                            mSignedAs = getEmail(loginProcessor);
                        }

                    }
                }
            }
            return isSignedIn;
        }
    }

    //todo create login provider here
    /**
     * this class provides login processors to LoginManager for auto (silent) login.
     */
    private static class LoginsProvider {
        static ArrayList<LoginProcessor> getLogins() {
            //Google+ LoginProcessor
            LoginProcessorGoogle loginGoogle = new LoginProcessorGoogle(activity);

            //Facebook LoginProcessor
            CallbackManager callbackManager = CallbackManager.Factory.create();
            LoginProcessorFacebook loginFacebook = new LoginProcessorFacebook(callbackManager);

            ArrayList<LoginProcessor> list = new ArrayList<>();
            list.add(loginGoogle);
            list.add(loginFacebook);
            return list;
        }
    }
}

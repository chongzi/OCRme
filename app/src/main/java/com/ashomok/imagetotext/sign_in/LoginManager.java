package com.ashomok.imagetotext.sign_in;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;
import android.util.Log;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.sign_in.social_networks.LoginFacebook;
import com.ashomok.imagetotext.sign_in.social_networks.LoginGoogle;
import com.ashomok.imagetotext.sign_in.social_networks.LoginProcessor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;


/**
 * Created by iuliia on 8/4/17.
 */

/**
 * Login manager for login, logout, etc
 * Works with social networks logins and native login
 */

//// TODO: 8/16/17 make as singleton 
public class LoginManager {

    private static final String TAG = DEV_TAG + LoginManager.class.getSimpleName();
    private Context context;
    private ArrayList<LoginProcessor> loginsInUseArray = new ArrayList<>();
    private OnSignedInListener onSignedInListener;

    public void setOnSignedInListener(OnSignedInListener onSignedInListener) {
        this.onSignedInListener = onSignedInListener;

        for (LoginProcessor login : loginsInUseArray) {
            login.setOnSignedInListener(onSignedInListener);
        }
    }

    //[START define login mode]
    @IntDef({
            LOGIN_MODE_UNDEFINED,
            LOGIN_MODE_STANDARD,
            LOGIN_MODE_GOOGLE_PLUS,
            LOGIN_MODE_FACEBOOK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginMode {
    }

    public static final int LOGIN_MODE_UNDEFINED = -1;
    public static final int LOGIN_MODE_STANDARD = 0;
    public static final int LOGIN_MODE_GOOGLE_PLUS = 1;
    public static final int LOGIN_MODE_FACEBOOK = 2;
    //[END define login mode]

    public LoginManager(Context context, ArrayList<LoginProcessor> logins) {
        this.context = context;
        this.loginsInUseArray = logins;
    }

    public boolean obtainIsSignedIn() {
        boolean result = false;
        for (LoginProcessor login : loginsInUseArray) {
            if (login.isSignedIn()) {
                result = true;
            }
        }
        return result;
    }

    public String obtainEmail() {
        String email = "";
        @LoginMode int loginMode = obtainLoginMode();
        if (loginMode == LOGIN_MODE_FACEBOOK || loginMode == LOGIN_MODE_GOOGLE_PLUS) {
            LoginProcessor login = getProcessorByMode(loginMode);
            email = login.getEmail();
        } else {
            Log.e(TAG, "Email can not be obtained. Unsupported login mode");
        }
        return email;
    }


    /**
     * if user signed in, get the way of login (login mode), otherwise return LOGIN_MODE_UNDEFINED };
     * if user signed in using two or more loginProcessor - chose the most suitable one.
     *
     * @return {@link LoginMode}
     */
    @LoginMode
    private int obtainLoginMode() {
        @LoginMode int loginMode = LOGIN_MODE_UNDEFINED;

        try {
            ArrayList<LoginProcessor> loginsSignedIn = new ArrayList<>();
            for (LoginProcessor login : loginsInUseArray) {
                if (login.isSignedIn()) {
                    loginsSignedIn.add(login);
                }
            }

            if (loginsSignedIn.size() > 1) {
                //more than one is active - chose the most suitable one.
                loginMode = toLoginMode(loginsSignedIn.get(0)); //take first as default

                //take the last used login mode if possible
                int lastUsedLoginMode = getLastLoginMode();
                for (LoginProcessor login : loginsSignedIn) {
                    if (toLoginMode(login) == lastUsedLoginMode) {
                        loginMode = lastUsedLoginMode;
                    }
                }

            } else if (loginsSignedIn.size() == 1) {
                //only one is active - take it
                loginMode = toLoginMode(loginsSignedIn.get(0));
            } else {
                //not signed in
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return loginMode;
    }

    private LoginProcessor getProcessorByMode(@LoginMode int mode) {
        LoginProcessor result = null;
        for (LoginProcessor processor : loginsInUseArray) {
            if (
                    (processor instanceof LoginFacebook && mode == LOGIN_MODE_FACEBOOK) ||
                            (processor instanceof LoginGoogle && mode == LOGIN_MODE_GOOGLE_PLUS)) {
                result = processor;
            }
        }
        return result;
    }

    @LoginMode
    private int getLastLoginMode() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        @LoginMode int defaultValue = LOGIN_MODE_GOOGLE_PLUS;
        @LoginMode int lastLoginMode = sharedPref.getInt(context.getString(R.string.last_login_mode), defaultValue);
        return lastLoginMode;
    }

    private void saveLoginMode() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(context.getString(R.string.last_login_mode), obtainLoginMode());
        editor.apply();
    }

    /**
     * user login will store on the device.
     */
    private void saveUserEmail() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.user_email), obtainEmail());
        editor.apply();
    }

    /**
     * logout from all accounts
     */
    public void logout() {
        if (obtainIsSignedIn()) {
            saveLoginMode();
            for (LoginProcessor loginProcessor : loginsInUseArray) {
                if (loginProcessor.isSignedIn()) {
                    loginProcessor.signOutAsync();
                }
            }
        } else {
            Log.w(TAG, "logout called for not signed in user");
        }
    }

    @LoginMode
    private int toLoginMode(LoginProcessor loginProcessor) {
        if (loginProcessor instanceof LoginFacebook) {
            return LOGIN_MODE_FACEBOOK;
        } else if (loginProcessor instanceof LoginGoogle) {
            return LOGIN_MODE_GOOGLE_PLUS;
        } else {
            return LOGIN_MODE_UNDEFINED;
        }
    }

    public void trySignInAutomatically() {
        for (LoginProcessor loginProcessor : loginsInUseArray) {
            loginProcessor.trySignIn();
        }
    }
}

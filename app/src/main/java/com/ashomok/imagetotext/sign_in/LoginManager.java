package com.ashomok.imagetotext.sign_in;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;
import android.util.Log;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.sign_in.social_networks.LoginProcessor;
import com.ashomok.imagetotext.sign_in.social_networks.LoginProcessorFacebook;
import com.ashomok.imagetotext.sign_in.social_networks.LoginProcessorGoogle;

import java.io.Serializable;
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
public class LoginManager {

    private Context context;

    private boolean isSignedIn;

    //user email
    private String mSignedAs;
    public String getSignedAs() {
        return mSignedAs;
    }

    private ArrayList<LoginProcessor> loginsInUseArray = new ArrayList<>();

    private static final String TAG = DEV_TAG + LoginManager.class.getSimpleName();

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

    @LoginMode
    public int mLoginMode = LOGIN_MODE_UNDEFINED;

    @LoginMode
    public int getLoginMode() {
        return mLoginMode;
    }

    @LoginMode
    public int getLastLoginMode() {
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
        editor.putInt(context.getString(R.string.last_login_mode), mLoginMode);
        editor.apply();
    }
    //[END define login mode]

    /**
     * user login will store on the device.
     */
    private void saveUserEmail() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.user_email), mSignedAs);
        editor.apply();
    }

    public LoginManager(Context context) {
        this(context, new ArrayList<LoginProcessor>());
    }

    public LoginManager(Context context, ArrayList<LoginProcessor> logins) {
        this.context = context;
        this.loginsInUseArray = logins;
    }

    public void addLogin(LoginProcessor loginProcessor) {
        loginsInUseArray.add(loginProcessor);
    }



    /**
     * logout from all accounts
     */
    public void logout() {
        if (isSignedIn) {
            for (LoginProcessor loginProcessor : loginsInUseArray) {
                if (loginProcessor.isSignedIn()) {
                    loginProcessor.signOut();
                }
                isSignedIn = false;
                saveLoginMode();
            }
        } else {
            Log.w(TAG, "logout called for not signed in user");
        }
    }


    private String getEmail(LoginProcessor loginProcessor) {
        return loginProcessor.getEmail();
    }

    @LoginMode
    private int getLoginMode(LoginProcessor loginProcessor) {
        if (loginProcessor instanceof LoginProcessorFacebook) {
            return LOGIN_MODE_FACEBOOK;
        } else if (loginProcessor instanceof LoginProcessorGoogle) {
            return LOGIN_MODE_GOOGLE_PLUS;
        } else {
            return LOGIN_MODE_UNDEFINED;
        }
    }
}

package com.ashomok.imagetotext.firebaseUiAuth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.MainThread;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.view.View;

import com.ashomok.imagetotext.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import static com.ashomok.imagetotext.utils.InfoSnackbarUtil.showError;
import static com.ashomok.imagetotext.utils.InfoSnackbarUtil.showInfo;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 9/24/17.
 */
//this class will be used by MainActivity and getMyDocs Activity
public abstract class BaseLoginActivity extends RxAppCompatActivity {
    public static final String TAG = DEV_TAG + BaseLoginActivity.class.getSimpleName();

    private static final String FIREBASE_TOS_URL = "https://firebase.google.com/terms/";
    private static final String FIREBASE_PRIVACY_POLICY_URL =
            "https://firebase.google.com/terms/analytics/#7_privacy";

    private static final int RC_SIGN_IN = 1;
    public boolean mIsUserSignedIn = false;
    public View mRootView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = findViewById(android.R.id.content);
        if (mRootView == null) {
            mRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            onSignedIn();
        }
    }

    public void signIn() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(getSelectedTheme())
                        .setLogo(getLogo())
                        .setAvailableProviders(getSelectedProviders())
                        .setTosUrl(getSelectedTosUrl())
                        .setPrivacyPolicyUrl(getSelectedPrivacyPolicyUrl())
                        .setIsSmartLockEnabled(true, true)
                        .setAllowNewEmailAccounts(true)
                        .build(), RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
        }
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == RESULT_OK) {
            onSignedIn();
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showError(R.string.sign_in_cancelled, mRootView);
            }
            else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showError(R.string.no_internet_connection, mRootView);
            }
            else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showError(R.string.unknown_error, mRootView);
            }
            else {
                showError(R.string.unknown_sign_in_response, mRootView);
            }
        }
    }

    //// TODO: 10/11/17 return MY theme
    @MainThread
    @StyleRes
    private int getSelectedTheme() {
        return AuthUI.getDefaultTheme();
//            return R.style.PurpleTheme; //custom
    }

    //// TODO: 9/24/17 return MY logo, should be 144x144 dp
    @MainThread
    @DrawableRes
    private int getLogo() {
        return R.drawable.com_facebook_button_login_logo;
    }

    @MainThread
    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();

        selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
        selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());
        selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

        return selectedProviders;
    }

    @MainThread
    private String getSelectedTosUrl() {
        return FIREBASE_TOS_URL;
    }

    @MainThread
    private String getSelectedPrivacyPolicyUrl() {
        return FIREBASE_PRIVACY_POLICY_URL;
    }

    public abstract void updateUi(boolean isUserSignedIn);

    public void onSignedIn() {
        Log.d(TAG, "onSignedIn");
        mIsUserSignedIn = true;
        updateUi(mIsUserSignedIn);
    }

    /**
     * called by sign out DialogFragment
     */
    public void onSignedOutCalled() {
        Log.d(TAG, "onSignedOutCalled");

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        showError(R.string.sign_out_failed, mRootView);
                    }
                });

        mIsUserSignedIn = false;
        updateUi(mIsUserSignedIn);
        showInfo(R.string.logged_out, mRootView);
    }
}

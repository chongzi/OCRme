package com.ashomok.imagetotext.firebaseUiAuth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.view.View;

import com.ashomok.imagetotext.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 9/24/17.
 */
//this class will be used by MainActivity and myDocs Activity
public abstract class BaseLoginActivity extends Activity {
    public static final String TAG = DEV_TAG + BaseLoginActivity.class.getSimpleName();

    private static final String UNCHANGED_CONFIG_VALUE = "CHANGE-ME";
    private static final String FIREBASE_TOS_URL = "https://firebase.google.com/terms/";
    private static final String FIREBASE_PRIVACY_POLICY_URL = "https://firebase.google.com/terms/analytics/#7_privacy";

    private static final int RC_SIGN_IN = 100;

    public static Intent createIntent(Context context) {
        return new Intent(context, BaseLoginActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startSignedInActivity();
            finish();
            return;
        }

        //// TODO: 9/24/17 remove later
        if (isGoogleMisconfigured() || isFacebookMisconfigured()) {
            showError(R.string.configuration_required);
        }
    }

    //// TODO: 9/24/17 call from superclass when sign-in btn clicked
    public void signIn(View view) {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(getSelectedTheme())
                        .setLogo(getLogo())
                        .setAvailableProviders(getSelectedProviders())
                        .setTosUrl(getSelectedTosUrl())
                        .setPrivacyPolicyUrl(getSelectedPrivacyPolicyUrl())
                        .setIsSmartLockEnabled(true, true)
                        .setAllowNewEmailAccounts(true)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        showError(R.string.unknown_response);
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == RESULT_OK) {
            startSignedInActivity();
            finish();
            return;
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showError(R.string.sign_in_cancelled);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showError(R.string.no_internet_connection);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showError(R.string.unknown_error);
                return;
            }
        }

        showError(R.string.unknown_sign_in_response);
    }


    @MainThread
    @StyleRes
    private int getSelectedTheme() {
        return AuthUI.getDefaultTheme();
//            return R.style.PurpleTheme; //custom
    }

    //// TODO: 9/24/17 logo should be 144x144 dp
    @MainThread
    @DrawableRes
    private int getLogo() {
        return R.drawable.com_facebook_button_login_logo;
    }

    @MainThread
    private List<AuthUI.IdpConfig> getSelectedProviders() {
        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();


        selectedProviders.add(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                        .setPermissions(getGooglePermissions())
                        .build());

        selectedProviders.add(
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER)
                        .setPermissions(getFacebookPermissions())
                        .build());

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

    @MainThread
    private boolean isGoogleMisconfigured() {
        return UNCHANGED_CONFIG_VALUE.equals(getString(R.string.default_web_client_id));
    }

    @MainThread
    private boolean isFacebookMisconfigured() {
        return UNCHANGED_CONFIG_VALUE.equals(getString(R.string.facebook_application_id));
    }


    @MainThread
    public abstract void showError(@StringRes int errorMessageRes);

    //todo remove this
    @MainThread
    private List<String> getFacebookPermissions() {
        return new ArrayList<>();
    }

    //todo remove this
    @MainThread
    private List<String> getGooglePermissions() {
        return new ArrayList<>();
    }

    public abstract void startSignedInActivity();
}

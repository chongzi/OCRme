/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ashomok.ocrme.firebaseUiAuth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ashomok.ocrme.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static com.ashomok.ocrme.utils.InfoSnackbarUtil.showError;
import static com.ashomok.ocrme.utils.InfoSnackbarUtil.showInfo;

import butterknife.OnClick;

import static com.ashomok.ocrme.utils.InfoSnackbarUtil.showError;
import static com.ashomok.ocrme.utils.InfoSnackbarUtil.showInfo;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

public abstract class AuthUiActivity extends AppCompatActivity {
    private static final String TAG =  DEV_TAG +"AuthUiActivity";

 private static final String FIREBASE_TOS_URL = "https://firebase.google.com/terms/";
 private static final String FIREBASE_PRIVACY_POLICY_URL = "https://firebase.google.com/terms/analytics/#7_privacy";

    private static final int RC_SIGN_IN = 100;
    public boolean mIsUserSignedIn = false;

    public View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = findViewById(android.R.id.content);
        if (mRootView == null) {
            mRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        }
    }

    public void signIn() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(getSelectedTheme())
                        .setLogo(getSelectedLogo())
                        .setAvailableProviders(getSelectedProviders())
                        .setTosAndPrivacyPolicyUrls(getSelectedTosUrl(),
                                getSelectedPrivacyPolicyUrl())
                        .setIsSmartLockEnabled(true, true)
                        .build(), RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null && auth.getCurrentUser().getEmail() != null) {
            onSignedIn();
        }
    }

    private void handleSignInResponse(int resultCode, @Nullable Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == RESULT_OK) {
            onSignedIn();
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showError(R.string.sign_in_cancelled, mRootView);
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                showError(R.string.no_internet_connection, mRootView);
                return;
            }

            showError(R.string.unknown_error, mRootView);
            Log.e(TAG, "Sign-in error: ", response.getError());
        }
    }


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

    public abstract void updateUi(boolean isUserSignedIn);

        @StyleRes
    private int getSelectedTheme() { return R.style.AppLoginTheme; }

    @DrawableRes
    private int getSelectedLogo() {return R.mipmap.ic_launcher; }

    private List<IdpConfig> getSelectedProviders() {
        List<IdpConfig> selectedProviders = new ArrayList<>();

            selectedProviders.add(
                    new IdpConfig.GoogleBuilder().setScopes(getGoogleScopes()).build());

            selectedProviders.add(new IdpConfig.FacebookBuilder()
                    .setPermissions(getFacebookPermissions())
                    .build());

            selectedProviders.add(new IdpConfig.EmailBuilder()
                    .setRequireName(true)
                    .setAllowNewAccounts(true)
                    .build());

        return selectedProviders;
    }

    private String getSelectedTosUrl() {
        return FIREBASE_TOS_URL;
    }

    private String getSelectedPrivacyPolicyUrl() {
        return FIREBASE_PRIVACY_POLICY_URL;
    }


    private List<String> getGoogleScopes() {
        return new ArrayList<>();
    }

    private List<String> getFacebookPermissions() {
        return new ArrayList<>();
    }
}

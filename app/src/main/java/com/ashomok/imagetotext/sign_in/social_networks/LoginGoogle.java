package com.ashomok.imagetotext.sign_in.social_networks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.sign_in.social_networks.silent_login.SilentLoginGoogle;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import static com.ashomok.imagetotext.sign_in.LoginActivity.RC_SIGN_IN;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;


/**
 * Created by iuliia on 8/4/17.
 */

public class LoginGoogle extends SilentLoginGoogle implements LoginProcessor,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = DEV_TAG + LoginGoogle.class.getSimpleName();

    private SignInButton button;
    private ProgressDialog ringProgressDialog;

    public LoginGoogle(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public void setSignInButton(Object button) {
        if (button instanceof SignInButton) {
            this.button = (SignInButton) button;
            this.button.setSize(SignInButton.SIZE_WIDE);
            this.button.setOnClickListener(this);
        }
    }

    public void disconnect() {
        super.disconnect();
        dismissProgressDialog();
    }

    @Override
    protected void handleSignInResult(GoogleSignInResult result) {
        super.handleSignInResult(result);
        dismissProgressDialog();
    }

    @Override
    public void signIn() {

        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.

        showProgressDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mShouldResolve = true;
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    activity.startActivityForResult(signInIntent, RC_SIGN_IN);
                } catch (Exception e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        super.onConnectionFailed(connectionResult);
        dismissProgressDialog();
    }

    private void showProgressDialog() {
        if (ringProgressDialog == null) {
            ringProgressDialog = new ProgressDialog(activity);
            ringProgressDialog.setMessage(activity.getString(R.string.loading));
            ringProgressDialog.setIndeterminate(true);
            ringProgressDialog.setCancelable(false);
        }

        ringProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (ringProgressDialog != null && ringProgressDialog.isShowing()) {
            ringProgressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        signIn();
    }
}

package com.ashomok.imagetotext.sign_in.social_networks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ashomok.imagetotext.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.Serializable;

import static com.ashomok.imagetotext.sign_in.LoginActivity.RC_SIGN_IN;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;


/**
 * Created by iuliia on 8/4/17.
 */

public class LoginProcessorGoogle implements LoginProcessor,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private static final String TAG = DEV_TAG + LoginProcessorGoogle.class.getSimpleName();
    private FragmentActivity activity;
    private String token;
    private boolean isSignedIn;
    private String email;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    private SignInButton button;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private ProgressDialog ringProgressDialog;

    public LoginProcessorGoogle(FragmentActivity activity) {
        this.activity = activity;

        init();
    }

    @Override
    public void setSignInButton(Object button) {
        if (button instanceof SignInButton) {
            this.button = (SignInButton) button;
            this.button.setSize(SignInButton.SIZE_WIDE);
            this.button.setOnClickListener(this);
        }
    }

    @Override
    public boolean isSignedIn() {
        return isSignedIn;
    }

   private void init() {
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]
    }

    @Nullable
    public String getAccessToken() {
        return token;
    }

    /**
     * There is no reason to keep connected to the GoogleApiClient unless you plan on directly
     * calling one of its APIs later.
     * Once you have the authorization code, you can close the GoogleApiClient. The best place to
     * call this - onStop() method of the Activity.
     */
    public void disconnect() {
        dismissProgressDialog();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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

                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient); //todo error here apiclient may be null - call init before
                    activity.startActivityForResult(signInIntent, RC_SIGN_IN);
                } catch (Exception e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void signOut() {
        if (isSignedIn) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                isSignedIn = false;
                                Log.d(TAG, "signed out");
                            } else {
                                Log.e(TAG, "error occurs in sign out");
                            }
                        }
                    });
        } else {
            Log.d(TAG, "User is already signed out.");
        }
    }

    @Override
    public String getEmail() {
        return email;
    }

    /**
     * should be called by Activity in onActivityResult.
     *
     * @param data
     */
    public void onSignedIn(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            isSignedIn = true;
            // Signed in successfully, show authenticated UI.
            mShouldResolve = false;
            GoogleSignInAccount acct = result.getSignInAccount();

            // Get account information
            String mFullName = acct.getDisplayName();
            email = acct.getEmail();
            token = acct.getIdToken(); //todo send token to the server and validate server-side see https://developers.google.com/identity/sign-in/android/backend-auth

            Log.d(TAG, "You logged as: " + email);

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            isSignedIn = false;
            dismissProgressDialog();
            updateUI(false);
        }
    }


    private void updateUI(boolean signedIn) {
        if (signedIn) {
            dismissProgressDialog();
        } else {
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        dismissProgressDialog();

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(activity, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    Toast.makeText(activity, "Could not resolve ConnectionResult", Toast.LENGTH_LONG).show();
                    mIsResolving = false;
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                Toast.makeText(activity, "Error on LoginProcessor, check your google + login method", Toast.LENGTH_LONG).show();
            }
        } else {
            // Show the signed-out UI
        }
    }

    @Override
    public void trySignIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    dismissProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
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

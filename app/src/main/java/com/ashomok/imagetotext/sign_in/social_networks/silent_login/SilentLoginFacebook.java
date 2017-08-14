package com.ashomok.imagetotext.sign_in.social_networks.silent_login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 8/10/17.
 */

public class SilentLoginFacebook implements SilentLogin {

    private static final String TAG = DEV_TAG + SilentLoginFacebook.class.getSimpleName();
    protected CallbackManager callbackManager;
    protected String token;
    private String email;
    protected boolean isSignedIn;

    public SilentLoginFacebook(CallbackManager callbackManager) {
        this.callbackManager = callbackManager;
    }

    @Override
    @Nullable
    public String getAccessToken() {
        return token;
    }

    @Override
    public boolean isSignedIn() {
        return isSignedIn;
    }

    @Override
    @Nullable
    public String getEmail() {
        return email;
    }

    @Override
    public void trySignIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            //already signed it
            token = accessToken.getToken();
            obtainEmail(accessToken);
            isSignedIn = true;
        }
    }

    @Override
    public void signOutAsync() {
        if (AccessToken.getCurrentAccessToken() == null) {
             // already logged out
        } else {
            new GraphRequest(AccessToken.getCurrentAccessToken(),
                    "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                    .Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {

                    LoginManager.getInstance().logOut();
                }
            }).executeAsync();
        }
    }

    protected void obtainEmail(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            email = object.getString("email");
                            Log.d(TAG, "You logged as: " + email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }
}

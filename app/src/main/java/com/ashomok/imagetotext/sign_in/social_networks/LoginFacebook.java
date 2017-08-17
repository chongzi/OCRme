package com.ashomok.imagetotext.sign_in.social_networks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ashomok.imagetotext.sign_in.OnSignedInListener;
import com.ashomok.imagetotext.sign_in.social_networks.silent_login.SilentLoginFacebook;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;


/**
 * Created by iuliia on 8/4/17.
 */

public class LoginFacebook extends SilentLoginFacebook implements LoginProcessor  {

    private LoginButton button;

    private static final String TAG = DEV_TAG + LoginFacebook.class.getSimpleName();


    public LoginFacebook(CallbackManager callbackManager) {
        super(callbackManager);
    }

    @Override
    public void signIn() {
        Log.w(TAG, "not implemented because of class logic");
    }

    @Override
    public void setSignInButton(Object button) {
        if (button instanceof LoginButton) {
            this.button = (LoginButton) button;

            this.button.setReadPermissions("email");

            // Callback registration
            this.button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    AccessToken accessToken = loginResult.getAccessToken();

                    if (accessToken != null) {
                        isSignedIn = true;

                        //get token string
                        token = accessToken.getToken();
                        obtainEmail(accessToken);

                        if (onSignedInListener != null) {
                            onSignedInListener.onSignedIn();
                        }

                    } else {
                        Log.e(TAG, "ERROR. accessToken == null.");
                    }
                    //todo move next to server side - working with grapf api
//                // App code
//                GraphRequest request = GraphRequest.newMeRequest(
//                        loginResult.getAccessToken(),
//                        new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(
//                                    JSONObject object,
//                                    GraphResponse response) {
//
//                                Log.d(TAG, "Facebook GraphResponse obtained");
//                                Log.d(TAG, response.toString());
//                                // accsess user token obtained - use it here
//                            }
//                        });
//
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id, name, email, gender, birthday");
//                request.setParameters(parameters);
//                request.executeAsync();
                }

                @Override
                public void onCancel() {
                    isSignedIn = false;
                    Log.i(TAG, "User cancelled");
                }

                @Override
                public void onError(FacebookException exception) {
                    isSignedIn = false;
                    Log.e(TAG, "Error on LoginProcessor, check your facebook app_id");
                }
            });
        }
    }
}

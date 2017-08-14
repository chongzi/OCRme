package com.ashomok.imagetotext.sign_in.social_networks.silent_login;

import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by iuliia on 8/14/17.
 */

public interface SilentLogin {

    /**
     * attempt to sign in without user interaction
     */
    void trySignIn();

    /**
     * is user signed in
     * @return
     */
    boolean isSignedIn();

    /**
     * sign out
     */
    void signOutAsync();

    /**
     * get user's email
     * @return email
     */
    @Nullable
    String getEmail();

    /**
     * get access token
     * @return
     */
    @Nullable
    String getAccessToken();
}

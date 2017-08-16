package com.ashomok.imagetotext.sign_in.social_networks;

import com.ashomok.imagetotext.sign_in.OnSignedInListener;
import com.ashomok.imagetotext.sign_in.social_networks.silent_login.SilentLogin;
import com.ashomok.imagetotext.sign_in.social_networks.silent_login.SilentLoginGoogle;

/**
 * Created by iuliia on 8/5/17.
 */
public interface LoginProcessor extends SilentLogin {

    /**
     * Sign in when user click on sign in button
     */
    void signIn();

    /**
     * Set sign in button
     * @param button
     */
    void setSignInButton(Object button);
}

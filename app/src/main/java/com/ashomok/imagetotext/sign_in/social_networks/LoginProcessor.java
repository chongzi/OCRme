package com.ashomok.imagetotext.sign_in.social_networks;

/**
 * Created by iuliia on 8/5/17.
 */
//// TODO: 8/6/17 make it abstract class
public interface LoginProcessor {
    boolean isSignedIn();

    void trySignIn();

    void signIn();

    void signOut();

    String getEmail();

    void setSignInButton(Object button);
}

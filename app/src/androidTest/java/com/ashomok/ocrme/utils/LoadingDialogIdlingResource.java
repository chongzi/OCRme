package com.ashomok.ocrme.utils;

import android.app.Activity;
import android.app.DialogFragment;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingResource;

public class LoadingDialogIdlingResource implements IdlingResource {
    private ResourceCallback resourceCallback;
    private boolean isIdle;

    @Override
    public String getName() {
        return LoadingDialogIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {

        return false;
    }

//    @Override
//    public boolean isIdleNow() {
//        if (isIdle) return true;
//        if (getCurrentActivity() == null) return false;
//
//        DialogFragment f = (DialogFragment) getCurrentActivity().
//                getFragmentManager().findFragmentByTag(LoadingDialog.TAG);
//
//        isIdle = f == null;
//        if (isIdle) {
//            resourceCallback.onTransitionToIdle();
//        }
//        return isIdle;
//    }
//
//    public Activity getCurrentActivity() {
//        return ((TestApplication) InstrumentationRegistry.
//                getTargetContext().getApplicationContext()).getCurrentActivity();
//    }

    @Override
    public void registerIdleTransitionCallback(
            ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}
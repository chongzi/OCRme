package com.ashomok.imagetotext.utils;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.ashomok.imagetotext.R;

/**
 * Created by iuliia on 10/12/17.
 */

public class InfoSnackbarUtil {

    public static void showError(@StringRes int errorMessageRes, View mRootView) {
        if (mRootView != null) {
            Snackbar snackbar = Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(mRootView.getContext(), R.color.red_500));
            snackbar.show();
        }
    }


    public static void showError(Throwable throwable, View mRootView) {
        if (mRootView != null) {
            String localizedMessage = throwable.getLocalizedMessage();
            if (localizedMessage != null && localizedMessage.length() > 0) {
                showError(throwable.getLocalizedMessage(), mRootView);
            } else {
                showError(throwable.getMessage(), mRootView);
            }
        }
    }

    public static void showError(String errorMessage, View mRootView) {
        if (mRootView != null) {
            if (errorMessage != null && errorMessage.length() > 0) {
                Snackbar snackbar = Snackbar.make(mRootView, errorMessage, Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(mRootView.getContext(), R.color.red_500));
                snackbar.show();
            }
        }
    }

    public static void showWarning(@StringRes int messageRes, View mRootView) {
        if (mRootView != null) {
            Snackbar snackbar = Snackbar.make(mRootView, messageRes, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(mRootView.getContext(), R.color.orange_500));
            snackbar.show();
        }
    }

    public static void showInfo(@StringRes int messageRes, View mRootView) {
        if (mRootView != null) {
            Snackbar snackbar = Snackbar.make(mRootView, messageRes, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(mRootView.getContext(), R.color.green_500));
            snackbar.show();
        }
    }
}

package com.ashomok.imagetotext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.ashomok.imagetotext.sign_in.LoginManager;
import com.ashomok.imagetotext.sign_in.LogoutAsyncTask;

/**
 * Created by iuliia on 8/9/17.
 */

public class LogoutDialogFragment extends DialogFragment {


    //todo make LogoutDialogFragment takes no params.
//    Create LoginAutoProcessorGoogle without referance to FragmentActivity - for this - see
//    https://developers.google.com/android/guides/api-client#manually_managed_connections
    public static LogoutDialogFragment newInstance(LoginManager loginManager) {
        LogoutDialogFragment frag = new LogoutDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("loginManager", loginManager);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LoginManager loginManager = (LoginManager) getArguments().getSerializable("loginManager");

        return new AlertDialog.Builder(getActivity())
                .setTitle("You logged as: " + loginManager.getSignedAs() + " Logout?")
                .setPositiveButton(R.string.alert_dialog_logout,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                logout(loginManager);
                            }
                        }
                )
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //nothing
                            }
                        }
                )
                .create();
    }

    private void logout(LoginManager loginManager) {
        LogoutAsyncTask task = new LogoutAsyncTask(loginManager);
        task.execute();
    }
}

package com.ashomok.imagetotext.sign_in;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.ashomok.imagetotext.R;

/**
 * Created by iuliia on 8/9/17.
 */

//todo pls answer https://stackoverflow.com/questions/13338113/managing-activity-from-dialogfragment
public class SignOutDialogFragment extends DialogFragment {

    public interface SignOutListener {
        void onSignedOut();
    }

    private SignOutListener mListener;

    public static SignOutDialogFragment newInstance(String title) {
        SignOutDialogFragment frag = new SignOutDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        mListener = (SignOutListener) getActivity();

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mListener.onSignedOut();
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

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }
}

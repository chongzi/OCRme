package com.ashomok.ocrme.firebaseUiAuth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.ashomok.ocrme.R;

/**
 * Created by iuliia on 8/9/17.
 */
public class SignOutDialogFragment extends DialogFragment {

    private OnSignedOutListener mListener;

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

        mListener = (OnSignedOutListener) getActivity();

        return new AlertDialog.Builder(getActivity())
                .setMessage(title)
                .setPositiveButton(R.string.ok,
                        (dialog, whichButton) -> mListener.onSignedOutCalled()
                )
                .setNegativeButton(R.string.cancel,
                        (dialog, whichButton) -> {
                            //nothing
                        }
                )
                .create();
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface OnSignedOutListener {
        void onSignedOutCalled();
    }
}

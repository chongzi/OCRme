package com.ashomok.imagetotext.ocr_result.tab_fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 8/22/17.
 * this class performs sign in to Firebase Storage
 */

//// TODO: 8/21/17  for better sequirity don't sign in anonymously
public abstract class TabFragment extends Fragment {
    private FirebaseAuth mAuth;
    private static final String TAG = DEV_TAG + TabFragment.class.getSimpleName();
    protected abstract void doStaff();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authenticate();
    }

    private void authenticate() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            doStaff();
        } else {
            signInAnonymously();
        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        doStaff();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                        try {
                            doStaff();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
    }
}

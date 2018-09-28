package com.ashomok.ocrme.rate_app;

import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.utils.RateAppUtils;

/**
 * Created by iuliia on 10/5/16.
 */

public class RateAppDialogFragment extends DialogFragment {
    private OnNeverAskReachedListener onStopAskListener;

    private Button laterBtn;
    private Button neverBtn;
    private Button okBtn;

    public RateAppDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static RateAppDialogFragment newInstance() {
        return new RateAppDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.rate_app_dialog_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        laterBtn = view.findViewById(R.id.later);
        neverBtn = view.findViewById(R.id.never);
        okBtn = view.findViewById(R.id.ok);

        laterBtn.setOnClickListener(view1 -> onLaterBtnClicked());
        neverBtn.setOnClickListener(view1 -> onNeverBtnClicked());
        okBtn.setOnClickListener(view1 -> onOkBtnClicked());
    }

    private void onOkBtnClicked() {
        rate();
        onStopAskListener.onStopAsk();
        dismiss();
    }

    private void onNeverBtnClicked() {
        onStopAskListener.onStopAsk();
        dismiss();
    }

    private void onLaterBtnClicked() {
        dismiss();
    }

    public void setOnStopAskListener(OnNeverAskReachedListener onStopAskListener) {
        this.onStopAskListener = onStopAskListener;
    }

    private void rate() {
        RateAppUtils rateAppUtils = new RateAppUtils();
        rateAppUtils.rate(getActivity());
    }
}

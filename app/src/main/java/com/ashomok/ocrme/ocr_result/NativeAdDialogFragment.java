package com.ashomok.ocrme.ocr_result;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.get_more_requests.GetMoreRequestsActivity;
import com.ashomok.ocrme.update_to_premium.UpdateToPremiumActivity;

/**
 * Created by iuliia on 3/13/18.
 */

public class NativeAdDialogFragment extends DialogFragment {
    private static final String NATIVE_AD_PLACEMENT_ID = "172310460079691_172774360033301";
    Button CTAbtn;
    Button removeAd;
    Button skip;


    public NativeAdDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static NativeAdDialogFragment newInstance() {
        NativeAdDialogFragment frag = new NativeAdDialogFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.request_counter_dialog_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        getMoreBtn = view.findViewById(R.id.get_more_btn);
//        checkPremiumBtn = view.findViewById(R.id.check_premium_btn);
//        message = view.findViewById(R.id.message);
//        counterText = view.findViewById(R.id.counter_text);
//
//        getMoreBtn.setOnClickListener(view1 -> startGetMoreRequestsActivity());
//        checkPremiumBtn.setOnClickListener(view12 -> startUpdateToPremiumActivity());
//
//        int messageResId = getArguments().getInt("message");
//        int requestCount = getArguments().getInt("requestCount");
//
//        String messageText = getString(messageResId, String.valueOf(requestCount));
//        message.setText(messageText);
//        counterText.setText(String.valueOf(requestCount));
    }

    private void startGetMoreRequestsActivity() {
        Intent intent = new Intent(getActivity(), GetMoreRequestsActivity.class);
        startActivity(intent);
    }

    private void startUpdateToPremiumActivity() {
        Intent intent = new Intent(getActivity(), UpdateToPremiumActivity.class);
        startActivity(intent);
    }
}

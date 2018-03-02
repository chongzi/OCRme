package com.ashomok.imagetotext.main;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.get_more_requests.GetMoreRequestsActivity;
import com.ashomok.imagetotext.update_to_premium.UpdateToPremiumActivity;

/**
 * Created by iuliia on 2/27/18.
 */

public class RequestsCounterDialogFragment extends DialogFragment {

    private Button getMoreBtn;
    private Button checkPremiumBtn;
    private TextView message;
    private TextView counterText;

    public RequestsCounterDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static RequestsCounterDialogFragment newInstance(
            @StringRes int message, int requestCount) {

        RequestsCounterDialogFragment frag = new RequestsCounterDialogFragment();
        Bundle args = new Bundle();
        args.putInt("message", message);
        args.putInt("requestCount", requestCount);
        frag.setArguments(args);
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

        getMoreBtn = view.findViewById(R.id.get_more_btn);
        checkPremiumBtn = view.findViewById(R.id.check_premium_btn);
        message = view.findViewById(R.id.message);
        counterText = view.findViewById(R.id.counter_text);

        getMoreBtn.setOnClickListener(view1 -> startGetMoreRequestsActivity());
        checkPremiumBtn.setOnClickListener(view12 -> startUpdateToPremiumActivity());

        int messageResId = getArguments().getInt("message");
        int requestCount = getArguments().getInt("requestCount");

        String messageText = getString(messageResId, String.valueOf(requestCount));
        message.setText(messageText);
        counterText.setText(String.valueOf(requestCount));
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
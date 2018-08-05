package com.ashomok.ocrme.rate_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.ashomok.ocrme.R;

/**
 * Created by iuliia on 10/5/16.
 */

public class RateAppDialogFragment extends DialogFragment {
    private OnNeverAskReachedListener onStopAskListener;

    public static RateAppDialogFragment newInstance(int title) {
        RateAppDialogFragment frag = new RateAppDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(R.string.share_opinion)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                rate();
                                onStopAskListener.onStopAsk();
                                dialog.cancel();

                            }
                        })

                .setNeutralButton(R.string.later,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(R.string.never,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onStopAskListener.onStopAsk();
                                dialog.cancel();
                            }
                        })
                .create();
    }

    public void setOnStopAskListener(OnNeverAskReachedListener onStopAskListener) {
        this.onStopAskListener = onStopAskListener;
    }

    private void rate() {
        Toast.makeText(getActivity(), R.string.thank_you_for_your_support, Toast.LENGTH_SHORT).show();
        String appPackageName = getActivity().getPackageName();
        openPackageInMarket(appPackageName);
    }

    private void openPackageInMarket(String appPackageName) {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        try {
            getActivity().startActivity(marketIntent);
        } catch (ActivityNotFoundException exception) {
            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}

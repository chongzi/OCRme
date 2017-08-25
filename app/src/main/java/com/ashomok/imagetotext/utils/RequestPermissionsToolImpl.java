package com.ashomok.imagetotext.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.support.v13.app.FragmentCompat;
import android.widget.Toast;

import com.ashomok.imagetotext.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iuliia on 10/15/16.
 * TODO unstable
 * works only with one permission
 */

public class RequestPermissionsToolImpl implements RequestPermissionsTool {

    private static final String CONFIRMATION_DIALOG = "ConfirmationDialog";
    private static final String TAG = RequestPermissionsToolImpl.class.getSimpleName();
    private Fragment fragment;


    @Override
    public void requestPermissions(Fragment fragment, String[] permissions) {
        Map<Integer, String> permissionsMap = new HashMap<>();
        this.fragment = fragment;


        for (int i = 0; i < permissions.length; ++i) {
            permissionsMap.put(i, permissions[i]);
        }

        for (Map.Entry<Integer, String> permission : permissionsMap.entrySet()) {
            if (!isPermissionGranted(fragment.getActivity(), permission.getValue())) {
                if (FragmentCompat.shouldShowRequestPermissionRationale(fragment, permission.getValue())) {

                    ConfirmationDialog.newInstance(permission.getKey(), permission.getValue()).
                            show(fragment.getFragmentManager(), CONFIRMATION_DIALOG);
                }
                else {
                    FragmentCompat.requestPermissions(fragment, permissions,
                            permission.getKey());
                    return;
                }
            }
        }
    }

    @Override
    public boolean isPermissionsGranted(Context context, String[] permissions) {

        for (String permission : permissions) {
            if (!isPermissionGranted(context, permission)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onPermissionDenied() {
        ErrorDialog.newInstance(fragment.getResources().getString(R.string.permissions_needs)).
        show(fragment.getFragmentManager(), CONFIRMATION_DIALOG);
    }

    private boolean isPermissionGranted(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context,
                permission)
                == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Shows OK/Cancel confirmation dialog about permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        private static final String ARG_PERMISSION = "permission";
        private static final String ARG_REQUEST_CODE = "request_code";

        public static ConfirmationDialog newInstance(int permissionKey, String permissionValue) {
            ConfirmationDialog dialog = new ConfirmationDialog();
            Bundle bundle = new Bundle();
            bundle.putString(ARG_PERMISSION, permissionValue);
            bundle.putInt(ARG_REQUEST_CODE, permissionKey);
            dialog.setArguments(bundle);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.allow_permissions)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{getArguments().getString(ARG_PERMISSION)},
                                    getArguments().getInt(ARG_REQUEST_CODE));
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(), R.string.not_avalible, Toast.LENGTH_SHORT).show();
                                }
                            })
                    .create();
        }
    }

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//nothing
                        }
                    })
                    .create();
        }

    }


}

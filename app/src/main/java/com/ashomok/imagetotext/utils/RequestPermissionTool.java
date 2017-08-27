package com.ashomok.imagetotext.utils;

import android.app.Fragment;
import android.content.Context;

/**
 * Created by iuliia on 10/15/16.
 */

public interface RequestPermissionTool {
    void requestPermission(Fragment context, String permission);
    void requestPermission(Fragment fragment, String permission, int requestCode);

    boolean isPermissionsGranted(Context context, String[] permissions);
    boolean isPermissionGranted(Context context, String permission);

    void onPermissionDenied();

}

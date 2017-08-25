package com.ashomok.imagetotext.utils;

import android.app.Fragment;
import android.content.Context;

/**
 * Created by iuliia on 10/15/16.
 */

public interface RequestPermissionsTool {
    void requestPermissions(Fragment context, String[] permissions);

    boolean isPermissionsGranted(Context context, String[] permissions);

    void onPermissionDenied();
}

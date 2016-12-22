package com.ashomok.imagetotext.ocr_task;

import android.graphics.Bitmap;

/**
 * Created by Iuliia on 24.12.2015.
 */
interface BitmapTaskDelegate {
    void TaskCompletionResult(Bitmap result);
}

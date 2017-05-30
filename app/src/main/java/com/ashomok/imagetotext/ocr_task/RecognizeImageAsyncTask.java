package com.ashomok.imagetotext.ocr_task;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Iuliia on 19.11.2015.
 */
public abstract class RecognizeImageAsyncTask extends AsyncTask<Void, Integer, OCRResult> {

    private static final String TAG = RecognizeImageAsyncTask.class.getSimpleName();
    private OnTaskCompletedListener onTaskCompletedListener;

    @Override
    protected abstract OCRResult doInBackground(Void... params);

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(OCRResult result) {
        if (result.getError() == null || result.getError().length() == 0) {
            String text = result.getText();
            onTaskCompletedListener.onTaskCompleted(text);
            if (text != null) {
                Log.d(TAG, text);
            }
        } else {
            String error = result.getError();
            onTaskCompletedListener.onError(error);
            if (error != null) {
                Log.e(TAG, error);
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    public void setOnTaskCompletedListener(OnTaskCompletedListener onTaskCompletedListener) {
        this.onTaskCompletedListener = onTaskCompletedListener;
    }

    public interface OnTaskCompletedListener {
        void onTaskCompleted(String result);
        void onError(String message);
    }
}


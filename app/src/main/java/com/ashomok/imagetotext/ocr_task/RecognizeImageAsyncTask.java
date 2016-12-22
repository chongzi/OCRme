package com.ashomok.imagetotext.ocr_task;

import android.os.AsyncTask;

/**
 * Created by Iuliia on 19.11.2015.
 */
public abstract class RecognizeImageAsyncTask extends AsyncTask<Void, Integer, String> {

    private OnTaskCompletedListener onTaskCompletedListener;

    @Override
    protected abstract String doInBackground(Void... params);

    @Override
    protected void onPreExecute() {
    }


    @Override
    protected void onPostExecute(String result) {

        onTaskCompletedListener.onTaskCompleted(result);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    public void setOnTaskCompletedListener(OnTaskCompletedListener onTaskCompletedListener) {
        this.onTaskCompletedListener = onTaskCompletedListener;
    }

    public interface OnTaskCompletedListener{
        void onTaskCompleted(String result);
    }
}


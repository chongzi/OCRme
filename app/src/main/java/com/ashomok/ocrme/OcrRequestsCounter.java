package com.ashomok.ocrme;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by iuliia on 3/9/18.
 */

@Singleton
public class OcrRequestsCounter {

    private static final String AVAILABLE_OCR_REQUESTS_COUNT_TAG = "availableOcrRequestsCount";
    private static final int INIT_OCR_REQUESTS_COUNT = 5;
    private final SharedPreferences sharedPreferences;
    @Inject
    public OcrRequestsCounter(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public int getAvailableOcrRequests() {
        return sharedPreferences.getInt(AVAILABLE_OCR_REQUESTS_COUNT_TAG, INIT_OCR_REQUESTS_COUNT);
    }

    public void saveAvailableOcrRequests(int available) {
        sharedPreferences.edit().putInt(AVAILABLE_OCR_REQUESTS_COUNT_TAG, available).apply();
    }

    public void consumeRequest() {
        int amount = getAvailableOcrRequests();
        saveAvailableOcrRequests(--amount);
    }
}

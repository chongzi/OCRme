package com.ashomok.ocrme.rate_app;

import android.app.DialogFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.utils.Repeat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static com.ashomok.ocrme.rate_app.RateAppAsker.RATE_APP_COUNT;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@SmallTest
//docs https://medium.com/@fabioCollini/android-testing-using-dagger-2-mockito-and-a-custom-junit-rule-c8487ed01b56
public class RateAppAskerTest  {
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    Context context;

    private RateAppAsker rateAppAsker;

    private static final String TAG = DEV_TAG + RateAppAskerTest.class.getSimpleName();

    @Before
    public void setUp() {
        RateAppTestComponent component = DaggerRateAppTestComponent.builder()
                .rateAppTestModule(new RateAppTestModule())
                .build();
        component.inject(this);

        rateAppAsker = new RateAppAsker(sharedPreferences, context);
    }

    @Test
    public void testAskNothing() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.times_app_was_used), 0);
        editor.apply();
        rateAppAsker.init(rateAppDialogFragment -> {
            Log.d(TAG, "dialog showed");
            throw new AssertionError("dialog showed when not expected");
        });

        int timesAppWasUsed = sharedPreferences.getInt(context.getString(R.string.times_app_was_used), 0);

        Assert.assertEquals(1, timesAppWasUsed);
    }

    @Test
    public void testAsk() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.times_app_was_used), RATE_APP_COUNT);
        editor.apply();
        rateAppAsker.init(rateAppDialogFragment -> {
            Log.d(TAG, "dialog showed");
            Assert.assertNotNull(rateAppDialogFragment);
        });

        int timesAppWasUsed = sharedPreferences.getInt(context.getString(R.string.times_app_was_used), 0);

        Assert.assertEquals(0, timesAppWasUsed);
    }
}
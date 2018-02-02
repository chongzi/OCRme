package com.ashomok.imagetotext.update_to_premium.model;

/**
 * Created by iuliia on 2/2/18.
 */

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.Log;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.di_dagger.ActivityScoped;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

public class FeaturesList {
    public static final String TAG = DEV_TAG + FeaturesList.class.getSimpleName();

    public FeaturesList() {
        Log.d(TAG, "constructor call");
    }

    public List<FeatureModel> getList() {
        List<FeatureModel> result = new ArrayList<>();
        result.add(new FeatureModel(R.drawable.ic_android_black_24dp, R.string.allow_new_email_acccount));
        result.add(new FeatureModel(R.drawable.ic_android_black_24dp, R.string.allow_new_email_acccount));
        result.add(new FeatureModel(R.drawable.ic_android_black_24dp, R.string.allow_new_email_acccount));
        result.add(new FeatureModel(R.drawable.ic_android_black_24dp, R.string.allow_new_email_acccount));
        result.add(new FeatureModel(R.drawable.ic_android_black_24dp, R.string.allow_new_email_acccount));
        result.add(new FeatureModel(R.drawable.ic_android_black_24dp, R.string.allow_new_email_acccount));
        return result;
    }

    public class FeatureModel {
        @DrawableRes
        private int drawableId;
        @StringRes
        private int stringId;

        public int getDrawableId() {
            return drawableId;
        }

        public int getStringId() {
            return stringId;
        }

        public FeatureModel(@DrawableRes int drawableId, @StringRes int stringId) {
            this.drawableId = drawableId;
            this.stringId = stringId;
        }
    }
}

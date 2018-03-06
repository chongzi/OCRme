package com.ashomok.imagetotext.update_to_premium;

/**
 * Created by iuliia on 2/2/18.
 */

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.ashomok.imagetotext.R;

import java.util.ArrayList;
import java.util.List;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

public class FeaturesList {
    public static final String TAG = DEV_TAG + FeaturesList.class.getSimpleName();

    public FeaturesList() {}

    public static List<FeatureModel> getList() {
        List<FeatureModel> result = new ArrayList<>();
        result.add(new FeatureModel(R.drawable.ic_translate_black_24dp, R.string.no_ads));
        result.add(new FeatureModel(R.drawable.ic_translate_black_24dp, R.string.unlimited_ocr_requests));
        result.add(new FeatureModel(R.drawable.ic_translate_black_24dp, R.string.unlimited_translate_requests));
        result.add(new FeatureModel(R.drawable.ic_translate_black_24dp, R.string.languages_supported_for_ocr));
        result.add(new FeatureModel(R.drawable.ic_translate_black_24dp, R.string.languages_supported_for_translate));
        result.add(new FeatureModel(R.drawable.ic_translate_black_24dp, R.string.highprecision_recognition_system));
        return result;
    }

    public static class FeatureModel {
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

package com.ashomok.imagetotext.ocr_result;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ashomok.imagetotext.ocr_result.tab_fragments.PDFFragment;
import com.ashomok.imagetotext.ocr_result.tab_fragments.TextFragment;

/**
 * Created by iuliia on 5/31/17.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    public static final int ITEM_COUNT = 2;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    @Nullable
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TextFragment();
            case 1:
                return new PDFFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }
}

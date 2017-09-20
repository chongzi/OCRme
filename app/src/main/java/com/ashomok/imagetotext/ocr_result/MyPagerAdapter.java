package com.ashomok.imagetotext.ocr_result;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;

import com.ashomok.imagetotext.ocr.ocr_task.OcrResponse;
import com.ashomok.imagetotext.ocr_result.tab_fragments.PdfFragment;
import com.ashomok.imagetotext.ocr_result.tab_fragments.TextFragment;

import static com.ashomok.imagetotext.ocr_result.tab_fragments.PdfFragment.EXTRA_PDF_URL;
import static com.ashomok.imagetotext.ocr_result.tab_fragments.TextFragment.EXTRA_TEXT;

/**
 * Created by iuliia on 5/31/17.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    private static final int ITEM_COUNT = 2;
    private OcrResponse ocrData;

    MyPagerAdapter(FragmentManager fm, OcrResponse ocrData) {
        super(fm);
        this.ocrData = ocrData;
    }

    @Override
    @Nullable
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                initTextFragment(ocrData.getTextResult());
            case 1:
                initPDFFragment(ocrData.getPdfResultUrl());
            default:
                return null;
        }
    }

    private TextFragment initTextFragment(String text) {
        TextFragment fragment = new TextFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(EXTRA_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    private PdfFragment initPDFFragment(String pdfUrl) {
        PdfFragment fragment = new PdfFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(EXTRA_PDF_URL, pdfUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }
}

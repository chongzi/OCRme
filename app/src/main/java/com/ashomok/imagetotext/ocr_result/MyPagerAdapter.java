package com.ashomok.imagetotext.ocr_result;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;

import com.ashomok.imagetotext.ocr.ocr_task.OcrResponse;
import com.ashomok.imagetotext.ocr.ocr_task.OcrResult;
import com.ashomok.imagetotext.ocr_result.tab_fragments.PdfFragment;
import com.ashomok.imagetotext.ocr_result.tab_fragments.TextFragment;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static com.ashomok.imagetotext.ocr_result.tab_fragments.PdfFragment.EXTRA_PDF_GS_URL;
import static com.ashomok.imagetotext.ocr_result.tab_fragments.PdfFragment.EXTRA_PDF_MEDIA_URL;
import static com.ashomok.imagetotext.ocr_result.tab_fragments.TextFragment.EXTRA_IMAGE_URL;
import static com.ashomok.imagetotext.ocr_result.tab_fragments.TextFragment.EXTRA_LANGUAGES;
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
        OcrResult ocrResult = ocrData.getOcrResult();
        switch (position) {
            case 0:
                return initTextFragment(ocrResult);
            case 1:
                return initPDFFragment(ocrResult);
            default:
                return null;
        }
    }

    private PdfFragment initPDFFragment(OcrResult ocrResult) {
        PdfFragment fragment = new PdfFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(EXTRA_PDF_GS_URL, ocrResult.getPdfResultGsUrl());
        bundle.putCharSequence(EXTRA_PDF_MEDIA_URL, ocrResult.getPdfResultGsUrl());
        fragment.setArguments(bundle);
        return fragment;
    }

    private TextFragment initTextFragment(OcrResult ocrResult) {
        TextFragment fragment = new TextFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(EXTRA_TEXT, ocrResult.getTextResult());
        bundle.putCharSequence(EXTRA_IMAGE_URL, ocrResult.getSourceImageUrl());
        bundle.putStringArray(EXTRA_LANGUAGES, ocrResult.getLanguages());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }
}

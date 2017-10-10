package com.ashomok.imagetotext.ocr_result;

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;

import com.ashomok.imagetotext.ocr.ocr_task.OcrResponse;
import com.ashomok.imagetotext.ocr_result.tab_fragments.PdfFragment;
import com.ashomok.imagetotext.ocr_result.tab_fragments.TextFragment;

import static com.ashomok.imagetotext.ocr_result.tab_fragments.PdfFragment.EXTRA_PDF_GS_URL;
import static com.ashomok.imagetotext.ocr_result.tab_fragments.PdfFragment.EXTRA_PDF_MEDIA_URL;
import static com.ashomok.imagetotext.ocr_result.tab_fragments.TextFragment.EXTRA_IMAGE_URI;
import static com.ashomok.imagetotext.ocr_result.tab_fragments.TextFragment.EXTRA_TEXT;

/**
 * Created by iuliia on 5/31/17.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    private static final int ITEM_COUNT = 2;
    private OcrResponse ocrData;
    private Uri imageUri;

    MyPagerAdapter(FragmentManager fm, OcrResponse ocrData, Uri imageUri) {
        super(fm);
        this.ocrData = ocrData;
        this.imageUri = imageUri;
    }

    @Override
    @Nullable
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return initTextFragment(ocrData.getTextResult());
            case 1:
                return initPDFFragment(ocrData.getPdfResultGsUrl(), ocrData.getPdfResultMediaUrl());
            default:
                return null;
        }
    }

    private TextFragment initTextFragment(String text) {
        TextFragment fragment = new TextFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(EXTRA_TEXT, text);
        bundle.putParcelable(EXTRA_IMAGE_URI, imageUri);
        fragment.setArguments(bundle);
        return fragment;
    }

    private PdfFragment initPDFFragment(String pdfResultGsUrl, String pdfResultMediaUrl) {
        PdfFragment fragment = new PdfFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(EXTRA_PDF_GS_URL, pdfResultGsUrl);
        bundle.putCharSequence(EXTRA_PDF_MEDIA_URL, pdfResultMediaUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }
}

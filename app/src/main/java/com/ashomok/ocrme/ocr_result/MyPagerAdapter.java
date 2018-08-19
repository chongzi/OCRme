package com.ashomok.ocrme.ocr_result;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.ashomok.ocrme.ocr.ocr_task.OcrResponse;
import com.ashomok.ocrme.ocr.ocr_task.OcrResult;
import com.ashomok.ocrme.ocr_result.tab_fragments.image_pdf.ImagePdfFragment;
import com.ashomok.ocrme.ocr_result.tab_fragments.searchable_pdf.SearchablePdfFragment;
import com.ashomok.ocrme.ocr_result.tab_fragments.text.TextFragment;

import java.util.ArrayList;
import java.util.List;

import static com.ashomok.ocrme.ocr_result.tab_fragments.text.TextFragment.EXTRA_IMAGE_URL;
import static com.ashomok.ocrme.ocr_result.tab_fragments.text.TextFragment.EXTRA_LANGUAGES;
import static com.ashomok.ocrme.ocr_result.tab_fragments.text.TextFragment.EXTRA_TEXT;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 5/31/17.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    private static int ITEM_COUNT = 3;
    private OcrResponse ocrData;
    public static final String TAG = DEV_TAG + MyPagerAdapter.class.getSimpleName();

    MyPagerAdapter(FragmentManager fm, OcrResponse ocrData) {
        super(fm);
        this.ocrData = ocrData;
        Log.d(TAG, "Adapter obtained ocr data: " + ocrData.toString());

        OcrResult ocrResult = ocrData.getOcrResult();
        //don't init imagePDFFragment for old docs - backward compatibility
        if (ocrResult.getPdfImageResultGsUrl() == null || ocrResult.getPdfImageResultMediaUrl() == null) {
            //old doc
            ITEM_COUNT = 2;
        } else {
            //new doc
            ITEM_COUNT = 3;
        }
    }

    @Override
    @Nullable
    public Fragment getItem(int position) {
        OcrResult ocrResult = ocrData.getOcrResult();

        if (ITEM_COUNT == 2) {
            //old doc
            switch (position) {
                case 0:
                    return initTextFragment(ocrResult);
                case 1:
                    return initSearchablePDFFragment(ocrResult);
                default:
                    return null;
            }

        } else if (ITEM_COUNT == 3) {
            //new doc
            switch (position) {
                case 0:
                    return initTextFragment(ocrResult);
                case 1:
                    return initImagePDFFragment(ocrResult);
                case 2:
                    return initSearchablePDFFragment(ocrResult);
                default:
                    return null;
            }
        } else {
            Log.e(TAG, "unexpected fragments count");
            return null;
        }
    }

    private SearchablePdfFragment initSearchablePDFFragment(OcrResult ocrResult) {
        SearchablePdfFragment fragment = new SearchablePdfFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SearchablePdfFragment.EXTRA_PDF_GS_URL, ocrResult.getPdfResultGsUrl());
        bundle.putString(SearchablePdfFragment.EXTRA_PDF_MEDIA_URL, ocrResult.getPdfResultMediaUrl());
        fragment.setArguments(bundle);
        return fragment;
    }

    private ImagePdfFragment initImagePDFFragment(OcrResult ocrResult) {
        ImagePdfFragment fragment = new ImagePdfFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ImagePdfFragment.EXTRA_PDF_GS_URL, ocrResult.getPdfImageResultGsUrl());
        bundle.putString(ImagePdfFragment.EXTRA_PDF_MEDIA_URL, ocrResult.getPdfImageResultMediaUrl());
        fragment.setArguments(bundle);
        return fragment;
    }

    private TextFragment initTextFragment(OcrResult ocrResult) {
        TextFragment fragment = new TextFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TEXT, ocrResult.getTextResult());
        bundle.putString(EXTRA_IMAGE_URL, ocrResult.getSourceImageUrl());
        List<String> languages = ocrResult.getLanguages();
        if (languages != null) {
            bundle.putStringArrayList(EXTRA_LANGUAGES, new ArrayList<>(languages));
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }
}

package com.ashomok.imagetotext.ocr_result.tab_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashomok.imagetotext.R;

/**
 * Created by iuliia on 5/31/17.
 */

public class PDFFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pdf_fragment, container, false);
    }
}

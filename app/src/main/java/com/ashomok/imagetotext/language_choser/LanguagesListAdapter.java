package com.ashomok.imagetotext.language_choser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.ashomok.imagetotext.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static com.ashomok.imagetotext.utils.InfoSnackbarUtil.showError;

/**
 * Created by iuliia on 12/11/16.
 */

public class LanguagesListAdapter extends BaseAdapter {

    private static final String TAG = LanguagesListAdapter.class.getSimpleName();
    private static final int MAX_CHECKED_ALLOWED = 5;
    List<String> allLanguages;
    List<String> checkedLanguages;

    public LanguagesListAdapter(List<String> allLanguages,
                                List<String> checkedLanguages) {
        this.allLanguages = allLanguages;
        this.checkedLanguages = checkedLanguages;
    }

    public List<String> getCheckedLanguages() {
        return checkedLanguages;
    }

    private void addToChecked(String language) {
        if (checkedLanguages.size() < MAX_CHECKED_ALLOWED) {
            checkedLanguages.add(language);
        } else {
            Log.w(TAG, "attempt to add checked language when max amount reached");
        }
    }

    private void removeFromChecked(String language) {
        checkedLanguages.remove(language);
    }

    @Override
    public int getCount() {
        return allLanguages.size();
    }

    @Override
    public String getItem(int i) {
        return allLanguages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final View view;

        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ocr_language_row, parent, false);
        } else {
            view = convertView;
        }

        String item = getItem(position);

        CheckedTextView checkedTextView = view.findViewById(R.id.language_name);
        checkedTextView.setText(item);
        checkedTextView.setOnClickListener(view12 -> {
            CheckedTextView ctv = (CheckedTextView) view12;
            String language = (String) ctv.getText();
            if (ctv.isChecked()) {
                if (checkedLanguages.size() < MAX_CHECKED_ALLOWED) {
                    addToChecked(language);
                } else {
                    ctv.setChecked(false); //todo reduntant

                    String message = String.format(view12.getContext().getString(R.string.max_checked_allowed),
                            String.valueOf(MAX_CHECKED_ALLOWED));
                    showError (message, parent);
                }
            } else {
                removeFromChecked(language);
            }
        });


        if (checkedLanguages.contains(item)) {
            checkedTextView.setChecked(true);
        } else {
            checkedTextView.setChecked(false);
        }

        return view;
    }
}

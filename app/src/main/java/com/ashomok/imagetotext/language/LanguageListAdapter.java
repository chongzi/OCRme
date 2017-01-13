package com.ashomok.imagetotext.language;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.ashomok.imagetotext.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by iuliia on 12/11/16.
 */

public class LanguageListAdapter extends BaseAdapter {

    private static final String TAG = LanguageListAdapter.class.getSimpleName();
    private final ArrayList<String> languages;
    private final Context context;

    public LinkedHashSet<String> getCheckedLanguages() {
        return checkedLanguages;
    }

    private LinkedHashSet<String> checkedLanguages;
    private static final int MAX_CHECKED_ALLOWED = 5;

    public LanguageListAdapter(Context context, Set<String> data, LinkedHashSet<String> checked) {
        this.context = context;
        languages = new ArrayList<>();
        languages.addAll(data);

        checkedLanguages = checked;
    }

    private void addToChecked(String language) {
        if (checkedLanguages.size() < MAX_CHECKED_ALLOWED) {

            //remove auto if another checked
            String auto = context.getString(R.string.auto);
            if ((!language.equals(auto)) && checkedLanguages.size() > 0) {
                checkedLanguages.remove(auto);
                notifyDataSetChanged();

            //remove another if auto checked
            } else if (language.equals(auto) && checkedLanguages.size() > 0) {
                checkedLanguages.clear();
                checkedLanguages.add(auto);
                notifyDataSetChanged();
            }

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
        return languages.size();
    }

    @Override
    public String getItem(int i) {
        return languages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final View view;

        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_row, parent, false);
        } else {
            view = convertView;
        }

        String item = getItem(position);
        TextView languageView = (TextView) view.findViewById(R.id.language);
        languageView.setText(item);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.setTag(item);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                String language = (String) cb.getTag();
                if (cb.isChecked()) {
                    if (checkedLanguages.size() < MAX_CHECKED_ALLOWED) {
                        addToChecked(language);
                    } else {
                        cb.setChecked(false);
                        String message = String.format(view.getContext().getString(R.string.max_checked_allowed),
                                String.valueOf(MAX_CHECKED_ALLOWED));
                        Toast.makeText(view.getContext(), message,
                                Toast.LENGTH_SHORT).show();

                    }
                } else {
                    removeFromChecked(language);
                }
            }
        });

        if (checkedLanguages.contains(item)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        return view;
    }
}

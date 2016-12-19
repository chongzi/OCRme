package com.ashomok.imagetotext.language;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ashomok.imagetotext.R;

/**
 * Created by iuliia on 12/11/16.
 */

public class LanguageListAdapter extends ArrayAdapter<Language> {

    private Context context;

    public LanguageListAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.language_row, parent, false);
        } else {
            view = convertView;
        }

        Language language = getItem(position);
        TextView languageView = (TextView) view.findViewById(R.id.language);
        languageView.setText(language.getName());

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.setTag(language);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                Language language = (Language) cb.getTag();
                language.setChecked(cb.isChecked());
            }
        });

        checkBox.setChecked(language.isChecked());

        return view;
    }
}

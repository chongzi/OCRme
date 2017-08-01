package com.ashomok.imagetotext.language_choser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.ashomok.imagetotext.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Created by iuliia on 12/11/16.
 */

public class LanguageActivity extends AppCompatActivity {
    private static final String TAG = LanguageActivity.class.getSimpleName();
    private LanguageListAdapter adapter;
    public static final String CHECKED_LANGUAGES = "checked_languages_set";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        initToolbar();

        LinkedHashSet<String> checkedLanguages = obtainSavedData();

        ListView listView = (ListView) findViewById(R.id.language_list);
        LanguageList data = new LanguageList(this);
        adapter = new LanguageListAdapter(this, data.getLanguages().keySet(), checkedLanguages);
        listView.setAdapter(adapter);
    }

    private LinkedHashSet<String> obtainSavedData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<String> data = extras.getStringArrayList(CHECKED_LANGUAGES);
            if (data != null) {
                return new LinkedHashSet<>(data);
            }
        }
        return new LinkedHashSet<>();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //back btn pressed
                Intent intent = new Intent();

                ArrayList<String> languages = new ArrayList<>();
                languages.addAll(adapter.getCheckedLanguages());
                intent.putExtra(CHECKED_LANGUAGES, languages);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}

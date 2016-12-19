package com.ashomok.imagetotext.language;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.ashomok.imagetotext.R;

/**
 * Created by iuliia on 12/11/16.
 */

public class LanguageActivity extends AppCompatActivity {
    private static final String TAG = LanguageActivity.class.getSimpleName();
    private ListView listView;
    private LanguageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        initToolbar();

        listView = (ListView) findViewById(R.id.language_list);
        adapter = new LanguageListAdapter(this, 0);
        listView.setAdapter(adapter);


        LanguageList data = LanguageList.getInstance();
        adapter.addAll(data.getLanguages());
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
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}

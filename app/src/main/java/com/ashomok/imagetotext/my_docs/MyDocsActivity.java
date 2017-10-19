package com.ashomok.imagetotext.my_docs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ashomok.imagetotext.R;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 8/18/17.
 */

//// TODO: 10/3/17 make extends @BaseLoginActivity
public class MyDocsActivity extends AppCompatActivity {
    private static final String TAG = DEV_TAG + MyDocsActivity.class.getSimpleName();
    public static final String IS_SIGNED_IN_TAG = "IS_SIGNED_IN_TAG";
    private boolean isUserSignedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_docs);

        initToolbar();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isUserSignedIn = getIntent().getBooleanExtra(IS_SIGNED_IN_TAG, false);
            updateUi();
        }
    }

    private void updateUi() {
        View askLoginView = findViewById(R.id.ask_login);
        if (isUserSignedIn) {
            askLoginView.setVisibility(View.GONE);
        } else {
            askLoginView.setVisibility(View.VISIBLE);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}

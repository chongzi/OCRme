package com.ashomok.imagetotext.my_docs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.firebaseUiAuth.BaseLoginActivity;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 8/18/17.
 */

//https://www.journaldev.com/13792/android-gridlayoutmanager-example
//        https://developer.android.com/training/material/lists-cards.html

public class MyDocsActivity extends BaseLoginActivity implements View.OnClickListener {
    private static final String TAG = DEV_TAG + MyDocsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_docs);

        initToolbar();

        Button signInBtn = findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(this);
    }

    /**
     * update UI if signed in/out
     */
    @Override
    public void updateUi(boolean isUserSignedIn) {
        Log.d(TAG, "updateUi called with " + isUserSignedIn);
        View askLoginView = findViewById(R.id.ask_login);
        View myDocsView = findViewById(R.id.my_docs);

        askLoginView.setVisibility(isUserSignedIn? View.GONE: View.VISIBLE);
        myDocsView.setVisibility(isUserSignedIn? View.VISIBLE: View.GONE);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_btn:
                signIn();
                break;
            default:
                break;
        }
    }
}

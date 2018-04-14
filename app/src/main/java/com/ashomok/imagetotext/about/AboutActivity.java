package com.ashomok.imagetotext.about;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ashomok.imagetotext.BuildConfig;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.Settings;
import com.ashomok.imagetotext.update_to_premium.UpdateToPremiumActivity;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;


/**
 * Created by Iuliia on 30.08.2015.
 */
public class AboutActivity extends AppCompatActivity {

    private TextView mTextView_email1;
    private TextView mTextView_email2;
    private static final String TAG = DEV_TAG + AboutActivity.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.about_layout);

            initToolbar();

            //base app data
            TextView mTextView_appName = findViewById(R.id.appName);
            mTextView_appName.setText(R.string.app_name);

            TextView mTextView_developer = findViewById(R.id.developer);
            mTextView_developer.setText(R.string.author);

            mTextView_email1 = findViewById(R.id.email);
            mTextView_email1.setText(
                    Html.fromHtml("<u>" + getString(R.string.my_email) + "</u>"));
            mTextView_email1.setOnClickListener(
                    view -> copyTextToClipboard(mTextView_email1.getText()));

            mTextView_email2 = findViewById(R.id.email2);
            mTextView_email2.setText(
                    Html.fromHtml("<u>" + getString(R.string.my_email) + "</u>"));
            mTextView_email2.setOnClickListener(
                    view -> copyTextToClipboard(mTextView_email2.getText()));

            TextView mTextView_version = findViewById(R.id.version);
            String version = getString(R.string.version) + " " + BuildConfig.VERSION_NAME;
            mTextView_version.setText(version);

            if (!Settings.isPremium) {
                View freeVersionCard = findViewById(R.id.free_version_explanation_card);
                freeVersionCard.setVisibility(View.VISIBLE);
                //please buy ads-free version
                TextView mTextView_buy_paid = findViewById(R.id.buy_paid);
                mTextView_buy_paid.setText(
                        Html.fromHtml("<u>" + getString(R.string.buy_paid) + "</u>"));

                mTextView_buy_paid.setOnClickListener(view -> startUpdateToPremiumActivity());
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_about_layout);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void copyTextToClipboard(CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.my_email), text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 300 milliseconds
        if (v != null) {
            v.vibrate(300);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startUpdateToPremiumActivity() {
        Intent intent = new Intent(this, UpdateToPremiumActivity.class);
        startActivity(intent);
    }
}

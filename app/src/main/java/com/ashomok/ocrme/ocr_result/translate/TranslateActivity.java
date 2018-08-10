package com.ashomok.ocrme.ocr_result.translate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.ashomok.ocrme.R;
import com.ashomok.ocrme.ocr_result.translate.translate_task.translate_task.SupportedLanguagesResponse;
import com.ashomok.ocrme.utils.InfoSnackbarUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.ashomok.ocrme.Settings.appPackageName;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 8/27/17.
 */

//// TODO: MAJOR 9/14/17 replace spinners with new Language Activity - see Google Translate APP for example
public class TranslateActivity extends RxAppCompatActivity implements View.OnClickListener,
        TranslateContract.View {

    private static final String TAG = DEV_TAG + TranslateActivity.class.getSimpleName();
    public static final String EXTRA_TEXT = "com.ashomokdev.imagetotext.TEXT";

    private Spinner sourceLanguagesSpinner;
    private Spinner targetLanguagesSpinner;
    private TextView sourceTextView;
    private EditText targetEditText;
    private ProgressBar progress;
    private View contentLayout;
    private String sourceText;

    public View mRootView;

    @Inject
    TranslatePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this); //todo or extends daggerappcompat activity instaead
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        mRootView = findViewById(android.R.id.content);
        if (mRootView == null) {
            mRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        }

        initToolbar();

        sourceText = getIntent().getStringExtra(EXTRA_TEXT);

        sourceTextView = findViewById(R.id.source_text);
        targetEditText = findViewById(R.id.target_text);

        progress = findViewById(R.id.progress);
        contentLayout = findViewById(R.id.content);

        initBottomPanel();

        sourceTextView.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mPresenter.updateSourceText(sourceTextView.getText().toString());
                handled = true;
            }
            return handled;
        });

        sourceTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        sourceTextView.setRawInputType(InputType.TYPE_CLASS_TEXT);
        sourceTextView.setText(sourceText);

        mPresenter.takeView(this);
    }


    @Override
    public void showError(String message) {
        showProgress(false);
        InfoSnackbarUtil.showError(message, mRootView);
    }

    @Override
    public void showError(int errorMessageRes) {
        showProgress(false);
        InfoSnackbarUtil.showError(errorMessageRes, mRootView);
    }

    /**
     * Shows the progress UI and hides the activity's content
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        contentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        contentLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        progress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void updateTargetText(String targetText) {
        targetEditText.setText(targetText);
    }

    @Override
    public void initSourceLanguagesSpinner(
            List<SupportedLanguagesResponse.Language> languages,
            String sourceLanguageCode) {

        sourceLanguagesSpinner = findViewById(R.id.spinner_source_languages);

        sourceLanguagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.updateSourceLanguageCode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Stream.of(languages).map(l -> l.getName()).toList());

        sourceLanguagesSpinner.setAdapter(adapter);

        sourceLanguagesSpinner.setSelection(IntStream.range(0, languages.size())
                .filter(i -> languages.get(i).getCode().equals(sourceLanguageCode))
                .findFirst()
                .orElse(0));
    }

    @Override
    public void initTargetLanguagesSpinner(
            List<SupportedLanguagesResponse.Language> languages,
            String targetLanguageCode) {

        targetLanguagesSpinner = findViewById(R.id.spinner_target_languages);

        targetLanguagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.updateTargetLanguageCode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Stream.of(languages).map(l -> l.getName()).toList());

        targetLanguagesSpinner.setAdapter(adapter);

        targetLanguagesSpinner.setSelection(IntStream.range(0, languages.size())
                .filter(i -> languages.get(i).getCode().equals(targetLanguageCode))
                .findFirst()
                .orElse(0));
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initBottomPanel() {
        View copyBtn = findViewById(R.id.copy_btn);
        copyBtn.setOnClickListener(this);

        View shareBtn = findViewById(R.id.share_text_btn);
        shareBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copy_btn:
                copyTextToClipboard();
                break;
            case R.id.share_text_btn:
                onShareClicked();
                break;
            default:
                break;
        }
    }

    private void copyTextToClipboard() {
        String text = targetEditText.getText().toString();
        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.text_result), text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.copied),
                Toast.LENGTH_SHORT).show();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 300 milliseconds
        v.vibrate(300);
    }

    @SuppressWarnings("deprecation")
    private void onShareClicked() {
        String text = targetEditText.getText().toString();

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        Resources res = getResources();
        String linkToApp = "https://play.google.com/store/apps/details?id=" + appPackageName;
        String sharedBody =
                String.format(res.getString(R.string.share_text_message), text, linkToApp);

        Spanned styledText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            styledText = Html.fromHtml(sharedBody, Html.FROM_HTML_MODE_LEGACY);
        } else {
            styledText = Html.fromHtml(sharedBody);
        }

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, res.getString(R.string.text_result));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, styledText);
        startActivity(Intent.createChooser(sharingIntent, res.getString(R.string.send_text_result_to)));
    }

    @Override
    public String getSourceText() {
        return sourceText;
    }
}

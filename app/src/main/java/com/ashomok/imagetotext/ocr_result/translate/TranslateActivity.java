package com.ashomok.imagetotext.ocr_result.translate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.util.Pair;
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
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.ocr_result.translate.translate_task.SupportedLanguagesResponse;
import com.ashomok.imagetotext.ocr_result.translate.translate_task.TranslateHttpClient;
import com.ashomok.imagetotext.ocr_result.translate.translate_task.TranslateResponse;
import com.ashomok.imagetotext.utils.NetworkUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.ashomok.imagetotext.Settings.appPackageName;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 8/27/17.
 */
//// TODO: MAJOR 9/14/17 replace spinners with new Language Activity - see Google Translate APP for example
public class TranslateActivity extends RxAppCompatActivity implements View.OnClickListener {
    private static final String TAG = DEV_TAG + TranslateActivity.class.getSimpleName();
    private String sourceText;
    private String targetText;
    private TranslateHttpClient translateHttpClient;

    private Spinner sourceLanguagesSpinner;
    private Spinner targetLanguagesSpinner;

    private TextView sourceEditText;
    private EditText targetEditText;
    private ProgressBar progress;
    private View contentLayout;

    private String sourceLanguageCode;
    private String targetLanguageCode;
    private List<SupportedLanguagesResponse.Language> languages;
    public static final String EXTRA_TEXT = "com.ashomokdev.imagetotext.TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        Intent intent = getIntent();
        sourceText = intent.getStringExtra(EXTRA_TEXT);
        if (sourceText == null || sourceText.length() < 1) {
           Log.d(TAG, "called with empty source text");
        }

        initToolbar();

        sourceLanguagesSpinner = findViewById(R.id.spinner_source_languages);
        targetLanguagesSpinner = findViewById(R.id.spinner_target_languages);

        sourceEditText = findViewById(R.id.source_text);
        targetEditText = findViewById(R.id.target_text);

        progress = findViewById(R.id.progress);
        contentLayout = findViewById(R.id.content);

        translateHttpClient = TranslateHttpClient.getInstance();

        callInitTranslate(sourceText);

        initBottomPanel();

        sourceEditText.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sourceText = sourceEditText.getText().toString();
                callTranslate(sourceText, sourceLanguageCode, targetLanguageCode);
                handled = true;
            }
            return handled;
        });

        sourceEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        sourceEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        final int[] check1 = {0}; //for preventing duplicate translate call in init process
        final int[] check2 = {0}; //for preventing duplicate translate call in init process
        sourceLanguagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (languages != null && ++check1[0] > 1) {
                    sourceLanguageCode = languages.get(position).getCode();
                    callTranslate(sourceText, sourceLanguageCode, targetLanguageCode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        targetLanguagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (languages != null && ++check2[0] > 1) {
                    targetLanguageCode = languages.get(position).getCode();
                    callTranslate(sourceText, sourceLanguageCode, targetLanguageCode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * init translate call performs in parallel
     * 1 Thread: get supported languages
     * 2 Thread: call translate for given Text, using auto for sourcelanguage and
     * deviceLanguage for targetLanguage
     */
    private void callInitTranslate(@NonNull String mInputText) {
        if (NetworkUtils.isOnline(this)) {
            showProgress(true);
            String deviceLanguageCode = Locale.getDefault().getLanguage();

            //first -------------------------------------------
            Single<SupportedLanguagesResponse> supportedLanguagesResponceSingle =
                    translateHttpClient.getSupportedLanguages(deviceLanguageCode)
                            .doOnEvent((supportedLanguagesResponce, throwable) -> {
                                        Log.d(TAG, "getSupportedLanguages called in thread: "
                                                + Thread.currentThread().getName());
                                    }
                            ).subscribeOn(Schedulers.io());


            //second---------------------------------------------
            Single<TranslateResponse> translateResponseSingle =
                    translateHttpClient.translate(deviceLanguageCode, mInputText)
                            .doOnEvent((supportedLanguagesResponce, throwable) -> {
                                        Log.d(TAG, "translate called in thread: "
                                                + Thread.currentThread().getName());
                                    }
                            ).subscribeOn(Schedulers.io());

            //zipped first + second-----------------------------------------
            Single<Pair<SupportedLanguagesResponse, TranslateResponse>> zipped =
                    Single.zip(
                            supportedLanguagesResponceSingle,
                            translateResponseSingle,
                            (a, b) -> new Pair<>(a, b))
                            .observeOn(AndroidSchedulers.mainThread());// Will switch to Main-Thread when finished

            zipped.compose(bindToLifecycle())
                    .subscribe(
                            myData -> {
                                Log.d(TAG, "zipped returns " + myData.toString());
                                updateUI(myData);
                            },
                            throwable -> {
                                String errorMessage = throwable.getMessage();
                                Log.e(TAG, errorMessage);
                                updateUI(errorMessage);
                            });
        } else {
            updateUI(getString(R.string.network_error));
        }
    }

    private void callTranslate(
            @NonNull String mInputText,
            @NonNull String sourceLanguageCode,
            @NonNull String targetLanguageCode) {

        if (NetworkUtils.isOnline(this)) {

            Single<TranslateResponse> translateResponseSingle =
                    translateHttpClient.translate(sourceLanguageCode, targetLanguageCode, mInputText)
                            .subscribeOn(Schedulers.io());

            translateResponseSingle
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(
                            myData -> {
                                Log.d(TAG, "translate returns " + myData.toString());
                                updateUI(myData);
                            },
                            throwable -> {
                                String errorMessage = throwable.getMessage();
                                Log.e(TAG, errorMessage);
                                updateUI(errorMessage);
                            });
        } else {
            updateUI(getString(R.string.network_error));
        }
    }

    private void updateUI(TranslateResponse translateResponse) {
        if (!translateResponse.getStatus().equals(TranslateResponse.Status.OK)) {
            updateUI(getString(R.string.error_while_translating));
        } else {
            //do staff
            targetText = translateResponse.getTextResult();
            targetEditText.setText(targetText);
        }
    }

    private void updateUI(String errorMessage) {
        showProgress(false);
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void updateUI(Pair<SupportedLanguagesResponse, TranslateResponse> myData) {
        showProgress(false);
        SupportedLanguagesResponse supportedLanguagesResponse = myData.first;
        TranslateResponse translateResponse = myData.second;

        if (!supportedLanguagesResponse.getStatus().equals(SupportedLanguagesResponse.Status.OK)) {
            updateUI(getString(R.string.can_not_get_supported_languages));
        } else if (!translateResponse.getStatus().equals(TranslateResponse.Status.OK)) {
            updateUI(getString(R.string.error_while_translating));
        } else {
            //do staff
            languages = supportedLanguagesResponse.getSupportedLanguages();
            sourceLanguageCode = translateResponse.getSourceLanguageCode();
            targetLanguageCode = translateResponse.getTargetLanguageCode();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item,
                    Stream.of(languages).map(l -> l.getName()).toList());

            sourceLanguagesSpinner.setAdapter(adapter);
            targetLanguagesSpinner.setAdapter(adapter);

            sourceLanguagesSpinner.setSelection(IntStream.range(0, languages.size())
                    .filter(i -> languages.get(i).getCode().equals(sourceLanguageCode))
                    .findFirst()
                    .orElse(0));
            targetLanguagesSpinner.setSelection(IntStream.range(0, languages.size())
                    .filter(i -> languages.get(i).getCode().equals(targetLanguageCode))
                    .findFirst()
                    .orElse(0));

            targetText = translateResponse.getTextResult();
            targetEditText.setText(targetText);
            sourceEditText.setText(sourceText);
        }
    }

    /**
     * Shows the progress UI and hides the activity's content
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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


        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progress.setVisibility(show ? View.VISIBLE : View.GONE);
            contentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
                copyTextToClipboard(targetText);
                break;
            case R.id.share_text_btn:
                onShareClicked();
                break;
            default:
                break;
        }
    }

    private void copyTextToClipboard(CharSequence text) {
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
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        Resources res = getResources();
        String linkToApp = "https://play.google.com/store/apps/details?id=" + appPackageName;
        String sharedBody =
                String.format(res.getString(R.string.share_text_message), targetText, linkToApp);

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
}

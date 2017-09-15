package com.ashomok.imagetotext.ocr_result.translate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.ocr_result.tab_fragments.TextFragment;
import com.ashomok.imagetotext.ocr_result.translate.translate_task.SupportedLanguagesResponce;
import com.ashomok.imagetotext.ocr_result.translate.translate_task.TranslateClient;
import com.ashomok.imagetotext.ocr_result.translate.translate_task.TranslateRequestBean;
import com.ashomok.imagetotext.ocr_result.translate.translate_task.TranslateResponse;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 8/27/17.
 */

//// TODO: 9/13/17 catch timeout exceptions in Singles
public class TranslateActivity extends RxAppCompatActivity implements View.OnClickListener {
    private static final String TAG = DEV_TAG + TranslateActivity.class.getSimpleName();
    private String mInputLanguage;
    private String mOutputLanguage;
    private String sourceText;
    private String mOutputText = "dummu output text";
    private TranslateClient translateHttpClient;

    //// TODO: 9/14/17 replace spinners with new Language Activity - see Google Translate APP for example
    private Spinner sourceLanguagesSpinner;
    private Spinner targetLanguagesSpinner;

    private EditText sourceEditText;
    private EditText targetEditText;
    private ProgressBar progress;
    private RelativeLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        Intent intent = getIntent();
        sourceText = intent.getStringExtra(TextFragment.EXTRA_TEXT);
        if (sourceText == null || sourceText.length() < 1) {
            sourceText = "моя мама умница";
            //todo remove dummu in production
        }

        initToolbar();

        sourceLanguagesSpinner = (Spinner) findViewById(R.id.spinner_source_languages);
        targetLanguagesSpinner = (Spinner) findViewById(R.id.spinner_target_languages);

        sourceEditText = (EditText) findViewById(R.id.source_text);
        targetEditText = (EditText) findViewById(R.id.target_text);

        progress = (ProgressBar) findViewById(R.id.progress);
        contentLayout = (RelativeLayout) findViewById(R.id.content);

        translateHttpClient = TranslateClient.getInstance();
        callInitTranslate(sourceText);
    }

    /**
     * init translate call performs in parallel
     * 1 Thread: get supported languages
     * 2 Thread: call translate for given Text, using auto for sourcelanguage and
     * deviceLanguage for targetLanguage
     */
    private void callInitTranslate(@NonNull String mInputText) {
        showProgress(true);
        String deviceLanguageCode = Locale.getDefault().getLanguage();

        //first -------------------------------------------
        Single<SupportedLanguagesResponce> supportedLanguagesResponceSingle =
                translateHttpClient.getSupportedLanguages(deviceLanguageCode)
                        .doOnEvent((supportedLanguagesResponce, throwable) -> {
                                    Log.d(TAG, "getSupportedLanguages called in thread: "
                                            + Thread.currentThread().getName());
                                }
                        ).subscribeOn(Schedulers.io());


        //second---------------------------------------------
        TranslateRequestBean translateRequest = new TranslateRequestBean();
        translateRequest.setDeviceLang(deviceLanguageCode);
        translateRequest.setSourceText(mInputText);

        Single<TranslateResponse> translateResponseSingle =
                translateHttpClient.translate(translateRequest)
                        .doOnEvent((supportedLanguagesResponce, throwable) -> {
                                    Log.d(TAG, "translate called in thread: "
                                            + Thread.currentThread().getName());
                                }
                        ).subscribeOn(Schedulers.io());

        //zipped first + second-----------------------------------------
        Single<Pair<SupportedLanguagesResponce, TranslateResponse>> zipped =
                Single.zip(
                        supportedLanguagesResponceSingle,
                        translateResponseSingle,
                        (a, b) -> new Pair<>(a, b))
                        .observeOn(AndroidSchedulers.mainThread());// Will switch to Main-Thread when finished

        zipped.compose(bindToLifecycle())
                .subscribe(
                        myData -> {
                            Log.d(TAG, "zipped called with " + myData.toString());
                            updateUI(myData);
                        },
                        throwable -> {
                            String errorMessage = throwable.getMessage();
                            Log.e(TAG, errorMessage);
                            updateUI(errorMessage);
                        });
    }

    private void updateUI(String errorMessage) {
        showProgress(false);
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void updateUI(Pair<SupportedLanguagesResponce, TranslateResponse> myData) {
        showProgress(false);
        SupportedLanguagesResponce supportedLanguagesResponce = myData.first;
        TranslateResponse translateResponse = myData.second;

        if (!supportedLanguagesResponce.getStatus().equals(SupportedLanguagesResponce.Status.OK)) {
            updateUI(getString(R.string.can_not_get_supported_languages));
        } else if (!translateResponse.getStatus().equals(TranslateResponse.Status.OK)) {
            updateUI(getString(R.string.error_while_translating));
        } else {
            //do staff
            List<SupportedLanguagesResponce.Language> languages =
                    supportedLanguagesResponce.getSupportedLanguages();

            String sourceLanguageCode = translateResponse.getSourceLanguageCode();
            String targetLanguageCode = translateResponse.getTargetLanguageCode();

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

            String targetText = translateResponse.getTextResult();
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {

    }
}

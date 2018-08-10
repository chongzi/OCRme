package com.ashomok.ocrme.ocr_result.translate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.ashomok.ocrme.R;
import com.ashomok.ocrme.ocr_result.translate.translate_task.translate_task.SupportedLanguagesResponse;
import com.ashomok.ocrme.ocr_result.translate.translate_task.translate_task.TranslateHttpClient;
import com.ashomok.ocrme.ocr_result.translate.translate_task.translate_task.TranslateResponse;
import com.ashomok.ocrme.utils.NetworkUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.ashomok.ocrme.utils.FirebaseUtils.getIdToken;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;
import static dagger.internal.Preconditions.checkNotNull;

public class TranslatePresenter implements TranslateContract.Presenter {

    public static final String TAG = DEV_TAG + TranslatePresenter.class.getSimpleName();

    @NonNull
    private final TranslateHttpClient httpClient;
    @Nullable
    private TranslateContract.View view;

    private Context context;
    @Nullable
    private String userIdToken;

    private List<SupportedLanguagesResponse.Language> languages;
    private String sourceLanguageCode;
    private String targetLanguageCode;
    private String targetText;
    private String sourceText;
    private boolean isInitSourceLanguage = false;
    private boolean isInitTargetLanguage = false;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    TranslatePresenter(Context context,
                       @NonNull TranslateHttpClient httpClient) {
        this.context = context;
        this.httpClient = checkNotNull(httpClient);
    }

    @Override
    public void takeView(TranslateContract.View translateActivity) {
        view = translateActivity;

        sourceText = view.getSourceText();
        callInitTranslate(sourceText);
    }


    /**
     * init translate call performs in parallel
     * 1 Thread: get supported languages
     * 2 Thread: call translate for given Text, using auto for sourcelanguage and
     * deviceLanguage for targetLanguage
     */
    private void callInitTranslate(@NonNull String mInputText) {
        if (view != null) {
            if (NetworkUtils.isOnline(context)) {

                view.showProgress(true);

                //todo not to call for id token every time - call ones in app and use.
                getIdToken()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                optionalToken -> {
                                    //logged in
                                    if (optionalToken.isPresent()) {
                                        userIdToken = optionalToken.get();
                                    }

                                    callInitTranslate(mInputText, userIdToken);

                                }, throwable -> {
                                    String errorMessage = throwable.getMessage();
                                    Log.e(TAG, errorMessage);
                                    view.showError(errorMessage);
                                });
            } else {
                view.showError(context.getString(R.string.network_error));
            }
        }
    }

    private void callInitTranslate(@NonNull String mInputText, @Nullable String userIdToken) {

        String deviceLanguageCode = Locale.getDefault().getLanguage();
        //first -------------------------------------------
        Single<SupportedLanguagesResponse> supportedLanguagesResponceSingle =
                httpClient.getSupportedLanguages(deviceLanguageCode)
                        .doOnEvent((supportedLanguagesResponce, throwable) -> {
                                    Log.d(TAG, "getSupportedLanguages called in thread: "
                                            + Thread.currentThread().getName());
                                }
                        ).subscribeOn(Schedulers.io());


        Log.d(TAG, "callInitTranslate");
        //second---------------------------------------------
        Single<TranslateResponse> translateResponseSingle =
                httpClient.translate(deviceLanguageCode, mInputText, userIdToken)
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
                        Pair::new)
                        .observeOn(AndroidSchedulers.mainThread());// Will switch to Main-Thread when finished

        zipped.compose(((TranslateActivity) view).bindToLifecycle())
                .subscribe(
                        myData -> {
                            Log.d(TAG,
                                    "SupportedLanguagesResponse + TranslateResponse zipped returns "
                                            + myData.toString());
                            updateUI(myData);
                        },
                        throwable -> {
                            String errorMessage = throwable.getMessage();
                            Log.e(TAG, errorMessage);
                            view.showError(errorMessage);
                        });
    }


    @Override
    public void callTranslate() {

        if (NetworkUtils.isOnline(context)) {

            Log.d(TAG, "callTranslate");
            Single<TranslateResponse> translateResponseSingle =
                    httpClient.translate(
                            sourceLanguageCode, targetLanguageCode, sourceText, userIdToken)
                            .subscribeOn(Schedulers.io());

            translateResponseSingle
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(((TranslateActivity) view).bindUntilEvent(ActivityEvent.DESTROY)) //specify the lifecycle event where RxLifecycle should terminate an Observable
                    .subscribe(
                            myData -> {
                                Log.d(TAG, "translate returns " + myData.toString());
                                updateUI(myData);
                            },
                            throwable -> {
                                String errorMessage = throwable.getMessage();
                                Log.e(TAG, errorMessage);
                                view.showError(errorMessage);
                            });
        } else {
            if (view != null) {
                view.showError(context.getString(R.string.network_error));
            }
        }
    }

    //update UI after translate (no initTranslate - so we don't need to update languages)
    private void updateUI(TranslateResponse translateResponse) {
        if (view != null) {
            if (!translateResponse.getStatus().equals(TranslateResponse.Status.OK)) {
                view.showError(context.getString(R.string.error_while_translating));
            } else {
                targetText = translateResponse.getTranslateResult().getTextResult();
                view.updateTargetText(targetText);
            }
        }
    }

    //update ui after init translate
    private void updateUI(Pair<SupportedLanguagesResponse, TranslateResponse> myData) {
        if (view != null) {
            view.showProgress(false);
            SupportedLanguagesResponse supportedLanguagesResponse = myData.first;
            TranslateResponse translateResponse = myData.second;

            if (!supportedLanguagesResponse.getStatus().equals(SupportedLanguagesResponse.Status.OK)) {
                view.showError(context.getString(R.string.can_not_get_supported_languages));
            } else if (!translateResponse.getStatus().equals(TranslateResponse.Status.OK)) {
                view.showError(context.getString(R.string.error_while_translating));
            } else {
                //do staff
                languages = supportedLanguagesResponse.getSupportedLanguages();
                sourceLanguageCode = translateResponse.getTranslateResult().getSourceLanguageCode();
                targetLanguageCode = translateResponse.getTranslateResult().getTargetLanguageCode();
                targetText = translateResponse.getTranslateResult().getTextResult();

                view.initSourceLanguagesSpinner(languages, sourceLanguageCode);
                view.initTargetLanguagesSpinner(languages, targetLanguageCode);
                view.updateTargetText(targetText);
            }
        }
    }

    @Override
    public void dropView() {
        view = null;
    }

    @Override
    public void updateSourceText(String sourceText) {
        if (!this.sourceText.equals(sourceText)) {
            this.sourceText = sourceText;

            if (sourceText == null || sourceText.length() < 1) {
                Log.d(TAG, "called with empty source text");
            } else {
                callTranslate();
            }
        }
    }

    @Override
    public void updateSourceLanguageCode(int index) {

        Log.d(TAG, "updateSourceLanguageCode called with index = " + index);
        if (languages != null) {
            sourceLanguageCode = languages.get(index).getCode();

            if (isInitSourceLanguage) {
                //don't call translate while update for a first time
                callTranslate();

            }
            isInitSourceLanguage = true;
        }
    }

    @Override
    public void updateTargetLanguageCode(int index) {

        Log.d(TAG, "updateTargetLanguageCode called with index = " + index);

        if (languages != null) {
            targetLanguageCode = languages.get(index).getCode();
            if (isInitTargetLanguage) {
                //don't call translate while update for a first time
                callTranslate();
            }
            isInitTargetLanguage = true;
        }
    }
}

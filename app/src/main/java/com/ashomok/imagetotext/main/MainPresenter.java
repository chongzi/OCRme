package com.ashomok.imagetotext.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.Settings;
import com.ashomok.imagetotext.main.billing.BillingProviderCallback;
import com.ashomok.imagetotext.main.billing.BillingProviderImpl;
import com.ashomok.imagetotext.utils.NetworkUtils;
import com.ashomok.imagetotext.utils.SharedPreferencesUtil;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 2/14/18.
 */

public class MainPresenter implements MainContract.Presenter {
    public static final String TAG = DEV_TAG + MainPresenter.class.getSimpleName();

    @Inject
    Context context;

    @Inject
    SharedPreferences mSharedPreferences;

    @Inject
    BillingProviderImpl billingProvider;

    @Nullable
    private MainContract.View view;
    private Optional<List<String>> languageCodes;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    MainPresenter() {}

    @Override
    public void takeView(MainContract.View mainActivity) {
        this.view = mainActivity;
        init();
    }

    private void init() {
        if (view != null) {
            checkConnection();
            languageCodes = obtainSavedLanguagesCodes();
            updateLanguageTextView(languageCodes);

            initBilling();
        }
    }

    private void initBilling() {
        if (billingProvider != null) {
            Log.d(TAG, "billingProvider != null");
        }
    }

    @Override
    public Optional<List<String>> getLanguageCodes() {
        return languageCodes;
    }

    @Override
    public BillingProviderCallback getBillingProviderCallback() {
        return () -> {
            Log.d(TAG, "onPurchasesUpdated");
            //todo update view
        };
    }

    private boolean isOnline() {
        return NetworkUtils.isOnline(context);
    }

    private void checkConnection() {
        if (view != null) {
            if (!isOnline()) {
                view.showError(R.string.no_internet_connection);
            }
        }
    }

    private void saveLanguages() {
        if (languageCodes.isPresent()) {
            SharedPreferencesUtil.pushStringList(
                    mSharedPreferences, languageCodes.get(), context.getString(R.string.saved_language_codes));
        }
    }

    @Override
    public void dropView() {
        view = null;
    }

    private Optional<List<String>> obtainSavedLanguagesCodes() {
        return Optional.ofNullable(SharedPreferencesUtil.pullStringList(
                mSharedPreferences, context.getString(R.string.saved_language_codes)));
    }

    @Override
    public void onCheckedLanguageCodesObtained(@Nullable List<String> checkedLanguageCodes) {
        languageCodes = Optional.ofNullable(checkedLanguageCodes);
        updateLanguageTextView(languageCodes);

        saveLanguages();
    }

    private void updateLanguageTextView(Optional<List<String>> checkedLanguageCodes) {
        if (view != null) {
            String languageString;
            if (checkedLanguageCodes.isPresent() && checkedLanguageCodes.get().size() > 0) {
                languageString = generateLanguageString(checkedLanguageCodes.get());
            } else {
                languageString = context.getString(R.string.auto);
            }

            view.updateLanguageString(languageString);
        }
    }

    @NonNull
    private String generateLanguageString(List<String> checkedLanguageCodes) {
        Map<String, String> allLanguages = Settings.getOcrLanguageSupportList(context);
        List<String> checkedLanguages = Stream.of(checkedLanguageCodes)
                .filter(allLanguages::containsKey)
                .map(allLanguages::get)
                .collect(Collectors.toList());

        StringBuilder languageString = new StringBuilder();
        for (String language : checkedLanguages) {
            languageString.append(language).append(", ");
        }

        if (languageString.toString().endsWith(", ")) {
            languageString = new StringBuilder(languageString.substring(0, languageString.length() - 2));
        }

        return languageString.toString();
    }
}

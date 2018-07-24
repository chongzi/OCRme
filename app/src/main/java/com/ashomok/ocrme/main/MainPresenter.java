package com.ashomok.ocrme.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.ashomok.ocrme.OcrRequestsCounter;
import com.ashomok.ocrme.R;
import com.ashomok.ocrme.Settings;
import com.ashomok.ocrme.billing.BillingProviderCallback;
import com.ashomok.ocrme.billing.BillingProviderImpl;
import com.ashomok.ocrme.utils.NetworkUtils;
import com.ashomok.ocrme.utils.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tbruyelle.rxpermissions2.Permission;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.ashomok.ocrme.Settings.isPremium;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 2/14/18.
 */

public class MainPresenter implements MainContract.Presenter {
    public static final String TAG = DEV_TAG + MainPresenter.class.getSimpleName();

    private Context context;

    private SharedPreferences mSharedPreferences;

    private BillingProviderImpl billingProvider;

    private OcrRequestsCounter ocrRequestsCounter;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    MainPresenter(Context context, SharedPreferences mSharedPreferences,
                  BillingProviderImpl billingProvider, OcrRequestsCounter ocrRequestsCounter) {
        this.context = context;
        this.mSharedPreferences = mSharedPreferences;
        this.billingProvider = billingProvider;
        this.ocrRequestsCounter = ocrRequestsCounter;
    }

    @Nullable
    public MainContract.View view;
    private Optional<List<String>> languageCodes;

    private BillingProviderCallback billingProviderCallback = new BillingProviderCallback() {
        @Override
        public void onPurchasesUpdated() {
            Log.d(TAG, "onPurchasesUpdated()");
            boolean isPremium = billingProvider.isPremiumMonthlySubscribed()
                    || billingProvider.isPremiumYearlySubscribed();
            onPremiumStatusUpdated(isPremium);
        }

        @Override
        public void showError(int stringResId) {
            if (view != null) {
                view.showError(stringResId);
            }
        }

        @Override
        public void showInfo(String message) {
            if (view != null) {
                view.showInfo(message);
            }
        }

        @Override
        public void onSkuRowDataUpdated() { //nothing
        }
    };

    private void onPremiumStatusUpdated(boolean isPremium) {
        if (view != null) {
            view.updateView(isPremium);
        }
    }


    @Override
    public void takeView(MainContract.View mainActivity) {
        view = mainActivity;
        init();
    }

    @Override
    public void showAdsIfNeeded() {
        if (Settings.isAdsActive) {
            if (view != null) {
                view.showAds();
            }
        }
    }

    private void init() {
        if (view != null) {
            billingProvider.setCallback(billingProviderCallback);
            billingProvider.init();


            checkConnection();
            languageCodes = obtainSavedLanguagesCodes();
            updateLanguageTextView(languageCodes);
            initRequestCounter();
        }
    }

    private void initRequestCounter() {
        if (view != null) {
            int requestCount = getRequestsCount();
            view.initRequestsCounter(requestCount);
        }
    }

    @Override
    public Optional<List<String>> getLanguageCodes() {
        return languageCodes;
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
        billingProvider.destroy();
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

    @Override
    public int getRequestsCount() {
        return ocrRequestsCounter.getAvailableOcrRequests();
    }

    @Override
    public void consumeRequest() {
        if (!isPremium) {
            ocrRequestsCounter.consumeRequest();
        }
    }

    @Override
    public void onPhotoBtnClicked(Permission permission) {
        if (view != null) {
            if (permission.granted) {
                if (isRequestsAvailable()) {
                    view.startCamera();
                    consumeRequest();
                } else {
                    view.showRequestsCounterDialog(getRequestsCount());
                }
            } else if (permission.shouldShowRequestPermissionRationale) {
                view.showWarning(R.string.needs_to_save);
            } else {
                view.showWarning(R.string.this_option_is_not_be_avalible);
            }
        }
    }

    @Override
    public void onGalleryChooserClicked() {
        if (view != null) {
            if (isRequestsAvailable()) {
                view.startGalleryChooser();
                consumeRequest();
            } else {
                view.showRequestsCounterDialog(getRequestsCount());
            }
        }
    }

    @Override
    public String getUserEmail() {
        String mEmail = "";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mEmail = user.getEmail();
        }
        return mEmail;
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

    @Override
    public boolean isRequestsAvailable() {
        return Settings.isPremium || getRequestsCount() > 0;
    }
}

package com.ashomok.ocrme.ocr_result.tab_fragments.image_pdf;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ashomok.ocrme.BuildConfig;
import com.ashomok.ocrme.R;
import com.ashomok.ocrme.utils.InfoSnackbarUtil;
import com.github.barteksc.pdfviewer.PDFView;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.List;

import javax.inject.Inject;


import dagger.android.support.DaggerFragment;

import static com.ashomok.ocrme.Settings.appPackageName;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

public class ImagePdfFragment extends DaggerFragment implements ImagePdfContract.View {
    public static final String EXTRA_PDF_GS_URL = "com.ashomok.ocrme.ocr_result.tab_fragments.image_pdf.PDF_URL";
    public static final String EXTRA_PDF_MEDIA_URL = "com.ashomok.ocrme.ocr_result.tab_fragments.image_pdf.EXTRA_PDF_MEDIA_URL";
    private static final String TAG = DEV_TAG + ImagePdfFragment.class.getSimpleName();
    private String mGsUrl;
    private String mDownloadURL; //for sharing pdf option only
    private View mRootView;

    @Inject
    ImagePdfContract.Presenter mPresenter;

    @Inject
    Context context;


    private PDFView mPdfView;
    private ProgressBar progressBar;
    private String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_pdf_fragment, container, false);
        mPdfView = view.findViewById(R.id.pdfView);
        progressBar = view.findViewById(R.id.progress);

        mRootView = view.findViewById(R.id.root_view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mGsUrl = bundle.getString(EXTRA_PDF_GS_URL);
            mDownloadURL = bundle.getString(EXTRA_PDF_MEDIA_URL);
        }

        mPresenter.takeView(this);

        showProgress(true);
        mPresenter.initPdfView(mGsUrl)
                .subscribe(
                        () -> showProgress(false),
                        error -> {
                            showProgress(false);
                            showError(error);
                        });

        return view;
    }

    public void onShareBtnClicked(String mDownloadURL) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        Resources res = getResources();
        String linkToApp = "https://play.google.com/store/apps/details?id=" + appPackageName;
        String sharedBody = String.format(
                res.getString(R.string.share_pdf_message), mDownloadURL, linkToApp);

        Spanned styledText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            styledText = Html.fromHtml(sharedBody, Html.FROM_HTML_MODE_LEGACY);
        } else {
            styledText = Html.fromHtml(sharedBody);
        }

        sharingIntent.putExtra(
                android.content.Intent.EXTRA_SUBJECT, res.getString(R.string.link_to_pdf));
        sharingIntent.putExtra(
                android.content.Intent.EXTRA_TEXT, styledText);
        startActivity(
                Intent.createChooser(sharingIntent, res.getString(R.string.send_pdf_to)));
    }

    //run intent for open another pdf reader
    @Override
    public void runPDFIntent(File pdfFile) {

        if (pdfFile.exists()) {
            Uri fileUri = null;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //explanation https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en

                fileUri = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        pdfFile);
            } else {
                fileUri = Uri.fromFile(pdfFile);
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (isAnyAppHandleIntent(intent)) {
                context.startActivity(intent);
            } else {
                showError(R.string.no_app);
            }
        } else {
            showError(R.string.no_file);
        }
    }

    private boolean isAnyAppHandleIntent(Intent intent) {
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        return infos.size() > 0;
    }

    @Override
    public void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }

    /**
     * Shows the progress UI and hides the login form.
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show, View view) {
        View pdfLayout = view.findViewById(R.id.pdf_layout);
        try {
            // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
            // for very easy animations. If available, use these APIs to fade-in
            // the progress spinner.
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            pdfLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            pdfLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    pdfLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void showProgress(final boolean show) { showProgress(show, mRootView); }

    @Override
    public void initBottomPanel() {

        Activity activity = getActivity();
        if (activity != null) {
            RxPermissions rxPermissions = new RxPermissions(activity);
            rxPermissions.setLogging(true);

            View downloadBtn = mRootView.findViewById(R.id.download_btn);
            RxView.clicks(downloadBtn)
                    // Ask for permissions when button is clicked
                    .compose(rxPermissions.ensureEach(permission))
                    .subscribe(permission -> {
                        if (permission.granted) {
                            mPresenter.saveFileOnDevice();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showWarning(R.string.file_must_be_saved_downloading);
                        } else {
                            showWarning(R.string.this_option_is_not_be_avalible);
                        }
                    }, this::showError);

            View openInAnotherAppBtn = mRootView.findViewById(R.id.open_in_another_app_btn);
            RxView.clicks(openInAnotherAppBtn)
                    .compose(rxPermissions.ensureEach(permission))
                    .subscribe(permission -> {
                        if (permission.granted) {
                            mPresenter.runPdfIntent();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showWarning(R.string.file_must_be_saved_opening);
                        } else {
                            showWarning(R.string.this_option_is_not_be_avalible);
                        }
                    }, this::showError);

            View shareBtn = mRootView.findViewById(R.id.share_pdf_btn);
            RxView.clicks(shareBtn)
                    .subscribe(aVoid -> onShareBtnClicked(mDownloadURL));
        }
    }

    @Override
    public void setToPDFView(File pdfFile) { mPdfView.fromFile(pdfFile).load(); }

    @Override
    public void showError(Throwable error) { InfoSnackbarUtil.showError(error, mRootView); }

    @Override
    public void showError(int errorMessageRes) {
        InfoSnackbarUtil.showError(errorMessageRes, mRootView);
    }

    @Override
    public void showWarning(int message) {
        InfoSnackbarUtil.showWarning(message, mRootView);
    }

    @Override
    public void showInfo(int infoMessageRes) {
        InfoSnackbarUtil.showInfo(infoMessageRes, mRootView);
    }

    @Override
    public void showInfo(String message) {
        InfoSnackbarUtil.showInfo(message, mRootView);
    }
}
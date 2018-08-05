package com.ashomok.ocrme.ocr_result.tab_fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ashomok.ocrme.BuildConfig;
import com.ashomok.ocrme.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.ashomok.ocrme.Settings.appPackageName;
import static com.ashomok.ocrme.utils.FileUtils.copy;
import static com.ashomok.ocrme.utils.FileUtils.prepareDirectory;
import static com.ashomok.ocrme.utils.InfoSnackbarUtil.showError;
import static com.ashomok.ocrme.utils.InfoSnackbarUtil.showWarning;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 5/31/17.
 */

public class PdfFragment extends Fragment implements FragmentCompat.OnRequestPermissionsResultCallback {
    public static final String EXTRA_PDF_GS_URL = "com.ashomokdev.imagetotext.PDF_URL";
    public static final String EXTRA_PDF_MEDIA_URL = "com.ashomokdev.imagetotext.EXTRA_PDF_MEDIA_URL";
    private static final String TAG = DEV_TAG + PdfFragment.class.getSimpleName();
    private FirebaseAuth mAuth; //needs for pdf downloading
    private String mStoreLocation;
    private String mDownloadURL; //for sharing pdf option only
    private File mPdfFile;
    private View mRootView;

    /**
     * copy of pdf saved on External Storage
     */
    private File mPdfFileCopy;
    private PDFView mPdfView;
    private ProgressBar progressBar;
    private String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pdf_fragment, container, false);
        mPdfView = view.findViewById(R.id.pdfView);
        progressBar = view.findViewById(R.id.progress);
        mRootView = view.findViewById(R.id.root_view);

        Bundle bundle = getArguments();
        mStoreLocation = bundle.getString(EXTRA_PDF_GS_URL);
        mDownloadURL = bundle.getString(EXTRA_PDF_MEDIA_URL);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showProgress(true);
        Completable completableAuthenticate = authenticate();

        completableAuthenticate
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(initPdfView())
                .subscribe(
                        () -> showProgress(false),
                        error -> {
                            showProgress(false);
                            showError(error, mRootView);
                        });
    }


    private void setToPDFView(File pdfFile) {
        mPdfView.fromFile(pdfFile).load();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        View pdfLayout = getActivity().findViewById(R.id.pdf_layout);
        try {
            // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
            // for very easy animations. If available, use these APIs to fade-in
            // the progress spinner.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                pdfLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void initBottomPanel() {
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions.setLogging(true);

        View downloadBtn = getActivity().findViewById(R.id.download_btn);
        RxView.clicks(downloadBtn)
                // Ask for permissions when button is clicked
                .compose(rxPermissions.ensureEach(permission))
                .subscribe(permission -> {
                    if (permission.granted) {
                        saveFileAndShowMessage();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        showWarning(R.string.file_must_be_saved_downloading, mRootView);
                    } else {
                        showWarning(R.string.this_option_is_not_be_avalible, mRootView);
                    }
                }, error -> showError(error, mRootView));

        View openInAnotherAppBtn = getActivity().findViewById(R.id.open_in_another_app_btn);
        RxView.clicks(openInAnotherAppBtn)
                .compose(rxPermissions.ensureEach(permission))
                .subscribe(permission -> {
                    if (permission.granted) {
                        runPdfIntent();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        showWarning(R.string.file_must_be_saved_opening, mRootView);
                    } else {
                        showWarning(R.string.this_option_is_not_be_avalible, mRootView);
                    }
                }, error -> showError(error, mRootView));

        View shareBtn = getActivity().findViewById(R.id.share_pdf_btn);
        RxView.clicks(shareBtn)
                .subscribe(aVoid -> {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");

                    Resources res = getActivity().getResources();
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
                    getActivity().startActivity(
                            Intent.createChooser(sharingIntent, res.getString(R.string.send_pdf_to)));
                });
    }

    private void runPdfIntent() {
        File pdfFile; //file should be saved in SD card for working, otherwise exception in FileProvider
        try {
            pdfFile = copyToExternalStorage(mPdfFile);
            runPDFIntent(pdfFile);
        } catch (Exception e) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.unknown_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    //run intent for open another pdf reader
    private void runPDFIntent(File pdfFile) {
        if (pdfFile.exists()) {
            Uri fileUri = null;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //explanation https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en

                fileUri = FileProvider.getUriForFile(getActivity(),
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
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.no_app),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.no_file),
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isAnyAppHandleIntent(Intent intent) {
        PackageManager manager = getActivity().getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        return infos.size() > 0;
    }

    private File copyToExternalStorage(File inputFile) throws Exception {
        if (mPdfFileCopy != null && mPdfFileCopy.exists()) {
            //do nothing
            return mPdfFileCopy;
        } else {
            Long tsLong = System.currentTimeMillis() / 1000;
            String fileNamePrefix = tsLong.toString();
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "Pdf_files");

            if (!storageDir.exists()) {
                prepareDirectory(storageDir.getPath());
            }
            mPdfFileCopy = new File(storageDir, fileNamePrefix + ".pdf");

            copy(inputFile, mPdfFileCopy);
            Log.d(TAG, "pdf file path = " + mPdfFileCopy.getPath());
            return mPdfFileCopy;
        }
    }

    private void saveFileAndShowMessage() {
        File copyedFile;
        String textMessage;
        try {
            copyedFile = copyToExternalStorage(mPdfFile);
            textMessage = getActivity().getString(R.string.file_saved) +
                    copyedFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
            textMessage = getActivity().getString(R.string.error_while_saving);
        }

        Toast.makeText(getActivity(), textMessage, Toast.LENGTH_LONG).show();
    }


    private Completable initPdfView() {
        return Completable.create(emitter -> {
            try {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference mStorageRef = storage.getReferenceFromUrl(mStoreLocation);
                mPdfFile = File.createTempFile("temp", ".pdf");
                mStorageRef.getFile(mPdfFile).addOnSuccessListener(
                        taskSnapshot -> {
                            if (mPdfFile.exists()) {
                                setToPDFView(mPdfFile);
                                initBottomPanel();
                                emitter.onComplete();
                            } else {
                                emitter.onError(new Throwable(
                                        "Unable to init pdf view. Pdf file not exist."));
                            }

                        }).addOnFailureListener(emitter::onError);
            } catch (Exception e) {
                emitter.onError(e);
                e.printStackTrace();
            }
        });
    }

    private Completable authenticate() {
        return Completable.create(emitter -> {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                emitter.onComplete();
            } else {
                mAuth.signInAnonymously()
                        .addOnSuccessListener(getActivity(), authResult -> emitter.onComplete())
                        .addOnFailureListener(getActivity(), emitter::onError);
            }
        });
    }

}

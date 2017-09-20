package com.ashomok.imagetotext.ocr_result.tab_fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
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

import com.ashomok.imagetotext.BuildConfig;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.utils.RequestPermissionTool;
import com.ashomok.imagetotext.utils.RequestPermissionToolImpl;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.ashomok.imagetotext.Settings.appPackageName;
import static com.ashomok.imagetotext.utils.FileUtils.copy;
import static com.ashomok.imagetotext.utils.FileUtils.prepareDirectory;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 5/31/17.
 */

//todo request permissions using rx
public class PdfFragment extends TabFragment
        implements View.OnClickListener, FragmentCompat.OnRequestPermissionsResultCallback {
    public static final String EXTRA_PDF_URL = "com.ashomokdev.imagetotext.PDF_URL";
    private static final String TAG = DEV_TAG + PdfFragment.class.getSimpleName();
    private static final int OPEN_IN_ANOTHER_APP_REQUEST_CODE = 0;
    private static final int DOWNLOAD_REQUEST_CODE = 1;

    private String mStoreLocation = "gs://imagetotext-149919.appspot.com/ru.pdf";
    private String mDownloadURL =
            "https://firebasestorage.googleapis.com/v0/b/imagetotext-149919.appspot.com/o/ru.pdf?alt=media&token=74581dc3-4460-476a-b478-c7dc7a17a573v";

    private File mPdfFile;

    /**
     * copy of pdf saved on External Storage
     */
    private File mPdfFileCopy;
    private PDFView mPdfView;
    private ProgressBar progressBar;
    private RequestPermissionTool requestTool;
    private String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pdf_fragment, container, false);
        mPdfView = (PDFView) view.findViewById(R.id.pdfView);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        requestTool = new RequestPermissionToolImpl();
        return view;
    }

    @Override
    protected void doStaff() {
        showProgress(true);

        initBottomPanel();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference mStorageRef = storage.getReferenceFromUrl(mStoreLocation);
        try {
            downloadToLocalFile(mStorageRef);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void downloadToLocalFile(StorageReference storageRef) throws IOException {
        mPdfFile = File.createTempFile("temp", ".pdf");

        storageRef.getFile(mPdfFile).addOnSuccessListener(
                new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        Log.d(TAG, "Local temp file has been created");
                        showProgress(false);
                        if (mPdfFile.exists()) {
                            Log.d(TAG, "file exists");
                            setToPDFView(mPdfFile);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                showProgress(false);
                Log.e(TAG, "error occurs in getting file from Storage Ref.");
            }
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
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mPdfView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPdfView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPdfView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mPdfView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void initBottomPanel() {
        View copyBtn = getActivity().findViewById(R.id.download_btn);
        copyBtn.setOnClickListener(this);

        View shareBtn = getActivity().findViewById(R.id.share_pdf_btn);
        shareBtn.setOnClickListener(this);

        View badResult = getActivity().findViewById(R.id.open_in_another_app_btn);
        badResult.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.download_btn:
                onDownloadClicked();
                break;
            case R.id.share_pdf_btn:
                onShareClicked();
                break;
            case R.id.open_in_another_app_btn:
                onOpenInAnotherAppClicked();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean grantedAllPermissions = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                grantedAllPermissions = false;
            }
        }

        if (grantResults.length != permissions.length || (!grantedAllPermissions)) {

            requestTool.onPermissionDenied();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == OPEN_IN_ANOTHER_APP_REQUEST_CODE) {
                runPdfIntent();
            } else if (requestCode == DOWNLOAD_REQUEST_CODE) {
                saveFileAndShowMessage();
            }
        }
    }

    private void onDownloadClicked() {
        //permission granted
        if (requestTool.isPermissionGranted(getActivity(), permission)) {
            saveFileAndShowMessage();
        } else {
            //request permission and then, on onRequestPermissionsResult do the stuff
            requestTool.requestPermission(this, permission, DOWNLOAD_REQUEST_CODE);
        }
    }

    private void onOpenInAnotherAppClicked() {
        //check if permission granted
        if (requestTool.isPermissionGranted(getActivity(), permission)) {
            runPdfIntent();
        } else {
            //request permission and then, on onRequestPermissionsResult do the stuff
            requestTool.requestPermission(this, permission, OPEN_IN_ANOTHER_APP_REQUEST_CODE);
        }
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
        if (infos.size() > 0) {
            //Then there is an Application(s) can handle your intent
            return true;
        } else {
            //No Application can handle your intent
            return false;
        }
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

    @SuppressWarnings("deprecation")
    private void onShareClicked() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        Resources res = getActivity().getResources();
        String linkToApp = "https://play.google.com/store/apps/details?id=" + appPackageName;
        String sharedBody =
                String.format(res.getString(R.string.share_pdf_message), mDownloadURL, linkToApp);

        Spanned styledText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            styledText = Html.fromHtml(sharedBody, Html.FROM_HTML_MODE_LEGACY);
        } else {
            styledText = Html.fromHtml(sharedBody);
        }

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, res.getString(R.string.link_to_pdf));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, styledText);
        getActivity().startActivity(Intent.createChooser(sharingIntent, res.getString(R.string.send_pdf_to)));
    }
}

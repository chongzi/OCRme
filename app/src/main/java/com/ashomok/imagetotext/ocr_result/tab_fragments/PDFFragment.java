package com.ashomok.imagetotext.ocr_result.tab_fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.ashomok.imagetotext.BuildConfig;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.utils.RequestPermissionsTool;
import com.ashomok.imagetotext.utils.RequestPermissionsToolImpl;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import static com.ashomok.imagetotext.utils.FileUtils.copy;
import static com.ashomok.imagetotext.utils.FileUtils.prepareDirectory;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 5/31/17.
 */

public class PDFFragment extends TabFragment implements View.OnClickListener, FragmentCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = DEV_TAG + PDFFragment.class.getSimpleName();
    private String mStoreLocation = "gs://imagetotext-149919.appspot.com/ru.pdf";
    private String mDownloadURL =
            "https://firebasestorage.googleapis.com/v0/b/imagetotext-149919.appspot.com/o/ru.pdf?alt=media&token=74581dc3-4460-476a-b478-c7dc7a17a573v";

    private File localPdfFile;
    private PDFView mPdfView;
    private ProgressBar progressBar;
    private RequestPermissionsTool requestTool;
    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pdf_fragment, container, false);
        mPdfView = (PDFView) view.findViewById(R.id.pdfView);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        requestTool = new RequestPermissionsToolImpl();
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
        localPdfFile = File.createTempFile("temp", ".pdf");

        storageRef.getFile(localPdfFile).addOnSuccessListener(
                new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        Log.d(TAG, "Local temp file has been created");
                        showProgress(false);
                        if (localPdfFile.exists()) {
                            Log.d(TAG, "file exists");
                            setToPDFView(localPdfFile);
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

        View shareBtn = getActivity().findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(this);

        View badResult = getActivity().findViewById(R.id.open_in_another_app_btn);
        badResult.setOnClickListener(this);
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
           //permissions granted
            //// TODO: 8/25/17  
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.download_btn:
                onDownloadClicked();
                break;
            case R.id.share_btn:
                onShareClicked();
                break;
            case R.id.open_in_another_app_btn:
                onOpenInAnotherAppClicked();
                break;
            default:
                break;
        }
    }

    private void onOpenInAnotherAppClicked() {
        if (localPdfFile.exists()) {
            try {
                Uri fileUri = null;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //explanation https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en

                    fileUri = FileProvider.getUriForFile(getActivity(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            moveToExternalStorage(localPdfFile));
                } else {
                    fileUri = Uri.fromFile(localPdfFile);
                }

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
                //// TODO: 8/25/17
            }
        } else {
            //// TODO: 8/25/17
        }
    }

    private File moveToExternalStorage(File cachedFile) throws IOException {
        requestPermissions();

        Long tsLong = System.currentTimeMillis() / 1000;
        String timeStamp = tsLong.toString();
        String fileNamePrefix = timeStamp;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");

        File savedFile = null;
        try {
            if (!storageDir.exists()) {
                //// TODO: 8/25/17 ask permissions here
                prepareDirectory(storageDir.getPath());
            }
            savedFile = new File(storageDir, fileNamePrefix + ".pdf");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        copy(cachedFile, savedFile);

        return savedFile;
    }

    private void requestPermissions() {
        requestTool.requestPermissions(this, permissions);
    }


    private void onDownloadClicked() {
        //// TODO: 8/25/17

//        if (savedFile != null) {
//            img_path = savedFile.getAbsolutePath();
//            Log.i(TAG, "img_path = " + img_path);
//        } else {
//            Log.e(TAG, "image == null");
//        }
    }

    //todo add link to the app on play market
    private void onShareClicked() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mDownloadURL);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent,
                getActivity().getResources().getText(R.string.send_to)));
    }

//    @SuppressWarnings("deprecation")
//    public void share()
//    {
//        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//        sharingIntent.setType("text/plain");
//
//        Resources res = context.getResources();
//        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
//        String sharedBody = String.format(res.getString(R.string.share_message), link);
//
//        Spanned styledText;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            styledText = Html.fromHtml(sharedBody,Html.FROM_HTML_MODE_LEGACY);
//        } else {
//            styledText = Html.fromHtml(sharedBody);
//        }
//
//        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, res.getString(R.string.i_want_to_recommend));
//        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, styledText);
//        context.startActivity(Intent.createChooser(sharingIntent, res.getString(R.string.share_via)));
//    }
}

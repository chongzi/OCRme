package com.ashomok.imagetotext.ocr_result.tab_fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashomok.imagetotext.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 5/31/17.
 */

public class PDFFragment extends TabFragment  {
    private static final String TAG = DEV_TAG + PDFFragment.class.getSimpleName();
    private String pdfLink = "gs://imagetotext-149919.appspot.com/ru.pdf";
    private File localPdfFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pdf_fragment, container, false);
    }

    @Override
    protected void doStaff() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference mStorageRef = storage.getReferenceFromUrl(pdfLink);
        try {
            downloadToLocalFile(mStorageRef);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void downloadToLocalFile(StorageReference storageRef) throws IOException {
        localPdfFile = File.createTempFile("temp", "pdf");

        storageRef.getFile(localPdfFile).addOnSuccessListener(
                new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Local temp file has been created");
                        if (localPdfFile.exists()) {
                            Log.d(TAG, "file exists");
                            setToPDFView(localPdfFile);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "error occurs in getting file from Storage Ref.");
            }
        });
    }

    private void setToPDFView(File pdfFile) {
        PDFView pdfView = (PDFView) getActivity().findViewById(R.id.pdfView);
        pdfView.fromFile(pdfFile).load();
    }
}

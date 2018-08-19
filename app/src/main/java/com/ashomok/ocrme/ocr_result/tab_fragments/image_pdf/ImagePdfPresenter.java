package com.ashomok.ocrme.ocr_result.tab_fragments.image_pdf;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ashomok.ocrme.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Completable;

import static com.ashomok.ocrme.utils.FileUtils.copy;
import static com.ashomok.ocrme.utils.FileUtils.prepareDirectory;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

public class ImagePdfPresenter implements ImagePdfContract.Presenter {
    public static final String TAG = DEV_TAG + ImagePdfPresenter.class.getSimpleName();
    private final Context context;

    @Nullable
    private ImagePdfContract.View view;
    private File mPdfFile;
    private File mPdfFileCopy; //copy of pdf saved on External Storage

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    ImagePdfPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void takeView(ImagePdfContract.View view) {
        this.view = view;
    }


    public Completable initPdfView(String mGsUrl) {
        return Completable.create(emitter -> {
            try {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference mStorageRef = storage.getReferenceFromUrl(mGsUrl);
                mPdfFile = File.createTempFile("temp", ".pdf");
                mStorageRef.getFile(mPdfFile).addOnSuccessListener(
                        taskSnapshot -> {
                            if (mPdfFile.exists()) {
                                if (view != null) {
                                    view.setToPDFView(mPdfFile);
                                    view.initBottomPanel();
                                }
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


    @Override
    public void runPdfIntent() {
        File pdfFile; //file should be saved in SD card for working, otherwise exception in FileProvider
        try {
            pdfFile = copyToExternalStorage(mPdfFile);
            view.runPDFIntent(pdfFile);
        } catch (Exception e) {
            if (view != null) {
                view.showError(R.string.unknown_error);
            }
        }
    }

    @Override
    public void saveFileOnDevice() {
        if (view != null) {
            File copiedFile;
            String textMessage;
            try {
                copiedFile = copyToExternalStorage(mPdfFile);
                textMessage = context.getString(R.string.file_saved) +
                        copiedFile.getPath();
                view.showInfo(textMessage);
            } catch (Exception e) {
                e.printStackTrace();
                view.showError(R.string.error_while_saving);
            }
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

    @Override
    public void dropView() {
        view = null;
    }
}
package com.ashomok.ocrme.ocr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.annimon.stream.Optional;
import com.ashomok.ocrme.BuildConfig;
import com.ashomok.ocrme.R;
import com.ashomok.ocrme.ocr.ocr_task.OcrHttpClient;
import com.ashomok.ocrme.ocr.ocr_task.OcrResponse;
import com.ashomok.ocrme.ocr_result.OcrResultActivity;
import com.ashomok.ocrme.utils.GlideApp;
import com.ashomok.ocrme.utils.NetworkUtils;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jakewharton.rxbinding2.view.RxView;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.ashomok.ocrme.ocr_result.OcrResultActivity.EXTRA_ERROR_MESSAGE;
import static com.ashomok.ocrme.ocr_result.OcrResultActivity.EXTRA_OCR_RESPONSE;
import static com.ashomok.ocrme.utils.FileUtils.scaleBitmapDown;
import static com.ashomok.ocrme.utils.FileUtils.toBytes;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

//import static com.ashomok.ocrme.Settings.firebaseFolderURL;


/**
 * Created by Iuliia on 13.12.2015.
 */
//todo mvp with dagger needs
public class OcrActivity extends RxAppCompatActivity {
    public static final int RESULT_CANCELED_BY_USER = 123;
    public static final String EXTRA_LANGUAGES = "com.ashomokdev.imagetotext.ocr.LANGUAGES";
    public static final String EXTRA_IMAGE_URI = "com.ashomokdev.imagetotext.ocr.IMAGE_URI"; //image stored on device
    public static final String EXTRA_IMAGE_URL = "com.ashomokdev.imagetotext.ocr.IMAGE_URL"; //image stored on Firebase storage
    public static final String TAG = DEV_TAG + OcrActivity.class.getSimpleName();

    @Nullable
    private Uri imageUri; //example content://com.ashomok.imagetotext.provider/my_images/DCIM/Camera/cropped.jpg Image stored on device - will be uploaded to firebase storage

    @Nullable
    private String imageUrl; //Url of image, stored in firebase storage example gs://imagetotext-149919.appspot.com/ocr_request_images/aeffe41d-7acc-44a3-883b-677bbab02a12cropped.jpg

    private StorageReference mImageRef;
    private ArrayList<String> sourceLanguageCodes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ocr_animation_layout);

        imageUri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);
        imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        if ((imageUri == null && imageUrl == null) ||
                (imageUri != null && imageUrl != null)) {
            Log.e(TAG, "You need provide ether imageUri or imageUrl");
        }

        sourceLanguageCodes = getIntent().getStringArrayListExtra(EXTRA_LANGUAGES);

        initCancelBtn();

        initImage().subscribe(() -> {
            if (!BuildConfig.DEBUG) {
                initAnimatedScanBand();
            }
        });

        OcrHttpClient httpClient = OcrHttpClient.getInstance();
        callOcr(httpClient);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageUri != null) {
            try {
                getContentResolver().delete(imageUri, null, null);
            }
            catch (Exception e){
                Log.e(TAG, "error while deleting");
            }
        }
    }

    public void callOcr(OcrHttpClient httpClient) {
        if (isOnline()) {

            //get user IdToken and upload photo to firebase storage in parallel:
            Single<Pair<Optional<String>, String>> zipped =
                    Single.zip(
                            getIdToken(),
                            uploadPhoto(),
                            Pair::new);

            //call ocr on the result
            Single<OcrResponse> ocrSingle = zipped
                    .subscribeOn(Schedulers.io())
                    .compose(bindToLifecycle())
                    .flatMap(pair ->
                            httpClient.ocr(
                                    pair.second,
                                    Optional.ofNullable(sourceLanguageCodes),
                                    pair.first)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .compose(bindToLifecycle())
                    );

            ocrSingle.subscribe(
                    myData -> {
                        Log.d(TAG, "ocr returns " + myData.toString());
                        startOcrResultActivity(myData);
                        finish();
                    },
                    throwable -> {
                        throwable.printStackTrace();
                        String errorMessage = throwable.getMessage();
                        if (errorMessage != null && errorMessage.length() > 0) {
                            startOcrResultActivity(errorMessage);
                        }
                        finish();
                    });
        } else {
            startOcrResultActivity(getString(R.string.network_error));
        }

    }

    boolean isOnline() {
        return NetworkUtils.isOnline(this);
    }

    private void startOcrResultActivity(OcrResponse data) {
        Intent intent = new Intent(this, OcrResultActivity.class);
        intent.putExtra(EXTRA_OCR_RESPONSE, data);
        startActivity(intent);
    }

    private void startOcrResultActivity(String errorMessage) {
        Log.e(TAG, "ERROR: " + errorMessage);
        Intent intent = new Intent(this, OcrResultActivity.class);
        intent.putExtra(EXTRA_ERROR_MESSAGE, errorMessage);
        startActivity(intent);
    }

    public void initAnimatedScanBand() {
        ImageView scan_band = findViewById(R.id.scan_band);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        TranslateAnimation animation = new TranslateAnimation(0.0f, width,
                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(5000);  // animation duration
        animation.setRepeatCount(Animation.INFINITE);  // animation repeat count
        animation.setRepeatMode(2);   // repeat animation (left to right, right to left )

        scan_band.startAnimation(animation);  // start animation
    }

    private void initCancelBtn() {
        Button cancel = findViewById(R.id.cancel_btn);
        RxView.clicks(cancel)
                .subscribe(aVoid -> {
                    Log.d(TAG, "OCR canceled by user");
                    setResult(RESULT_CANCELED_BY_USER);
                    finish();
                });
    }

    private Completable initImage() {
        return Completable.create(emitter -> {
            ImageView imageView = findViewById(R.id.image);

            if (imageUrl != null) { //uploaded
                //called only from TextFragment when Language Changed and ocr re-run.
                //init image for uploaded source - url
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference gsReference = storage.getReferenceFromUrl(imageUrl);
                Log.d(TAG, "image Url = " + imageUrl);

                GlideApp.with(this)
                        .load(gsReference)
                        .error(R.drawable.ic_broken_image)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                emitter.onError(e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                emitter.onComplete();
                                return false;
                            }
                        })
                        .fitCenter()
                        .into(imageView);
            } else {
                //init image from device
                Log.d(TAG, "image Uri = " + imageUri);
                GlideApp.with(this)
                        .load(imageUri)
                        .error(R.drawable.ic_broken_image)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                emitter.onError(e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                emitter.onComplete();
                                return false;
                            }
                        })
                        .fitCenter()
                        .into(imageView);
            }
        });
    }

    /**
     * async upload photo to firebase storage if not yet uploaded
     */
    public Single<String> uploadPhoto() {
        if (imageUrl != null) {
            return Single.just(imageUrl);
        } else {

            return Single.create(emitter -> {
                String uuid = UUID.randomUUID().toString();

                //generate image ref
                String lastPathSegment = imageUri.getLastPathSegment();
                mImageRef = FirebaseStorage.getInstance().getReference().child(
                        "ocr_request_images/" + uuid + lastPathSegment);


                //scale bitmap if needed and upload
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap.CompressFormat compressFormat = getCompressFormat(lastPathSegment);
                if (compressFormat == null){
                    emitter.onError(new Throwable("Unknown image extension"));
                }else {

                    byte[] byteArray = toBytes(BitmapFactory.decodeStream(imageStream), compressFormat);
                    mImageRef
                            .putBytes(byteArray)
                            .addOnSuccessListener(taskSnapshot -> {
                                String path = taskSnapshot.getMetadata().getReference().getPath();
                                Log.d(TAG, "uploadPhoto:onSuccess:" + path);
                                String gcsImageUrl = BuildConfig.FIREBASE_FOLDER_URL + path;
                                emitter.onSuccess(gcsImageUrl);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "uploadPhoto:onError", e);
                                emitter.onError(e);
                            });
                }
            });
        }
    }

    @Nullable
    private Bitmap.CompressFormat getCompressFormat(String lastPathSegment) {
        Bitmap.CompressFormat compressFormat = null;
        if (lastPathSegment.toLowerCase().contains("jpg") ||
                lastPathSegment.toLowerCase().contains("jpeg")) {
            compressFormat = Bitmap.CompressFormat.JPEG;
        } else if (lastPathSegment.toLowerCase().contains("png")) {
            compressFormat = Bitmap.CompressFormat.PNG;
        } else {
            Log.e(TAG, "Unknown image extension");
        }
        return compressFormat;
    }

    /**
     * async get idToken, docs: https://firebase.google.com/docs/auth/admin/verify-id-tokens
     */
    public Single<Optional<String>> getIdToken() {
        return Single.create(emitter -> {
            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            if (mUser != null) {
                mUser.getIdToken(false)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                emitter.onSuccess(Optional.ofNullable(idToken));
                            } else {
                                emitter.onSuccess(Optional.empty());
                            }
                        });
            } else {
                emitter.onSuccess(Optional.empty());
            }
        });
    }
}

package com.ashomok.imagetotext.crop_image;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ashomok.imagetotext.BuildConfig;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.ocr.OcrActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import static com.ashomok.imagetotext.utils.FileUtils.prepareDirectory;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 11/28/17.
 */

//todo require permisions
public class CropImageActivity extends AppCompatActivity
        implements CropImageView.OnCropImageCompleteListener {

    private static final String TAG = DEV_TAG + CropImageActivity.class.getSimpleName();
    public static final String EXTRA_IMAGE_URI = "com.ashomokdev.imagetotext.crop_image.IMAGE_URI";
    private CropImageView mCropImageView;
    private Uri imageUri;
    private FloatingActionButton cropBtn;
    private CropImageView.CropResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        initToolbar();

        mCropImageView = findViewById(R.id.cropImageView);
        mCropImageView.setOnCropImageCompleteListener(this);

        Uri imageUriSave;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //explanation https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en

            imageUriSave = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    createImageFile());
        } else {
            imageUriSave = Uri.fromFile(createImageFile());
        }

        imageUri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);
        mCropImageView.setImageUriAsync(imageUri);
        cropBtn = findViewById(R.id.crop_btn);
        cropBtn.setOnClickListener(view -> mCropImageView.saveCroppedImageAsync(imageUriSave));
    }

    private File createImageFile() {
        // Create an image file name
        String imageFileName = "cropped";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");

        File image = null;
        try {
            if (!storageDir.exists()) {
                prepareDirectory(storageDir.getPath());
            }

            image = new File(storageDir, imageFileName + ".jpg");


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crop_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      if (item.getItemId() == R.id.main_action_rotate) {
            mCropImageView.rotateImage(90);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            //back btn pressed
            //save data if you need here
            finish();
        });
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        handleCropResult(result);
    }

    //todo finish activity here
    private void handleCropResult(CropImageView.CropResult result) {
        this.result = result;
        if (result.getError() == null) {
            Intent intent = new Intent(this, OcrActivity.class);
            intent.putExtra("SAMPLE_SIZE", result.getSampleSize());
            if (result.getUri() != null) {
                intent.putExtra(OcrActivity.EXTRA_IMAGE_URI, result.getUri());
            } else {
                //todo
//                CropResultActivity.mImage =
//                        mCropImageView.getCropShape() == CropImageView.CropShape.OVAL
//                                ? CropImage.toOvalBitmap(result.getBitmap())
//                                : result.getBitmap();
            }
            startActivity(intent);
        } else {
            Log.e("AIC", "Failed to crop image", result.getError());
//            Toast.makeText(
//                    getActivity(),
//                    "Image crop failed: " + result.getError().getMessage(),
//                    Toast.LENGTH_LONG)
//                    .show();
        }
    }
}

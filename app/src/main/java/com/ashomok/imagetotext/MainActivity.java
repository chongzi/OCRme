package com.ashomok.imagetotext;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashomok.imagetotext.language.Language;
import com.ashomok.imagetotext.language.LanguageActivity;
import com.ashomok.imagetotext.language.LanguageList;
import com.ashomok.imagetotext.menu.ItemClickListener;
import com.ashomok.imagetotext.menu.Menu;
import com.ashomok.imagetotext.menu.Row;
import com.ashomok.imagetotext.menu.RowsAdapter;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LANGUAGE_ACTIVITY_REQUEST_CODE = 1;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private static final int CaptureImage_REQUEST_CODE = 1;
    //    private static final int OCRAnimationActivity_REQUEST_CODE = 2;
    public static final int CAMERA_PERMISSIONS_REQUEST = 3;
    public static final String LANGUAGE_EXTRA = "language";
    private String img_path;
    private LanguageList languageList;
//    private RecognizeImageAsyncTask recognizeImageAsyncTask;

    private TextView languageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            //screen rotated
            languageList = (LanguageList) savedInstanceState.getSerializable(LANGUAGE_EXTRA);
        }

        initLeftMenu();

        initImageSourceBtns();

        languageTextView = (TextView) findViewById(R.id.language);
        languageTextView.setPaintFlags(languageTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        languageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LanguageActivity.class);

                startActivityForResult(intent, LANGUAGE_ACTIVITY_REQUEST_CODE);

            }
        });
        updateLanguageTextView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LANGUAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            updateLanguageTextView();
        }
    }

    private void updateLanguageTextView() {
        String languageString = generateLanguageString();
        updateTextView(languageTextView, languageString);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void updateTextView(TextView textView, String newValue) {
        textView.setText(newValue);
    }

    @NonNull
    private String generateLanguageString() {
        String languageString = "";

        LinkedHashSet<Language> checkedLanguages = LanguageList.getInstance().getChecked();
        for (Language l : checkedLanguages) {
            languageString += l.getName() + ", ";
        }

        if (languageString.endsWith(", ")) {
            languageString = languageString.substring(0, languageString.length() - 2);
        }

        return languageString;
    }

    private void initImageSourceBtns() {
        final ImageButton photoBtn = (ImageButton) findViewById(R.id.photo_btn);
        final ImageButton galleryBtn = (ImageButton) findViewById(R.id.gallery_btn);
        equalizeSides(photoBtn);
        equalizeSides(galleryBtn);

        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBuildInCameraActivity();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //// TODO: 12/16/16  
            }
        });
    }

    private void equalizeSides(final View v) {
        v.post(new Runnable() {

            @Override
            public void run() {
                LinearLayout.LayoutParams mParams;
                mParams = (LinearLayout.LayoutParams) v.getLayoutParams();
                mParams.height = v.getWidth();
                v.setLayoutParams(mParams);
                v.postInvalidate();
            }
        });
    }

    private void initLeftMenu() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            toggle = new ActionBarDrawerToggle(
                    this,
                    mDrawerLayout,
                    toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close) {
            };

            toggle.setDrawerIndicatorEnabled(true);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
            }

            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(toggle);

            mDrawerList = (ListView) findViewById(R.id.lv_navigation_drawer);

            List<Row> menuItems = Menu.getRows();

            mDrawerList.setAdapter(new RowsAdapter(this, menuItems));

            mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed()" + getFragmentManager().getBackStackEntryCount());
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {
            ExitDialogFragment exitDialogFragment = ExitDialogFragment.newInstance(R.string.exit_dialog_title);

            exitDialogFragment.show(getFragmentManager(), "dialog");
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        private ItemClickListener itemClickListener;


        DrawerItemClickListener(Context context) {
            itemClickListener = new ItemClickListener(context);
        }

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            itemClickListener.onRowClicked(position);

            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.menu);
            mDrawerLayout.closeDrawer(layout);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(
                requestCode,
                CAMERA_PERMISSIONS_REQUEST,
                grantResults)) {
            startCamera();
        }
    }

    /**
     * to get high resolution image from camera
     */
    private void startBuildInCameraActivity() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startCamera();
        }
    }


    void startCamera() {
        try {
            dispatchTakePictureIntent();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    private File createImageFile() {
        // Create an image file name
        String imageFileName = "ocr";
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

        // Save a file: path for use with ACTION_VIEW intents
        if (image != null) {
            img_path = image.getAbsolutePath();
        }
        return image;
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {
                Log.e(TAG, "Error occurred while creating the File ");
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri outputFileUri;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //explanation https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en

                    outputFileUri = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            createImageFile());
                } else {
                    outputFileUri = Uri.fromFile(createImageFile());
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(takePictureIntent, CaptureImage_REQUEST_CODE);
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.camera_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    public static void prepareDirectory(String path) throws Exception {

        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "ERROR: Creation of directory " + path
                        + " failed");
                throw new Exception(
                        "Could not create folder" + path);
            }
        } else {
            Log.d(TAG, "Created directory " + path);
        }
    }
}

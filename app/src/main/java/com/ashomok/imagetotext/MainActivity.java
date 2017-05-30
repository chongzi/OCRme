package com.ashomok.imagetotext;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashomok.imagetotext.language.LanguageActivity;
import com.ashomok.imagetotext.language.LanguageList;
import com.ashomok.imagetotext.ocr_task.OCRAnimationActivity;
import com.ashomok.imagetotext.ocr_task.RecognizeImageAsyncTask;
import com.ashomok.imagetotext.ocr_task.RecognizeImageRESTClient;
import com.ashomok.imagetotext.utils.FileUtils;
import com.ashomok.imagetotext.utils.NetworkUtils;
import com.ashomok.imagetotext.utils.PermissionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LANGUAGE_ACTIVITY_REQUEST_CODE = 1;
    private static final int CaptureImage_REQUEST_CODE = 2;
    private static final int OCRAnimationActivity_REQUEST_CODE = 3;
    private static final int CAMERA_PERMISSIONS_REQUEST = 4;
    private static final int GALLERY_IMAGE_REQUEST = 5;
    private DrawerLayout mDrawerLayout;
    private View mErrorView;
    private TextView mErrorMessage;

    private Uri imageUri;
    public static final String CHECKED_LANGUAGES = "checked_languages";

    private RecognizeImageAsyncTask recognizeImageAsyncTask;
    private TextView languageTextView;

    private final BroadcastReceiver mConnectivityChangeReceiver = new BroadcastReceiver() {
        private boolean oldOnline = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isOnline = NetworkUtils.isOnline(context);
            if (isOnline != oldOnline) {
                oldOnline = isOnline;
                checkForUserVisibleErrors(null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();

        setUpNavigationDrawer();

        initImageSourceBtns();

        languageTextView = (TextView) findViewById(R.id.language);
        languageTextView.setPaintFlags(languageTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        languageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LanguageActivity.class);

                intent.putExtra(LanguageActivity.CHECKED_LANGUAGES, getCheckedLanguages());
                startActivityForResult(intent, LANGUAGE_ACTIVITY_REQUEST_CODE);

            }
        });

        updateLanguageTextView(getCheckedLanguages());

        mErrorView = findViewById(R.id.ocr_error);
        mErrorMessage = (TextView) mErrorView.findViewById(R.id.error_message);
        checkForUserVisibleErrors(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Registers BroadcastReceiver to track network connection changes.
        registerReceiver(mConnectivityChangeReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mConnectivityChangeReceiver);
    }

    @NonNull
    private ArrayList<String> getCheckedLanguages() {
        Set<String> checkedLanguageNames = obtainSavedLanguages();
        ArrayList<String> checkedLanguages = new ArrayList<>();
        checkedLanguages.addAll(checkedLanguageNames);
        return checkedLanguages;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LANGUAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            Bundle bundle = data.getExtras();
            ArrayList<String> checkedLanguages = bundle.getStringArrayList(LanguageActivity.CHECKED_LANGUAGES);
            updateLanguageTextView(checkedLanguages);

            saveLanguages(new LinkedHashSet<>(checkedLanguages));
        }

        //making photo
        else if (requestCode == CaptureImage_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            startOCRtask(imageUri);
        }

        //photo obtained from gallery
        else if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                startOCRtask(uri);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        //ocr canceled
        else if (requestCode == OCRAnimationActivity_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            recognizeImageAsyncTask.cancel(true);
        }
    }

    private void startOCRtask(Uri uri) {

        runOCRAnimation(uri);

        //start ocr
        if (NetworkUtils.isOnline(this)) {

            ArrayList<String> languages = obtainLanguageShortcuts();
            String path = getImagePath(uri);
            if (path != null) {
                recognizeImageAsyncTask = new RecognizeImageRESTClient(path, languages);

                RecognizeImageAsyncTask.OnTaskCompletedListener onTaskCompletedListener = new RecognizeImageAsyncTask.OnTaskCompletedListener() {
                    @Override
                    public void onTaskCompleted(String result) {
                        finishActivity(OCRAnimationActivity_REQUEST_CODE);

                        //// TODO: 12/22/16
                        //open new activity and show result
                    }

                    @Override
                    public void onError(String message) {
                        finishActivity(OCRAnimationActivity_REQUEST_CODE);
                        checkForUserVisibleErrors(message);
                    }
                };
                recognizeImageAsyncTask.setOnTaskCompletedListener(onTaskCompletedListener);
                recognizeImageAsyncTask.execute();
            }
        }
    }

    @Nullable
    private String getImagePath(Uri uri) {
        String path = null;
        try {
            path = FileUtils.getRealPath(this, uri);
        } catch (IOException e) {
            e.printStackTrace();
            checkForUserVisibleErrors(getResources().getString(R.string.file_not_found));
        }
        return path;
    }

    private void runOCRAnimation(Uri image) {
        Intent intent = new Intent(this, OCRAnimationActivity.class);
        intent.setData(image);
        startActivityForResult(intent, OCRAnimationActivity_REQUEST_CODE);
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
////// TODO: 1/18/17 add/remove special items for free/PRO version
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        Menu navigationMenu = navigationView.getMenu();
//
////        add(int groupId, int itemId, int order, CharSequence title)
////        Add a new item to the menu.
//
//        navigationMenu.add(0, 0, Menu.NONE, "ADDED PROGRAMMATICALLY").setIcon(R.drawable.ic_android_black_24dp);
//
//        MenuItem item = navigationView.getMenu().getItem(0);
//        item.setVisible(false);
//
//        return super.onPrepareOptionsMenu(menu);
//    }

    private ArrayList<String> obtainLanguageShortcuts() {
        ArrayList<String> languageNames = getCheckedLanguages();

        LanguageList data = new LanguageList(this);
        LinkedHashMap<String, String> languages = data.getLanguages();

        ArrayList<String> result = new ArrayList<>();
        for (String name : languageNames) {
            if (languages.containsKey(name)) {
                result.add(languages.get(name));
            }
        }

        return result;
    }

    //todo messages will not be translated - fix it
    //// TODO: 5/29/17 there is no mechanism to close error view - fix it
    private void checkForUserVisibleErrors(@Nullable String forceErrorMessage) {

        boolean showError = false;

        // If offline, message is about the lack of connectivity:
        if (!NetworkUtils.isOnline(this)) {
            mErrorMessage.setText(R.string.error_no_connection);
            showError = true;
        } else {
            if (forceErrorMessage != null && forceErrorMessage.length() > 0) {
                // Finally, if the caller requested to show error, show an error message:
                mErrorMessage.setText(forceErrorMessage);
                showError = true;
            }
        }
        mErrorView.setVisibility(showError ? View.VISIBLE : View.GONE);
        Log.d(TAG, mErrorMessage.getText().toString());
    }

    private void updateLanguageTextView(ArrayList<String> checkedLanguages) {
        String languageString = generateLanguageString(checkedLanguages);
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
    private String generateLanguageString(ArrayList<String> checkedLanguages) {
        String languageString = "";

        for (String l : checkedLanguages) {
            languageString += l + ", ";
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
                startGalleryChooser();
            }
        });
    }

    public void startGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_photo)),
                GALLERY_IMAGE_REQUEST);
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

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Set up the navigation drawer.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.list_navigation_menu_item:
                                // Do nothing, we're already on that screen
                                break;
                            case R.id.statistics_navigation_menu_item:
                                // TODO: 1/14/17
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
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
    public void setTitle(CharSequence title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
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

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //explanation https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en

                    imageUri = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            createImageFile());
                } else {
                    imageUri = Uri.fromFile(createImageFile());
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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


    private Set<String> obtainSavedLanguages() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> auto = new HashSet<String>() {{
            add(getString(R.string.auto));
        }};
        TreeSet<String> checkedLanguagesNames = new TreeSet<>(sharedPref.getStringSet(CHECKED_LANGUAGES, auto));
        return checkedLanguagesNames;
    }

    private void saveLanguages(LinkedHashSet<String> data) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        LinkedHashSet<String> checkedLanguages = new LinkedHashSet<>();
        for (String name : data) {
            checkedLanguages.add(name);
        }
        editor.putStringSet(CHECKED_LANGUAGES, checkedLanguages);
        editor.apply();
    }
}

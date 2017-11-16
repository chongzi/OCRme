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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.ashomok.imagetotext.firebaseUiAuth.BaseLoginActivity;
import com.ashomok.imagetotext.firebaseUiAuth.SignOutDialogFragment;
import com.ashomok.imagetotext.language_choser_mvp_di.LanguageOcrActivity;
import com.ashomok.imagetotext.my_docs.MyDocsActivity;
import com.ashomok.imagetotext.ocr.OcrActivity;
import com.ashomok.imagetotext.utils.NetworkUtils;
import com.ashomok.imagetotext.utils.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.ashomok.imagetotext.Settings.isAdsActive;
import static com.ashomok.imagetotext.language_choser_mvp_di.LanguageOcrActivity.CHECKED_LANGUAGE_CODES;
import static com.ashomok.imagetotext.ocr.OcrActivity.RESULT_CANCELED_BY_USER;
import static com.ashomok.imagetotext.utils.FileUtils.prepareDirectory;
import static com.ashomok.imagetotext.utils.InfoSnackbarUtil.showError;
import static com.ashomok.imagetotext.utils.InfoSnackbarUtil.showWarning;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

public class MainActivity extends BaseLoginActivity
        implements SignOutDialogFragment.OnSignedOutListener, View.OnClickListener {

    private static final String TAG = DEV_TAG + MainActivity.class.getSimpleName();
    private static final int LANGUAGE_ACTIVITY_REQUEST_CODE = 1;
    private static final int CaptureImage_REQUEST_CODE = 2;
    private static final int OCR_Activity_REQUEST_CODE = 3;
    private static final int GALLERY_IMAGE_REQUEST = 4;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private final BroadcastReceiver mConnectivityChangeReceiver = new BroadcastReceiver() {
        private boolean oldOnline = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isOnline = NetworkUtils.isOnline(context);
            if (isOnline != oldOnline) {
                oldOnline = isOnline;
                checkConnection();
            }
        }
    };
    private Uri imageUri;
    private TextView languageTextView;
    private Button myDocsBtn;
    private String mEmail = "No email";
    private String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE; //todo use rx permissions
    private Optional<List<String>> languageCodes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView = findViewById(R.id.drawer_layout);

        setUpToolbar();
        setUpNavigationDrawer();

        checkConnection();

        initImageSourceBtns();
        initLanguageViews();
        initMyDocsView();
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


    private void initMyDocsView() {
        Button signInBtn = findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(this);

        myDocsBtn = findViewById(R.id.my_docs_btn);
        myDocsBtn.setOnClickListener(this);

        updateUi(mIsUserSignedIn);
    }

    private void initLanguageViews() {
        languageTextView = findViewById(R.id.language);
        languageTextView.setPaintFlags(languageTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        languageTextView.setOnClickListener(this);
        languageCodes = obtainSavedLanguagesCodes();
        updateLanguageTextView(languageCodes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LANGUAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                List<String> checkedLanguageCodes = bundle.getStringArrayList(CHECKED_LANGUAGE_CODES);
                languageCodes = Optional.ofNullable(checkedLanguageCodes);
                updateLanguageTextView(languageCodes);

                saveLanguages();
            }
        }

        //photo obtained from camera
        if (requestCode == CaptureImage_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            startOcrActivity(imageUri);
        }

        //photo obtained from gallery
        else if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                startOcrActivity(uri);
            } catch (Exception e) {
                showError(R.string.file_not_found, mRootView);
                e.printStackTrace();
            }
        }

        //ocr canceled
        else if (requestCode == OCR_Activity_REQUEST_CODE && resultCode == RESULT_CANCELED_BY_USER) {
            showWarning(R.string.canceled, mRootView);
        }
    }

    private void saveLanguages() {
        if (languageCodes.isPresent()) {
            SharedPreferences preferences = getDefaultSharedPreferences(this);
            SharedPreferencesUtil.pushStringList(
                    preferences, languageCodes.get(), getString(R.string.saved_language_codes));
        }
    }

    private void startOcrActivity(Uri uri) {
        Intent intent = new Intent(this, OcrActivity.class);
        intent.setData(uri);
        if (languageCodes.isPresent()) {
            intent.putStringArrayListExtra(OcrActivity.EXTRA_LANGUAGES, new ArrayList<>(languageCodes.get()));
        }
        startActivityForResult(intent, OCR_Activity_REQUEST_CODE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Menu navigationMenu = navigationView.getMenu();

        navigationMenu.findItem(R.id.remove_ads).setVisible(isAdsActive);
        return super.onPrepareOptionsMenu(menu);
    }

    private void checkConnection() {
        if (!NetworkUtils.isOnline(this)) {
            showError(R.string.no_internet_connection, mRootView);
        }
    }

    private void updateLanguageTextView(Optional<List<String>> checkedLanguageCodes) {
        String languageString;
        if (checkedLanguageCodes.isPresent()) {
            languageString = generateLanguageString(checkedLanguageCodes.get());
        } else {
            languageString = getString(R.string.auto);
        }
        languageTextView.setText(languageString);
    }

    @NonNull
    private String generateLanguageString(List<String> checkedLanguageCodes) {
        Map<String, String> allLanguages = Settings.getOcrLanguageSupportList(this);
        List<String> checkedLanguages = Stream.of(checkedLanguageCodes)
                .filter(allLanguages::containsKey)
                .map(allLanguages::get)
                .collect(Collectors.toList());

        StringBuilder languageString = new StringBuilder();
        for (String l : checkedLanguages) {
            languageString.append(l).append(", ");
        }

        if (languageString.toString().endsWith(", ")) {
            languageString = new StringBuilder(languageString.substring(0, languageString.length() - 2));
        }

        return languageString.toString();
    }

    private void initImageSourceBtns() {
        //gallery btn init
        final ImageButton galleryBtn = findViewById(R.id.gallery_btn);

        equalizeSides(galleryBtn);
        galleryBtn.setOnClickListener(this);

        //photo btn init
        final ImageButton photoBtn = findViewById(R.id.photo_btn);
        equalizeSides(photoBtn);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);

        RxView.clicks(photoBtn)
                .compose(rxPermissions.ensureEach(permission))
                .subscribe(permission -> {
                    if (permission.granted) {
                        startCamera();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        showWarning(R.string.camera_needs_to_save, mRootView);
                    } else {
                        showWarning(R.string.this_option_is_not_be_avalible, mRootView);
                    }
                }, throwable -> {
                    String localizedMessage = throwable.getLocalizedMessage();
                    if (localizedMessage != null && localizedMessage.length() > 0) {
                        showError(throwable.getLocalizedMessage(), mRootView);
                    } else {
                        showError(throwable.getMessage(), mRootView);
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
        v.post(() -> {
            LinearLayout.LayoutParams mParams;
            mParams = (LinearLayout.LayoutParams) v.getLayoutParams();
            mParams.height = v.getWidth();
            v.setLayoutParams(mParams);
            v.postInvalidate();
        });
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        // Set up the navigation drawer.
        navigationView = findViewById(R.id.nav_view);
        setupDrawerContent(navigationView);
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

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.my_docs:
                            startMyDocsActivity();
                            break;
                        case R.id.about:
                            // TODO:
                            break;
                        case R.id.remove_ads:
                            // TODO:
                            break;
                        case R.id.logout:
                            logout();
                            break;


                        default:
                            break;
                    }
                    // Close the navigation drawer when an item is selected.
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                });

        //set up login header
        LinearLayout loginHeader = navigationView.getHeaderView(0).findViewById(R.id.propose_login_menu_item);
        loginHeader.setOnClickListener(this);
    }

    private void startMyDocsActivity() {
        Intent intent = new Intent(this, MyDocsActivity.class);
        intent.putExtra(MyDocsActivity.IS_SIGNED_IN_TAG, mIsUserSignedIn);
        startActivity(intent);
    }

    private void logout() {
        SignOutDialogFragment dialog =
                SignOutDialogFragment.newInstance(getString(R.string.ask_sign_out, mEmail));
        dialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed()" + getFragmentManager().getBackStackEntryCount());
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {
            ExitDialogFragment exitDialogFragment = ExitDialogFragment.newInstance(
                    R.string.exit_dialog_title);

            exitDialogFragment.show(getFragmentManager(), "dialog");
        }

    }

    @Override
    public void setTitle(CharSequence title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
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

    private Optional<List<String>> obtainSavedLanguagesCodes() {
        SharedPreferences sharedPref = getDefaultSharedPreferences(this);
        return Optional.ofNullable(SharedPreferencesUtil.pullStringList(
                sharedPref, getString(R.string.saved_language_codes)));
    }

    /**
     * update UI if signed in/out
     */
    @Override
    public void updateUi(boolean isUserSignedIn) {
        Log.d(TAG, "updateUi called with " + isUserSignedIn);
        try {
            updateNavigationDrawer(isUserSignedIn);
            updateMainScreen(isUserSignedIn);
        } catch (NullPointerException e) {
            Log.e(TAG, "View is not ready to be updated.");
            //ignore
        }
    }

    /**
     * show/hide SignIn proposition on the main screen
     *
     * @param isUserSignedIn
     */
    private void updateMainScreen(boolean isUserSignedIn) {
        View askLoginView = findViewById(R.id.ask_login);
        if (isUserSignedIn) {
            askLoginView.setVisibility(View.GONE);
            myDocsBtn.setVisibility(View.VISIBLE);
        } else {
            askLoginView.setVisibility(View.VISIBLE);
            myDocsBtn.setVisibility(View.GONE);
        }
    }

    private void updateNavigationDrawer(boolean isUserSignedIn) {

        Menu navigationMenu = navigationView.getMenu();
        navigationMenu.findItem(R.id.logout).setVisible(isUserSignedIn);

        View signedInNavHeader = navigationView.getHeaderView(0).findViewById(R.id.signed_in_layout);
        View askSignInNavHeader = navigationView.getHeaderView(0).findViewById(R.id.propose_login_menu_item);

        askSignInNavHeader.setVisibility(isUserSignedIn ? View.GONE : View.VISIBLE);
        signedInNavHeader.setVisibility(isUserSignedIn ? View.VISIBLE : View.GONE);

        if (isUserSignedIn) {

            //set "signed as" text
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                mEmail = user.getEmail();
            }
            TextView signedAsText = signedInNavHeader.findViewById(R.id.you_signed_as);
            signedAsText.setText(getString(R.string.you_signed_in_as, mEmail));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_btn:
                signIn();
                break;
            case R.id.propose_login_menu_item:
                signIn();
                break;
            case R.id.language:
                onLanguageTextViewClicked();
                break;
            case R.id.gallery_btn:
                startGalleryChooser();
                break;
            case R.id.my_docs_btn:
                startMyDocsActivity();
                break;
            default:
                break;
        }
    }

    private void onLanguageTextViewClicked() {
        Intent intent = new Intent(this, LanguageOcrActivity.class);
        if (languageCodes.isPresent()) {
            ArrayList<String> extra = new ArrayList<>(languageCodes.get());
            intent.putStringArrayListExtra(CHECKED_LANGUAGE_CODES, extra);
        }
        startActivityForResult(intent, LANGUAGE_ACTIVITY_REQUEST_CODE);
    }
}

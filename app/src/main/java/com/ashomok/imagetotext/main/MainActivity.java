package com.ashomok.imagetotext.main;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashomok.imagetotext.BuildConfig;
import com.ashomok.imagetotext.ExitDialogFragment;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.Settings;
import com.ashomok.imagetotext.crop_image.CropImageActivity;
import com.ashomok.imagetotext.firebaseUiAuth.BaseLoginActivity;
import com.ashomok.imagetotext.firebaseUiAuth.SignOutDialogFragment;
import com.ashomok.imagetotext.language_choser.LanguageOcrActivity;
import com.ashomok.imagetotext.my_docs.MyDocsActivity;
import com.ashomok.imagetotext.ocr.OcrActivity;
import com.ashomok.imagetotext.update_to_premium.UpdateToPremiumActivity;
import com.ashomok.imagetotext.utils.InfoSnackbarUtil;
import com.ashomok.imagetotext.utils.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.ashomok.imagetotext.Settings.isAdsActive;
import static com.ashomok.imagetotext.Settings.isPremium;
import static com.ashomok.imagetotext.language_choser.LanguageOcrActivity.CHECKED_LANGUAGE_CODES;
import static com.ashomok.imagetotext.ocr.OcrActivity.RESULT_CANCELED_BY_USER;
import static com.ashomok.imagetotext.utils.FileUtils.createFile;
import static com.ashomok.imagetotext.utils.InfoSnackbarUtil.showWarning;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

//todo use butterknife
public class MainActivity extends BaseLoginActivity implements
        SignOutDialogFragment.OnSignedOutListener,
        View.OnClickListener,
        MainContract.View {

    private static final String TAG = DEV_TAG + MainActivity.class.getSimpleName();

    @Inject
    MainPresenter mPresenter;

    private static final int LANGUAGE_ACTIVITY_REQUEST_CODE = 1;
    private static final int CaptureImage_REQUEST_CODE = 2;
    private static final int OCR_Activity_REQUEST_CODE = 3;
    private static final int GALLERY_IMAGE_REQUEST = 4;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    private Uri imageUri;
    private TextView languageTextView;
    private ImageView premiumButton;
    private Button myDocsBtn;
    private String mEmail = "No email";
    private String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String imageFileNameFromCamera = "ocr.jpg";


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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        setContentView(R.layout.activity_main);

        setUpToolbar();
        setUpNavigationDrawer();

        initImageSourceBtns();
        initLanguageViews();
        initMyDocsView();

        updateUi(mIsUserSignedIn);

        mPresenter.takeView(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        // Registers BroadcastReceiver to track network connection changes.
        registerReceiver(mConnectivityChangeReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LANGUAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                @Nullable List<String> checkedLanguageCodes =
                        bundle.getStringArrayList(CHECKED_LANGUAGE_CODES);

                mPresenter.onCheckedLanguageCodesObtained(checkedLanguageCodes);
            }
        }

        //photo obtained from camera
        else if (requestCode == CaptureImage_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            startCropImageActivity(imageUri);
        }

        //photo obtained from gallery
        else if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                startCropImageActivity(uri);
            } catch (Exception e) {
                showError(R.string.file_not_found);
                e.printStackTrace();
            }
        }

        //ocr canceled
        else if (requestCode == OCR_Activity_REQUEST_CODE && resultCode == RESULT_CANCELED_BY_USER) {
            showWarning(R.string.canceled, mRootView);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mConnectivityChangeReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();  //prevent leaking activity in
        // case presenter is orchestrating a long running task
    }


    private void initMyDocsView() {
        Button signInBtn = findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(this);

        myDocsBtn = findViewById(R.id.my_docs_btn);
        myDocsBtn.setOnClickListener(this);
    }

    private void initLanguageViews() {
        languageTextView = findViewById(R.id.language);
        languageTextView.setPaintFlags(languageTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        languageTextView.setOnClickListener(this);
    }

    private void checkConnection() {
        if (!NetworkUtils.isOnline(this)) {
            showError(R.string.no_internet_connection);
        }
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

    private void startCropImageActivity(Uri uri) {
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.putExtra(CropImageActivity.EXTRA_IMAGE_URI, uri);
        if (mPresenter.getLanguageCodes().isPresent()) {
            intent.putStringArrayListExtra(
                    OcrActivity.EXTRA_LANGUAGES, new ArrayList<>(mPresenter.getLanguageCodes().get()));
        }
        startActivityForResult(intent, OCR_Activity_REQUEST_CODE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Menu navigationMenu = navigationView.getMenu();

        updateUpdateToPremiumMenuItem(navigationMenu);
        return super.onPrepareOptionsMenu(menu);
    }

    private void updateUpdateToPremiumMenuItem(Menu navigationMenu) {
        MenuItem updateToPremiumMenuItem = navigationMenu.findItem(R.id.update_to_premium);
        CharSequence menuItemText = updateToPremiumMenuItem.getTitle();
        SpannableString spannableString = new SpannableString(menuItemText);
        spannableString.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.orange_600)),
                0,
                spannableString.length(),
                0);
        updateToPremiumMenuItem.setTitle(spannableString);
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
                        showWarning(R.string.needs_to_save, mRootView);
                    } else {
                        showWarning(R.string.this_option_is_not_be_avalible, mRootView);
                    }
                }, throwable -> showError(throwable.getMessage()));
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
            int width = v.getWidth();
            if (width > 0) {
                mParams.height = v.getWidth();
                v.setLayoutParams(mParams);
                v.postInvalidate();
            }
        });
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
                        case R.id.update_to_premium:
                            startUpdateToPremiumActivity();
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
        LinearLayout loginHeader =
                navigationView.getHeaderView(0).findViewById(R.id.propose_sign_in_layout);
        loginHeader.setOnClickListener(this);
    }

    private void startUpdateToPremiumActivity() {
        Intent intent = new Intent(this, UpdateToPremiumActivity.class);
        startActivity(intent);
    }

    private void startMyDocsActivity() {
        Intent intent = new Intent(this, MyDocsActivity.class);
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
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile;
            try {
                photoFile = createFile(imageFileNameFromCamera);
            } catch (Exception ex) {
                showError(R.string.error_while_creating_file);
                return;
            }

            // Continue only if the File was successfully created
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //explanation https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en

                imageUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
            } else {
                imageUri = Uri.fromFile(photoFile);
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, CaptureImage_REQUEST_CODE);
        } else {
            showError(R.string.camera_not_found);
        }
    }

    /**
     * update UI if signed in/out
     */
    @Override
    public void updateUi(boolean isUserSignedIn) {
        Log.d(TAG, "updateUi called with " + isUserSignedIn);
        updateNavigationDrawerForSignIn(isUserSignedIn);
        updateMainScreen(isUserSignedIn);
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

    private void updateNavigationDrawerForSignIn(boolean isUserSignedIn) {

        Menu navigationMenu = navigationView.getMenu();
        navigationMenu.findItem(R.id.logout).setVisible(isUserSignedIn);

        View signedInNavHeader =
                navigationView.getHeaderView(0).findViewById(R.id.signed_in_layout);
        View askSignInNavHeader =
                navigationView.getHeaderView(0).findViewById(R.id.propose_sign_in_layout);

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

    private void updateNavigationDrawerForPremium(boolean isPremium) {
        if (isPremium) {
            //in header
            View signedInNavHeader =
                    navigationView.getHeaderView(0).findViewById(R.id.signed_in_layout);
            View premiumBtn = signedInNavHeader.findViewById(R.id.premium_btn);
            premiumBtn.setVisibility(View.VISIBLE);
            premiumBtn.setOnClickListener(view -> startUpdateToPremiumActivity());

            //in menu
            Menu navigationMenu = navigationView.getMenu();
            navigationMenu.findItem(R.id.update_to_premium).setTitle(R.string.my_premium);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_btn:
                signIn();
                break;
            case R.id.propose_sign_in_layout:
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
        if (mPresenter.getLanguageCodes().isPresent()) {
            ArrayList<String> extra = new ArrayList<>(mPresenter.getLanguageCodes().get());
            intent.putStringArrayListExtra(CHECKED_LANGUAGE_CODES, extra);
        }
        startActivityForResult(intent, LANGUAGE_ACTIVITY_REQUEST_CODE);
    }

    private void showError(String message) {
        InfoSnackbarUtil.showError(message, mRootView);
    }

    @Override
    public void showError(int errorMessageRes) {
        InfoSnackbarUtil.showError(errorMessageRes, mRootView);
    }

    @Override
    public void showInfo(int infoMessageRes) {
        InfoSnackbarUtil.showInfo(infoMessageRes, mRootView);
    }

    @Override
    public void updateLanguageString(String languageString) {
        languageTextView.setText(languageString);
    }

    @Override
    public void updateView(boolean isPremium) {
        Log.d(TAG, "updateUi called with " + isPremium);
        Settings.isPremium = isPremium;
        Settings.isAdsActive = !isPremium; //todo what if not premium but bought no ads

        if (isAdsActive) {
            showAds();
        }

        updateNavigationDrawerForPremium(isPremium);
    }

    private void showAds() {
        //todo
    }
}
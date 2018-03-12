package com.ashomok.imagetotext.my_docs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.annimon.stream.Stream;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.firebaseUiAuth.BaseLoginActivity;
import com.ashomok.imagetotext.my_docs.get_my_docs_task.MyDocsHttpClient;
import com.ashomok.imagetotext.ocr.ocr_task.OcrResponse;
import com.ashomok.imagetotext.ocr.ocr_task.OcrResult;
import com.ashomok.imagetotext.ocr_result.OcrResultActivity;
import com.ashomok.imagetotext.utils.AlertDialogHelper;
import com.ashomok.imagetotext.utils.AutoFitGridLayoutManager;
import com.ashomok.imagetotext.utils.EndlessRecyclerViewScrollListener;
import com.ashomok.imagetotext.utils.InfoSnackbarUtil;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.ashomok.imagetotext.ocr_result.OcrResultActivity.EXTRA_OCR_RESPONSE;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 8/18/17.
 */
public class MyDocsActivity extends BaseLoginActivity implements View.OnClickListener, MyDocsContract.View {

    private static final int DELETE_TAG = 1;
    private static final String TAG = DEV_TAG + MyDocsActivity.class.getSimpleName();

    private List<Object> dataList; //list of OcrResult and Ad objectsS
    private List<OcrResult> multiSelectDataList;
    private RecyclerViewAdapter adapter;
    private ActionMode mActionMode;
    boolean isMultiSelect = false;
    AlertDialogHelper alertDialogHelper;
    private ProgressBar progress;
    private Ad ad;
    private boolean adLoaded = false;

    @Inject
    MyDocsPresenter mPresenter;

    @Inject
    MyDocsHttpClient httpClient;

    //"Select" docs action mode
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.my_docs_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_select_all:
                    selectAll(mode);
                    return true;
                case R.id.action_unselect_all:
                    unselectAll(mode);
                    return true;
                case R.id.action_delete:
                    showAlertDialog();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiSelectDataList.clear();
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        setContentView(R.layout.activity_my_docs);
        initToolbar();
        initSignInView();
        initRecyclerView();
        progress = findViewById(R.id.progress);

        updateUi(mIsUserSignedIn);

        mPresenter.takeView(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_docs_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.select:
                onSelectMode();
                return true;
            //add more menu items - see my_docs_common
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();  //prevent leaking activity in
        // case presenter is orchestrating a long running task
    }

    private void initSignInView() {
        Button signInBtn = findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(this);
    }

    private void onSelectMode() {
        if (!isMultiSelect) {
            multiSelectDataList.clear();
            isMultiSelect = true;

            if (mActionMode == null) {
                mActionMode = startActionMode(mActionModeCallback);
            }

            mActionMode.setTitle(multiSelectDataList.size() + getString(R.string.selected));
        }
    }

    private void showAlertDialog() {
        alertDialogHelper = new AlertDialogHelper(this, new AlertDialogHelper.AlertDialogListener() {
            @Override
            public void onPositiveClick(int from) {
                if (from == DELETE_TAG) {
                    if (multiSelectDataList.size() > 0) {

                        mPresenter.deleteDocs(multiSelectDataList);
                        if (mActionMode != null) {
                            mActionMode.finish();
                        }
                    }
                }
            }

            @Override
            public void onNegativeClick(int from) {
            }

            @Override
            public void onNeutralClick(int from) {
            }
        });

        alertDialogHelper.showAlertDialog(
                "",
                getString(R.string.delete_docs, String.valueOf(multiSelectDataList.size())),
                getString(R.string.delete),
                getString(R.string.cancel),
                DELETE_TAG,
                false);
    }

    public void onMultiSelect(int position) {
        if (mActionMode != null) {
            if (multiSelectDataList.contains(dataList.get(position))) {
                multiSelectDataList.remove(dataList.get(position));
            } else {
                if (dataList.get(position) instanceof OcrResult) {
                    multiSelectDataList.add((OcrResult) dataList.get(position));
                }
            }
            mActionMode.setTitle(multiSelectDataList.size() + getString(R.string.selected));

            adapter.notifyDataSetChanged();
        }
    }

    private RecyclerViewCallback callback = new RecyclerViewCallback() {
        @Override
        public void onItemClick(int position) {
            if (isMultiSelect) {
                onMultiSelect(position);
            } else {
                if (dataList.get(position) instanceof OcrResult) {
                    OcrResult doc = (OcrResult) dataList.get(position);
                    startOcrResultActivity(new OcrResponse(doc, OcrResponse.Status.OK));
                }
            }
        }

        @Override
        public void onItemLongClick(int position) {
            onSelectMode();
            onMultiSelect(position);
        }

        @Override
        public void onItemDelete(int position) {
            //todo
            multiSelectDataList.add((OcrResult) dataList.get(position));
            mPresenter.deleteDocs(multiSelectDataList);
            multiSelectDataList.clear();
        }

        @Override
        public void onItemShareText(int position) {
            //todo
            OcrResult doc = (OcrResult) dataList.get(position);
            String textResult = doc.getTextResult();
            mPresenter.onShareTextClicked(textResult);
        }

        @Override
        public void onItemSharePdf(int position) {
            //todo
            OcrResult doc = (OcrResult) dataList.get(position);
            String mDownloadURL = doc.getPdfResultMediaUrl();
            mPresenter.onSharePdfClicked(mDownloadURL);
        }
    };

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        AutoFitGridLayoutManager layoutManager =
                new AutoFitGridLayoutManager(this, 500);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        dataList = new ArrayList<>();
        multiSelectDataList = new ArrayList<>();

        adapter = new RecyclerViewAdapter(this, dataList, multiSelectDataList, callback);
        recyclerView.setAdapter(adapter);
        EndlessRecyclerViewScrollListener scrollListener =
                new EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list
                        mPresenter.loadMoreDocs();
                    }
                };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);

        NativeAd nativeAd = new NativeAd(this, "172310460079691_172447986732605");
        nativeAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad1) {
                ad = ad1;
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        nativeAd.loadAd();
    }

    private void startOcrResultActivity(OcrResponse data) {
        Intent intent = new Intent(this, OcrResultActivity.class);
        intent.putExtra(EXTRA_OCR_RESPONSE, data);
        startActivity(intent);
    }

    private void unselectAll(ActionMode mode) {
        multiSelectDataList.clear();
        mode.setTitle(multiSelectDataList.size() + getString(R.string.selected));

        //update menu buttons
        Menu menu = mode.getMenu();
        menu.findItem(R.id.action_select_all).setVisible(true);
        menu.findItem(R.id.action_unselect_all).setVisible(false);

        adapter.notifyItemRangeChanged(0, dataList.size());
    }

    private void selectAll(ActionMode mode) {
        multiSelectDataList.clear();
        multiSelectDataList.addAll(Stream.of(dataList)
                .filter(l -> l instanceof OcrResult)
                .map(l -> (OcrResult) l).toList());

        mode.setTitle(multiSelectDataList.size() + getString(R.string.selected));

        //update menu buttons
        Menu menu = mode.getMenu();
        menu.findItem(R.id.action_select_all).setVisible(false);
        menu.findItem(R.id.action_unselect_all).setVisible(true);

        adapter.notifyItemRangeChanged(0, dataList.size());
    }

    /**
     * update UI if signed in/out
     */
    @Override
    public void updateUi(boolean isUserSignedIn) {
        Log.d(TAG, "Update UI. Is user signed in " + isUserSignedIn);
        View askLoginView = findViewById(R.id.ask_login);
        View myDocsView = findViewById(R.id.my_docs);

        askLoginView.setVisibility(isUserSignedIn ? View.GONE : View.VISIBLE);
        myDocsView.setVisibility(isUserSignedIn ? View.VISIBLE : View.GONE);

        if (isUserSignedIn) {
            //load docs
            mPresenter.initWithDocs();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_btn:
                signIn();
                break;
            default:
                break;
        }
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
    public void addNewLoadedDocs(List<OcrResult> newLoadedDocs) {
        int positionStart = dataList.size() - 1;
        dataList.addAll(newLoadedDocs);
        adapter.notifyItemRangeInserted(positionStart, newLoadedDocs.size());

        if (dataList.size() == 0) {
            //show empty view
            View emptyView = findViewById(R.id.empty_result_layout);
            emptyView.setVisibility(View.VISIBLE);
        }

        onDocsLoaded();
    }

    private void onDocsLoaded() {
        //load ad once
        if (!adLoaded && dataList.size() > 0) {
            int adPosition = dataList.size() / 2;
            dataList.add(adPosition, ad);
            adapter.notifyDataSetChanged();
            adLoaded = true;
        }
    }

    @Override
    public void clearDocsList() {
        int size = dataList.size();
        dataList.clear();
        adapter.notifyItemRangeRemoved(0, size);
    }

    /**
     * Shows the progress UI
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            progress.setVisibility(show ? View.VISIBLE : View.GONE);
            progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    interface RecyclerViewCallback {
        void onItemClick(int position);

        void onItemLongClick(int position);

        void onItemDelete(int position);

        void onItemShareText(int position);

        void onItemSharePdf(int position);
    }
}

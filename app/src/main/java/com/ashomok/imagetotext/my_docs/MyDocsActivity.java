package com.ashomok.imagetotext.my_docs;

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
import android.widget.Toast;

import com.annimon.stream.Optional;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.firebaseUiAuth.BaseLoginActivity;
import com.ashomok.imagetotext.my_docs.get_my_docs_task.MyDocsHttpClient;
import com.ashomok.imagetotext.my_docs.get_my_docs_task.MyDocsResponse;
import com.ashomok.imagetotext.utils.AlertDialogHelper;
import com.ashomok.imagetotext.utils.AutoFitGridLayoutManager;
import com.ashomok.imagetotext.utils.EndlessRecyclerViewScrollListener;
import com.ashomok.imagetotext.utils.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 8/18/17.
 */

//https://www.journaldev.com/13792/android-gridlayoutmanager-example
//        https://developer.android.com/training/material/lists-cards.html
    //https://stackoverflow.com/questions/29831083/how-to-use-itemanimator-in-a-recyclerview

//todo add progress bar while load items - minor

public class MyDocsActivity extends BaseLoginActivity implements View.OnClickListener,
        AlertDialogHelper.AlertDialogListener, MyDocsActivity.RecyclerViewCallback {

    private static final int DELETE_TAG = 1;
    private static final String TAG = DEV_TAG + MyDocsActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private String idToken;
    private String startCursor;
    private List<MyDocsResponse.MyDoc> dataList;

    private RecyclerViewAdapter adapter;
    private ActionMode mActionMode;
    AlertDialogHelper alertDialogHelper;

    //"Chosen" docs action mode
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
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog(
                            "",
                            "Delete Contact",
                            "DELETE",
                            "CANCEL",
                            DELETE_TAG,
                            false);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiSelectDataList = new ArrayList<>();
            refreshAdapter();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_docs_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            //todo reduntant
//            case android.R.id.home:
//                onBackPressed();
//                return true;

            case R.id.check:
                openCheck();
                return true;
            //todo add more menu items - see my_docs_common
        }

        return super.onOptionsItemSelected(item);
    }

    private void openCheck() {
        //todo
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_docs);

        initToolbar();

        alertDialogHelper = new AlertDialogHelper(this);

        Button signInBtn = findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(this);

        initRecyclerView();
        fillRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        AutoFitGridLayoutManager layoutManager =
                new AutoFitGridLayoutManager(this, 500);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        dataList = new ArrayList<>();
        adapter = new RecyclerViewAdapter(dataList);
        recyclerView.setAdapter(adapter);

        EndlessRecyclerViewScrollListener scrollListener =
                new EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list
                        loadNextDataFromApi();
                    }
                };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void fillRecyclerView() {
        MyDocsHttpClient httpClient = MyDocsHttpClient.getInstance();
        callMyDocs(httpClient);
    }


    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi() {
        Log.d(TAG, "more loaded");
        if (isOnline()) {
            if (!loadingCompleted()) {
                MyDocsHttpClient httpClient = MyDocsHttpClient.getInstance();
                callApiForDocs(httpClient, idToken, startCursor);
            }
        } else {
            //todo network error
//            startOcrResultActivity(getString(R.string.network_error));
        }
    }

    private boolean loadingCompleted() {
        return startCursor == null;
    }

    private void callMyDocs(MyDocsHttpClient httpClient) {
        if (isOnline()) {
            //get user IdToken
            Single<Optional<String>> idTokenSingle = getIdToken()
                    .subscribeOn(Schedulers.io())
                    .compose(bindToLifecycle());

            idTokenSingle.subscribe(
                    optionalToken -> {
                        if (optionalToken.isPresent()) {
                            idToken = optionalToken.get();
                            callApiForDocs(httpClient, idToken, startCursor);
                        } else {
                            //todo show error unable to identify user - try login
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        String errorMessage = throwable.getMessage();
                        //todo show error unable to identify user - try login
                    });
        } else {
            //todo network error
//            startOcrResultActivity(getString(R.string.network_error));
        }
    }

    private void callApiForDocs(MyDocsHttpClient httpClient, String idToken, String startCursor) {
        Log.d(TAG, "calApiForDocs with cursor: " + startCursor);
        Single<MyDocsResponse> myDocs =
                httpClient.myDocs(idToken, startCursor);

        myDocs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle()).subscribe(
                myDocsResponce -> {
                    MyDocsResponse.Status status = myDocsResponce.getStatus();
                    if (status.equals(MyDocsResponse.Status.USER_NOT_FOUND)) {
                        //todo show eeror user not found
                    } else if (status.equals(MyDocsResponse.Status.UNKNOWN_ERROR)) {
                        //todo show error unknown error
                    } else if (status.equals(MyDocsResponse.Status.OK)) {
                        updateCursor(myDocsResponce.getEndCursor());
                        updateRecyclerView(myDocsResponce.getRequestList());
                    } else {
                        Log.e(TAG, "Unknown status received");
                    }
                },
                throwable -> {
                    throwable.printStackTrace();
                    String errorMessage = throwable.getMessage();
                    //todo
                });
    }

    private void updateCursor(String cursor) {
        startCursor = cursor;
    }

    private void updateRecyclerView(List<MyDocsResponse.MyDoc> newData) {
        dataList.addAll(newData);
        adapter.notifyItemInserted(dataList.size() - 1);
    }

    boolean isOnline() {
        return NetworkUtils.isOnline(this);
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

    /**
     * update UI if signed in/out
     */
    @Override
    public void updateUi(boolean isUserSignedIn) {
        Log.d(TAG, "updateUi called with " + isUserSignedIn);
        View askLoginView = findViewById(R.id.ask_login);
        View myDocsView = findViewById(R.id.my_docs);

        askLoginView.setVisibility(isUserSignedIn ? View.GONE : View.VISIBLE);
        myDocsView.setVisibility(isUserSignedIn ? View.VISIBLE : View.GONE);
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

    // AlertDialog Callback Functions
    @Override
    public void onPositiveClick(int from) {
        if (from == DELETE_TAG) {
            if (multiSelectDataList.size() > 0) {
                dataList.removeAll(multiSelectDataList);

                multiSelectAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
                Toast.makeText(getApplicationContext(), "Delete Click", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNegativeClick(int from) {
//redundant
    }

    @Override
    public void onNeutralClick(int from) {
//redundant
    }

    @Override
    public void onChoseMode() {
        if (mActionMode == null) {
            mActionMode = startActionMode(mActionModeCallback);
        }
    }

    //todo update strings
    @Override
    public void updateTitle(int selectedItemsCount) {
        if (mActionMode != null) {
            if (selectedItemsCount > 0) {
                mActionMode.setTitle("" + selectedItemsCount);
            } else {
                mActionMode.setTitle("");
            }
        }
    }

    interface RecyclerViewCallback {
        void onChoseMode();
        void updateTitle(int selectedItemsCount);
    }
}

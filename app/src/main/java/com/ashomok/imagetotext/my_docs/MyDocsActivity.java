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

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.firebaseUiAuth.BaseLoginActivity;
import com.ashomok.imagetotext.my_docs.get_my_docs_task.MyDocsHttpClient;
import com.ashomok.imagetotext.my_docs.get_my_docs_task.MyDocsResponse;
import com.ashomok.imagetotext.utils.AlertDialogHelper;
import com.ashomok.imagetotext.utils.AutoFitGridLayoutManager;
import com.ashomok.imagetotext.utils.EndlessRecyclerViewScrollListener;
import com.ashomok.imagetotext.utils.InfoSnackbarUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 8/18/17.
 */

//https://www.journaldev.com/13792/android-gridlayoutmanager-example
//        https://developer.android.com/training/material/lists-cards.html
//https://stackoverflow.com/questions/29831083/how-to-use-itemanimator-in-a-recyclerview

//todo add progress bar while load items - minor

public class MyDocsActivity extends BaseLoginActivity implements View.OnClickListener,
        AlertDialogHelper.AlertDialogListener, MyDocsContract.View {

    private static final int DELETE_TAG = 1;
    private static final String TAG = DEV_TAG + MyDocsActivity.class.getSimpleName();
    private RecyclerView recyclerView;

    private List<MyDocsResponse.MyDoc> dataList;
    private List<MyDocsResponse.MyDoc> multiSelectDataList;
    private RecyclerViewAdapter adapter;
    private ActionMode mActionMode;
    boolean isMultiSelect = false;
    AlertDialogHelper alertDialogHelper;

    @Inject
    MyDocsPresenter mPresenter;

    @Inject
    MyDocsHttpClient httpClient;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.activity_my_docs);

        initToolbar();

        alertDialogHelper = new AlertDialogHelper(this);

        Button signInBtn = findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(this);

        initRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();  //prevent leaking activity in
        // case presenter is orchestrating a long running task
    }

    private void openCheck() {
        //todo
    }

    public void refreshAdapter() {
        adapter.notifyDataSetChanged();
    }

    public void multiSelect(int position) {
        if (mActionMode != null) {
            if (multiSelectDataList.contains(dataList.get(position))) {
                multiSelectDataList.remove(dataList.get(position));
            } else {
                multiSelectDataList.add(dataList.get(position));
            }
            if (multiSelectDataList.size() > 0) {
                mActionMode.setTitle("" + multiSelectDataList.size());
            } else {
                mActionMode.setTitle("");
            }
            refreshAdapter();
        }
    }


    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        AutoFitGridLayoutManager layoutManager =
                new AutoFitGridLayoutManager(this, 500);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        dataList = new ArrayList<>();
        multiSelectDataList = new ArrayList<>();
        adapter = new RecyclerViewAdapter(dataList, multiSelectDataList);
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

        //todo particular move code to presenter
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (isMultiSelect) {
                                    multiSelect(position);
                                } else {
                                    //todo
                                    Toast.makeText(getApplicationContext(), "Details Page",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                if (!isMultiSelect) {
                                    multiSelectDataList = new ArrayList<>();
                                    isMultiSelect = true;

                                    if (mActionMode == null) {
                                        mActionMode = startActionMode(mActionModeCallback);
                                    }
                                }

                                multiSelect(position);
                            }
                        }));
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

                adapter.notifyDataSetChanged();

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
    public void showAllDocs(List<MyDocsResponse.MyDoc> data) {

    }

    @Override
    public void choseDocs(List<MyDocsResponse.MyDoc> choseDocs) {

    }

    @Override
    public void deleteDocs(List<MyDocsResponse.MyDoc> deleteDocs) {

    }

    @Override
    public void showError(int errorMessageRes) {
        InfoSnackbarUtil.showError(errorMessageRes, mRootView);
    }

    @Override
    public void addNewLoadedDocs(List<MyDocsResponse.MyDoc> newLoadedDocs) {
        dataList.addAll(newLoadedDocs);
        adapter.notifyItemInserted(dataList.size() - 1);
    }
}

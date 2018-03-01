package com.ashomok.imagetotext.my_docs;

/**
 * Created by iuliia on 1/10/18.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.my_docs.get_my_docs_task.MyDocsHttpClient;
import com.ashomok.imagetotext.my_docs.get_my_docs_task.MyDocsResponse;
import com.ashomok.imagetotext.ocr.ocr_task.OcrResult;
import com.ashomok.imagetotext.utils.NetworkUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.ashomok.imagetotext.utils.FirebaseUtils.getIdToken;
import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;
import static dagger.internal.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link MyDocsActivity}), retrieves the data and updates
 * the UI as required.
 * <p>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */
public class MyDocsPresenter implements MyDocsContract.Presenter {

    public static final String TAG = DEV_TAG + MyDocsPresenter.class.getSimpleName();
    @NonNull
    private final MyDocsHttpClient httpClient;
    @Nullable
    private MyDocsContract.View view;

    @Inject
    Context context;

    private String idToken;
    private String startCursor;


    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    MyDocsPresenter(@NonNull MyDocsHttpClient httpClient) {
        this.httpClient = checkNotNull(httpClient);
    }

    @Override
    public void takeView(MyDocsContract.View myDocsActivity) {
        view = myDocsActivity;
        initWithDocs();
    }

    void initWithDocs() {
        if (view != null) {
            if (isOnline()) {
                startCursor = null;
                view.clearDocsList();

                if (idToken == null) {
                    //get user IdToken
                    Single<Optional<String>> idTokenSingle = getIdToken();

                    view.showProgress(true);
                    idTokenSingle
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    optionalToken -> {
                                        view.showProgress(false);
                                        if (optionalToken.isPresent()) {
                                            idToken = optionalToken.get();
                                            callApiForDocs(httpClient, idToken, startCursor);
                                        }
                                    }, throwable -> {
                                        view.showProgress(false);
                                        throwable.printStackTrace();
                                        view.showError(R.string.unable_identify_user);
                                    });
                } else {
                    callApiForDocs(httpClient, idToken, startCursor);
                }
            } else {
                view.showError(R.string.no_internet_connection);
            }
        }
    }

    private void callApiForDocs(MyDocsHttpClient httpClient, String idToken, String startCursor) {
        if (view != null) {
            Single<MyDocsResponse> myDocs =
                    httpClient.getMyDocs(idToken, startCursor);

            view.showProgress(true);
            myDocs
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            myDocsResponce -> {
                                view.showProgress(false);
                                MyDocsResponse.Status status = myDocsResponce.getStatus();
                                if (status.equals(MyDocsResponse.Status.USER_NOT_FOUND)) {
                                    view.showError(R.string.unable_identify_user);
                                } else if (status.equals(MyDocsResponse.Status.UNKNOWN_ERROR)) {
                                    view.showError(R.string.unknown_error);
                                } else if (status.equals(MyDocsResponse.Status.OK)) {
                                    updateCursor(myDocsResponce.getEndCursor());
                                    view.addNewLoadedDocs(myDocsResponce.getRequestList());
                                } else {
                                    Log.e(TAG, "Unknown status received");
                                    view.showError(R.string.unknown_error);
                                }
                            },
                            throwable -> {
                                view.showProgress(false);
                                throwable.printStackTrace();
                                view.showError(R.string.unknown_error);
                            });
        }
    }

    private boolean loadingCompleted() {
        return startCursor == null;
    }

    private void updateCursor(String cursor) {
        startCursor = cursor;
    }

    private boolean isOnline() {
        return NetworkUtils.isOnline(context);
    }

    @Override
    public void dropView() {
        view = null;
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    @Override
    public void loadMoreDocs() {
        if (view != null) {
            if (isOnline()) {
                if (!loadingCompleted()) {
                    MyDocsHttpClient httpClient = MyDocsHttpClient.getInstance();
                    callApiForDocs(httpClient, idToken, startCursor);
                }
            } else {
                view.showError(R.string.no_internet_connection);
            }
        }
    }

    void deleteDocs(List<OcrResult> multiSelectDataList) {
        if (view != null) {
            if (isOnline()) {
                List<Long> ids = Stream.of(multiSelectDataList)
                        .map(OcrResult::getId)
                        .collect(Collectors.toList());

                startCursor = null;
                //delete and reload my docs
                Single<MyDocsResponse> myDocs = httpClient
                        .deleteMyDocs(ids)
                        .andThen(httpClient.getMyDocs(idToken, startCursor));

                view.showProgress(true);
                myDocs.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                myDocsResponse -> {
                                    view.showProgress(false);
                                    MyDocsResponse.Status status = myDocsResponse.getStatus();
                                    if (status.equals(MyDocsResponse.Status.USER_NOT_FOUND)) {
                                        view.showError(R.string.unable_identify_user);
                                    } else if (status.equals(MyDocsResponse.Status.UNKNOWN_ERROR)) {
                                        view.showError(R.string.unknown_error);
                                    } else if (status.equals(MyDocsResponse.Status.OK)) {
                                        //ok
                                        view.clearDocsList(); //empty view
                                        view.addNewLoadedDocs(myDocsResponse.getRequestList()); //reload
                                        updateCursor(myDocsResponse.getEndCursor()); //update cursor
                                        view.showInfo(R.string.deleted);
                                    } else {
                                        view.showError(R.string.unknown_error);
                                    }
                                },
                                throwable -> {
                                    view.showProgress(false);
                                    throwable.printStackTrace();
                                    view.showError(R.string.unknown_error);
                                });
            } else {
                view.showError(R.string.no_internet_connection);
            }
        }
    }
}

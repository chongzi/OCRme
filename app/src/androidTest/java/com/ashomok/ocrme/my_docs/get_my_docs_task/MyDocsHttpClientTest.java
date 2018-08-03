package com.ashomok.ocrme.my_docs.get_my_docs_task;

import com.ashomok.ocrme.utils.FirebaseAuthUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Completable;
import io.reactivex.Single;

import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 12/19/17.
 */
public class MyDocsHttpClientTest {

    private MyDocsHttpClient client;
    private static final String TAG = DEV_TAG + MyDocsHttpClientTest.class.getSimpleName();

    @Before
    public void init() {
        client = MyDocsHttpClient.getInstance();
    }

    @Test
    public void getMyDocs() {

        try {
            Single<MyDocsResponse> single = client.getMyDocs(
                    FirebaseAuthUtil.getIdToken().blockingGet().get(), null);
            MyDocsResponse response = single.blockingGet();

            Assert.assertTrue(response.getRequestList().size() > 0);

            for (int i = 0; i < response.getRequestList().size(); i++) {
                Assert.assertTrue(response.getRequestList().get(i).getTextResult().length() > 0);
            }
        } catch (NoSuchElementException exception) {
            throw new AssertionError(
                    "Test not failed but you need to Login to the app for testing this.");
        }

    }

    @Test
    public void deleteMyDocs() {

        //obtain data
        Single<MyDocsResponse> singleStart = client.getMyDocs(
                FirebaseAuthUtil.getIdToken().blockingGet().get(), null);
        MyDocsResponse responseStart = singleStart.blockingGet();
        Long id = responseStart.getRequestList().get(0).getId();

        boolean contains =
                responseStart.getRequestList().stream().anyMatch(
                        ocrResult -> ocrResult.getId().equals(id));
        Assert.assertTrue(contains);


        //delete one
        List<Long> inputData = new ArrayList<>();
        inputData.add(id);
        Completable res = client.deleteMyDocs(inputData);
        res.blockingGet();


        //check does response contains deleted
        Single<MyDocsResponse> singleEnd = client.getMyDocs(
                FirebaseAuthUtil.getIdToken().blockingGet().get(), null);
        MyDocsResponse responseEnd = singleEnd.blockingGet();

        boolean containsDeleted =
                responseEnd.getRequestList().stream().anyMatch(
                        ocrResult -> ocrResult.getId().equals(id));

        Assert.assertFalse(containsDeleted);
    }
}
package com.ashomok.ocrme.my_docs.get_my_docs_task;

import com.ashomok.ocrme.utils.FirebaseAuthUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

        Single<MyDocsResponse> single = client.getMyDocs(
                FirebaseAuthUtil.getIdToken().blockingGet().get(), null);
        MyDocsResponse response = single.blockingGet();

        Assert.assertTrue(response.getRequestList().size() > 0);

        for (int i = 0; i < response.getRequestList().size(); i++) {
            Assert.assertTrue(response.getRequestList().get(i).getTextResult().length() > 0);
        }
    }

    @Test
    public void deleteMyDocs() throws Exception {
        List<Long> inputData = new ArrayList<>();
        inputData.add(Long.parseLong(String.valueOf("5637641986899968")));
        Completable res = client.deleteMyDocs(inputData);
        Throwable throwable = res.blockingGet();
    }
}
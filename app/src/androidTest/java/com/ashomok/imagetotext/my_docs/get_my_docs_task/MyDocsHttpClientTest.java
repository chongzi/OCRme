package com.ashomok.imagetotext.my_docs.get_my_docs_task;

import com.ashomok.imagetotext.ocr.ocr_task.OcrResponse;
import com.ashomok.imagetotext.utils.FirebaseAuthUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

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

        Single<MyDocsResponse> single = client.myDocs(
                FirebaseAuthUtil.getIdToken().blockingGet().get(), null);
        MyDocsResponse response = single.blockingGet();

        Assert.assertTrue(response.getRequestList().size() > 0);

        for (int i = 0; i < response.getRequestList().size(); i++) {
            Assert.assertTrue(response.getRequestList().get(i).getTextResult().length() > 0);
        }
    }
}
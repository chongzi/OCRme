package com.ashomok.ocrme.my_docs.get_my_docs_task;

import android.support.test.filters.MediumTest;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.ashomok.ocrme.ocr.ocr_task.OcrResult;
import com.ashomok.ocrme.utils.Repeat;
import com.ashomok.ocrme.utils.RepeatRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import io.reactivex.Completable;
import io.reactivex.Single;

import static com.ashomok.ocrme.utils.FirebaseUtils.getIdToken;
import static com.ashomok.ocrme.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 12/19/17.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class MyDocsHttpClientTest {

    @Rule
    public RepeatRule repeatRule = new RepeatRule();

    private MyDocsHttpClient client;
    private String idToken;
    private MyDocsResponse response;
    private static final String TAG = DEV_TAG + MyDocsHttpClientTest.class.getSimpleName();

    @Before
    public void init() {
        client = MyDocsHttpClient.getInstance();

        idToken = null;
        try {
            idToken = getIdToken().blockingGet().get();
        } catch (NoSuchElementException exception) {
            throw new AssertionError("Test not failed, but needs authentificate user");
        }
        Single<MyDocsResponse> single = client.getMyDocs(idToken, null);
        response = single.blockingGet();
    }

    @Test
    public void getMyDocs() {
        try {
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
    @Repeat(10)
    @MediumTest
    public void getMyDocsWithNoDuplicates() {

        //obtain data
        String startCursor = null;

        Single<MyDocsResponse> single = client.getMyDocs(idToken, startCursor);
        MyDocsResponse response = single.blockingGet();
        List<OcrResult> ocrResults = new ArrayList<>(response.getRequestList());

        startCursor = response.getEndCursor();

        while (startCursor != null) {
            single = client.getMyDocs(idToken, startCursor);
            response = single.blockingGet();
            ocrResults.addAll(response.getRequestList());
            startCursor = response.getEndCursor();
        }

        //check does data has duplicated
        Map<String, Integer> mappedData = new HashMap<>();
        for (int i = 0; i < ocrResults.size(); i++) {
            String key = ocrResults.get(i).getTimeStamp();
            Integer frequency = mappedData.get(key);
            mappedData.put(key, frequency == null ? 1 : frequency + 1);
        }

        Map<String, Integer> duplicates = mappedData.entrySet()
                .stream().filter(frequency -> frequency.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        Assert.assertTrue(duplicates.size() == 0);
    }


    @Test
    public void deleteMyDocs() {

        //obtain data
        Long id = response.getRequestList().get(0).getId();

        boolean contains = response.getRequestList().stream().anyMatch(
                ocrResult -> ocrResult.getId().equals(id));
        Assert.assertTrue(contains);


        //delete one
        List<Long> inputData = new ArrayList<>();
        inputData.add(id);
        Completable res = client.deleteMyDocs(inputData);
        res.blockingGet();

        //check does response contains deleted
        Single<MyDocsResponse> withoutDeletedSingle = client.getMyDocs(idToken, null);
        MyDocsResponse withoutDeleted = withoutDeletedSingle.blockingGet();

        boolean containsDeleted =
                withoutDeleted.getRequestList().stream().anyMatch(
                        ocrResult -> ocrResult.getId().equals(id));

        Assert.assertFalse(containsDeleted);
    }
}
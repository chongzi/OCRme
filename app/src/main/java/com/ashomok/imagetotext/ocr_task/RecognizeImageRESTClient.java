package com.ashomok.imagetotext.ocr_task;

import android.support.annotation.Nullable;

import com.ashomok.imagetotext.Settings;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


/**
 * Created by Iuliia on 10.12.2015.
 */
public final class RecognizeImageRESTClient extends RecognizeImageAsyncTask {

    private static final String TAG = RecognizeImageRESTClient.class.getSimpleName();
    private final List<String> languages;
    private final String image_path;
    final private String URL = Settings.OCR_POST_URL;


    public RecognizeImageRESTClient(String image_path, @Nullable List<String> languages) {
        this.image_path = image_path;
        this.languages = languages;
    }

    @Override
    protected OCRResult doInBackground(Void... params) {
        OCRResult result = new OCRResult();
        try {
            String text = doPOST(image_path);
            result.setText(text);

        } catch (ResourceAccessException e) {
            result.setError("Can not access file. File not found.");

        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().length() > 0) {
                result.setError(e.getMessage());
            } else {
                result.setError("Unknown error");
            }
        }
        return result;
    }

    private String doPOST(String image_path) {
        String result;
        RestTemplate restTemplate = new RestTemplate(true);

//tell RestTemplate to POST with UTF-8 encoding?
//        restTemplate.getMessageConverters()
//                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("file", new FileSystemResource(image_path));
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL);
        if (languages != null && languages.size() > 0) {
            for (String language : languages) {
                builder.queryParam("language", language);
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
                params, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.POST,
                requestEntity,
                String.class);

        HttpStatus statusCode = responseEntity.getStatusCode();

        if (statusCode == HttpStatus.ACCEPTED) {
            result = responseEntity.getBody();
        } else {
            result = "";
        }
        return result;
    }
}

package com.ashomok.imagetotext.ocr_task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * Created by Iuliia on 10.12.2015.
 */
public final class RecognizeImageRESTClient extends RecognizeImageAsyncTask {

    private static final String TAG = RecognizeImageRESTClient.class.getSimpleName();
    private static final int MaxFileSizeInMb = 2;
    private final List<String> languages;
    private final Uri image;
    final private String URL = Settings.OCR_POST_URL;


    public RecognizeImageRESTClient(Uri image, @Nullable List<String> languages) {
        this.image = image;
        this.languages = languages;
    }

    @Override
    protected OCRResult doInBackground(Void... params) {
        File file = new File(image.getPath());
        long fileSizeInBytes = file.length();
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInBytes / 1024 / 1024;
        String text;
        OCRResult result = new OCRResult();
        try {
            if (fileSizeInMB < MaxFileSizeInMb) {
                text = doPOST(file);
            } else {

                File decrised = decrese(file);
                text = doPOST(decrised);
            }
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

    private File decrese(File file) throws IOException {
        Bitmap bitmap = toBitmap(file);
        File overriden = new File(image.getPath());
        FileOutputStream fooStream = new FileOutputStream(overriden, false); // false to overwrite.
        byte[] myBytes = toByteArray(bitmap);
        fooStream.write(myBytes);
        fooStream.close();
        return overriden;
    }

    private String doPOST(File file) {
        String result;
        RestTemplate restTemplate = new RestTemplate(true);

//tell RestTemplate to POST with UTF-8 encoding?
//        restTemplate.getMessageConverters()
//                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("file", new FileSystemResource(file));
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

    private byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap toBitmap(File image) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        bitmap = scaleBitmapDown(bitmap, 1200);
        return bitmap;
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
}

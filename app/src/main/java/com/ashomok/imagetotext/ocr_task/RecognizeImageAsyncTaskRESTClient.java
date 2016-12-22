package com.ashomok.imagetotext.ocr_task;

import java.io.File;


/**
 * Created by Iuliia on 10.12.2015.
 */
public final class RecognizeImageAsyncTaskRESTClient extends RecognizeImageAsyncTask {


    private String img_path;

    public RecognizeImageAsyncTaskRESTClient(String img_path) {
        this.img_path = img_path;
    }


    @Override
    protected String doInBackground(Void... params) {

//        RestTemplate restTemplate = new RestTemplate(true);
//
//        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
//        map.add("file", new FileSystemResource(new File(img_path)));
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
//                map, headers);
//        ResponseEntity<String[]> responseEntity = restTemplate.exchange(
//                URL, HttpMethod.POST, requestEntity,
//                String[].class);
//
//        HttpStatus statusCode = responseEntity.getStatusCode();
//        String[] result;
//        if (statusCode == HttpStatus.ACCEPTED) {
//            result = responseEntity.getBody();
//        } else {
//            result = new String[0];
//        }
        return "mock result";
    }

}

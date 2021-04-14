package com.lk.userapp.fcmHelper;


import android.os.AsyncTask;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class FCmClient extends AsyncTask<String,String,String> {

    public void sendFCMMessage(String token, String title, String message) {
//            HttpResponse<String> response = Unirest.post("https://fcm.googleapis.com/fcm/send")
//                    .header("authorization", "key=AAAAoqKjsrw:APA91bEOo49oXKmgsTv43uHWh3XgmifRJVLsQZnACIPQsUyeiDtZUt_fUMdVwX65KqhbkGZ_6J5ZvlYogrMz5MwNk71pklwYEUXJPzEJmlrS_K2GEsRAO7UBfF7jPbKcyVxfRyPfthTt")
//                    .header("content-type", "application/json")
//                    .header("cache-control", "no-cache")
//                    .header("postman-token", "0d532394-3024-a720-d3ea-2e98f68d1ead")
//                    .body("{\r\n   \"to\":\"c7E52aHQQa6SxKuSF6A_9d:APA91bFrQIiyfkOFYQfGttHrhzQWdZ1YKbl72aH5Ia4AwYkyTMuuS7ReS9qTfiIWrUjlOgxWAcYqRQFhmHuTHkmnJdOe6GB-OmjXQrlPC-dp6yTi0geza3DvBbycoMSEtDVFNiYbcXjB\",\r\n   \"notification\":{\r\n      \"sound\":\"default\",\r\n      \"body\":\"adooooo\",\r\n      \"title\":\"test title\",\r\n      \"content_available\":true,\r\n      \"priority\":\"high\"\r\n   }\r\n}")
//                    .asString();

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\r\n   \"to\":\""+token+"\",\r\n   \"notification\":{\r\n      \"sound\":\"default\",\r\n      \"body\":\""+message+"\",\r\n      \"title\":\""+title+"\",\r\n      \"content_available\":true,\r\n      \"priority\":\"high\"\r\n   }\r\n}");
            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(body)
                    .addHeader("authorization", "key=AAAAN-IdNVA:APA91bF6wJSIZHx05DNN0o9ESDvwbgk2Bt7ueu_GInCav3gGF9qTBHYCFxw2RdzBnMWFWpqlmoFXhQpMw1b-ypxvPX5p0G6lpK1fTaatVSNtQGQcgAOCdrxr9__cIvLDW5URgRaMNium")
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .build();

            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    @Override
    protected String doInBackground(String... strings) {
        sendFCMMessage(strings[0],strings[1],strings[2]);
        return null;
    }
}

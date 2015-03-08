package com.simonkenny.environfuse;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by simonkenny on 08/03/15.
 */
public class HttpInterface {

    private static final String POST_URL = "http://alt-surftens.rhcloud.com/postmessage";

    public static class HttpAsyncTaskPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... jsonObjs) {
            return HttpInterface.POST(POST_URL, jsonObjs[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO : something
            Log.d("HttpInterface", "Finished post, result: " + result);
        }
    }

    public static class HttpAsyncTaskGetWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.d("HttpAsyncTaskGetWeather","Sending HTTP GET request");
            return HttpInterface.GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("HttpAsyncTaskGetWeather","Got weather data from API");
            Log.d("HttpAsyncTaskGetWeather","Data: "+result);
            // convert JSON string data to JSON object
            try {
                JSONObject json = new JSONObject(result);
                AppSupport.getInstance().setWeather(json);
            } catch (JSONException e) {
                Log.d("HttpAsyncTask","JSON parse error!");
                e.printStackTrace();
            }
        }
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            Log.d("HttpInterface","Making HTTP GET request to: "+url);
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static String POST(String url, String jsonAsString) {
        InputStream inputStream = null;
        String result = "";
        try {
            // create HttpClient and post request object
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);

            post.setEntity(new StringEntity(jsonAsString, "UTF8"));
            post.setHeader(new BasicHeader("Content-Type", "application/json;charset=UTF-8"));

            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(post);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
            }
        } catch(Exception e) {
            Log.d("HttpInterface::POST", e.getLocalizedMessage());
        }
        return result;
    }
}

package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import com.loopj.android.http.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;



public class TSTask extends AsyncTask<String, String, List<Model>>{

    private Context mContext;
    private TSTask.JsonListener mListener;

    public TSTask(Context context, TSTask.JsonListener listener) {
        mContext = context;
        mListener = listener;
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected void onPostExecute(List<Model> feeds) {
        if (mListener == null) {
            return;
        }

        if (feeds.size() > 0) {

            mListener.onStreamResponse(feeds);
        } else {
            mListener.onStreamError(feeds);
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    protected List<Model> doInBackground(String... strings) {
        String url = strings[0];
        List<Model> feeds = new ArrayList<>();

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                String firstResponse = convertStreamToString(instream);
                JSONObject object = new JSONObject(firstResponse);
                JSONArray feedsObj = object.getJSONArray("feeds");

                instream.close();

                for (int i = 0;i < feedsObj.length(); i++) {
                    try {
                        Model feed = new Model();

                        JSONObject obj = (JSONObject) feedsObj.get(i);
                        feed.field1 = obj.getInt("field1");
                        feed.field2 = obj.getInt("field2");
                        feeds.add(feed);
                    } catch (Exception e) {
                    }
                }


                return feeds;
            }


        } catch (Exception e) {
        }

        return null;
    }

    public interface JsonListener {
        void onStreamResponse(List<Model> feeds);

        void onStreamError(List<Model> feeds);
    }
}

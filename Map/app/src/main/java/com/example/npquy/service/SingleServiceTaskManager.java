package com.example.npquy.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public abstract class SingleServiceTaskManager extends
        AsyncTask<String, Integer, String> {

    public static final int POST_TASK = 1;
    public static final int GET_TASK = 2;
    public static final String URL = "http://104.42.107.187:82/api/";

    private static final String TAG = "WebServiceTaskManager";
    // connection timeout, in milliseconds (waiting to connect)
    private static final int CONN_TIMEOUT = 6000;

    // socket timeout, in milliseconds (waiting for data)
    private static final int SOCKET_TIMEOUT = 10000;

    private int taskType = GET_TASK;
    private Activity mContext = null;

    private StringEntity paramsNoName;

    private String processMessage = "Processing...";

    private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

    private ProgressDialog pDlg = null;
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public SingleServiceTaskManager(int taskType, Activity mContext,
                                    String processMessage) {

        this.taskType = taskType;
        this.mContext = mContext;
        this.processMessage = processMessage;
    }

    public SingleServiceTaskManager(int taskType, Activity mContext) {
        this.taskType = taskType;
        this.mContext = mContext;
    }

    public void addNameValuePair(String name, String value) {
        if(name.isEmpty()) {
            try {
                paramsNoName = new StringEntity(value);
                paramsNoName.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        params.add(new BasicNameValuePair(name, value));
    }

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        String result = "";

        HttpResponse response = doResponse(url);

        if (response == null) {
            System.out.println(result + " null");
            return result;
        } else {

            try {
                result = inputStreamToString(response.getEntity().getContent());

            } catch (IllegalStateException e) {
                Log.e(SingleServiceTaskManager.TAG, e.getLocalizedMessage(), e);

            } catch (IOException e) {
                Log.e(SingleServiceTaskManager.TAG, e.getLocalizedMessage(), e);
            }

        }
        return result;
    }

    @SuppressWarnings("deprecation")
    private void showProgressDialog() {
        pDlg = new ProgressDialog(mContext);
        pDlg.setMessage(processMessage);
        pDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDlg.setProgressDrawable(mContext.getWallpaper());
        pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDlg.setCancelable(false);
        if(processMessage.length() > 0) {
            pDlg.show();
        }

       /* pDlg = new ProgressDialog(mContext, R.style.MyTheme);
        pDlg.setMessage(processMessage);
        pDlg.setCancelable(false);
        pDlg.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if(processMessage.length() > 0) {
            pDlg.show();
        }*/
    }

    @Override
    protected void onPreExecute() {
        showProgressDialog();
    }

    @Override
    protected void onPostExecute(String response) {
        pDlg.dismiss();
        handleResponse(response);
    }

    // Establish connection and socket (data retrieval) timeouts
    private HttpParams getHttpParams() {

        HttpParams htpp = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
        HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);

        return htpp;
    }

    private HttpResponse doResponse(String url) {

        // Use our connection and data timeouts as parameters for our
        // DefaultHttpClient
        HttpClient httpclient = new DefaultHttpClient(getHttpParams());

        HttpResponse response = null;

        try {
            switch (taskType) {

                case POST_TASK:
                    HttpPost httpPost = new HttpPost(url);
                    // Add parameters
    /*                httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");*/
                    if(params.isEmpty()) {
                        httpPost.setEntity(new UrlEncodedFormEntity(params));
                    }else {
                        httpPost.setEntity(paramsNoName);
                    }
                    Log.i(TAG,"HTTP Entiry : " + convertStreamToString(httpPost.getEntity().getContent()));
                    int versionPost = StopHandler.createInstance().getVersion();
                    Log.i(TAG,"version 1 " + versionPost);
                    Thread.currentThread().sleep(1000);
                    Log.i(TAG, "version 2 " + StopHandler.createInstance().getVersion());
                    if (versionPost == StopHandler.createInstance().getVersion()) {
                        response = httpclient.execute(httpPost);
                    } else {
                        return response;
                    }
                    break;
                case GET_TASK:
                    if (!params.isEmpty()) {
                        String paramString = URLEncodedUtils.format(params, "utf-8");
                        url += "?" + paramString;
                    }
                    HttpGet httpget = new HttpGet(url);
                    Log.i(TAG,"HTTP Entiry : " + url);
                    int versionGet = StopHandler.createInstance().getVersion();
                    Thread.currentThread().sleep(1000);
                    if (versionGet == StopHandler.createInstance().getVersion()) {
                        response = httpclient.execute(httpget);
                    } else {
                        return response;
                    }
                    break;
            }
        }
        catch (Exception e) {
            mContext.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mContext, "Connection to server refused! Please check your connection!", Toast.LENGTH_SHORT).show();

                }});
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

        return response;
    }

    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    private String inputStreamToString(InputStream is) {

        String line = "";
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try {
            // Read response until the end
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            Log.e(SingleServiceTaskManager.TAG, e.getLocalizedMessage(), e);
        }

        // Return full string
        return total.toString();
    }

    public abstract void handleResponse(String response);
}

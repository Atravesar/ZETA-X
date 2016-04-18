package com.example.npquy.service;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

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

public class WebServiceThread implements Callable<String> {

    private WebServiceMethod taskType = WebServiceMethod.GET;

    private StringEntity paramsNoName;

    private String processMessage = "Processing...";

    private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

    private String response;

    private String url;

    public WebServiceMethod getTaskType() {
        return taskType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTaskType(WebServiceMethod taskType) {
        this.taskType = taskType;
    }

    public StringEntity getParamsNoName() {
        return paramsNoName;
    }

    public void setParamsNoName(StringEntity paramsNoName) {
        this.paramsNoName = paramsNoName;
    }

    public String getProcessMessage() {
        return processMessage;
    }

    public void setProcessMessage(String processMessage) {
        this.processMessage = processMessage;
    }

    public ArrayList<NameValuePair> getParams() {
        return params;
    }

    public void setParams(ArrayList<NameValuePair> params) {
        this.params = params;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public WebServiceThread() {
        super();
    }

    public void addNameValuePair(String name, String value) {
        if (name.isEmpty()) {
            try {
                paramsNoName = new StringEntity(value);
                paramsNoName.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                        "application/json"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        params.add(new BasicNameValuePair(name, value));
    }

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
                // Log.e(WebServiceTaskManager.TAG, e.getLocalizedMessage(), e);

            } catch (IOException e) {
                // Log.e(WebServiceTaskManager.TAG, e.getLocalizedMessage(), e);
            }

        }
        return result;
    }

    private HttpParams getHttpParams() {

        HttpParams htpp = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(htpp, Const.CONN_TIMEOUT);
        HttpConnectionParams.setSoTimeout(htpp, Const.SOCKET_TIMEOUT);

        return htpp;
    }

    private HttpResponse doResponse(String url) {

        // Use our connection and data timeouts as parameters for our
        // DefaultHttpClient
        HttpClient httpclient = new DefaultHttpClient(getHttpParams());

        HttpResponse response = null;

        try {
            switch (taskType) {
                case POST:
                    HttpPost httpPost = new HttpPost(url);
                    // Add parameters
    /*                httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");*/
                    if(params.isEmpty()) {
                        httpPost.setEntity(new UrlEncodedFormEntity(params));
                    }else {
                        httpPost.setEntity(paramsNoName);
                    }
                    Log.i("WebServiceThread","HTTP Entiry : " + convertStreamToString(httpPost.getEntity().getContent()));
                    response = httpclient.execute(httpPost);
                case GET:
                    if (!params.isEmpty()) {
                        String paramString = URLEncodedUtils
                                .format(params, "utf-8");
                        url += "?" + paramString;
                    }
                    HttpGet httpget = new HttpGet(url);
                    Log.i("WebService", "HTTP Entiry : " + url);
                    int num = StopHandler.createInstance().getVersion();
                    Thread.currentThread().sleep(1000);
                    if (num == StopHandler.createInstance().getVersion()) {
                        response = httpclient.execute(httpget);
                    } else {
                        return response;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
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
            // Log.e(WebServiceTaskManager.TAG, e.getLocalizedMessage(), e);
        }

        // Return full string
        return total.toString();
    }

    @Override
    public String call() throws Exception {
        // TODO Auto-generated method stub

        return doInBackground(new String[]{url});
    }

	/*
	* private static String getAddressNewWay() {
		// StopHandler.createInstance().setRunning(0);
		String result = "";
		String url = Const.URL + "SearchAddress";
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				Const.CORE_POOL_SIZE, Const.CORE_POOL_SIZE,
				Const.KeepAliveTime, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		CompletionService<String> pool = new ExecutorCompletionService<String>(
				threadPoolExecutor);

		WebServiceThread webServiceThread = new WebServiceThread();
		webServiceThread.setTaskType(WebServiceMethod.GET);
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair("prefix", "se137jj"));
		webServiceThread.setParams(postParams);
		webServiceThread.setUrl(url);
		pool.submit(webServiceThread);
		threadPoolExecutor.shutdown();
		try {
			result = pool.take().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}*/

}

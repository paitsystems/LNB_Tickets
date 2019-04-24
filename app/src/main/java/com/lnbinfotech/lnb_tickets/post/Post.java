package com.lnbinfotech.lnb_tickets.post;

import com.lnbinfotech.lnb_tickets.constant.Constant;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;

// Created by lnb on 8/11/2016.

public class Post {
    static public String POST(String url) {
        String responseBody = null;
        /*try
        {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpParams hp = new BasicHttpParams();
            HttpClient httpclient = new DefaultHttpClient(hp);
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = httpclient.execute(httpget, responseHandler);
        }catch (HttpHostConnectException e){
           e.printStackTrace();
        }
        catch (ClientProtocolException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        Log.d("responseBody.toString()",responseBody.toString());
        return responseBody.toString();*/

       /* final DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(u);
        try{
            HttpResponse response = client.execute(httpget);
            final int statuscode = response.getStatusLine().getStatusCode();
            if(statuscode != HttpStatus.SC_OK){
                return null;
            }
            final HttpEntity entity = response.getEntity();
            responseBody = new BasicResponseHandler().handleResponse(response);
        }catch(IOException e){
            httpget.abort();
            e.printStackTrace();
        }
        return  responseBody;*/

        DefaultHttpClient httpClient = null;
        try {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, Constant.TIMEOUT_CON);
            HttpConnectionParams.setSoTimeout(httpParams, Constant.TIMEOUT_SO);
            httpClient = new DefaultHttpClient(httpParams);
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = httpClient.execute(httpget, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
            Constant.showLog("POST - Timeout-" + url);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.getConnectionManager().shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
                //writeLog("showCheckoutOrderDetailsClass_finally_"+e.getMessage());
            }
        }
        return responseBody;
    }
}

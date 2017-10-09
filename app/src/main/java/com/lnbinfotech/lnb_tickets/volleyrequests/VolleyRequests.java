package com.lnbinfotech.lnb_tickets.volleyrequests;

//Created by lnb on 9/15/2017.

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lnbinfotech.lnb_tickets.constant.AppSingleton;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.interfaces.ServerCallback;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.parse.ParseJSON;

public class VolleyRequests {

    private Context context;
    private String writeFilename = "Write.txt";

    public VolleyRequests(Context _context) {
        this.context = _context;
    }

    public void checkVersion(String url, final ServerCallback callback) {
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Constant.showLog(response);
                        response = response.replace("\\", "");
                        response = response.replace("''", "");
                        response = response.substring(1, response.length() - 1);
                        String _data = new ParseJSON(response).parseVersion();
                        callback.onSuccess(_data);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure("Error");
                        Constant.showLog(error.getMessage());
                        writeLog("checkVersion_"+error.getMessage());
                    }
                }
        );
        AppSingleton.getInstance(context).addToRequestQueue(request, "VERSION");
    }

    public void loadTicketData(String url, final ServerCallback callback) {
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Constant.showLog(result);
                        result = result.replace("\\", "");
                        result = result.replace("''", "");
                        result = result.substring(1, result.length() - 1);
                        new ParseJSON(result, context).parseAllTicket();
                        callback.onSuccess(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure("Error");
                        Constant.showLog(error.getMessage());
                        writeLog("loadTicketData_"+error.getMessage());
                    }
                }
        );
        AppSingleton.getInstance(context).addToRequestQueue(request, "VERSION");
    }

    public void loadCustomerData(String url, final ServerCallback callback) {
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Constant.showLog(result);
                        result = result.replace("\\", "");
                        result = result.replace("''", "");
                        result = result.substring(1,result.length()-1);
                        new ParseJSON(result, context).parseSMLMASTData();
                        callback.onSuccess(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure("Error");
                        Constant.showLog(error.getMessage());
                        writeLog("loadCustomerData_"+error.getMessage());
                    }
                }
        );
        AppSingleton.getInstance(context).addToRequestQueue(request, "VERSION");
    }

    private void writeLog(String _data){
        new WriteLog().writeLog(context,"VolleyRequest_"+_data);
    }

}

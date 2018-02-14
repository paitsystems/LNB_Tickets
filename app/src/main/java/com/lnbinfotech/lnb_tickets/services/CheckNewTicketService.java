package com.lnbinfotech.lnb_tickets.services;

//Created by lnb on 8/23/2017.

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lnbinfotech.lnb_tickets.FirstActivity;
import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.model.TicketDetailClass;
import com.lnbinfotech.lnb_tickets.parse.ParseJSON;

import java.util.List;

public class CheckNewTicketService extends IntentService{

    public CheckNewTicketService() {
        super(CheckNewTicketService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DBHandler db = new DBHandler(getApplicationContext());
        FirstActivity.pref = getApplicationContext().getSharedPreferences(FirstActivity.PREF_NAME,MODE_PRIVATE);
        int clientAuto = FirstActivity.pref.getInt(getString(R.string.pref_auto),0);
        Constant.showLog("Service Started");
        writeLog(getApplicationContext(),"CheckNewTicketService_onHandleIntent_Service_Started");
        /*String url1 = Constant.ipaddress+"/GetCount?clientAuto="+ FirstActivity.pref.getInt(getString(R.string.pref_auto),0);
        Constant.showLog(url1);
        StringRequest countRequest = new StringRequest(url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Constant.showLog(result);
                        result = result.replace("\\", "");
                        result = result.replace("''", "");
                        result = result.substring(1,result.length()-1);
                        String _data = new ParseJSON(result).parseGetCountData();
                        int lastTotal = FirstActivity.pref.getInt(getString(R.string.pref_ticketTotal),0);
                        if(_data!=null && !_data.equals("0")){
                            String[] data = _data.split("\\^");
                            int _total = Integer.parseInt(data[0]);
                            if(_total>lastTotal){

                                showNotification();
                                writeLog(getApplicationContext(),"CheckNewTicketService_onHandleIntent_Notification_Showed");
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        writeLog(getApplicationContext(),"CheckNewTicketService_onHandleIntent_"+error.getMessage());
                    }
                }
        );*/
        String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
        int auto = db.getAutoTD();
        String url1 = Constant.ipaddress + "/GetAllTicketDetail?clientAuto="+clientAuto+"&auto="+auto+"&type="+type;
        Constant.showLog(url1);
        StringRequest request = new StringRequest(url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Constant.showLog(result);
                        result = result.replace("\\", "");
                        result = result.replace("''", "");
                        result = result.substring(1, result.length() - 1);
                        List<TicketDetailClass> ticketDetailClassList = new ParseJSON(result, getApplicationContext()).parseTicketDetail();
                        if(ticketDetailClassList.size()!=0){
                            showNotification();
                            writeLog("onHandleIntent_notificationShowed");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        writeLog("onHandleIntent_volley_"+error.getMessage());
                        error.printStackTrace();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void showNotification(){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(getApplicationContext(),FirstActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("You Have New Message")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(uri)
                .setAutoCancel(true)
                .build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,notification);
    }

    private void writeLog(Context context, String _data){
        new WriteLog().writeLog(context,_data);
    }

    private void writeLog(String data){
        new WriteLog().writeLog(getApplicationContext(),"CheckNewTicketService_"+data);
    }
}

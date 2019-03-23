package com.lnbinfotech.lnb_tickets.constant;

//Created by lnb on 8/11/2017.

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.braodcasts.AutoUpdateBroadcastReceiver;
import com.lnbinfotech.lnb_tickets.log.WriteLog;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Constant {

    //TODO: Check VersionCode and Version Name
    //TODO: Update VersionCode and Version Name In Version Table in LiveDatabase
    //TODO: Uncomment SendSMS in Webservice

    public static String folder_name = "Ticket",
            log_file_name = "Ticket",
            automailID = "automail@lnbinfotech.com",
            autoamilPass = "auto#456",
            mail_subject = "Log File",
            mail_body = "Find the Attached Log File",
            mailReceipient = "anup.p@paitsystems.com",
            ftp_adress = "ftp.lnbinfotech.com",
            ftp_username = "supportftp@lnbinfotech.com",
            ftp_password = "support$456";
            //ftp_directory = "SMVisit_Indus";

    //TODO: Check Ip AddressAd
    //public static final String ipaddress = "http://172.30.1.38/LNBTCK_16/service.svc";
    public static final String ipaddress = "http://license.lnbinfotech.com/LNBTCK_16/service.svc";
    public static final String imgIpaddress = "http://license.lnbinfotech.com/PAIT/";

    //TODO: Check liveTestFlag 1-Live, 0-Test
    public static int liveTestFlag = 0;

    private Activity activity;
    private Context context;

    private static ProgressDialog pd;

    public static void showLog(String log) {
        if(liveTestFlag==0) {
            Log.d("Log", log);
        }
    }

    public Constant(Activity activity) {
        this.activity = activity;
        pd = new ProgressDialog(activity);
        pd.setCancelable(false);
        pd.setMessage("Please Wait");
    }

    public Constant(Context context) {
        this.context = context;
    }

    public void showPD() {
        if (pd.isShowing()) {
            pd.dismiss();
        } else {
            pd.show();
        }
    }

    public static File checkFolder(String foldername) {
        File extFolder = new File(android.os.Environment.getExternalStorageDirectory() + File.separator + foldername);
        if (!extFolder.exists()) {
            if (extFolder.mkdir()) {
                Constant.showLog("Directory Created");
            }
        }
        return extFolder;
    }

    public void doFinish() {
        activity.finish();
        activity.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    public void setRecurringAlarm() {
        /*Intent intent = new Intent(context, AutoUpdateBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(Constant.liveTestFlag==1) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+AlarmManager.INTERVAL_FIFTEEN_MINUTES,AlarmManager.INTERVAL_FIFTEEN_MINUTES,pendingIntent);
        }else {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 120000, 60*60*1000, pendingIntent);
        }
        Constant.showLog("Alarm Set");
        Date date = new Date(SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        String formatted = formatter.format(date);
        String s = "Alarm Set To " + formatted + "_";
        writeLog(s);*/
    }

    public static void deleteLogFile() {
        File sdFile = checkFolder(folder_name);
        File writeFile = new File(sdFile, log_file_name);
        if (writeFile.exists()) {
            if (writeFile.delete()) {
                showLog("Log File Delete");
            }
        }
    }

    public String getIMEINo1(){
        String imeino="";
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getFirstMethod = telephonyClass.getMethod("getDeviceId", parameter);
            Log.d("Log", getFirstMethod.toString());
            Object[] obParameter = new Object[1];
            obParameter[0] = 0;
            //TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            String first = (String) getFirstMethod.invoke(telephony, obParameter);
            Log.d("Log", "FIRST :" + first);
            obParameter[0] = 1;
            String second = (String) getFirstMethod.invoke(telephony, obParameter);
            Log.d("Log", "SECOND :" + second);
            imeino = first;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imeino;
    }

    private void writeLog(String _data) {
        new WriteLog().writeLog(context, _data);
    }

}

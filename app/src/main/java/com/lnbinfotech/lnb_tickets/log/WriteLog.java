package com.lnbinfotech.lnb_tickets.log;

import android.content.Context;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

// Created by lnb on 8/11/2016.

public class WriteLog {

    public boolean writeLog(Context context, String data) {
        try {
            String todayDateTime = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String str = todayDateTime+"_"+data;
            FileOutputStream fOut = context.openFileOutput(Constant.log_file_name, Context.MODE_APPEND);
            fOut.write(str.getBytes());
            fOut.write(System.getProperty("line.separator").getBytes());
            fOut.close();
            return true;
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        }
    }

    /*File sdFile = SplashActivity.checkFolder(Constant.folder_name);
            File writeFile = new File(sdFile, Constant.log_file_name);

            if(!writeFile.exists()){
                if(writeFile.createNewFile()){
                    Log.d("Log","Log File Created");
                }
            }*/

}

package com.lnbinfotech.lnb_tickets;

//Created by ANUP on 2/14/2018.

import android.app.Application;
import android.util.Log;

import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.log.WriteLog;

public class ApplicationClass extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Constant.showLog("onCreate Called");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Constant.showLog("onTerminate Called");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Constant.showLog("onTrimMemory Called");
        if(level == TRIM_MEMORY_UI_HIDDEN){
            Log.d("Log", "onTrimMemory Called_TRIM_MEMORY_UI_HIDDEN");
            new WriteLog().writeLog(getApplicationContext(),"ApplicationClass_onTrimMemory_Called");
            new Constant(getApplicationContext()).setRecurringAlarm();
        }
    }

}

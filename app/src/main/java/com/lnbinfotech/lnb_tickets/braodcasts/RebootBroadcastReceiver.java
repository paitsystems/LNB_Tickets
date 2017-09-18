package com.lnbinfotech.lnb_tickets.braodcasts;

//Created by lnb on 8/23/2017.

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lnbinfotech.lnb_tickets.constant.Constant;

public class RebootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                new Constant(context).setRecurringAlarm();
            }
        }else{
            Log.d("Log", "Intent Null");
        }
    }
}

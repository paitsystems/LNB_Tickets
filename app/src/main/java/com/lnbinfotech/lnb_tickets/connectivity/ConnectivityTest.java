package com.lnbinfotech.lnb_tickets.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

// Created by lnb on 8/14/2016.

public class ConnectivityTest {

    static public boolean getNetStat(Context context){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info!=null){
            connected = info.isConnected();
        }
        return  connected;
    }
}

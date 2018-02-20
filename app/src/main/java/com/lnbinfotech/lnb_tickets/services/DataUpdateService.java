package com.lnbinfotech.lnb_tickets.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.lnbinfotech.lnb_tickets.FirstActivity;
import com.lnbinfotech.lnb_tickets.R;

import java.util.Timer;
import java.util.TimerTask;

public class DataUpdateService extends Service {

    private Timer timer;
    private Handler handler = new Handler();
    private long period = 30*60*1000;

    public DataUpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirstActivity.pref = getApplicationContext().getSharedPreferences(FirstActivity.PREF_NAME,MODE_PRIVATE);
        if(FirstActivity.pref.contains(getString(R.string.pref_emptype))) {
            String emptype = FirstActivity.pref.getString(getString(R.string.pref_emptype), "NA");
            if (!emptype.equals("NA")) {
                if(emptype.equals("E")){
                    period = 5*60*1000;
                }else{
                    period = 30*60*1000;
                }
            }
        }
        if(timer==null) {
            timer = new Timer();
        }
        timer.scheduleAtFixedRate(new UpdateData(), 0, period);
    }

    private class UpdateData extends TimerTask {

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    getApplicationContext().startService(new Intent(getApplicationContext(), CheckNewTicketService.class));
                }
            });

        }
    }
}

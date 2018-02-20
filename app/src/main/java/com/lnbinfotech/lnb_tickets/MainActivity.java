package com.lnbinfotech.lnb_tickets;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lnbinfotech.lnb_tickets.adapter.ShortDescListAdapter;
import com.lnbinfotech.lnb_tickets.connectivity.ConnectivityTest;
import com.lnbinfotech.lnb_tickets.constant.AppSingleton;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.interfaces.DatabaseUpgradeInterface;
import com.lnbinfotech.lnb_tickets.log.CopyLog;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.mail.GMailSender;
import com.lnbinfotech.lnb_tickets.model.TicketDetailClass;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;
import com.lnbinfotech.lnb_tickets.model.ViewReachToMDClass;
import com.lnbinfotech.lnb_tickets.parse.ParseJSON;
import com.lnbinfotech.lnb_tickets.post.Post;
import com.lnbinfotech.lnb_tickets.services.DataUpdateService;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

// Created by lnb on 8/11/2016.

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DatabaseUpgradeInterface{

    private TextView tv_total, tv_complete, tv_pending;
    private Button btn_view_all, btn_add;
    private AutoCompleteTextView auto;
    private ImageView img_add_new, img_view_all;
    private Constant constant;
    private Toast toast;
    private ListView listView;
    public static int isUpdate = 0;
    private DBHandler db;
    private AdView mAdView;
    private int isDiaShowed = 0;
    private String version = "0";
    private ArrayList<TicketMasterClass> updateList,addList;
    private String writeFilename = "Write.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);

        init();

        btn_add.setOnClickListener(this);
        btn_view_all.setOnClickListener(this);
        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest;
        if(Constant.liveTestFlag==1) {
            adRequest = new AdRequest.Builder().build();
        }else {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("0558B791C50AB34B5650C3C48C9BD15E")
                    .build();
        }
        mAdView.loadAd(adRequest);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode+"."+pInfo.versionName;
            Constant.showLog("App Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            writeLog("MainActivity_"+e.getMessage());
        }

        if(ConnectivityTest.getNetStat(getApplicationContext())) {
            loadData();
        }else{
            setData();
            toast.setText("You Are Offline");
            toast.show();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TicketMasterClass pendingTicketClass = (TicketMasterClass) listView.getItemAtPosition(i);
                Intent intent = new Intent(getApplicationContext(),UpdateTicketActivity.class);
                intent.putExtra("data",pendingTicketClass);
                startActivity(intent);
                overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });

        /*Cursor cursor = SQLiteDatabase.openOrCreateDatabase(":memory:", null).rawQuery("select sqlite_version() AS sqlite_version", null);
        String sqliteVersion = "";
        while(cursor.moveToNext()){
            sqliteVersion += cursor.getString(0);
        }
        cursor.close();
        Constant.showLog("SqliteVersion "+sqliteVersion);*/

        //new Constant(getApplicationContext()).setRecurringAlarm();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isUpdate==1){
            writeLog("MainActivity_onResume_loadData");
            isUpdate = 0;
            constant = new Constant(MainActivity.this);
            isDiaShowed = 0;
            loadData();
        }
        if(mAdView!=null){
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        if(mAdView!=null){
            mAdView.pause();
        }
        isUpdate = 0;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mAdView!=null){
            mAdView.destroy();
        }
        isUpdate = 0;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        showDia(2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                startActivity(new Intent(getApplicationContext(),AddNewTicketActivity.class));
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
            case R.id.btn_view_all:
                //startActivity(new Intent(getApplicationContext(),AllTicketListActivity.class));
                startActivity(new Intent(getApplicationContext(),AllTicketTabPagerActivity.class));
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
            case R.id.img_add_new:
                startActivity(new Intent(getApplicationContext(),AddNewTicketActivity.class));
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
            case R.id.img_view_all:
                startActivity(new Intent(getApplicationContext(),AllTicketTabPagerActivity.class));
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.logout){
            showDia(0);
        }else if(item.getItemId() == R.id.refresh){
            showDia(1);
        }else if(item.getItemId() == R.id.clear_data){
            showDia(7);
        }else if(item.getItemId() == R.id.report_error){
            showDia(6);
        }else if(item.getItemId() == R.id.changePassword){
            startActivity(new Intent(getApplicationContext(),ChangePasswordActivity.class));
            overridePendingTransition(R.anim.enter,R.anim.exit);
        }else if(item.getItemId() == R.id.reach_to_md){
            String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
            if(type.equals("E")) {
                Intent intent = new Intent(getApplicationContext(), ViewReachToMDActivity.class);
                intent.putExtra("type","E");
                startActivity(intent);
            }else if(type.equals("C")){
                Intent intent = new Intent(getApplicationContext(), ReachToMDActivity.class);
                intent.putExtra("type","C");
                intent.putExtra("data",new ViewReachToMDClass());
                startActivity(intent);
            }
            overridePendingTransition(R.anim.enter,R.anim.exit);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void dbUpgraded() {
        Constant.showLog("dbUpgraded Called");
        //db.deleteTabel(DBHandler.Ticket_Detail_Table);
    }

    private void init(){
        auto = (AutoCompleteTextView) findViewById(R.id.auto);
        tv_total = (TextView) findViewById(R.id.tv_total);
        tv_complete = (TextView) findViewById(R.id.tv_complete);
        tv_pending = (TextView) findViewById(R.id.tv_pending);
        btn_view_all = (Button) findViewById(R.id.btn_view_all);
        btn_add = (Button) findViewById(R.id.btn_add);
        img_view_all = (ImageView) findViewById(R.id.img_view_all);
        img_add_new = (ImageView) findViewById(R.id.img_add_new);
        img_view_all.setOnClickListener(this);
        img_add_new.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        constant = new Constant(MainActivity.this);
        db = new DBHandler(getApplicationContext());
        db.initInterface(this);
        updateList = new ArrayList<>();
    }

    private void refreshUserData(){
        constant = new Constant(MainActivity.this);
        constant.showPD();
        try {
            String user = FirstActivity.pref.getString(getString(R.string.pref_username), "");
            String pass = FirstActivity.pref.getString(getString(R.string.pref_password), "");
            user = URLEncoder.encode(user,"UTF-8");
            pass = URLEncoder.encode(pass,"UTF-8");
            String url = Constant.ipaddress + "/getEmpValid?UserName=" + user + "&Password=" + pass;
            writeLog("MainActivity_refreshUserData_"+url);
            Constant.showLog(url);
            StringRequest request = new StringRequest(url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String result) {
                            Constant.showLog(result);
                            result = result.replace("\\", "");
                            result = result.replace("''", "");
                            result = result.substring(1, result.length() - 1);
                            constant.showPD();
                            if(new ParseJSON(result,getApplicationContext()).parseUserData() == 1){
                                //showDia(4);
                                db.deleteTabel(DBHandler.Ticket_Master_Table);
                                db.deleteTabel(DBHandler.Ticket_Detail_Table);
                                db.deleteTabel(DBHandler.SMLMAST_Table);
                                isDiaShowed = 0;
                                loadData();
                                writeLog("MainActivity_refreshUserData_Success");
                            }else{
                                writeLog("MainActivity_refreshUserData_UnSuccess");
                                showDia(5);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            writeLog("MainActivity_refreshUserData_volley_"+ error.getMessage());
                            error.printStackTrace();
                            constant.showPD();
                            showDia(5);
                        }
                    }
            );
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request);
        }catch (Exception e){
            writeLog("MainActivity_refreshUserData_"+e.getMessage());
            e.printStackTrace();
            toast.setText("Something Went Wrong");
            toast.show();
        }
    }

    private void loadData(){
        constant = new Constant(MainActivity.this);
        constant.showPD();
        final AtomicInteger atomicInteger = new AtomicInteger(3);
        String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
        String isHWapplicable = FirstActivity.pref.getString(getString(R.string.pref_isHWapplicable),"");

        int autoId = db.getMaxAutoId(type);
        String moddate1 = db.getLatestModDate1();

        String url1 = Constant.ipaddress+"/GetVersion";

        String url2 = Constant.ipaddress+"/GetTicketMaster?clientAuto="+FirstActivity.pref.getInt(getString(R.string.pref_auto),0)+
                "&type="+type+"&autoId="+autoId+"&isHWapplicable="+isHWapplicable;


        new getTicketMaster(autoId,atomicInteger).execute(url2);

        String url3 = Constant.ipaddress+"/GetCustNameBranch?groupId="+FirstActivity.pref.getInt(getString(R.string.pref_groupid),0)+
                "&auto="+db.getSMLMASTMaxAuto();
        String url4 = Constant.ipaddress+"/GetUpdatedTicketMaster?clientAuto="+FirstActivity.pref.getInt(getString(R.string.pref_auto),0)+
                "&type="+type+"&autoId="+autoId+"&isHWapplicable="+isHWapplicable+"&moddate="+moddate1;

        Constant.showLog(url1);
        Constant.showLog(url2);
        Constant.showLog(url3);
        Constant.showLog(url4);

        StringRequest versionRequest = new StringRequest(url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Constant.showLog(result);
                        result = result.replace("\\", "");
                        result = result.replace("''", "");
                        result = result.substring(1, result.length() - 1);
                        String _data = new ParseJSON(result).parseVersion();
                        int taskLeft = atomicInteger.decrementAndGet();
                        Constant.showLog("versionRequest_"+taskLeft);
                        if (taskLeft == 0) {
                            constant.showPD();
                            //setData();
                        }
                        if (_data != null && !_data.equals("0")) {
                            /*if (version.equals(_data)) {
                                SharedPreferences.Editor editor = FirstActivity.pref.edit();
                                editor.putString(getString(R.string.pref_version), _data);
                                editor.apply();
                            }else{
                                showDia(8);
                            }*/
                            String versionArr[] = version.split("\\.");
                            String dataArr[] = _data.split("\\.");
                            int currVersion = Integer.parseInt(versionArr[0]);
                            int dataVersion = Integer.parseInt(dataArr[0]);

                            if (currVersion>dataVersion) {
                                SharedPreferences.Editor editor = FirstActivity.pref.edit();
                                editor.putString(getString(R.string.pref_version), _data);
                                editor.apply();
                            }else if (currVersion<=dataVersion){
                                showDia(8);
                            }
                        } else if (_data == null) {
                            if(isDiaShowed!=1) {
                                showDia(3);
                                isDiaShowed = 1;
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        writeLog("MainActivity_loadData_versionRequest_"+ error.getMessage());
                        int taskLeft = atomicInteger.decrementAndGet();
                        Constant.showLog("versionRequest_ErrorListener_"+taskLeft);
                        if(taskLeft==0) {
                            constant.showPD();
                        }
                        if(isDiaShowed!=1) {
                            showDia(3);
                            isDiaShowed = 1;
                        }
                    }
                }
        );

        StringRequest descRequest = new StringRequest(url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Constant.showLog(result);
                        result = result.replace("\\", "");
                        result = result.replace("''", "");
                        result = result.substring(1, result.length() - 1);
                        addList = new ParseJSON(result, getApplicationContext()).parseAllTicket();
                        int taskLeft = atomicInteger.decrementAndGet();
                        Constant.showLog("descRequest_"+taskLeft);
                        if (taskLeft == 0) {
                            constant.showPD();
                            //setData();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        writeLog("MainActivity_loadData_descRequest_"+ error.getMessage());
                        error.printStackTrace();
                        int taskLeft = atomicInteger.decrementAndGet();
                        Constant.showLog("descRequest_ErrorListener_"+taskLeft);
                        if(taskLeft==0) {
                            constant.showPD();
                        }
                        if(isDiaShowed!=1) {
                            showDia(3);
                            isDiaShowed = 1;
                        }
                    }
                }
        );

        StringRequest custRequest = new StringRequest(url3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Constant.showLog(result);
                        result = result.replace("\\", "");
                        result = result.replace("''", "");
                        result = result.substring(1,result.length()-1);
                        new ParseJSON(result, getApplicationContext()).parseSMLMASTData();
                        int taskLeft = atomicInteger.decrementAndGet();
                        Constant.showLog("custRequest_"+taskLeft);
                        if (taskLeft == 0) {
                            constant.showPD();
                            //setData();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        writeLog("MainActivity_loadData_custRequest_"+ error.getMessage());
                        error.printStackTrace();
                        int taskLeft = atomicInteger.decrementAndGet();
                        Constant.showLog("custRequest_ErrorListener_"+taskLeft);
                        if(taskLeft==0) {
                            constant.showPD();
                        }
                        if(isDiaShowed!=1) {
                            showDia(3);
                            isDiaShowed = 1;
                        }
                    }
                }
        );

        StringRequest updateRequest = new StringRequest(url4,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Constant.showLog(result);
                        result = result.replace("\\", "");
                        result = result.replace("''", "");
                        result = result.substring(1, result.length() - 1);
                        updateList = new ParseJSON(result, getApplicationContext()).parseUpdatedTicket();
                        int taskLeft = atomicInteger.decrementAndGet();
                        Constant.showLog("updateRequest_"+taskLeft);
                        if (taskLeft == 0) {
                            constant.showPD();
                           // setData();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        writeLog("MainActivity_loadData_updateRequest_"+ error.getMessage());
                        error.printStackTrace();
                        int taskLeft = atomicInteger.decrementAndGet();
                        Constant.showLog("updateRequest_ErrorListener_"+taskLeft);
                        if(taskLeft==0) {
                            constant.showPD();
                        }
                        if(isDiaShowed!=1) {
                            showDia(3);
                            isDiaShowed = 1;
                        }
                    }
                }
        );

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(versionRequest,"ABC");
        //AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(descRequest,"ABC");
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(custRequest,"ABC");
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(updateRequest,"ABC");

        /*RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(versionRequest);
        queue.add(descRequest);
        queue.add(custRequest);
        queue.add(updateRequest);*/
    }

    private void setData(){
        /*if(addList.size()!=0){
            for(TicketMasterClass mast : addList) {
                db.addTicketMaster(mast);
            }
        }*/
        if(updateList.size()!=0){
            for (TicketMasterClass mast : updateList){
                db.updateTicketMaster(mast);
            }
        }

        String crby = FirstActivity.pref.getString(getString(R.string.pref_ClientName),"");
        String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
        ArrayList<TicketMasterClass> pendingTicketClassList = db.getTicketMaster(3,crby,type);
        if (pendingTicketClassList != null) {
            if (pendingTicketClassList.size() != 0) {
                listView.setAdapter(null);
                listView.setAdapter(new ShortDescListAdapter(pendingTicketClassList, getApplicationContext()));
            }
        }
        String _data = db.getCount();
        String[] data = _data.split("\\^");
        tv_total.setText(data[0]);
        tv_complete.setText(data[1]);
        tv_pending.setText(data[2]);
        int total = Integer.parseInt(data[0]);
        SharedPreferences.Editor editor = FirstActivity.pref.edit();
        editor.putInt(getString(R.string.pref_ticketTotal),total);
        editor.apply();

        Intent i= new Intent(getApplicationContext(), DataUpdateService.class);
        startService(i);
    }

    private void showDia(int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        if(i==0){
            builder.setMessage("Do You Want To Logout App?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor editor = FirstActivity.pref.edit();
                    editor.putBoolean(getResources().getString(R.string.pref_logged),false);
                    editor.apply();
                    dialogInterface.dismiss();
                    Constant.deleteLogFile();
                    new Constant(MainActivity.this).doFinish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if(i==1){
            builder.setMessage("Do You Want To Refresh Data?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(ConnectivityTest.getNetStat(getApplicationContext())) {
                        refreshUserData();
                    }else{
                        toast.setText("You Are Offline");
                        toast.show();
                    }
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if(i==2){
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Constant.deleteLogFile();
                    new Constant(MainActivity.this).doFinish();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if(i==3) {
            builder.setMessage("Error While Loading Data");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isDiaShowed = 0;
                    loadData();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if(i==4) {
            builder.setMessage("Data Updated Successfully");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if(i==5) {
            builder.setMessage("Error While Updating Data");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(ConnectivityTest.getNetStat(getApplicationContext())) {
                        refreshUserData();
                    }else{
                        toast.setText("You Are Offline");
                        toast.show();
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if(i==6) {
            builder.setMessage("Do You Want To Report An Issue?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(ConnectivityTest.getNetStat(getApplicationContext())) {
                        exportfile();
                    }else{
                        toast.setText("You Are Offline");
                        toast.show();
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if(i==7) {
            builder.setMessage("Do You Want To Clear Data?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.deleteTabel(DBHandler.Ticket_Master_Table);
                    db.deleteTabel(DBHandler.Ticket_Detail_Table);
                    db.deleteTabel(DBHandler.SMLMAST_Table);
                    FirstActivity.pref.edit().clear().commit();
                    new Constant(MainActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if(i==8) {
            builder.setTitle("Update App");
            builder.setMessage("Smart Ticket's New Version Is Available");
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //FirstActivity.pref.edit().clear().commit();
                    //db.deleteTabel(DBHandler.Ticket_Master_Table);
                    //db.deleteTabel(DBHandler.SMLMAST_Table);
                    new Constant(MainActivity.this).doFinish();
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    }
                    catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void exportfile(){
        if(new CopyLog().copyLog(getApplicationContext())) {
            writeLog("MainActivity_exportfile_Log_File_Exported");
            sendMail1();
        }else{
            writeLog("MainActivity_exportfile_Error_While_Log_File_Exporting");
        }
    }

    private void sendMail1(){
        try {
            File sdFile = Constant.checkFolder(Constant.folder_name);
            File writeFile = new File(sdFile, Constant.log_file_name);
            GMailSender sender = new GMailSender(Constant.automailID, Constant.autoamilPass);
            Constant.showLog("Attached Log File :- " + writeFile.getAbsolutePath());
            sender.addAttachment(sdFile.getAbsolutePath() + File.separator + Constant.log_file_name, Constant.log_file_name, Constant.mail_body);
            String resp[] = {Constant.mailReceipient};
            AtomicInteger workCounter = new AtomicInteger(resp.length);
            for (String aResp : resp) {
                if (!aResp.equals("")) {
                    Constant.showLog("send Mail Recp :- " + aResp);
                    new sendMail(workCounter, aResp, sender).execute("");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class sendMail extends AsyncTask<String, Void, String> {
        private final AtomicInteger workCounter;

        ProgressDialog pd;
        String respMailId;
        GMailSender sender;

        sendMail(AtomicInteger workCounter, String _respMailId,GMailSender _sender){
            respMailId = _respMailId;
            sender = _sender;
            this.workCounter = workCounter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String res = respMailId;
                sender.sendMail(Constant.mail_subject,Constant.mail_body,Constant.automailID,res);
                return "1";
            } catch (Exception e) {
                writeLog("MainActivity_sendMailClass_"+e.getMessage());
                e.printStackTrace();
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                int tasksLeft = this.workCounter.decrementAndGet();
                Constant.showLog("sendMail Work Counter " + tasksLeft);
                if(result.equals("1")) {
                    if (tasksLeft == 0) {
                        writeLog("MainActivity_sendMailClass_Mail_Send_Successfully");
                        Constant.showLog("sendMail END MULTI THREAD");
                        Constant.showLog("sendMail Work Counter END " + tasksLeft);
                        toast.setText("File Exported Successfully");
                    } else {
                        writeLog("MainActivity_sendMailClass_Mail_Send_UnSuccessfull1");
                        toast.setText("Error While Sending Mail");
                    }
                }else{
                    toast.setText("Error While Exporting Log File");
                    writeLog("MainActivity_sendMailClass_Mail_Send_UnSuccessfull");
                }
                toast.show();
                pd.dismiss();
            }catch (Exception e){
                writeLog("MainActivity_sendMailClass_"+e.getMessage());
                e.printStackTrace();
                pd.dismiss();
            }
        }
    }

    private void writeLog(String _data){
        new WriteLog().writeLog(getApplicationContext(),_data);
    }

    private class getTicketMaster extends AsyncTask<String, Void, String> {

        private int to;
        private AtomicInteger atomicInteger;
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please Wait");
            pd.setCancelable(false);
            pd.show();
        }

        public getTicketMaster(int _to, AtomicInteger _atomicInteger) {
            this.to = _to;
            this.atomicInteger = _atomicInteger;
        }

        @Override
        protected String doInBackground(String... strings) {
            return Post.POST(strings[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            pd.dismiss();
            if(response!=null) {
                response = response.substring(1, response.length() - 1);
                new readJSON(response, "SizeNDesign", to, atomicInteger).execute();
            }else{
                setData();
            }
        }
    }

    private class readJSON extends AsyncTask<Void, Void, String> {
        private int to;
        private String result, parseType;
        private AtomicInteger atomicInteger;
        private File writeFile = null;

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please Wait");
            pd.setCancelable(false);
            pd.show();
        }

        public readJSON(String _result, String _parseType, int _to, AtomicInteger _atomicInteger) {
            this.result = _result;
            this.parseType = _parseType;
            this.to = _to;
            this.atomicInteger = _atomicInteger;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String retValue = "B";
            File sdFile = Constant.checkFolder(Constant.folder_name);
            FileWriter writer;
            try {
                String search = "\\\\", replace = "";
                writeFile = new File(sdFile, writeFilename);
                writer = new FileWriter(writeFile);
                int size = result.length();
                if (size > 2) {
                    Log.d("Log", "Replacing");
                    int b = 50000;
                    for (int i = 0; i < size; i++) {
                        if (b >= size) {
                            b = size;
                        }
                        String q = result.substring(i, b);
                        String g = q.replaceAll(search, replace);
                        System.gc();
                        writer.append(g);
                        i = b - 1;
                        b = b + 50000;
                    }
                    retValue = "A";
                }
                writer.flush();
                writer.close();
                return retValue;
            } catch (IOException | OutOfMemoryError e) {
                /*int taskLeft = atomicInteger.decrementAndGet();
                Constant.showLog("descRequest_replace_"+taskLeft);
                if (taskLeft == 0) {
                    constant.showPD();
                }*/
                if(writeFile!=null) {
                    writeFile.delete();
                }
                pd.dismiss();
                try {
                    writer = new FileWriter(new File(sdFile, "Log.txt"));
                    writer.append(e.getMessage());
                    writer.flush();
                    writer.close();
                } catch (Exception e1) {
                    e.printStackTrace();
                }
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (s.equals("A")) {
                new writeDB(parseType, to,atomicInteger).execute();
            } else if (s.equals("B")){
                /*int taskLeft = atomicInteger.decrementAndGet();
                Constant.showLog("descRequest_replace_"+taskLeft);
                if (taskLeft == 0) {
                    constant.showPD();
                    setData();
                }*/
                //setData();
                String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
                int clientAuto = FirstActivity.pref.getInt(getString(R.string.pref_auto),0);
                int auto = db.getAutoTD();
                String url1 = Constant.ipaddress + "/GetAllTicketDetail?clientAuto="+clientAuto+"&auto="+auto+"&type="+type;
                Constant.showLog(url1);
                new getTicketDetail(0,new AtomicInteger(1)).execute(url1);
            }else {
                showDia(3);
            }
        }
    }

    private class writeDB extends AsyncTask<Void, String, String> {

        private File writeFile;
        private String parseType;
        private int to;
        private AtomicInteger atomicInteger;

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please Wait");
            pd.setCancelable(false);
            pd.show();
        }

        public writeDB(String _parseType, int _to, AtomicInteger _atomicInteger) {
            this.parseType = _parseType;
            this.to = _to;
            this.atomicInteger = _atomicInteger;
        }

        @Override
        protected String doInBackground(Void... voids) {
            File sdFile = Constant.checkFolder(Constant.folder_name);
            JsonFactory f = new JsonFactory();
            try {
                writeFile = new File(sdFile, writeFilename);
                JsonParser jp = f.createJsonParser(writeFile);
                parseSizeNDesign(jp, to);
                return "";
            } catch (Exception e) {
                /*int taskLeft = atomicInteger.decrementAndGet();
                Constant.showLog("descRequest_"+taskLeft);
                if (taskLeft == 0) {
                    constant.showPD();
                }*/
                pd.dismiss();
                try {
                    FileWriter writer = new FileWriter(new File(sdFile, "Log.txt"));
                    writer.append(e.getMessage());
                    writer.flush();
                    writer.close();
                } catch (Exception e1) {
                    e.printStackTrace();
                    return null;
                }
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();

            if (s != null) {
                if (s.equals("")) {
                    Constant.showLog("Success");
                    /*int taskLeft = atomicInteger.decrementAndGet();
                    Constant.showLog("descRequest_"+taskLeft);
                    if (taskLeft == 0) {
                        constant.showPD();
                        String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
                        int clientAuto = FirstActivity.pref.getInt(getString(R.string.pref_auto),0);
                        int auto = db.getAutoTD();
                        String url1 = Constant.ipaddress + "/GetTicketDetail?clientAuto="+clientAuto+"&auto="+auto+"&type="+type;
                        Constant.showLog(url1);
                        new getTicketDetail(0,new AtomicInteger(1)).execute(url1);
                        //setData();
                    }*/
                    String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
                    int clientAuto = FirstActivity.pref.getInt(getString(R.string.pref_auto),0);
                    int auto = db.getAutoTD();
                    String url1 = Constant.ipaddress + "/GetAllTicketDetail?clientAuto="+clientAuto+"&auto="+auto+"&type="+type;
                    Constant.showLog(url1);
                    new getTicketDetail(0,new AtomicInteger(1)).execute(url1);
                    //setData();
                } else {
                    showDia(3);
                }
            } else {
                /*int taskLeft = atomicInteger.decrementAndGet();
                Constant.showLog("descRequest_"+taskLeft);
                if (taskLeft == 0) {
                    constant.showPD();
                }*/
                showDia(3);
            }
        }
    }

    private void parseSizeNDesign(JsonParser jp, int to) {
        try {
            int count = 0;
            List<TicketMasterClass> list = new ArrayList<>();
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                count++;
                TicketMasterClass ticketClass = new TicketMasterClass();
                String moddate = "null";
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String token = jp.getCurrentName();
                    if ("auto".equals(token)) {
                        jp.nextToken();
                        ticketClass.setAuto(jp.getValueAsInt());
                    } else if ("id".equals(token)) {
                        jp.nextToken();
                        ticketClass.setId(jp.getValueAsInt());
                    } else if ("ClientAuto".equals(token)) {
                        jp.nextToken();
                        ticketClass.setClientAuto(jp.getValueAsInt());
                    } else if ("ClientName".equals(token)) {
                        jp.nextToken();
                        ticketClass.setClientName(jp.getText());
                    } else if ("finyr".equals(token)) {
                        jp.nextToken();
                        ticketClass.setFinyr(jp.getText());
                    } else if ("ticketNo".equals(token)) {
                        jp.nextToken();
                        ticketClass.setTicketNo(jp.getText());
                    } else if ("Particular".equals(token)) {
                        jp.nextToken();
                        ticketClass.setParticular(jp.getText());
                    } else if ("Subject".equals(token)) {
                        jp.nextToken();
                        ticketClass.setSubject(jp.getText());
                    } else if ("ImagePAth".equals(token)) {
                        jp.nextToken();
                        ticketClass.setImagePAth(jp.getText());
                    } else if ("Status".equals(token)) {
                        jp.nextToken();
                        ticketClass.setStatus(jp.getText());
                    } else if ("CrBy".equals(token)) {
                        jp.nextToken();
                        ticketClass.setCrBy(jp.getText());
                    } else if ("CrDate".equals(token)) {
                        jp.nextToken();
                        ticketClass.setCrDate(jp.getText());
                    } else if ("CrTime".equals(token)) {
                        jp.nextToken();
                        ticketClass.setCrTime(jp.getText());
                    } else if ("ModBy".equals(token)) {
                        jp.nextToken();
                        ticketClass.setModBy(jp.getText());
                    } else if ("ModDate".equals(token)) {
                        jp.nextToken();
                        moddate = jp.getText();
                        ticketClass.setModDate(moddate);
                    } else if ("ModTime".equals(token)) {
                        jp.nextToken();
                        ticketClass.setModTime(jp.getText());
                    } else if ("AssignTo".equals(token)) {
                        jp.nextToken();
                        ticketClass.setAssignTO(jp.getText());
                    } else if ("AssignDate".equals(token)) {
                        jp.nextToken();
                        ticketClass.setAssignTODate(jp.getText());
                    } else if ("AssignTime".equals(token)) {
                        jp.nextToken();
                        ticketClass.setAssignTOTime(jp.getText());
                    } else if ("Assignby".equals(token)) {
                        jp.nextToken();
                        ticketClass.setAssignBy(jp.getText());
                    } else if ("AssignbyDate".equals(token)) {
                        jp.nextToken();
                        ticketClass.setAssignByDate(jp.getText());
                    } else if ("AssignbyTime".equals(token)) {
                        jp.nextToken();
                        ticketClass.setAssignByTime(jp.getText());
                    } else if ("type".equals(token)) {
                        jp.nextToken();
                        ticketClass.setType(jp.getText());
                    } else if ("GenType".equals(token)) {
                        jp.nextToken();
                        ticketClass.setGenType(jp.getText());
                    } else if ("Branch".equals(token)) {
                        jp.nextToken();
                        ticketClass.setBranch(jp.getText());
                    } else if ("PointType".equals(token)) {
                        jp.nextToken();
                        ticketClass.setPointtype(jp.getText());
                        String moddate1 = "null";
                        if (!moddate.equals("null")) {
                            Date d = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).parse(moddate);
                            moddate1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(d);
                        }
                        ticketClass.setModdate1(moddate1);
                    }
                }
                list.add(ticketClass);
            }
            db.addTicketMaster(list);
            Constant.showLog("" + count);
        } catch (Exception e) {
            writeLog("parseTicketMaster_" + e.getMessage());
            e.printStackTrace();
            showDia(3);
        }
    }

    private class getTicketDetail extends AsyncTask<String, Void, String> {

        private int to;
        private AtomicInteger atomicInteger;

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please Wait");
            pd.setCancelable(false);
            pd.show();
        }

        public getTicketDetail(int _to, AtomicInteger _atomicInteger) {
            this.to = _to;
            this.atomicInteger = _atomicInteger;
        }

        @Override
        protected String doInBackground(String... strings) {
            return Post.POST(strings[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            pd.dismiss();
            if(response!=null) {
                response = response.substring(1, response.length() - 1);
                new readJSONTD(response, "SizeNDesign", to, atomicInteger).execute();
            }else{
                setData();
            }
        }
    }

    private class readJSONTD extends AsyncTask<Void, Void, String> {
        private int to;
        private String result, parseType;
        private AtomicInteger atomicInteger;

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please Wait");
            pd.setCancelable(false);
            pd.show();
        }

        public readJSONTD(String _result, String _parseType, int _to, AtomicInteger _atomicInteger) {
            this.result = _result;
            this.parseType = _parseType;
            this.to = _to;
            this.atomicInteger = _atomicInteger;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String retValue = "B";
            File sdFile = Constant.checkFolder(Constant.folder_name);
            FileWriter writer;
            File writeFile = null;
            try {
                String search = "\\\\", replace = "";
                writeFile = new File(sdFile, writeFilename);
                writer = new FileWriter(writeFile);

                int size = result.length();
                if (size > 2) {
                    Log.d("Log", "Replacing");
                    int b = 50000;
                    for (int i = 0; i < size; i++) {
                        if (b >= size) {
                            b = size;
                        }
                        String q = result.substring(i, b);
                        String g = q.replaceAll(search, replace);
                        System.gc();
                        writer.append(g);
                        i = b - 1;
                        b = b + 50000;
                    }
                    retValue = "A";
                }
                writer.flush();
                writer.close();
                return retValue;
            } catch (IOException | OutOfMemoryError e) {
                /*int taskLeft = atomicInteger.decrementAndGet();
                Constant.showLog("descRequest_replace_"+taskLeft);
                if (taskLeft == 0) {
                    constant.showPD();
                }*/
                if (writeFile!=null) {
                    writeFile.delete();
                }
                pd.dismiss();
                try {
                    writer = new FileWriter(new File(sdFile, "Log.txt"));
                    writer.append(e.getMessage());
                    writer.flush();
                    writer.close();
                } catch (Exception e1) {
                    e.printStackTrace();
                }
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (s.equals("A")) {
                new writeDBTD(parseType, to,atomicInteger).execute();
            } else if (s.equals("B")){
                /*int taskLeft = atomicInteger.decrementAndGet();
                Constant.showLog("descRequest_replace_"+taskLeft);
                if (taskLeft == 0) {
                    constant.showPD();
                    setData();
                }*/
                setData();
            }else {
                showDia(3);
            }
        }
    }

    private class writeDBTD extends AsyncTask<Void, String, String> {

        private File writeFile;
        private String parseType;
        private int to;
        private AtomicInteger atomicInteger;

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please Wait");
            pd.setCancelable(false);
            pd.show();
        }

        public writeDBTD(String _parseType, int _to, AtomicInteger _atomicInteger) {
            this.parseType = _parseType;
            this.to = _to;
            this.atomicInteger = _atomicInteger;
        }

        @Override
        protected String doInBackground(Void... voids) {
            File sdFile = Constant.checkFolder(Constant.folder_name);
            JsonFactory f = new JsonFactory();
            try {
                writeFile = new File(sdFile, writeFilename);
                JsonParser jp = f.createJsonParser(writeFile);
                parseTicketDetail(jp, to);
                return "";
            } catch (Exception e) {
                /*int taskLeft = atomicInteger.decrementAndGet();
                Constant.showLog("descRequest_"+taskLeft);
                if (taskLeft == 0) {
                    constant.showPD();
                }*/
                pd.dismiss();
                try {
                    FileWriter writer = new FileWriter(new File(sdFile, "Log.txt"));
                    writer.append(e.getMessage());
                    writer.flush();
                    writer.close();
                } catch (Exception e1) {
                    e.printStackTrace();
                    return null;
                }
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (s != null) {
                if (s.equals("")) {
                    Constant.showLog("Success");
                    /*int taskLeft = atomicInteger.decrementAndGet();
                    Constant.showLog("descRequest_"+taskLeft);
                    if (taskLeft == 0) {
                        constant.showPD();
                        setData();
                    }*/
                    setData();
                } else {
                    showDia(3);
                }
            } else {
                /*int taskLeft = atomicInteger.decrementAndGet();
                Constant.showLog("descRequest_"+taskLeft);
                if (taskLeft == 0) {
                    constant.showPD();
                }*/
                showDia(3);
            }
        }
    }

    private void parseTicketDetail(JsonParser jp, int to) {
        try {
            int count = 0;
            List<TicketDetailClass> list = new ArrayList<>();
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                count++;
                TicketDetailClass ticketClass = new TicketDetailClass();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String token = jp.getCurrentName();
                    if ("Auto".equals(token)) {
                        jp.nextToken();
                        ticketClass.setAuto(jp.getValueAsInt());
                    } else if ("MastAuto".equals(token)) {
                        jp.nextToken();
                        ticketClass.setMastAuto(jp.getValueAsInt());
                    } else if ("Description".equals(token)) {
                        jp.nextToken();
                        ticketClass.setDesc(jp.getText());
                    } else if ("CrBy".equals(token)) {
                        jp.nextToken();
                        ticketClass.setCrby(jp.getText());
                    } else if ("CrDate".equals(token)) {
                        jp.nextToken();
                        String crdate1 = jp.getText();
                        ticketClass.setCrDate(crdate1);
                        Date d = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).parse(crdate1);
                        String crdate2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(d);
                        ticketClass.setCrDate1(crdate2);
                    } else if ("CrTime".equals(token)) {
                        jp.nextToken();
                        ticketClass.setCrTime(jp.getText());
                    } else if ("Type".equals(token)) {
                        jp.nextToken();
                        ticketClass.setType(jp.getText());
                    } else if ("GenType".equals(token)) {
                        jp.nextToken();
                        ticketClass.setGenType(jp.getText());
                    } else if ("Id".equals(token)) {
                        jp.nextToken();
                        ticketClass.setId(jp.getValueAsInt());
                    } else if ("ClientAuto".equals(token)) {
                        jp.nextToken();
                        ticketClass.setClientAuto(jp.getValueAsInt());
                    } else if ("PointType".equals(token)) {
                        jp.nextToken();
                        ticketClass.setPointType(jp.getText());
                    }
                }
                list.add(ticketClass);
            }
            db.addTicketDetail(list);
            Constant.showLog("" + count);
        } catch (Exception e) {
            writeLog("parseTicketDetail_" + e.getMessage());
            e.printStackTrace();
            showDia(3);
        }
    }
}

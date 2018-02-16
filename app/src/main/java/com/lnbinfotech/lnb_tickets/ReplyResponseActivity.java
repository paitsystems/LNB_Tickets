package com.lnbinfotech.lnb_tickets;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lnbinfotech.lnb_tickets.adapter.AllTicketListAdapter;
import com.lnbinfotech.lnb_tickets.adapter.ReplyResponseListAdapter;
import com.lnbinfotech.lnb_tickets.constant.AppSingleton;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.model.TicketDetailClass;
import com.lnbinfotech.lnb_tickets.parse.ParseJSON;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReplyResponseActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private Constant constant;
    private Toast toast;
    private EditText ed_reply;
    private Button btn_reply;
    private ImageView img_reply;
    private ArrayList<TicketDetailClass> ticketDetailClassList;
    private ReplyResponseListAdapter adapter;
    private AdView mAdView;
    private String from;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_response);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();
        img_reply.setOnClickListener(this);
        btn_reply.setOnClickListener(this);

        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest;
        if (Constant.liveTestFlag == 1) {
            adRequest = new AdRequest.Builder().build();
        } else {
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("0558B791C50AB34B5650C3C48C9BD15E").build();
        }

        mAdView.loadAd(adRequest);

        from = getIntent().getExtras().getString("from");

        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAdView!=null){
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        if(mAdView!=null){
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mAdView!=null){
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reply:
                if(!ed_reply.getText().toString().equals("") && ed_reply.getText().toString().length()!=0) {
                    addTicketDetail();
                }else{
                    toast.setText("Please Enter Message");
                    toast.show();
                }
                break;
            case R.id.img_reply:
                if(!ed_reply.getText().toString().equals("") && ed_reply.getText().toString().length()!=0) {
                    addTicketDetail();
                }else{
                    toast.setText("Please Enter Message");
                    toast.show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btn_reply.getWindowToken(),0);
        new Constant(ReplyResponseActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btn_reply.getWindowToken(),0);
                new Constant(ReplyResponseActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        constant = new Constant(ReplyResponseActivity.this);
        listView = (ListView) findViewById(R.id.listView);
        ed_reply = (EditText) findViewById(R.id.ed_reply);
        btn_reply = (Button) findViewById(R.id.btn_reply);
        img_reply = (ImageView) findViewById(R.id.img_reply);
        ticketDetailClassList = new ArrayList<>();
        db = new DBHandler(getApplicationContext());
    }

    private void loadData() {
        constant.showPD();
        int auto,id;
        auto = db.getAutoTD();
        id = db.getIDTD();
        final String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
        String url1 = Constant.ipaddress + "/GetTicketDetail?auto="+auto+"&mastAuto="+UpdateTicketActivity.auto
                                            +"&id="+id+"&type="+type;
        Constant.showLog(url1);
        StringRequest request = new StringRequest(url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Constant.showLog(result);
                        result = result.replace("\\", "");
                        result = result.replace("''", "");
                        result = result.substring(1, result.length() - 1);
                        ticketDetailClassList.clear();
                        ticketDetailClassList = new ParseJSON(result, getApplicationContext()).parseTicketDetail();
                        ticketDetailClassList.clear();
                        setData();
                        /*if(from.equals("U")) {
                            if (ticketDetailClassList.size() != 0) {
                                listView.setAdapter(null);
                                adapter = new ReplyResponseListAdapter(ticketDetailClassList, getApplicationContext(), type);
                                listView.setAdapter(adapter);
                                listView.setSelection(listView.getAdapter().getCount() - 1);
                            }
                        }else {
                            ticketDetailClassList.clear();
                            setData();
                        }*/
                        constant.showPD();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        writeLog("ReplyResponseActivity_loadData_volley_"+error.getMessage());
                        error.printStackTrace();
                        constant.showPD();
                        showDia(3);
                    }
                }
        );

        //RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        //queue.add(request);

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(request,"ABC");
    }

    private void addTicketDetail(){
        try {
            constant.showPD();
            //int auto = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
            final String type = FirstActivity.pref.getString(getString(R.string.pref_emptype), "");
            String crby = FirstActivity.pref.getString(getString(R.string.pref_ClientName), "");
            String nickname = FirstActivity.pref.getString(getString(R.string.pref_nickname), "NA");
            final String comment = ed_reply.getText().toString();
            String reply = URLEncoder.encode(comment, "UTF-8");
            crby = URLEncoder.encode(crby, "UTF-8");
            nickname = URLEncoder.encode(nickname, "UTF-8");

            if(nickname.equals("NA")){
                nickname = crby;
            }

            String url1 = Constant.ipaddress + "/AddTicketDetail?mastAuto=" + UpdateTicketActivity.auto +
                    "&desc="+reply+"&CrBy="+nickname+"&type="+type;
            Constant.showLog(url1);
            StringRequest request = new StringRequest(url1,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String result) {
                            Constant.showLog(result);
                            result = result.replace("\\", "");
                            result = result.replace("''", "");
                            result = result.replace("\"","");
                            Constant.showLog(result);
                            ed_reply.setText(null);
                            loadData();
                            constant.showPD();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            writeLog("ReplyResponseActivity_addTicketDetail_volley_"+error.getMessage());
                            error.printStackTrace();
                            constant.showPD();
                            showDia(3);
                        }
                    }
            );

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setData(){
        ticketDetailClassList.clear();
        Cursor res = db.getParticularTicketDetail();
        if(res.moveToFirst()){
            do{
                try {
                    TicketDetailClass ticketDetailClass = new TicketDetailClass();
                    ticketDetailClass.setAuto(res.getInt(res.getColumnIndex(DBHandler.TicketD_Auto)));
                    ticketDetailClass.setMastAuto(res.getInt(res.getColumnIndex(DBHandler.TicketD_MastAuto)));
                    ticketDetailClass.setDesc(res.getString(res.getColumnIndex(DBHandler.TicketD_Description)));
                    ticketDetailClass.setCrby(res.getString(res.getColumnIndex(DBHandler.TicketD_CrBy)));
                    String crdate1 = res.getString(res.getColumnIndex(DBHandler.TicketD_CrDate));
                    ticketDetailClass.setCrDate(crdate1);
                    ticketDetailClass.setCrTime(res.getString(res.getColumnIndex(DBHandler.TicketD_CrTime)));
                    ticketDetailClass.setType(res.getString(res.getColumnIndex(DBHandler.TicketD_Type)));
                    ticketDetailClass.setId(res.getInt(res.getColumnIndex(DBHandler.TicketD_Id)));
                    ticketDetailClass.setClientAuto(res.getInt(res.getColumnIndex(DBHandler.TicketD_ClientAuto)));
                    ticketDetailClass.setPointType(res.getString(res.getColumnIndex(DBHandler.TicketD_PointType)));

                    Date d = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).parse(crdate1);
                    String crdate2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(d);
                    ticketDetailClass.setCrDate1(crdate2);

                    ticketDetailClassList.add(ticketDetailClass);
                }catch (Exception e){
                    e.printStackTrace();
                    writeLog("setData_"+e.getMessage());
                }
            }while (res.moveToNext());
        }
        final String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
        adapter = new ReplyResponseListAdapter(ticketDetailClassList,getApplicationContext(),type);
        listView.setAdapter(adapter);
        listView.setSelection(listView.getAdapter().getCount() - 1);
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReplyResponseActivity.this);
        if (a == 0) {
            builder.setMessage("Do You Want To Go Back?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(ReplyResponseActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }if (a == 3) {
            builder.setMessage("Error While Replying...");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addTicketDetail();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void writeLog(String _data){
        new WriteLog().writeLog(getApplicationContext(),_data);
    }

}

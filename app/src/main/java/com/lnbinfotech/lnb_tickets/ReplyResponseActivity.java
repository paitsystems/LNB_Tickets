package com.lnbinfotech.lnb_tickets;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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
import com.lnbinfotech.lnb_tickets.adapter.ReplyResponseListAdapter;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.model.TicketDetailClass;
import com.lnbinfotech.lnb_tickets.parse.ParseJSON;

import java.net.URLEncoder;
import java.util.ArrayList;

public class ReplyResponseActivity extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    Constant constant;
    Toast toast;
    EditText ed_reply;
    Button btn_reply;
    ImageView img_reply;
    ArrayList<TicketDetailClass> ticketDetailClassList;
    ReplyResponseListAdapter adapter;

    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_response);

        init();
        img_reply.setOnClickListener(this);
        btn_reply.setOnClickListener(this);

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

        loadData();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
                if(!ed_reply.getText().toString().equals("")) {
                    addTicketDetail();
                }else{
                    toast.setText("Please Enter Comment");
                    toast.show();
                }
                break;
            case R.id.img_reply:
                if(!ed_reply.getText().toString().equals("")) {
                    addTicketDetail();
                }else{
                    toast.setText("Please Enter Comment");
                    toast.show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new Constant(ReplyResponseActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new Constant(ReplyResponseActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void init(){
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        constant = new Constant(ReplyResponseActivity.this);
        listView = (ListView) findViewById(R.id.listView);
        ed_reply = (EditText) findViewById(R.id.ed_reply);
        btn_reply = (Button) findViewById(R.id.btn_reply);
        img_reply = (ImageView) findViewById(R.id.img_reply);
        ticketDetailClassList = new ArrayList<>();
    }

    void loadData() {
        constant.showPD();
        final String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
        String url1 = Constant.ipaddress + "/GetTicketDetail?mastAuto="+UpdateTicketActivity.auto+"&type="+type;
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
                        ticketDetailClassList = new ParseJSON(result,getApplicationContext()).parseTicketDetail();
                        if (ticketDetailClassList.size()!= 0) {
                            listView.setAdapter(null);
                            adapter = new ReplyResponseListAdapter(ticketDetailClassList,getApplicationContext(),type);
                            listView.setAdapter(adapter);
                            listView.setSelection(listView.getAdapter().getCount()-1);
                        }
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

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    void addTicketDetail(){
        try {
            constant.showPD();
            //int auto = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
            final String type = FirstActivity.pref.getString(getString(R.string.pref_emptype), "");
            String crby = FirstActivity.pref.getString(getString(R.string.pref_ClientName), "");
            final String comment = ed_reply.getText().toString();
            String reply = URLEncoder.encode(comment, "UTF-8");
            crby = URLEncoder.encode(crby, "UTF-8");
            String url1 = Constant.ipaddress + "/AddTicketDetail?mastAuto=" + UpdateTicketActivity.auto +
                    "&desc="+reply+"&CrBy="+crby+"&type="+type;
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

    void showDia(int a) {
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

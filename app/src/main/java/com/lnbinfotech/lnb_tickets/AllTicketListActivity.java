package com.lnbinfotech.lnb_tickets;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;
import com.lnbinfotech.lnb_tickets.parse.ParseJSON;

import java.util.ArrayList;
import java.util.Locale;

public class AllTicketListActivity extends AppCompatActivity {

    private ListView listView;
    private Constant constant;
    private Toast toast;
    public static int selPos;
    public static String selStat;
    private AllTicketListAdapter adapter;
    private EditText ed_search;
    private DBHandler db;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_ticket_list);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        init();

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

        //loadData();
        setData();

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = ed_search.getText().toString().toLowerCase(Locale.getDefault());
                if(adapter!=null) {
                    adapter.filter(text);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selPos = i;
                TicketMasterClass pendingTicketClass = (TicketMasterClass) listView.getItemAtPosition(i);
                Intent intent = new Intent(getApplicationContext(),UpdateTicketActivity.class);
                intent.putExtra("data",pendingTicketClass);
                startActivity(intent);
                overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.isUpdate==1) {
            ed_search.setText(null);
            constant = new Constant(AllTicketListActivity.this);
            //loadData();
            setData();
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
    public void onBackPressed() {
        new Constant(AllTicketListActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                new Constant(AllTicketListActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void init(){
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        constant = new Constant(AllTicketListActivity.this);
        listView = (ListView) findViewById(R.id.listView);
        ed_search = (EditText) findViewById(R.id.ed_search);
        db = new DBHandler(getApplicationContext());
    }

    void loadData() {
        constant.showPD();
        String url1 = Constant.ipaddress + "/GetAllTickets?clientAuto=" + FirstActivity.pref.getInt(getString(R.string.pref_auto), 0)+"&type="+FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
        writeLog("AllTicketListActivity_loadData_"+url1);
        Constant.showLog(url1);
        StringRequest request = new StringRequest(url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        Constant.showLog(result);
                        result = result.replace("\\", "");
                        result = result.replace("''", "");
                        result = result.substring(1, result.length() - 1);
                        ArrayList<TicketMasterClass> pendingTicketClassList= new ParseJSON(result,getApplicationContext()).parseAllTicket();
                        if (pendingTicketClassList.size()!= 0) {
                            listView.setAdapter(null);
                            adapter = new AllTicketListAdapter(getApplicationContext(),pendingTicketClassList);
                            listView.setAdapter(adapter);
                        }/* else {
                            showDia(3);
                        }*/
                        constant.showPD();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        writeLog("AllTicketListActivity_loadData_volley_"+error.getMessage());
                        error.printStackTrace();
                        constant.showPD();
                        showDia(3);
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void setData(){
        String crby = FirstActivity.pref.getString(getString(R.string.pref_ClientName),"");
        String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
        ArrayList<TicketMasterClass> pendingTicketClassList = db.getTicketMaster(0, crby,type);
        if (pendingTicketClassList.size()!= 0) {
            listView.setAdapter(null);
            adapter = new AllTicketListAdapter(getApplicationContext(),pendingTicketClassList);
            listView.setAdapter(adapter);
        }
    }

    void showDia(int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(AllTicketListActivity.this);
        builder.setCancelable(false);
        if(i==1){
            builder.setMessage("Do You Want To Refresh Data?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
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
                    new Constant(AllTicketListActivity.this).doFinish();
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }else if(i==0){
            builder.setMessage("Do You Want To Logout App?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences.Editor editor = FirstActivity.pref.edit();
                    editor.putBoolean(getResources().getString(R.string.pref_logged),false);
                    editor.apply();
                    dialogInterface.dismiss();
                    new Constant(AllTicketListActivity.this).doFinish();
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
                    //loadData();
                    setData();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

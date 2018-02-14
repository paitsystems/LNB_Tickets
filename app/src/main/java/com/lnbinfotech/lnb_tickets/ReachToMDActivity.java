package com.lnbinfotech.lnb_tickets;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lnbinfotech.lnb_tickets.connectivity.ConnectivityTest;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;
import com.lnbinfotech.lnb_tickets.model.ViewReachToMDClass;
import com.lnbinfotech.lnb_tickets.post.Post;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ReachToMDActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_refer_ticket;
    private EditText ed_description, ed_refer_ticket;
    private ImageView img;
    private Button btn_send;
    private String imageName = null, empType;
    public static String ticketRefer = "";
    private Spinner sp_status;
    private List<String> statusList;
    public static int auto = 0;
    private Constant constant;
    private Toast toast;
    private AdView mAdView;
    private ViewReachToMDClass viewReachToMD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reach_to_md);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

        sp_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String type = statusList.get(i);
                if(type.equals("Ticket Query")){
                    ed_refer_ticket.setVisibility(View.VISIBLE);
                    //tv_refer_ticket.setVisibility(View.VISIBLE);
                }else{
                    ed_refer_ticket.setVisibility(View.GONE);
                    //tv_refer_ticket.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        empType = getIntent().getExtras().getString("type");
        viewReachToMD = (ViewReachToMDClass) getIntent().getSerializableExtra("data");

        if(empType.equals("E")){
            sp_status.setEnabled(false);
            sp_status.setClickable(false);
            ed_description.setFocusable(false);
            ed_description.setClickable(false);
            ed_refer_ticket.setFocusable(false);
            ed_refer_ticket.setClickable(false);
            ed_refer_ticket.setEnabled(false);
            btn_send.setVisibility(View.GONE);
            setData();
        }else{
            sp_status.setEnabled(true);
            sp_status.setClickable(true);
            ed_description.setFocusable(true);
            ed_description.setClickable(true);
            ed_refer_ticket.setFocusable(false);
            btn_send.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        constant = new Constant(ReachToMDActivity.this);
        if(mAdView!=null){
            mAdView.resume();
        }
        if(ed_refer_ticket.getVisibility()==View.VISIBLE){
            ed_refer_ticket.setText(ticketRefer);
            ed_description.requestFocus();
        }
        /*if(tv_refer_ticket.getVisibility()==View.VISIBLE){
            tv_refer_ticket.setText(ticketRefer);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAdView!=null){
            mAdView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mAdView!=null){
            mAdView.destroy();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ed_refer_ticket:
                startActivity(new Intent(getApplicationContext(),MDAllTicketListActivity.class));
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
            case R.id.btn_send:
                generateQuery();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        ticketRefer = "";
        new Constant(ReachToMDActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                ticketRefer = "";
                new Constant(ReachToMDActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        ed_description = (EditText) findViewById(R.id.ed_description);
        ed_refer_ticket = (EditText) findViewById(R.id.ed_refer_ticket);
        tv_refer_ticket = (TextView) findViewById(R.id.tv_ticket_refer);

        btn_send = (Button) findViewById(R.id.btn_send);
        img = (ImageView) findViewById(R.id.img);
        sp_status = (Spinner) findViewById(R.id.sp_status);

        statusList = new ArrayList<>();
        statusList.add("Enquiry");statusList.add("Feedback");statusList.add("Other");
        statusList.add("Query");statusList.add("Ticket Query");statusList.add("Suggestion");

        sp_status.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.custom_spinner,statusList));

        btn_send.setOnClickListener(this);
        ed_refer_ticket.setOnClickListener(this);
        /*ed_refer_ticket.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                startActivity(new Intent(getApplicationContext(),MDAllTicketListActivity.class));
                overridePendingTransition(R.anim.enter,R.anim.exit);
                return false;
            }
        });*/
    }

    private void generateQuery(){
        try {
            int clientAuto = 0;
            String type, _description = null, _imagePath = "NA", ticketNo;

            String emp_type = FirstActivity.pref.getString(getString(R.string.pref_emptype), "");

            type = statusList.get(sp_status.getSelectedItemPosition());
            ticketNo = ed_refer_ticket.getText().toString();
            if(!ticketNo.equals("")){
                String ticketArr[] = ticketNo.split("\\-");
                ticketNo = ticketArr[0];
            }else{
                ticketNo = "NA";
            }

            if (emp_type.equals("C")) {
                clientAuto = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
                _description = ed_description.getText().toString();
            } else {
                clientAuto = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
                _description = ed_description.getText().toString();
            }

            type = URLEncoder.encode(type, "UTF-8");
            ticketNo = URLEncoder.encode(ticketNo, "UTF-8");
            _description = URLEncoder.encode(_description, "UTF-8");
            _imagePath = URLEncoder.encode(_imagePath, "UTF-8");

            String url = Constant.ipaddress + "/AddQueryMaster?clientAuto=" + clientAuto +
                    "&ticketNo=" + ticketNo + "&type=" + type + "&desc=" + _description + "&imagePath=" + _imagePath +
                    "&CrBy=" + clientAuto;

            writeLog("ReachToMDActivity_generateTicket_" + url);
            Constant.showLog(url);

            if (ConnectivityTest.getNetStat(getApplicationContext())) {
                new saveQuery().execute(url);
            } else {
                writeLog("ReachToMDActivity_generateTicket_Network_Connection_Error");
                toast.setText("Network Connection Error");
                toast.show();
            }
        }catch (Exception e) {
            writeLog("ReachToMDActivity_generateTicket_" + e.getMessage());
            e.printStackTrace();
        }
    }

    private class saveQuery extends AsyncTask<String,Void,String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ReachToMDActivity.this);
            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return Post.POST(strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();
            result = result.replace("\\", "");
            result = result.replace("''", "");
            result = result.replace("\"", "");
            Constant.showLog(result);
            if (!result.equals("0") && !result.equals("")) {
                writeLog("ReachToMDActivity_saveTicket_Success");
                showDia(1);
            } else {
                writeLog("ReachToMDActivity_saveTicket_UnSuccess");
                showDia(2);
            }
        }
    }

    private void setData(){
        ed_refer_ticket.setText(ticketRefer);
        ed_description.setText(viewReachToMD.getParticular());
        String spType = viewReachToMD.getType();
        for(int i=0;i<statusList.size();i++){
            if(spType.equals(statusList.get(i))){
                sp_status.setSelection(i);
                break;
            }
        }
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReachToMDActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(ReachToMDActivity.this).doFinish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else if (a == 1) {
            builder.setMessage("Query Save Successfully");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btn_send.getWindowToken(),0);
                    new Constant(ReachToMDActivity.this).doFinish();

                    dialog.dismiss();
                }
            });

        }else if (a == 2) {
            builder.setMessage("Error While Saving Data");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    generateQuery();
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

    private void writeLog(String _data) {
        new WriteLog().writeLog(getApplicationContext(), "ReachToMD_" + _data);
    }
}

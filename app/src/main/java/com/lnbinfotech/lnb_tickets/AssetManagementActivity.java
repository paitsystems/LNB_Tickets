package com.lnbinfotech.lnb_tickets;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lnbinfotech.lnb_tickets.adapter.AssetManagementAdapter;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.model.AssetClass;
import com.lnbinfotech.lnb_tickets.parse.ParseJSON;
import com.lnbinfotech.lnb_tickets.post.Post;

import java.util.ArrayList;
import java.util.List;

public class AssetManagementActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText ed_search;
    private Toast toast;
    private Constant constant;
    private AdView mAdView;
    private RadioButton rdo_desktop, rdo_server, rdo_laptop, rdo_router, rdo_printer, rdo_other;
    private LinearLayout lay_assettype;
    public static String  machineType = "";
    public static int clientAuto = 0, isUpdated = 0;
    private ListView listView;
    private AssetManagementAdapter adapter;
    private List<AssetClass> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_management);

        init();

        ed_search.setOnClickListener(this);
        rdo_desktop.setOnClickListener(this);
        rdo_server.setOnClickListener(this);
        rdo_laptop.setOnClickListener(this);
        rdo_router.setOnClickListener(this);
        rdo_printer.setOnClickListener(this);
        rdo_other.setOnClickListener(this);

        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest;
        if(Constant.liveTestFlag==1) {
            adRequest = new AdRequest.Builder().build();
        }else {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(Constant.adMobID)
                    .build();
        }

        mAdView.loadAd(adRequest);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AssetClass asset = list.get(i);
                String type = FirstActivity.pref.getString(getString(R.string.pref_emptype), "");
                int mode;
                if(type.equals("C")){
                    mode = 1;
                }else{
                    mode = 2;
                }
                Constant.showLog(asset.getMachineType());
                if(asset.getMachineType().equals("Desktop") || asset.getMachineType().equals("Server") ||
                        asset.getMachineType().equals("Laptop")){
                    startNewActivity(R.id.lay_desktop,mode,asset);
                }else if(asset.getMachineType().equals("Router")){
                    startNewActivity(R.id.lay_router,mode,asset);
                }else if(asset.getMachineType().equals("Printer")){
                    startNewActivity(R.id.lay_printer,mode,asset);
                }else if(asset.getMachineType().equals("Other")){
                    startNewActivity(R.id.lay_other,mode,asset);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAdView!=null){
            mAdView.resume();
        }
        if(AddNewTicketActivity.selBranch!=null){
            ed_search.setText(AddNewTicketActivity.selBranch);
            if(isUpdated==1) {
                isUpdated=0;
                getAssetInfo();
            }
        }
        constant = new Constant(AssetManagementActivity.this);
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
        machineType = "";
        clientAuto = 0;
        AddNewTicketActivity.selBranch="";
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lay_attachment:
                showDia(5);
                break;
            case R.id.ed_search:
                startActivity(new Intent(getApplicationContext(),ClientSearchActivity.class));
                overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
            case R.id.btn_attachment:
                showDia(5);
                break;
            case R.id.btn_generate_ticket:
                break;
            case R.id.rdo_desktop:
                rdo_desktop.setChecked(true);
                rdo_server.setChecked(false);
                rdo_laptop.setChecked(false);
                rdo_printer.setChecked(false);
                rdo_router.setChecked(false);
                rdo_other.setChecked(false);
                machineType = "Desktop";
                startNewActivity(R.id.lay_desktop,0,new AssetClass());
                break;
            case R.id.rdo_server:
                rdo_desktop.setChecked(false);
                rdo_server.setChecked(true);
                rdo_laptop.setChecked(false);
                rdo_printer.setChecked(false);
                rdo_router.setChecked(false);
                rdo_other.setChecked(false);
                machineType = "Server";
                startNewActivity(R.id.lay_desktop,0,new AssetClass());
                break;
            case R.id.rdo_laptop:
                rdo_desktop.setChecked(false);
                rdo_server.setChecked(false);
                rdo_laptop.setChecked(true);
                rdo_printer.setChecked(false);
                rdo_router.setChecked(false);
                rdo_other.setChecked(false);
                machineType = "Laptop";
                startNewActivity(R.id.lay_desktop,0,new AssetClass());
                break;
            case R.id.rdo_printer:
                rdo_desktop.setChecked(false);
                rdo_server.setChecked(false);
                rdo_laptop.setChecked(false);
                rdo_printer.setChecked(true);
                rdo_router.setChecked(false);
                rdo_other.setChecked(false);
                machineType = "Printer";
                startNewActivity(R.id.lay_printer,0,new AssetClass());
                break;
            case R.id.rdo_router:
                rdo_desktop.setChecked(false);
                rdo_server.setChecked(false);
                rdo_laptop.setChecked(false);
                rdo_printer.setChecked(false);
                rdo_router.setChecked(true);
                rdo_other.setChecked(false);
                machineType = "Router";
                startNewActivity(R.id.lay_router,0,new AssetClass());
                break;
            case R.id.rdo_other:
                rdo_desktop.setChecked(false);
                rdo_server.setChecked(false);
                rdo_laptop.setChecked(false);
                rdo_printer.setChecked(false);
                rdo_router.setChecked(false);
                rdo_other.setChecked(true);
                machineType = "Other";
                startNewActivity(R.id.lay_other,0,new AssetClass());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new Constant(AssetManagementActivity.this).doFinish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String type = FirstActivity.pref.getString(getString(R.string.pref_emptype), "");
        if(type.equals("E")) {
            getMenuInflater().inflate(R.menu.assetactivity_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                new Constant(AssetManagementActivity.this).doFinish();
                break;
            case R.id.assetadd:
                if(AddNewTicketActivity.selBranch!=null) {
                    if (!AddNewTicketActivity.selBranch.equals("")) {
                        lay_assettype.setVisibility(View.VISIBLE);
                    } else {
                        toast.setText("Please Select Client");
                        toast.show();
                    }
                }else {
                    toast.setText("Please Select Client");
                    toast.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        ed_search = (EditText) findViewById(R.id.ed_search);
        toast = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        constant = new Constant(AssetManagementActivity.this);
        FirstActivity.pref = getSharedPreferences(FirstActivity.PREF_NAME,MODE_PRIVATE);

        rdo_desktop = (RadioButton) findViewById(R.id.rdo_desktop);
        rdo_server = (RadioButton) findViewById(R.id.rdo_server);
        rdo_laptop = (RadioButton) findViewById(R.id.rdo_laptop);
        rdo_router = (RadioButton) findViewById(R.id.rdo_router);
        rdo_printer = (RadioButton) findViewById(R.id.rdo_printer);
        rdo_other = (RadioButton) findViewById(R.id.rdo_other);

        lay_assettype = (LinearLayout) findViewById(R.id.lay_assettype);

        listView = (ListView) findViewById(R.id.listview);
        list = new ArrayList<>();
    }

    private void showDia(int a){
        AlertDialog.Builder builder = new AlertDialog.Builder(AssetManagementActivity.this);
        builder.setCancelable(false);
        if(a==0) {
            builder.setMessage("Do You Want To Go Back?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(AssetManagementActivity.this).doFinish();
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

    private void getAssetInfo(){
        if(!AddNewTicketActivity.selBranch.equals("")) {
            clientAuto = new DBHandler(getApplicationContext()).getClientAuto(AddNewTicketActivity.selBranch);
            String url = Constant.ipaddress + "/GetAssetInfo?clientAuto=" + clientAuto;
            Constant.showLog(url);
            new getAssetInfo().execute(url);
        }
    }

    private class getAssetInfo extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(AssetManagementActivity.this);
            pd.setMessage("Please Wait");
            pd.setCancelable(false);
            pd.show();
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
                response = response.replace("\\","");
                response = response.replace("''","");
                response = response.substring(1, response.length() - 1);
                list.clear();
                listView.setAdapter(null);
                list = new ParseJSON(response,getApplicationContext()).parseAssetInfo();
                if(!list.isEmpty()){
                    adapter = new AssetManagementAdapter(getApplicationContext(),list);
                    listView.setAdapter(adapter);
                }else{
                    toast.setText("No Record Available");
                    toast.show();
                }
            }else{
                toast.setText("Please Try Again");
                toast.show();
            }
        }
    }

    private void startNewActivity(int id, int mode, AssetClass asset){
        Intent intent = new Intent(getApplicationContext(),AddAssetInfo.class);
        intent.putExtra("id",id);
        intent.putExtra("mode",mode);
        intent.putExtra("asset",asset);
        startActivity(intent);
        overridePendingTransition(R.anim.enter,R.anim.exit);
    }

    private void writeLog(String _data){
        new WriteLog().writeLog(getApplicationContext(),_data);
    }

}


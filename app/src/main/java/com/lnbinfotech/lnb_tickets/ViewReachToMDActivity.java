package com.lnbinfotech.lnb_tickets;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lnbinfotech.lnb_tickets.adapter.ViewReachToMDAdapter;
import com.lnbinfotech.lnb_tickets.connectivity.ConnectivityTest;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.model.ViewReachToMDClass;
import com.lnbinfotech.lnb_tickets.post.Post;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Locale;

public class ViewReachToMDActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private ArrayList<ViewReachToMDClass> list = new ArrayList<>();
    private ListView listView;
    private ViewReachToMDAdapter adapter;
    private EditText ed_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewreachtomd);

        init();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(ConnectivityTest.getNetStat(getApplicationContext())){
            loadData();
        }else{
            toast.setText("You Are Offline");
            toast.show();
        }

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
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(listView.getWindowToken(),0);
                ViewReachToMDClass ticketMast = (ViewReachToMDClass) listView.getItemAtPosition(i);
                String str = ticketMast.getTicketNo()+"-"+ticketMast.getParticular();
                Constant.showLog(str);
                Intent intent = new Intent(getApplicationContext(), ReachToMDActivity.class);
                intent.putExtra("type","E");
                ReachToMDActivity.ticketRefer = ticketMast.getTicketNo();
                intent.putExtra("data",ticketMast);
                startActivity(intent);
                overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //showDia(0);
        new Constant(ViewReachToMDActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(ViewReachToMDActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        constant = new Constant(ViewReachToMDActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        listView = (ListView) findViewById(R.id.listView);
        ed_search = (EditText) findViewById(R.id.ed_search);
        FirstActivity.pref = getSharedPreferences(FirstActivity.PREF_NAME,MODE_PRIVATE);
    }

    private void loadData() {
        int clientAuto;
        String emp_type = FirstActivity.pref.getString(getString(R.string.pref_emptype), "");

        if (emp_type.equals("C")) {
            clientAuto = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
        } else {
            clientAuto = FirstActivity.pref.getInt(getString(R.string.pref_auto), 0);
        }

        String url = Constant.ipaddress + "/GetNewQueryMaster?clientAuto="+clientAuto+"&userType="+emp_type+"&autoId=0&crDate=12/Feb/2018";
        Constant.showLog(url);
        new getQueryData().execute(url);
    }

    private class getQueryData extends AsyncTask<String,Void,String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ViewReachToMDActivity.this);
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
            if(!result.equals("")) {
                result = result.replace("\\", "");
                result = result.replace("''", "");
                result = result.substring(1,result.length()-1);
                Constant.showLog(result);
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    Constant.showLog("" + jsonArray.length());
                    if (jsonArray.length() >= 1) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ViewReachToMDClass mdClass = new ViewReachToMDClass();
                            mdClass.setAuto(jsonArray.getJSONObject(i).getInt("auto"));
                            mdClass.setId(jsonArray.getJSONObject(i).getInt("id"));
                            mdClass.setClientid(jsonArray.getJSONObject(i).getInt("ClientAuto"));
                            mdClass.setCrby(jsonArray.getJSONObject(i).getString("CrBy"));
                            mdClass.setTicketNo(jsonArray.getJSONObject(i).getString("ticketNo"));
                            mdClass.setParticular(jsonArray.getJSONObject(i).getString("Particular"));
                            mdClass.setAttachment(jsonArray.getJSONObject(i).getString("attachment"));
                            mdClass.setCrDate(jsonArray.getJSONObject(i).getString("CrDate"));
                            mdClass.setCrTime(jsonArray.getJSONObject(i).getString("CrTime"));
                            mdClass.setType(jsonArray.getJSONObject(i).getString("type"));
                            list.add(mdClass);
                            setData();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toast.setText("Something Went Wrong");
                    toast.show();
                    writeLog("ParseJSON_parseQuery_" + e.getMessage());
                }
            }else{
                toast.setText("No Record Available");
                toast.show();
            }
        }
    }

    private void setData(){
        listView.setAdapter(null);
        adapter = new ViewReachToMDAdapter(getApplicationContext(),list);
        listView.setAdapter(adapter);
    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewReachToMDActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(ViewReachToMDActivity.this).doFinish();
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
        new WriteLog().writeLog(getApplicationContext(), "ViewReachToMDActivity_" + _data);
    }
}

package com.lnbinfotech.lnb_tickets;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lnbinfotech.lnb_tickets.adapter.ClientSearchAdapter;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientSearchActivity extends AppCompatActivity implements View.OnClickListener {

    EditText ed_search;
    ListView listView;
    DBHandler db;
    List<String> branchList;
    AdView mAdView;
    ClientSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_search);

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

        //mAdView.loadAd(adRequest);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                AddNewTicketActivity.selBranch = (String) listView.getItemAtPosition(i);
                new Constant(ClientSearchActivity.this).doFinish();
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
        new Constant(ClientSearchActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(ClientSearchActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void init() {
        ed_search = (EditText) findViewById(R.id.ed_search);
        listView = (ListView) findViewById(R.id.listView);
        db = new DBHandler(getApplicationContext());
        branchList = new ArrayList<>();
        branchList = db.getDistinctBranch();
        if(branchList.size()==0) {
            branchList.add("NA");
        }
        adapter = new ClientSearchAdapter(branchList, getApplicationContext());
        listView.setAdapter(adapter);
    }

    void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClientSearchActivity.this);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(ClientSearchActivity.this).doFinish();
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

}

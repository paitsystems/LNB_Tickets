package com.lnbinfotech.lnb_tickets;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;
import android.view.Gravity;

import com.lnbinfotech.lnb_tickets.adapter.MDAllticketListAdapter;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MDAllTicketListActivity extends AppCompatActivity implements View.OnClickListener {

    private Constant constant, constant1;
    private Toast toast;
    private EditText ed_search;
    private ListView listView;
    private DBHandler db;
    private MDAllticketListAdapter adapter;
    private List<TicketMasterClass> ticketMastList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rdallticketactivity);

        init();

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
                TicketMasterClass ticketMast = (TicketMasterClass) listView.getItemAtPosition(i);
                String str = ticketMast.getTicketNo()+"-"+ticketMast.getParticular();
                Constant.showLog(str);
                ReachToMDActivity.ticketRefer = str;
                new Constant(MDAllTicketListActivity.this).doFinish();
            }
        });

        setData();
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
        new Constant(MDAllTicketListActivity.this).doFinish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //showDia(0);
                new Constant(MDAllTicketListActivity.this).doFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        constant = new Constant(MDAllTicketListActivity.this);
        constant1 = new Constant(getApplicationContext());
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        ed_search = (EditText) findViewById(R.id.ed_search);
        listView = (ListView) findViewById(R.id.listView);
        db = new DBHandler(getApplicationContext());
        FirstActivity.pref = getSharedPreferences(FirstActivity.PREF_NAME,MODE_PRIVATE);
    }

    private void setData(){
        ticketMastList = new ArrayList<>();
        Cursor res = db.getAllMDTicket();
        if(res.moveToFirst()){
            do{
                TicketMasterClass ticketMastClass = new TicketMasterClass();
                ticketMastClass.setTicketNo(res.getString(res.getColumnIndex(DBHandler.TicketM_TicketNo)));
                ticketMastClass.setParticular(res.getString(res.getColumnIndex(DBHandler.TicketM_Particular)));
                ticketMastClass.setCrDate(res.getString(res.getColumnIndex(DBHandler.TicketM_CrDate)));
                ticketMastClass.setStatus(res.getString(res.getColumnIndex(DBHandler.TicketM_Status)));
                ticketMastList.add(ticketMastClass);
            }while(res.moveToNext());
        }
        res.close();
        adapter = new MDAllticketListAdapter( getApplicationContext(),ticketMastList);
        listView.setAdapter(adapter);

    }

    private void showDia(int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MDAllTicketListActivity.this);
        builder.setCancelable(false);
        if (a == 0) {
            builder.setMessage("Do You Want To Exit App?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Constant(MDAllTicketListActivity.this).doFinish();
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
        new WriteLog().writeLog(getApplicationContext(), "ProductSearchActivity_" + _data);
    }
}

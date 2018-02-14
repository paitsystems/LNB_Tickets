package com.lnbinfotech.lnb_tickets.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.lnbinfotech.lnb_tickets.FirstActivity;
import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.ReplyResponseActivity;
import com.lnbinfotech.lnb_tickets.UpdateTicketActivity;
import com.lnbinfotech.lnb_tickets.adapter.AllMessageAdapter;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.model.MessageClass;

import java.util.ArrayList;
import java.util.Locale;

//Created by lnb on 9/19/2017.

public class MessageFragment extends Fragment {

    private ListView listView;
    private AllMessageAdapter adapter;
    private DBHandler db;
    private EditText ed_search;

    public MessageFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.showLog("PendingFragments_onResume");
        setData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message,container,false);
        listView = (ListView) view.findViewById(R.id.listView);
        db = new DBHandler(getContext());
        ed_search = (EditText) view.findViewById(R.id.ed_search);
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
                MessageClass msg = (MessageClass) listView.getItemAtPosition(i);
                UpdateTicketActivity.auto = msg.getTdMastAuto();
                Intent intent1 = new Intent(getContext(),ReplyResponseActivity.class);
                intent1.putExtra("from","M");
                startActivity(intent1);
                getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });

        //setData();
        return view;
    }

    private void setData(){
        String crby = FirstActivity.pref.getString(getString(R.string.pref_ClientName),"");
        int auto = FirstActivity.pref.getInt(getString(R.string.pref_auto),0);
        String type = FirstActivity.pref.getString(getString(R.string.pref_emptype),"");
        ArrayList<MessageClass> list = new ArrayList<>();
        Cursor res = db.getTicketDetail(auto,type);
        if(res.moveToFirst()){
            do{
                MessageClass msg = new MessageClass();
                msg.setTmAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_Auto)));
                msg.setTmClientAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_ClientAuto)));
                msg.setTmClientId(res.getString(res.getColumnIndex(DBHandler.SMLMAST_ClientID)));
                msg.setTmTicketno(res.getString(res.getColumnIndex(DBHandler.TicketM_TicketNo)));
                msg.setTmSubject(res.getString(res.getColumnIndex(DBHandler.TicketM_Subject)));
                msg.setTmStatus(res.getString(res.getColumnIndex(DBHandler.TicketM_Status)));
                msg.setTdAuto(res.getInt(res.getColumnIndex(DBHandler.TicketD_Auto)));
                msg.setTdMastAuto(res.getInt(res.getColumnIndex(DBHandler.TicketD_MastAuto)));
                msg.setTdDescription(res.getString(res.getColumnIndex(DBHandler.TicketD_Description)));
                msg.setTdCrBy(res.getString(res.getColumnIndex(DBHandler.TicketD_CrBy)));
                msg.setTdCrDate(res.getString(res.getColumnIndex(DBHandler.TicketD_CrDate)));
                msg.setTdCrTime(res.getString(res.getColumnIndex(DBHandler.TicketD_CrTime)));
                list.add(msg);
            }while (res.moveToNext());
        }
        res.close();
        adapter = new AllMessageAdapter(getContext(),list);
        listView.setAdapter(adapter);
    }

}

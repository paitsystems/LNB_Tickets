package com.lnbinfotech.lnb_tickets.fragments;

//Created by lnb on 9/19/2017.

import android.content.Intent;
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
import com.lnbinfotech.lnb_tickets.UpdateTicketActivity;
import com.lnbinfotech.lnb_tickets.adapter.AllTicketListAdapter;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;

import java.util.ArrayList;
import java.util.Locale;

public class CompleteFragments extends Fragment{

    private ListView listView;
    private AllTicketListAdapter adapter;
    private DBHandler db;
    private EditText ed_search;
    public static int selPos;

    public CompleteFragments(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete,container,false);
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
                selPos = i;
                TicketMasterClass pendingTicketClass = (TicketMasterClass) listView.getItemAtPosition(i);
                Intent intent = new Intent(getContext(),UpdateTicketActivity.class);
                intent.putExtra("data",pendingTicketClass);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });

        //setData();
        return view;
    }

    private void setData(){
        String isHWApplicable = FirstActivity.pref.getString(getString(R.string.pref_isHWapplicable),"");
        ArrayList<TicketMasterClass> pendingTicketClassList = db.getClosedTicket(isHWApplicable);
        if (pendingTicketClassList.size()!= 0) {
            listView.setAdapter(null);
            adapter = new AllTicketListAdapter(getContext(),pendingTicketClassList);
            listView.setAdapter(adapter);
        }
    }

}

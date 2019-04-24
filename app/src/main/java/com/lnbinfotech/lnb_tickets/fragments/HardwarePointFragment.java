package com.lnbinfotech.lnb_tickets.fragments;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.lnbinfotech.lnb_tickets.FirstActivity;
import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.UpdateTicketActivity;
import com.lnbinfotech.lnb_tickets.adapter.AllTicketListAdapter;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;

import java.util.ArrayList;
import java.util.Locale;

//Created by lnb on 9/19/2017.

public class HardwarePointFragment extends Fragment {

    private ListView listView;
    private AllTicketListAdapter adapter;
    private DBHandler db;
    private EditText ed_search;
    public static int selPos;
    private String searchText = null;
    private int pagecount = 5;
    private ArrayList<TicketMasterClass> pendingTicketClassList;

    public HardwarePointFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.showLog("HardwarePointFragment_onResume");
        setData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hardware,container,false);
        listView = view.findViewById(R.id.listView);
        db = new DBHandler(getContext());

        Button btnLoadMore = new Button(getContext());
        btnLoadMore.setText("Load More");
        listView.addFooterView(btnLoadMore);

        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                pagecount++;
                String isHWApplicable = FirstActivity.pref.getString(getString(R.string.pref_isHWapplicable), "");
                int a = pendingTicketClassList.size()-5;
                pendingTicketClassList.clear();
                pendingTicketClassList.addAll(db.getHardwarePointLM(pagecount));
                listView.setAdapter(adapter);
                listView.setSelection(a);
            }
        });

        ed_search = view.findViewById(R.id.ed_search);
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchText = ed_search.getText().toString().toLowerCase(Locale.getDefault());
                if(adapter!=null) {
                    adapter.filter(searchText);
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
        pendingTicketClassList = db.getHardwarePointLM(pagecount);
        if (pendingTicketClassList.size()!= 0) {
            if(searchText==null) {
                listView.setAdapter(null);
                adapter = new AllTicketListAdapter(getContext(), pendingTicketClassList);
                listView.setAdapter(adapter);
            }else{
                listView.setAdapter(null);
                adapter = new AllTicketListAdapter(getContext(), pendingTicketClassList);
                listView.setAdapter(adapter);
                if(adapter!=null) {
                    adapter.filter(searchText);
                }
            }
        }
    }

}

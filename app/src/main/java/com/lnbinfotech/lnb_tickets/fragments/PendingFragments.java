package com.lnbinfotech.lnb_tickets.fragments;

//Created by lnb on 9/19/2017.

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lnbinfotech.lnb_tickets.FirstActivity;
import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.UpdateTicketActivity;
import com.lnbinfotech.lnb_tickets.adapter.AllTicketListAdapter;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PendingFragments extends Fragment{

    private ListView listView;
    private AllTicketListAdapter adapter;
    private DBHandler db;
    private EditText ed_search;
    public static int selPos;
    private String searchText = null;

    public PendingFragments(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.showLog("PendingFragments_onResume");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending,container,false);
        listView = view.findViewById(R.id.listView);
        db = new DBHandler(getContext());
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
                /*Intent intent = new Intent(getContext(),UpdateTicketActivity.class);
                intent.putExtra("data",pendingTicketClass);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);*/
                showTicket(pendingTicketClass);
            }
        });

        setData();
        return view;
    }

    private void showTicket(final TicketMasterClass pendingTicketClass) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
        //dialogBuilder.setTitle("Ticket Details");
        dialogBuilder.setMessage("Ticket Details");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_ticket_detail, null);

        TextView tv_date = view.findViewById(R.id.tv_date);
        TextView tv_time = view.findViewById(R.id.tv_time);
        TextView tv_subject = view.findViewById(R.id.tv_subject);
        TextView tv_desc = view.findViewById(R.id.tv_desc);
        TextView tv_status = view.findViewById(R.id.tv_status);
        TextView tv_comments = view.findViewById(R.id.tv_comments);
        TextView tv_ticket_no = view.findViewById(R.id.tv_ticket_no);
        TextView tv_assignto = view.findViewById(R.id.tv_assignto);
        TextView tv_client_name = view.findViewById(R.id.tv_client_name);
        TextView tv_cr_by = view.findViewById(R.id.tv_cr_by);
        TextView tv_closed_by = view.findViewById(R.id.tv_closed_by);
        TextView tv_closed_date = view.findViewById(R.id.tv_closed_date);
        TextView tv_closed_time = view.findViewById(R.id.tv_closed_time);
        LinearLayout lay_closed_by = view.findViewById(R.id.lay_closed_by);
        ImageView img =  view.findViewById(R.id.img);

        tv_cr_by.setText(pendingTicketClass.getCrBy());
        tv_date.setText(pendingTicketClass.getCrDate());
        String _time = pendingTicketClass.getCrTime();
        String time[] = _time.split("\\:");
        if(time.length>1) {
            String s = time[0]+":"+time[1];
            tv_time.setText(s);
        }else{
            tv_time.setText(_time);
        }
        tv_subject.setText(pendingTicketClass.getSubject());
        String desc = pendingTicketClass.getParticular();
        tv_desc.setText(desc);
        String status = pendingTicketClass.getStatus();
        if (status.equals("Closed")) {
            tv_status.setTextColor(getContext().getResources().getColor(R.color.darkgreen));
            if (!pendingTicketClass.getModBy().equals("null")) {
                lay_closed_by.setVisibility(View.VISIBLE);
                tv_closed_by.setText(pendingTicketClass.getModBy());
                tv_closed_date.setText(pendingTicketClass.getModDate());

                String _modtime = pendingTicketClass.getModTime();
                String modtime[] = _modtime.split("\\:");
                if (modtime.length > 1) {
                    String s = modtime[0] + ":" + modtime[1];
                    tv_closed_time.setText(s);
                } else {
                    tv_closed_time.setText(_modtime);
                }
            }
        } else {
            tv_status.setTextColor(getContext().getResources().getColor(R.color.red));
            lay_closed_by.setVisibility(View.GONE);
        }
        tv_status.setText(status);
        tv_ticket_no.setText(pendingTicketClass.getTicketNo());
        tv_assignto.setText(pendingTicketClass.getAssignTO());
        tv_client_name.setText(pendingTicketClass.getClientName());

       /* String imageName = pendingTicketClass.getImagePAth();
        if(!imageName.equals("") && imageName!=null) {
            img.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(Constant.imgIpaddress+imageName)
                    .thumbnail(1f)
                    .crossFade()
                    .placeholder(R.drawable.ic_male)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .override(100,100)
                    .into(img);
        }*/
        dialogBuilder.setButton(Dialog.BUTTON_NEGATIVE,"Update",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(getContext(),UpdateTicketActivity.class);
                intent.putExtra("data",pendingTicketClass);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
            }
        });
        dialogBuilder.setButton(Dialog.BUTTON_POSITIVE,"Back",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.setView(view);
        dialogBuilder.show();
    }

    private void setData(){
        Constant.showLog("setData");

        String isHWApplicable = FirstActivity.pref.getString(getString(R.string.pref_isHWapplicable),"");

        /*Observable<ArrayList<TicketMasterClass>> observable = Observable
                .just(db.getPendingTicket(isHWApplicable));

        Observer<ArrayList<TicketMasterClass>> observer = new Observer<ArrayList<TicketMasterClass>>() {
            @Override
            public void onCompleted() {
                Constant.showLog("All data emitted.");
            }
            @Override
            public void onError(Throwable e) {
                Constant.showLog("Error received: " + e.getMessage());
            }
            @Override
            public void onNext(ArrayList<TicketMasterClass> pendingTicketClassList) {
                Constant.showLog("onNext");
                adapter = new AllTicketListAdapter(getContext(), pendingTicketClassList);
                listView.setAdapter(adapter);
                *//*if (pendingTicketClassList.size()!= 0) {
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
                }*//*
            }
        };

        Subscription subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);*/

        ArrayList<TicketMasterClass> pendingTicketClassList = db.getPendingTicket(isHWApplicable);
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

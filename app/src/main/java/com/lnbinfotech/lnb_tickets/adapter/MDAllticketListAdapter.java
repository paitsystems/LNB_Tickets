package com.lnbinfotech.lnb_tickets.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//Created by ANUP on 2/10/2018.

public class MDAllticketListAdapter extends BaseAdapter {

    Context context;
    List<TicketMasterClass> ticketMastList;
    List<TicketMasterClass> _ticketClassList;

    public MDAllticketListAdapter(Context _context, List<TicketMasterClass> __ticketMastList){
        this.context = _context;
        this.ticketMastList = __ticketMastList;
        this._ticketClassList = new ArrayList<>();
        this._ticketClassList.addAll(this.ticketMastList);
    }

    @Override
    public int getCount() {
        return ticketMastList.size();
    }

    @Override
    public Object getItem(int i) {
        return ticketMastList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_mdallticket, null);
            holder = new ViewHolder();
            holder.tv_crDate = (TextView) view.findViewById(R.id.tv_crDate);
            holder.tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            holder.tv_status = (TextView) view.findViewById(R.id.tv_status);
            holder.tv_ticket_no = (TextView) view.findViewById(R.id.tv_ticket_no);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        TicketMasterClass pendingTicketClass = (TicketMasterClass) getItem(i);
        holder.tv_crDate.setText(pendingTicketClass.getCrDate());

        String desc = pendingTicketClass.getParticular();
        if(desc.length()>100){
            desc = desc.substring(0,99)+"...";
        }
        holder.tv_desc.setText(desc);
        String status = pendingTicketClass.getStatus();
        holder.tv_status.setText(status);
        holder.tv_ticket_no.setText(pendingTicketClass.getTicketNo());
        return view;
    }

    private class ViewHolder{
        TextView tv_crDate, tv_desc, tv_status, tv_ticket_no;
    }

    public void filter(String searchText){
        if(_ticketClassList.size()!=0 && ticketMastList.size()!=0) {
            searchText = searchText.toLowerCase().toLowerCase(Locale.getDefault());
            ticketMastList.clear();
            if (searchText.length() == 0) {
                ticketMastList.addAll(_ticketClassList);
            } else {
                for (TicketMasterClass ticketMasterClass : _ticketClassList) {
                    if (ticketMasterClass.getCrDate().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        ticketMastList.add(ticketMasterClass);
                    } else if (ticketMasterClass.getTicketNo().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        ticketMastList.add(ticketMasterClass);
                    } else if (ticketMasterClass.getStatus().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        ticketMastList.add(ticketMasterClass);
                    } else if (ticketMasterClass.getParticular().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        ticketMastList.add(ticketMasterClass);
                    }
                }
            }
            notifyDataSetChanged();
        }else if(_ticketClassList.size()!=0 && ticketMastList.size()==0) {
            ticketMastList.addAll(_ticketClassList);
        }
    }

}

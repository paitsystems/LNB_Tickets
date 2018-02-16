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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//Created by lnb on 8/12/2017.

public class AllTicketListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<TicketMasterClass> pendingTicketClassList;
    private ArrayList<TicketMasterClass> _pendingTicketClassList;
    private Date todayDate;

    public AllTicketListAdapter(Context _context, ArrayList<TicketMasterClass> _pendingTicketClassList) {
        this.context = _context;
        this.pendingTicketClassList = _pendingTicketClassList;
        this._pendingTicketClassList = new ArrayList<>();
        this._pendingTicketClassList.addAll(this.pendingTicketClassList);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        todayDate = parseDate(day + "/" + month + "/" + year);
    }

    @Override
    public int getCount() {
        return pendingTicketClassList.size();
    }

    @Override
    public Object getItem(int i) {
        return pendingTicketClassList.get(i);
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
            view = inflater.inflate(R.layout.list_item_all_ticket_list, null);
            //view = LayoutInflater.from(context).inflate(R.layout.list_item_all_ticket_list, viewGroup,false);
            holder = new ViewHolder();
            holder.tv_date = (TextView) view.findViewById(R.id.tv_date);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_subject = (TextView) view.findViewById(R.id.tv_subject);
            holder.tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            holder.tv_status = (TextView) view.findViewById(R.id.tv_status);
            holder.tv_comments = (TextView) view.findViewById(R.id.tv_comments);
            holder.tv_ticket_no = (TextView) view.findViewById(R.id.tv_ticket_no);
            holder.tv_assignto = (TextView) view.findViewById(R.id.tv_assignto);
            holder.tv_client_name = (TextView) view.findViewById(R.id.tv_client_name);
            holder.tv_cr_by = (TextView) view.findViewById(R.id.tv_cr_by);
            holder.tv_closed_by = (TextView) view.findViewById(R.id.tv_closed_by);
            holder.tv_closed_date = (TextView) view.findViewById(R.id.tv_closed_date);
            holder.tv_closed_time = (TextView) view.findViewById(R.id.tv_closed_time);
            holder.lay_closed_by = (LinearLayout) view.findViewById(R.id.lay_closed_by);
            holder.lay = (LinearLayout) view.findViewById(R.id.lay);
            holder.tv_point_type = (TextView) view.findViewById(R.id.tv_point_type);

            //holder.status_lay = (LinearLayout) view.findViewById(R.id.status_lay);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        TicketMasterClass pendingTicketClass = (TicketMasterClass) getItem(i);
        holder.tv_cr_by.setText(pendingTicketClass.getCrBy());
        holder.tv_date.setText(pendingTicketClass.getCrDate());
        String _time = pendingTicketClass.getCrTime();
        String time[] = _time.split("\\:");
        if(time.length>1) {
            String s = time[0]+":"+time[1];
            holder.tv_time.setText(s);
        }else{
            holder.tv_time.setText(_time);
        }
        holder.tv_subject.setText(pendingTicketClass.getSubject());
        String desc = pendingTicketClass.getParticular();
        if(desc.length()>100){
            desc = desc.substring(0,99)+"...";
        }
        holder.tv_desc.setText(desc);
        String status = pendingTicketClass.getStatus();
        if(status.equals("Closed")){
            //holder.tv_status.setBackground(context.getResources().getDrawable(R.drawable.status_btn_draw_green));
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.darkgreen));
            if(!pendingTicketClass.getModBy().equals("null")) {
                holder.lay_closed_by.setVisibility(View.VISIBLE);
                holder.tv_closed_by.setText(pendingTicketClass.getModBy());
                holder.tv_closed_date.setText(pendingTicketClass.getModDate());
                //holder.tv_closed_time.setText(pendingTicketClass.getModTime());

                String _modtime = pendingTicketClass.getModTime();
                String modtime[] = _modtime.split("\\:");
                if (modtime.length > 1) {
                    String s = modtime[0] + ":" + modtime[1];
                    holder.tv_closed_time.setText(s);
                } else {
                    holder.tv_closed_time.setText(_modtime);
                }
            }
        }else {
            //holder.tv_status.setBackground(context.getResources().getDrawable(R.drawable.status_btn_draw_red));
            if (status.equals("Open") || status.equals("ReOpen") || status.equals("Pending")) {
                String crdt = pendingTicketClass.getCrDate();
                Date crDate = parseDate1(crdt);
                if (todayDate.compareTo(crDate) == 0) {
                    holder.lay.setBackgroundResource(R.color.lightyellow);
                } else {
                    holder.lay.setBackgroundResource(0);
                }
            }
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
            holder.lay_closed_by.setVisibility(View.GONE);
        }
        holder.tv_status.setText(status);
        holder.tv_ticket_no.setText(pendingTicketClass.getTicketNo());
        holder.tv_assignto.setText(pendingTicketClass.getAssignTO());
        holder.tv_client_name.setText(pendingTicketClass.getClientName());
        holder.tv_point_type.setText(pendingTicketClass.getPointtype());
        return view;
    }

    private class ViewHolder{
        TextView tv_date, tv_time, tv_subject, tv_desc, tv_status, tv_comments, tv_ticket_no, tv_assignto,
                    tv_client_name,tv_cr_by, tv_closed_by, tv_closed_date, tv_closed_time, tv_point_type;
        LinearLayout lay_closed_by, lay;
    }

    public void filter(String searchText){
        if(_pendingTicketClassList.size()!=0 && pendingTicketClassList.size()!=0) {
            searchText = searchText.toLowerCase().toLowerCase(Locale.getDefault());
            pendingTicketClassList.clear();
            if (searchText.length() == 0) {
                pendingTicketClassList.addAll(_pendingTicketClassList);
            } else {
                for (TicketMasterClass ticketMasterClass : _pendingTicketClassList) {
                    if (ticketMasterClass.getCrDate().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        pendingTicketClassList.add(ticketMasterClass);
                    } else if (ticketMasterClass.getCrTime().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        pendingTicketClassList.add(ticketMasterClass);
                    } else if (ticketMasterClass.getTicketNo().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        pendingTicketClassList.add(ticketMasterClass);
                    } else if (ticketMasterClass.getStatus().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        pendingTicketClassList.add(ticketMasterClass);
                    } else if (ticketMasterClass.getSubject().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        pendingTicketClassList.add(ticketMasterClass);
                    } else if (ticketMasterClass.getParticular().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        pendingTicketClassList.add(ticketMasterClass);
                    } else if (ticketMasterClass.getAssignTO().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        pendingTicketClassList.add(ticketMasterClass);
                    }else if (ticketMasterClass.getClientName().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        pendingTicketClassList.add(ticketMasterClass);
                    }else if (ticketMasterClass.getCrBy().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        pendingTicketClassList.add(ticketMasterClass);
                    }else if (ticketMasterClass.getModBy().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        pendingTicketClassList.add(ticketMasterClass);
                    }
                }
            }
            notifyDataSetChanged();
        }else if(_pendingTicketClassList.size()!=0 && pendingTicketClassList.size()==0) {
            pendingTicketClassList.addAll(_pendingTicketClassList);
        }
    }

    private Date parseDate(String date){
        Date d = null;
        try {
            d = new SimpleDateFormat("dd/M/yyyy", Locale.ENGLISH).parse(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return d;
    }

    private Date parseDate1(String date){
        Date d = null;
        try {
            d = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).parse(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return d;
    }
}

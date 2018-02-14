package com.lnbinfotech.lnb_tickets.adapter;

//Created by ANUP on 2/13/2018.

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.model.MessageClass;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;

import java.util.ArrayList;
import java.util.Locale;

public class AllMessageAdapter extends BaseAdapter {

    Context context;
    ArrayList<MessageClass> msgClassList;
    ArrayList<MessageClass> _msgClassList;

    public AllMessageAdapter(Context _context, ArrayList<MessageClass> _msgClassList){
        this.context = _context;
        this.msgClassList = _msgClassList;
        this._msgClassList = new ArrayList<>();
        this._msgClassList.addAll(this.msgClassList);
    }

    @Override
    public int getCount() {
        return msgClassList.size();
    }

    @Override
    public Object getItem(int i) {
        return msgClassList.get(i);
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
            view = inflater.inflate(R.layout.list_item_all_message_list, null);
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
            holder.tv_point_type = (TextView) view.findViewById(R.id.tv_point_type);

            //holder.status_lay = (LinearLayout) view.findViewById(R.id.status_lay);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        MessageClass msgClass = (MessageClass) getItem(i);
        holder.tv_cr_by.setText(msgClass.getTdCrBy());
        holder.tv_date.setText(msgClass.getTdCrDate());
        String _time = msgClass.getTdCrTime();
        String time[] = _time.split("\\:");
        if(time.length>1) {
            String s = time[0]+":"+time[1];
            holder.tv_time.setText(s);
        }else{
            holder.tv_time.setText(_time);
        }
        holder.tv_subject.setText(msgClass.getTmSubject());
        String status = msgClass.getTmStatus();
        if(status.equals("Closed")){
            //holder.tv_status.setBackground(context.getResources().getDrawable(R.drawable.status_btn_draw_green));
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.darkgreen));
            /*if(!msgClass.getModBy().equals("null")) {
                holder.lay_closed_by.setVisibility(View.VISIBLE);
                holder.tv_closed_by.setText(msgClass.getModBy());
                holder.tv_closed_date.setText(msgClass.getModDate());
                //holder.tv_closed_time.setText(msgClass.getModTime());

                String _modtime = msgClass.getModTime();
                String modtime[] = _modtime.split("\\:");
                if (modtime.length > 1) {
                    String s = modtime[0] + ":" + modtime[1];
                    holder.tv_closed_time.setText(s);
                } else {
                    holder.tv_closed_time.setText(_modtime);
                }
            }*/
        }else{
            //holder.tv_status.setBackground(context.getResources().getDrawable(R.drawable.status_btn_draw_red));
            holder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
            //holder.lay_closed_by.setVisibility(View.GONE);
        }
        holder.tv_desc.setText(msgClass.getTdDescription());
        holder.tv_status.setText(status);
        holder.tv_ticket_no.setText(msgClass.getTmTicketno());
        //holder.tv_assignto.setText(msgClass.getAssignTO());
        holder.tv_client_name.setText(msgClass.getTmClientId());
        //holder.tv_point_type.setText(msgClass.getPointtype());
        return view;
    }

    private class ViewHolder{
        TextView tv_date, tv_time, tv_subject, tv_desc, tv_status, tv_comments, tv_ticket_no, tv_assignto,
                tv_client_name,tv_cr_by, tv_closed_by, tv_closed_date, tv_closed_time, tv_point_type;
        LinearLayout lay_closed_by;
    }

    public void filter(String searchText){
        if(_msgClassList.size()!=0 && msgClassList.size()!=0) {
            searchText = searchText.toLowerCase().toLowerCase(Locale.getDefault());
            msgClassList.clear();
            if (searchText.length() == 0) {
                msgClassList.addAll(_msgClassList);
            } else {
                for (MessageClass msg : _msgClassList) {
                    if (msg.getTdCrDate().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        msgClassList.add(msg);
                    } else if (msg.getTdCrTime().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        msgClassList.add(msg);
                    } else if (msg.getTmTicketno().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        msgClassList.add(msg);
                    } else if (msg.getTmStatus().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        msgClassList.add(msg);
                    } else if (msg.getTmSubject().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        msgClassList.add(msg);
                    } else if (msg.getTmClientId().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        msgClassList.add(msg);
                    }else if (msg.getTdCrBy().toLowerCase(Locale.getDefault()).contains(searchText)) {
                        msgClassList.add(msg);
                    }
                }
            }
            notifyDataSetChanged();
        }else if(_msgClassList.size()!=0 && msgClassList.size()==0) {
            msgClassList.addAll(_msgClassList);
        }
    }
}


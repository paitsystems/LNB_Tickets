package com.lnbinfotech.lnb_tickets.adapter;

//Created by lnb on 8/15/2017.

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;
import com.lnbinfotech.lnb_tickets.model.ShortDescClass;

import java.util.ArrayList;

public class ShortDescListAdapter extends BaseAdapter{

    ArrayList<ShortDescClass> descClassList;
    Context context;
    ArrayList<TicketMasterClass> pendingTicketClassList;

    /*public ShortDescListAdapter(ArrayList<ShortDescClass> _descClassList, Context _context){
        this.descClassList = _descClassList;
        this.context = _context;
    }*/

    public ShortDescListAdapter(ArrayList<TicketMasterClass> _pendingTicketClassList, Context _context){
        this.pendingTicketClassList = _pendingTicketClassList;
        this.context = _context;
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
        if(view==null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_short_desc,null);
            holder.tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        //ShortDescClass descClass = (ShortDescClass) getItem(i);
        //holder.tv_desc.setText(descClass.getDesc());
        TicketMasterClass pendingTicketClass = (TicketMasterClass) getItem(i);
        String desc = pendingTicketClass.getSubject();
        if(desc!=null) {
            holder.tv_desc.setText(desc);
        }
        return view;
    }

    private class ViewHolder{
        TextView tv_desc;
    }
}

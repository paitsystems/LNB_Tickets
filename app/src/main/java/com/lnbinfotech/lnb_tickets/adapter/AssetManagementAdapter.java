package com.lnbinfotech.lnb_tickets.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lnbinfotech.lnb_tickets.AssetManagementActivity;
import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.model.AssetClass;

import java.util.List;

//Created by ANUP on 18-04-2018.


public class AssetManagementAdapter extends BaseAdapter {

    private Context context;
    private List<AssetClass> list;

    public AssetManagementAdapter(Context _context, List<AssetClass> _list){
        this.context = _context;
        this.list = _list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
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
            view = inflater.inflate(R.layout.list_item_asset_info,null);
            holder.tv_machineId = (TextView) view.findViewById(R.id.tv_machineId);
            holder.tv_machinetype = (TextView) view.findViewById(R.id.tv_machinetype);
            holder.tv_location = (TextView) view.findViewById(R.id.tv_location);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        AssetClass asset = (AssetClass) getItem(i);
        holder.tv_machineId.setText(asset.getMachineId());
        holder.tv_machinetype.setText(asset.getMachineType());
        holder.tv_location.setText(asset.getLocation());
        return view;
    }

    private class ViewHolder{
        private TextView tv_machineId, tv_machinetype, tv_location;
    }
}

package com.lnbinfotech.lnb_tickets.adapter;

//Created by lnb on 8/15/2017.

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lnbinfotech.lnb_tickets.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientSearchAdapter extends BaseAdapter {

    List<String> branchList;
    List<String> _branchList;
    Context context;

    public ClientSearchAdapter(List<String> __branchList, Context _context) {
        this.branchList = __branchList;
        this._branchList = new ArrayList<>();
        this._branchList.addAll(this.branchList);
        this.context = _context;
    }

    @Override
    public int getCount() {
        return branchList.size();
    }

    @Override
    public Object getItem(int i) {
        return branchList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_client_search, null);
            holder.tv_branch = (TextView) view.findViewById(R.id.tv_branch);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String branch = (String) getItem(i);
        holder.tv_branch.setText(branch);
        return view;
    }

    private class ViewHolder {
        TextView tv_branch;
    }

    public void filter(String searchText){
        if(_branchList.size()!=0 && branchList.size()!=0) {
            searchText = searchText.toLowerCase().toLowerCase(Locale.getDefault());
            branchList.clear();
            if (searchText.length() == 0) {
                branchList.addAll(_branchList);
            } else {
                for (String str : _branchList) {
                    if (str.toLowerCase(Locale.getDefault()).contains(searchText)) {
                        branchList.add(str);
                    }
                }
            }
            notifyDataSetChanged();
        }else if(_branchList.size()!=0 && branchList.size()==0) {
            branchList.addAll(_branchList);
        }
    }
}

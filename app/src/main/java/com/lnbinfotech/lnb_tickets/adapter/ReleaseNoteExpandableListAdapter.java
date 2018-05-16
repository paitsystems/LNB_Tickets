package com.lnbinfotech.lnb_tickets.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.model.ReleaseNoteClass;

import java.util.HashMap;
import java.util.List;

//Created by ANUP on 2/21/2018.

public class ReleaseNoteExpandableListAdapter implements ExpandableListAdapter {

    private List<Integer> parentIdList;
    private HashMap<Integer,List<ReleaseNoteClass>> parentIdchildListMap;
    private HashMap<Integer,ReleaseNoteClass> parentMap;
    private Context context;

    public ReleaseNoteExpandableListAdapter(Context _context,List<Integer> _parentIdList, HashMap<Integer,List<ReleaseNoteClass>> _parentIdchildListMap,
                                            HashMap<Integer,ReleaseNoteClass> _parentMap){
        this.context = _context;
        this.parentIdchildListMap = _parentIdchildListMap;
        this.parentIdList =  _parentIdList;
        this.parentMap = _parentMap;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return parentIdList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return parentIdchildListMap.get(parentIdList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return parentMap.get(parentIdList.get(i));
    }

    @Override
    public Object getChild(int i, int i1) {
        return parentIdchildListMap.get(parentIdList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        ViewHolderParent holder;
        if(view==null){
            holder = new ViewHolderParent();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_rn_parent,null);
            holder.tv_parent = (TextView) view.findViewById(R.id.tv_parent);
            holder.tv_date = (TextView) view.findViewById(R.id.tv_date);
            view.setTag(holder);
        }else{
            holder = (ViewHolderParent) view.getTag();
        }
        ReleaseNoteClass note = (ReleaseNoteClass) getGroup(i);
        String verNo = "Version No :- "+note.getVersionNo();
        String onDate = "On Date :- "+note.getCrDate();
        holder.tv_parent.setText(verNo);
        holder.tv_date.setText(onDate);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ViewHolderChild holder;
        if(view==null){
            holder = new ViewHolderChild();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_rn_child,null);
            holder.tv_child = (TextView) view.findViewById(R.id.tv_child);
            view.setTag(holder);
        }else{
            holder = (ViewHolderChild) view.getTag();
        }
        ReleaseNoteClass note = (ReleaseNoteClass) getChild(i,i1);
        holder.tv_child.setText(note.getDesc());
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }

    private class ViewHolderParent{
        private TextView tv_parent, tv_date;
    }

    private class ViewHolderChild{
        private TextView tv_child;
    }
}

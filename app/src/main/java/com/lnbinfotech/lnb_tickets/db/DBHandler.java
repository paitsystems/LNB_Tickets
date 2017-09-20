package com.lnbinfotech.lnb_tickets.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.model.SMLMASTClass;
import com.lnbinfotech.lnb_tickets.model.TicketDetailClass;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Created by lnb on 8/11/2016.

public class DBHandler extends SQLiteOpenHelper {

    public static final String Database_Name = "APITTECH.db";
    public static final int Database_Version = 4;

    public static final String Ticket_Master_Table = "TicketMaster";
    public static final String TicketM_Auto = "Auto";
    public static final String TicketM_Id = "Id";
    public static final String TicketM_ClientAuto = "ClientAuto";
    public static final String TicketM_ClientName = "ClientName";
    public static final String TicketM_FinYr = "FinYr";
    public static final String TicketM_TicketNo = "TicketNo";
    public static final String TicketM_Particular = "Particular";
    public static final String TicketM_Subject = "Subject";
    public static final String TicketM_ImagePath = "ImagePath";
    public static final String TicketM_Status = "Status";
    public static final String TicketM_CrBy = "CrBy";
    public static final String TicketM_CrDate = "CrDate";
    public static final String TicketM_CrTime = "CrTime";
    public static final String TicketM_ModBy = "ModBy";
    public static final String TicketM_ModDate = "ModDate";
    public static final String TicketM_ModTime = "ModTime";
    public static final String TicketM_AssignTo = "AssignTo";
    public static final String TicketM_AssignDate = "AssignDate";
    public static final String TicketM_AssignTime = "AssignTime";
    public static final String TicketM_Type = "Type";
    public static final String TicketM_GenType = "GenType";
    public static final String TicketM_AssignBy = "AssignBy";
    public static final String TicketM_AssignByDate = "AssignByDate";
    public static final String TicketM_AssignByTime = "AssignByTime";
    public static final String TicketM_Branch = "Branch";
    public static final String TicketM_PointType = "PointType";

    public static final String Ticket_Detail_Table = "TicketDetail";
    public static final String TicketD_Auto = "Auto";
    public static final String TicketD_MastAuto = "MastAuto";
    public static final String TicketD_Description = "Description";
    public static final String TicketD_CrBy = "CrBy";
    public static final String TicketD_CrDate = "CrDate";
    public static final String TicketD_CrTime = "CrTime";
    public static final String TicketD_Type = "Type";
    public static final String TicketD_GenType = "GenType";

    public static final String SMLMAST_Table = "SMLMAST";
    public static final String SMLMAST_Auto = "Auto";
    public static final String SMLMAST_ClientID = "ClientID";
    public static final String SMLMAST_ClientName = "ClientName";
    public static final String SMLMAST_Mobile = "Mobile";
    public static final String SMLMAST_Email = "Email";
    public static final String SMLMAST_FTPLocation = "FTPLocation";
    public static final String SMLMAST_FTPUser = "FTPUser";
    public static final String SMLMAST_FTPPass = "FTPPass";
    public static final String SMLMAST_FTPImgFolder = "FTPImgFolder";
    public static final String SMLMAST_CustomerName = "CustomerName";
    public static final String SMLMAST_GroupId = "GroupId";
    public static final String SMLMAST_isHO = "isHO";
    public static final String SMLMAST_isHWapplicable = "isHWapplicable";

    private String create_table_master = "create table if not exists "+ Ticket_Master_Table+"("+
            TicketM_Auto+" int,"+TicketM_Id+" int,"+TicketM_ClientAuto+" int,"+TicketM_ClientName+" text,"+
            TicketM_FinYr+" text,"+
            TicketM_TicketNo+" text,"+TicketM_Particular+" text,"+TicketM_Subject+" text,"+
            TicketM_ImagePath+" text,"+TicketM_Status+" text,"+TicketM_CrBy+" text,"+
            TicketM_CrDate+" text,"+TicketM_CrTime+" text,"+TicketM_ModBy+" text,"+
            TicketM_ModDate+" text,"+TicketM_ModTime+" text,"+TicketM_AssignTo+" text,"+
            TicketM_AssignDate+" text,"+TicketM_AssignTime+" text,"+TicketM_Type+" text,"+
            TicketM_GenType+" text,"+TicketM_AssignBy+" text,"+TicketM_AssignByDate+" text,"+TicketM_AssignByTime+" text,"+
            TicketM_Branch+" text,"+TicketM_PointType+" text);";

    private String create_table_detail = "create table if not exists "+ Ticket_Detail_Table+"("+
            TicketD_Auto+" int,"+TicketD_MastAuto+" int,"+TicketD_Description+" text,"+
            TicketD_CrBy+" text,"+TicketD_CrDate+" text,"+TicketD_CrTime+" text,"+
            TicketD_Type+" text,"+TicketD_GenType+" text);";

    private String create_table_smlmast = "create table if not exists "+ SMLMAST_Table+"("+
            SMLMAST_Auto+" int,"+SMLMAST_ClientID+" text,"+SMLMAST_ClientName+" text,"+
            SMLMAST_Mobile+" text,"+SMLMAST_Email+" text,"+SMLMAST_FTPLocation+" text,"+
            SMLMAST_FTPUser+" text,"+SMLMAST_FTPPass+" text,"+SMLMAST_FTPImgFolder+" text,"+
            SMLMAST_CustomerName+" text,"+SMLMAST_GroupId+" int,"+SMLMAST_isHO+" text,"+SMLMAST_isHWapplicable+" text);";

    public DBHandler(Context context) {
        super(context, Database_Name, null, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table_master);
        db.execSQL(create_table_detail);
        db.execSQL(create_table_smlmast);
        Constant.showLog(create_table_smlmast);
        Constant.showLog(create_table_detail);
        Constant.showLog(create_table_master);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<newVersion){
            db.execSQL("drop table "+Ticket_Master_Table);
            db.execSQL("drop table "+Ticket_Detail_Table);
            db.execSQL("drop table "+SMLMAST_Table);
            db.execSQL(create_table_master);
            db.execSQL(create_table_detail);
            db.execSQL(create_table_smlmast);
            Constant.showLog("Update "+create_table_smlmast);
            Constant.showLog("Update "+create_table_detail);
            Constant.showLog("Update "+create_table_master);
        }
    }

    public void addSMLMAST(SMLMASTClass custClass){
        ContentValues cv = new ContentValues();
        cv.put(SMLMAST_Auto,custClass.getAuto());
        cv.put(SMLMAST_ClientID,custClass.getClientID());
        cv.put(SMLMAST_ClientName,custClass.getClientName());
        cv.put(SMLMAST_Mobile,custClass.getMobile());
        cv.put(SMLMAST_Email,custClass.getEmail());
        cv.put(SMLMAST_FTPLocation,custClass.getFTPLocation());
        cv.put(SMLMAST_FTPUser,custClass.getFTPUser());
        cv.put(SMLMAST_FTPPass,custClass.getFTPPass());
        cv.put(SMLMAST_FTPImgFolder,custClass.getFTPImgFolder());
        cv.put(SMLMAST_CustomerName,custClass.getCustomerName());
        cv.put(SMLMAST_GroupId,custClass.getGroupId());
        cv.put(SMLMAST_isHO,custClass.getIsHO());
        cv.put(SMLMAST_isHWapplicable,custClass.getIsHWapplicable());
        getWritableDatabase().insert(SMLMAST_Table,null,cv);
    }

    public void addTicketMaster(TicketMasterClass ticketMaster){
        ContentValues cv = new ContentValues();
        cv.put(TicketM_Auto,ticketMaster.getAuto());
        cv.put(TicketM_Id,ticketMaster.getId());
        cv.put(TicketM_ClientAuto,ticketMaster.getClientAuto());
        cv.put(TicketM_ClientName,ticketMaster.getClientName());
        cv.put(TicketM_FinYr,ticketMaster.getFinyr());
        cv.put(TicketM_TicketNo,ticketMaster.getTicketNo());
        cv.put(TicketM_Particular,ticketMaster.getParticular());
        cv.put(TicketM_Subject,ticketMaster.getSubject());
        cv.put(TicketM_ImagePath,ticketMaster.getImagePAth());
        cv.put(TicketM_Status,ticketMaster.getStatus());
        cv.put(TicketM_CrBy,ticketMaster.getCrBy());
        cv.put(TicketM_CrDate,ticketMaster.getCrDate());
        cv.put(TicketM_CrTime,ticketMaster.getCrTime());
        cv.put(TicketM_ModBy,ticketMaster.getModBy());
        cv.put(TicketM_ModDate,ticketMaster.getModDate());
        cv.put(TicketM_ModTime,ticketMaster.getModTime());
        cv.put(TicketM_AssignTo,ticketMaster.getAssignTO());
        cv.put(TicketM_AssignDate,ticketMaster.getAssignTODate());
        cv.put(TicketM_AssignTime,ticketMaster.getAssignTOTime());
        cv.put(TicketM_Type,ticketMaster.getType());
        cv.put(TicketM_GenType,ticketMaster.getGenType());
        cv.put(TicketM_AssignBy,ticketMaster.getAssignBy());
        cv.put(TicketM_AssignByDate,ticketMaster.getAssignByDate());
        cv.put(TicketM_AssignByTime,ticketMaster.getAssignByTime());
        cv.put(TicketM_Branch,ticketMaster.getBranch());
        cv.put(TicketM_PointType,ticketMaster.getPointtype());
        getWritableDatabase().insert(Ticket_Master_Table,null,cv);
    }

    public void addTicketDetail(TicketDetailClass ticketDetail){
        ContentValues cv = new ContentValues();
        cv.put(TicketD_Auto,ticketDetail.getAuto());
        cv.put(TicketD_MastAuto,ticketDetail.getMastAuto());
        cv.put(TicketD_Description,ticketDetail.getDesc());
        cv.put(TicketD_CrBy,ticketDetail.getCrby());
        cv.put(TicketD_CrDate,ticketDetail.getCrDate());
        cv.put(TicketD_CrTime,ticketDetail.getCrTime());
        cv.put(TicketD_Type,ticketDetail.getType());
        getWritableDatabase().insert(Ticket_Detail_Table,null,cv);
    }

    public int getSMLMASTMaxAuto(){
        int maxAuto = 0;
        String str = "Select max("+SMLMAST_Auto+") from "+SMLMAST_Table;
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            maxAuto = res.getInt(0);
        }
        res.close();
        return maxAuto;
    }

    public List<String> getDistinctBranch(){
        List<String> list = new ArrayList<>();
        String str = "select distinct "+SMLMAST_ClientID+" from "+SMLMAST_Table+" order by "+SMLMAST_ClientID;
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do{
                list.add(res.getString(0));
            }while (res.moveToNext());
        }
        res.close();
        return list;
    }

    public int getMaxAutoId(String type){
        int autoId = 0;
        Cursor res;
        if(type.equals("C")) {
            String str = "select max("+TicketM_Id+") from "+Ticket_Master_Table;
            res = getWritableDatabase().rawQuery(str, null);
        }else {
            String str = "select max("+TicketM_Auto+") from "+Ticket_Master_Table;
            res = getWritableDatabase().rawQuery(str, null);
        }
        if(res.moveToFirst()){
            autoId = res.getInt(0);
        }
        res.close();
        return autoId;
    }

    public String getCount(){
        String count  = "0-0-0";
        String str = "select " +
                "(select count("+TicketM_Auto+") from "+Ticket_Master_Table+" where "+TicketM_PointType+"<>'I') as Total," +
                "(select count("+TicketM_Auto+") from "+Ticket_Master_Table+" where "+TicketM_Status+"='Closed' or "+TicketM_Status+"='Cancel' or "+TicketM_Status+"='ClientClosed' and "+TicketM_PointType+"<>'I') as Complete," +
                "(select count("+TicketM_Auto+") from "+Ticket_Master_Table+" where "+TicketM_Status+"<>'Closed' and "+TicketM_Status+"<>'Cancel' and "+TicketM_Status+"<>'ClientClosed' and "+TicketM_PointType+"<>'I') as Pending";
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do{
                count = res.getString(0)+"^"+res.getString(1)+"^"+res.getString(2);
            }while (res.moveToNext());
        }
        res.close();
        return count;
    }

    public ArrayList<TicketMasterClass> getTicketMaster(int limit, String crby, String type){
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str;
        if(type.equals("E")) {
            if (limit != 0) {
                str = "select * from " + Ticket_Master_Table + " where " + TicketM_CrBy + "='" + crby + "' or " + TicketM_AssignTo +
                        "='" + crby + "' or " + TicketM_AssignTo + "='NotAssigned' and " + TicketM_Status + "<>'Closed' and " +
                        TicketM_Status + "<>'Cancel' and " + TicketM_Status + "<>'ClientClosed' order by " + TicketM_Auto + " desc limit " + limit;
            } else {
                str = "select * from " + Ticket_Master_Table + " where " + TicketM_CrBy + "='" + crby + "' or " + TicketM_AssignTo +
                        "='" + crby + "' or " + TicketM_AssignTo + "='NotAssigned' and " + TicketM_Status + "<>'Cancel' order by " +
                        TicketM_Auto + " desc";
            }
        }else {
            if (limit != 0) {
                str = "select * from " + Ticket_Master_Table + " where "+ TicketM_Status + "<>'Closed' and " + TicketM_Status + "<>'Cancel' and "
                        + TicketM_Status + "<>'ClientClosed' order by " + TicketM_Auto + " desc limit " + limit;
            } else {
                str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + "<>'Cancel' order by " + TicketM_Auto + " desc";
            }
        }
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do {
                TicketMasterClass pendingTicketClass = new TicketMasterClass();
                pendingTicketClass.setAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_Auto)));
                pendingTicketClass.setId(res.getInt(res.getColumnIndex(DBHandler.TicketM_Id)));
                pendingTicketClass.setClientAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_ClientAuto)));
                pendingTicketClass.setClientName(res.getString(res.getColumnIndex(DBHandler.TicketM_ClientName)));
                pendingTicketClass.setFinyr(res.getString(res.getColumnIndex(DBHandler.TicketM_FinYr)));
                pendingTicketClass.setTicketNo(res.getString(res.getColumnIndex(DBHandler.TicketM_TicketNo)));
                pendingTicketClass.setParticular(res.getString(res.getColumnIndex(DBHandler.TicketM_Particular)));
                pendingTicketClass.setSubject(res.getString(res.getColumnIndex(DBHandler.TicketM_Subject)));
                pendingTicketClass.setImagePAth(res.getString(res.getColumnIndex(DBHandler.TicketM_ImagePath)));
                pendingTicketClass.setStatus(res.getString(res.getColumnIndex(DBHandler.TicketM_Status)));
                pendingTicketClass.setCrBy(res.getString(res.getColumnIndex(DBHandler.TicketM_CrBy)));
                pendingTicketClass.setCrDate(res.getString(res.getColumnIndex(DBHandler.TicketM_CrDate)));
                pendingTicketClass.setCrTime(res.getString(res.getColumnIndex(DBHandler.TicketM_CrTime)));
                pendingTicketClass.setModBy(res.getString(res.getColumnIndex(DBHandler.TicketM_ModBy)));
                pendingTicketClass.setModDate(res.getString(res.getColumnIndex(DBHandler.TicketM_ModDate)));
                pendingTicketClass.setModTime(res.getString(res.getColumnIndex(DBHandler.TicketM_ModTime)));
                pendingTicketClass.setAssignTO(res.getString(res.getColumnIndex(DBHandler.TicketM_AssignTo)));
                pendingTicketClass.setPointtype(res.getString(res.getColumnIndex(DBHandler.TicketM_PointType)));
                pendingTicketClassList.add(pendingTicketClass);
            }while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getPendingTicket(String isHWApplicable){
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        if(isHWApplicable.equals("S") || isHWApplicable.equals("SH")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Open','Pending','ReOpen') and " +
                    TicketM_PointType + "='S' order by " + TicketM_Auto + " desc";
        }else if(isHWApplicable.equals("H")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Open','Pending','ReOpen') and " +
                    TicketM_PointType + "='H' order by " + TicketM_Auto + " desc";
        }

        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do {
                TicketMasterClass pendingTicketClass = new TicketMasterClass();
                pendingTicketClass.setAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_Auto)));
                pendingTicketClass.setId(res.getInt(res.getColumnIndex(DBHandler.TicketM_Id)));
                pendingTicketClass.setClientAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_ClientAuto)));
                pendingTicketClass.setClientName(res.getString(res.getColumnIndex(DBHandler.TicketM_ClientName)));
                pendingTicketClass.setFinyr(res.getString(res.getColumnIndex(DBHandler.TicketM_FinYr)));
                pendingTicketClass.setTicketNo(res.getString(res.getColumnIndex(DBHandler.TicketM_TicketNo)));
                pendingTicketClass.setParticular(res.getString(res.getColumnIndex(DBHandler.TicketM_Particular)));
                pendingTicketClass.setSubject(res.getString(res.getColumnIndex(DBHandler.TicketM_Subject)));
                pendingTicketClass.setImagePAth(res.getString(res.getColumnIndex(DBHandler.TicketM_ImagePath)));
                pendingTicketClass.setStatus(res.getString(res.getColumnIndex(DBHandler.TicketM_Status)));
                pendingTicketClass.setCrBy(res.getString(res.getColumnIndex(DBHandler.TicketM_CrBy)));
                pendingTicketClass.setCrDate(res.getString(res.getColumnIndex(DBHandler.TicketM_CrDate)));
                pendingTicketClass.setCrTime(res.getString(res.getColumnIndex(DBHandler.TicketM_CrTime)));
                pendingTicketClass.setModBy(res.getString(res.getColumnIndex(DBHandler.TicketM_ModBy)));
                pendingTicketClass.setModDate(res.getString(res.getColumnIndex(DBHandler.TicketM_ModDate)));
                pendingTicketClass.setModTime(res.getString(res.getColumnIndex(DBHandler.TicketM_ModTime)));
                pendingTicketClass.setAssignTO(res.getString(res.getColumnIndex(DBHandler.TicketM_AssignTo)));
                pendingTicketClass.setPointtype(res.getString(res.getColumnIndex(DBHandler.TicketM_PointType)));
                pendingTicketClassList.add(pendingTicketClass);
            }while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getClosedTicket(String isHWApplicable){
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        if(isHWApplicable.equals("S") || isHWApplicable.equals("SH")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Closed','ClientClosed') and "
                    + TicketM_PointType + "='S' order by " + TicketM_Auto + " desc";
        }else if(isHWApplicable.equals("H")){
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Closed','ClientClosed') and "
                    + TicketM_PointType + "='H' order by " + TicketM_Auto + " desc";
        }

        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do {
                TicketMasterClass pendingTicketClass = new TicketMasterClass();
                pendingTicketClass.setAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_Auto)));
                pendingTicketClass.setId(res.getInt(res.getColumnIndex(DBHandler.TicketM_Id)));
                pendingTicketClass.setClientAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_ClientAuto)));
                pendingTicketClass.setClientName(res.getString(res.getColumnIndex(DBHandler.TicketM_ClientName)));
                pendingTicketClass.setFinyr(res.getString(res.getColumnIndex(DBHandler.TicketM_FinYr)));
                pendingTicketClass.setTicketNo(res.getString(res.getColumnIndex(DBHandler.TicketM_TicketNo)));
                pendingTicketClass.setParticular(res.getString(res.getColumnIndex(DBHandler.TicketM_Particular)));
                pendingTicketClass.setSubject(res.getString(res.getColumnIndex(DBHandler.TicketM_Subject)));
                pendingTicketClass.setImagePAth(res.getString(res.getColumnIndex(DBHandler.TicketM_ImagePath)));
                pendingTicketClass.setStatus(res.getString(res.getColumnIndex(DBHandler.TicketM_Status)));
                pendingTicketClass.setCrBy(res.getString(res.getColumnIndex(DBHandler.TicketM_CrBy)));
                pendingTicketClass.setCrDate(res.getString(res.getColumnIndex(DBHandler.TicketM_CrDate)));
                pendingTicketClass.setCrTime(res.getString(res.getColumnIndex(DBHandler.TicketM_CrTime)));
                pendingTicketClass.setModBy(res.getString(res.getColumnIndex(DBHandler.TicketM_ModBy)));
                pendingTicketClass.setModDate(res.getString(res.getColumnIndex(DBHandler.TicketM_ModDate)));
                pendingTicketClass.setModTime(res.getString(res.getColumnIndex(DBHandler.TicketM_ModTime)));
                pendingTicketClass.setAssignTO(res.getString(res.getColumnIndex(DBHandler.TicketM_AssignTo)));
                pendingTicketClass.setPointtype(res.getString(res.getColumnIndex(DBHandler.TicketM_PointType)));
                pendingTicketClassList.add(pendingTicketClass);
            }while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getHoldTicket(String isHWApplicable){
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        if(isHWApplicable.equals("S") || isHWApplicable.equals("SH")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Hold','hold') and "
                    + TicketM_PointType + "='S' order by " + TicketM_Auto + " desc";
        }else if(isHWApplicable.equals("H")){
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Hold','hold') and "
                    + TicketM_PointType + "='H' order by " + TicketM_Auto + " desc";
        }
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do {
                TicketMasterClass pendingTicketClass = new TicketMasterClass();
                pendingTicketClass.setAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_Auto)));
                pendingTicketClass.setId(res.getInt(res.getColumnIndex(DBHandler.TicketM_Id)));
                pendingTicketClass.setClientAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_ClientAuto)));
                pendingTicketClass.setClientName(res.getString(res.getColumnIndex(DBHandler.TicketM_ClientName)));
                pendingTicketClass.setFinyr(res.getString(res.getColumnIndex(DBHandler.TicketM_FinYr)));
                pendingTicketClass.setTicketNo(res.getString(res.getColumnIndex(DBHandler.TicketM_TicketNo)));
                pendingTicketClass.setParticular(res.getString(res.getColumnIndex(DBHandler.TicketM_Particular)));
                pendingTicketClass.setSubject(res.getString(res.getColumnIndex(DBHandler.TicketM_Subject)));
                pendingTicketClass.setImagePAth(res.getString(res.getColumnIndex(DBHandler.TicketM_ImagePath)));
                pendingTicketClass.setStatus(res.getString(res.getColumnIndex(DBHandler.TicketM_Status)));
                pendingTicketClass.setCrBy(res.getString(res.getColumnIndex(DBHandler.TicketM_CrBy)));
                pendingTicketClass.setCrDate(res.getString(res.getColumnIndex(DBHandler.TicketM_CrDate)));
                pendingTicketClass.setCrTime(res.getString(res.getColumnIndex(DBHandler.TicketM_CrTime)));
                pendingTicketClass.setModBy(res.getString(res.getColumnIndex(DBHandler.TicketM_ModBy)));
                pendingTicketClass.setModDate(res.getString(res.getColumnIndex(DBHandler.TicketM_ModDate)));
                pendingTicketClass.setModTime(res.getString(res.getColumnIndex(DBHandler.TicketM_ModTime)));
                pendingTicketClass.setAssignTO(res.getString(res.getColumnIndex(DBHandler.TicketM_AssignTo)));
                pendingTicketClass.setPointtype(res.getString(res.getColumnIndex(DBHandler.TicketM_PointType)));
                pendingTicketClassList.add(pendingTicketClass);
            }while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getCancelTicket(String isHWApplicable){
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        if(isHWApplicable.equals("S") || isHWApplicable.equals("SH")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Cancel') and "
                    + TicketM_PointType + "='S' order by " + TicketM_Auto + " desc";
        }else if(isHWApplicable.equals("H")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Cancel') and "
                    + TicketM_PointType + "='H' order by " + TicketM_Auto + " desc";
        }
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do {
                TicketMasterClass pendingTicketClass = new TicketMasterClass();
                pendingTicketClass.setAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_Auto)));
                pendingTicketClass.setId(res.getInt(res.getColumnIndex(DBHandler.TicketM_Id)));
                pendingTicketClass.setClientAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_ClientAuto)));
                pendingTicketClass.setClientName(res.getString(res.getColumnIndex(DBHandler.TicketM_ClientName)));
                pendingTicketClass.setFinyr(res.getString(res.getColumnIndex(DBHandler.TicketM_FinYr)));
                pendingTicketClass.setTicketNo(res.getString(res.getColumnIndex(DBHandler.TicketM_TicketNo)));
                pendingTicketClass.setParticular(res.getString(res.getColumnIndex(DBHandler.TicketM_Particular)));
                pendingTicketClass.setSubject(res.getString(res.getColumnIndex(DBHandler.TicketM_Subject)));
                pendingTicketClass.setImagePAth(res.getString(res.getColumnIndex(DBHandler.TicketM_ImagePath)));
                pendingTicketClass.setStatus(res.getString(res.getColumnIndex(DBHandler.TicketM_Status)));
                pendingTicketClass.setCrBy(res.getString(res.getColumnIndex(DBHandler.TicketM_CrBy)));
                pendingTicketClass.setCrDate(res.getString(res.getColumnIndex(DBHandler.TicketM_CrDate)));
                pendingTicketClass.setCrTime(res.getString(res.getColumnIndex(DBHandler.TicketM_CrTime)));
                pendingTicketClass.setModBy(res.getString(res.getColumnIndex(DBHandler.TicketM_ModBy)));
                pendingTicketClass.setModDate(res.getString(res.getColumnIndex(DBHandler.TicketM_ModDate)));
                pendingTicketClass.setModTime(res.getString(res.getColumnIndex(DBHandler.TicketM_ModTime)));
                pendingTicketClass.setAssignTO(res.getString(res.getColumnIndex(DBHandler.TicketM_AssignTo)));
                pendingTicketClass.setPointtype(res.getString(res.getColumnIndex(DBHandler.TicketM_PointType)));
                pendingTicketClassList.add(pendingTicketClass);
            }while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getHardwarePoint(){
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        str = "select * from " + Ticket_Master_Table + " where " + TicketM_PointType + "='H' order by " +
                TicketM_Auto + " desc";

        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do {
                TicketMasterClass pendingTicketClass = new TicketMasterClass();
                pendingTicketClass.setAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_Auto)));
                pendingTicketClass.setId(res.getInt(res.getColumnIndex(DBHandler.TicketM_Id)));
                pendingTicketClass.setClientAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_ClientAuto)));
                pendingTicketClass.setClientName(res.getString(res.getColumnIndex(DBHandler.TicketM_ClientName)));
                pendingTicketClass.setFinyr(res.getString(res.getColumnIndex(DBHandler.TicketM_FinYr)));
                pendingTicketClass.setTicketNo(res.getString(res.getColumnIndex(DBHandler.TicketM_TicketNo)));
                pendingTicketClass.setParticular(res.getString(res.getColumnIndex(DBHandler.TicketM_Particular)));
                pendingTicketClass.setSubject(res.getString(res.getColumnIndex(DBHandler.TicketM_Subject)));
                pendingTicketClass.setImagePAth(res.getString(res.getColumnIndex(DBHandler.TicketM_ImagePath)));
                pendingTicketClass.setStatus(res.getString(res.getColumnIndex(DBHandler.TicketM_Status)));
                pendingTicketClass.setCrBy(res.getString(res.getColumnIndex(DBHandler.TicketM_CrBy)));
                pendingTicketClass.setCrDate(res.getString(res.getColumnIndex(DBHandler.TicketM_CrDate)));
                pendingTicketClass.setCrTime(res.getString(res.getColumnIndex(DBHandler.TicketM_CrTime)));
                pendingTicketClass.setModBy(res.getString(res.getColumnIndex(DBHandler.TicketM_ModBy)));
                pendingTicketClass.setModDate(res.getString(res.getColumnIndex(DBHandler.TicketM_ModDate)));
                pendingTicketClass.setModTime(res.getString(res.getColumnIndex(DBHandler.TicketM_ModTime)));
                pendingTicketClass.setAssignTO(res.getString(res.getColumnIndex(DBHandler.TicketM_AssignTo)));
                pendingTicketClass.setPointtype(res.getString(res.getColumnIndex(DBHandler.TicketM_PointType)));
                pendingTicketClassList.add(pendingTicketClass);
            }while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getInternalePoint(){
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str;
        str = "select * from " + Ticket_Master_Table + " where " + TicketM_PointType + "='I' order by " +
                TicketM_Auto + " desc";

        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            do {
                TicketMasterClass pendingTicketClass = new TicketMasterClass();
                pendingTicketClass.setAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_Auto)));
                pendingTicketClass.setId(res.getInt(res.getColumnIndex(DBHandler.TicketM_Id)));
                pendingTicketClass.setClientAuto(res.getInt(res.getColumnIndex(DBHandler.TicketM_ClientAuto)));
                pendingTicketClass.setClientName(res.getString(res.getColumnIndex(DBHandler.TicketM_ClientName)));
                pendingTicketClass.setFinyr(res.getString(res.getColumnIndex(DBHandler.TicketM_FinYr)));
                pendingTicketClass.setTicketNo(res.getString(res.getColumnIndex(DBHandler.TicketM_TicketNo)));
                pendingTicketClass.setParticular(res.getString(res.getColumnIndex(DBHandler.TicketM_Particular)));
                pendingTicketClass.setSubject(res.getString(res.getColumnIndex(DBHandler.TicketM_Subject)));
                pendingTicketClass.setImagePAth(res.getString(res.getColumnIndex(DBHandler.TicketM_ImagePath)));
                pendingTicketClass.setStatus(res.getString(res.getColumnIndex(DBHandler.TicketM_Status)));
                pendingTicketClass.setCrBy(res.getString(res.getColumnIndex(DBHandler.TicketM_CrBy)));
                pendingTicketClass.setCrDate(res.getString(res.getColumnIndex(DBHandler.TicketM_CrDate)));
                pendingTicketClass.setCrTime(res.getString(res.getColumnIndex(DBHandler.TicketM_CrTime)));
                pendingTicketClass.setModBy(res.getString(res.getColumnIndex(DBHandler.TicketM_ModBy)));
                pendingTicketClass.setModDate(res.getString(res.getColumnIndex(DBHandler.TicketM_ModDate)));
                pendingTicketClass.setModTime(res.getString(res.getColumnIndex(DBHandler.TicketM_ModTime)));
                pendingTicketClass.setAssignTO(res.getString(res.getColumnIndex(DBHandler.TicketM_AssignTo)));
                pendingTicketClass.setPointtype(res.getString(res.getColumnIndex(DBHandler.TicketM_PointType)));
                pendingTicketClassList.add(pendingTicketClass);
            }while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public String getAutoFolder(String clienID){
        String data = "0";
        String str = "select "+SMLMAST_Auto+","+SMLMAST_FTPImgFolder+" from "+SMLMAST_Table+" where "+SMLMAST_GroupId+
                "=(select "+SMLMAST_GroupId+" from "+SMLMAST_Table+" where "+SMLMAST_ClientID+"='"+clienID+"') and "+SMLMAST_FTPLocation+"<>'null'";
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            data = res.getString(0)+"^"+res.getString(1);
        }
        res.close();
        return data;
    }

    public String getFolder(int clientAuto){
        String data = "0";
        String str = "select "+SMLMAST_FTPImgFolder+" from "+SMLMAST_Table+" where "+SMLMAST_Auto+"="+clientAuto;
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            data = res.getString(0);
        }
        res.close();
        return data;
    }

    public String getMobile(int clientAuto){
        String data = "0";
        String str = "select "+SMLMAST_GroupId+" from "+SMLMAST_Table+" where "+SMLMAST_Auto+"="+clientAuto;
        Cursor res = getWritableDatabase().rawQuery(str,null);
        if(res.moveToFirst()){
            data = res.getString(0);
        }
        res.close();
        String str1 = "select "+SMLMAST_Mobile+" from "+SMLMAST_Table+" where "+SMLMAST_GroupId+"="+data+" and "+SMLMAST_FTPLocation+"<>'' and "+SMLMAST_FTPUser+"<>'' and "+SMLMAST_FTPPass+"<>''" ;
        Cursor res1 = getWritableDatabase().rawQuery(str1,null);
        if(res1.moveToFirst()){
            data = res1.getString(0);
        }
        res1.close();
        return data;
    }

    public void updateTicketStatus(int auto, int id, int clientAuto, String finyr, String status, String clientName,String ticketno){
        String date = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        ContentValues cv = new ContentValues();
        cv.put(TicketM_Status,status);
        cv.put(TicketM_ModBy,clientName);
        cv.put(TicketM_ModDate,date);
        cv.put(TicketM_ModTime,time);
        getWritableDatabase().update(Ticket_Master_Table,cv,
                TicketM_Auto+"=? and "+TicketM_Id+"=? and "+TicketM_ClientAuto+"=? and "+
                TicketM_FinYr+"=? and "+TicketM_TicketNo+"=?",
                new String[]{String.valueOf(auto),String.valueOf(id),String.valueOf(clientAuto),finyr,ticketno});
    }

    public void deleteTabel(String tableName){
        getWritableDatabase().execSQL("delete from "+tableName);
    }
}


package com.lnbinfotech.lnb_tickets.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lnbinfotech.lnb_tickets.FirstActivity;
import com.lnbinfotech.lnb_tickets.MainActivity;
import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.UpdateTicketActivity;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.interfaces.DatabaseUpgradeInterface;
import com.lnbinfotech.lnb_tickets.model.FeedbackClass;
import com.lnbinfotech.lnb_tickets.model.ReleaseNoteClass;
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

    public DatabaseUpgradeInterface dbInterface;

    public static final String Database_Name = "APITTECH.db";
    //public static final int Database_Version = 9;
    public static final int Database_Version = 10;
    private int limit = 5;

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
    public static final String TicketM_ModDate1 = "ModDate1";
    public static final String TicketM_NickName = "NickName";

    public static final String Ticket_Detail_Table = "TicketDetail";
    public static final String TicketD_Auto = "Auto";
    public static final String TicketD_MastAuto = "MastAuto";
    public static final String TicketD_Description = "Description";
    public static final String TicketD_CrBy = "CrBy";
    public static final String TicketD_CrDate = "CrDate";
    public static final String TicketD_CrTime = "CrTime";
    public static final String TicketD_Type = "Type";
    public static final String TicketD_GenType = "GenType";
    public static final String TicketD_Id = "Id";
    public static final String TicketD_ClientAuto = "ClientAuto";
    public static final String TicketD_PointType = "PointType";
    public static final String TicketD_CrDate1 = "CrDate1";

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
    public static final String SMLMAST_NickName = "NickName";
    public static final String SMLMAST_OtherMobNo = "OtherMobNo";

    public static final String SoftwareVersionDetail_Table = "SoftwareVersionDetail";
    public static final String SVD_Auto = "Auto";
    public static final String SVD_Version = "VersionNo";
    public static final String SVD_Desc = "Description";
    public static final String SVD_CrBy = "CrBy";
    public static final String SVD_CrDate = "CrDate";
    public static final String SVD_CrTime = "CrTime";
    public static final String SVD_ModBy = "ModBy";
    public static final String SVD_ModDate = "ModDate";
    public static final String SVD_ModTime = "ModTime";

    public static final String Table_QuestBank = "QuestBank";
    public static final String FED_AUTO = "Auto";
    public static final String FED_QUESTION = "Question";
    public static final String FED_CAT1 = "Cat1";
    public static final String FED_CAT2 = "Cat2";
    public static final String FED_CAT3 = "Cat3";
    public static final String FED_CAT4 = "Cat4";
    public static final String FED_CAT5 = "Cat5";
    public static final String FED_CAT6 = "Cat6";
    public static final String FED_STATUS = "Status";
    public static final String FED_TYPE = "Type";

    private String create_table_master = "create table if not exists " + Ticket_Master_Table + "(" +
            TicketM_Auto + " int," + TicketM_Id + " int," + TicketM_ClientAuto + " int," + TicketM_ClientName + " text," +
            TicketM_FinYr + " text," +
            TicketM_TicketNo + " text," + TicketM_Particular + " text," + TicketM_Subject + " text," +
            TicketM_ImagePath + " text," + TicketM_Status + " text," + TicketM_CrBy + " text," +
            TicketM_CrDate + " text," + TicketM_CrTime + " text," + TicketM_ModBy + " text," +
            TicketM_ModDate + " text," + TicketM_ModTime + " text," + TicketM_AssignTo + " text," +
            TicketM_AssignDate + " text," + TicketM_AssignTime + " text," + TicketM_Type + " text," +
            TicketM_GenType + " text," + TicketM_AssignBy + " text," + TicketM_AssignByDate + " text," + TicketM_AssignByTime + " text," +
            TicketM_Branch + " text," + TicketM_PointType + " text," + TicketM_ModDate1 + " text);";

    private String create_table_detail = "create table if not exists " + Ticket_Detail_Table + "(" +
            TicketD_Auto + " int," + TicketD_MastAuto + " int," + TicketD_Description + " text," +
            TicketD_CrBy + " text," + TicketD_CrDate + " text," + TicketD_CrTime + " text," +
            TicketD_Type + " text," + TicketD_GenType + " text," + TicketD_Id + " int," + TicketD_ClientAuto
            + " int," + TicketD_PointType + " text," + TicketD_CrDate1 + " text)";

    private String create_table_smlmast = "create table if not exists " + SMLMAST_Table + "(" +
            SMLMAST_Auto + " int," + SMLMAST_ClientID + " text," + SMLMAST_ClientName + " text," +
            SMLMAST_Mobile + " text," + SMLMAST_Email + " text," + SMLMAST_FTPLocation + " text," +
            SMLMAST_FTPUser + " text," + SMLMAST_FTPPass + " text," + SMLMAST_FTPImgFolder + " text," +
            SMLMAST_CustomerName + " text," + SMLMAST_GroupId + " int," + SMLMAST_isHO + " text," +
            SMLMAST_isHWapplicable + " text," + SMLMAST_NickName + " text," + SMLMAST_OtherMobNo + " text);";

    private String create_table_svd = "create table if not exists " + SoftwareVersionDetail_Table + "(" +
            SVD_Auto + " int," + SVD_Version + " text," + SVD_Desc + " text," + SVD_CrBy + " int," + SVD_CrDate + " text,"
            + SVD_CrTime + " text," + SVD_ModBy + " text," + SVD_ModDate + " text," + SVD_ModTime + " text)";

    private String create_questbank_table = "create table if not exists " + Table_QuestBank + "(" + FED_AUTO + " int," + FED_QUESTION + " text," + FED_CAT1 + " text," +
            FED_CAT2 + " text," + FED_CAT3 + " text," + FED_CAT4 + " text," + FED_CAT5 + " text," + FED_CAT6 + " text," + FED_STATUS + " text," + FED_TYPE + " text)";

    public DBHandler(Context context) {
        super(context, Database_Name, null, Database_Version);
    }

    public void initInterface(MainActivity main) {
        dbInterface = main;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table_master);
        db.execSQL(create_table_detail);
        db.execSQL(create_table_smlmast);
        db.execSQL(create_table_svd);
        db.execSQL(create_questbank_table);
        Constant.showLog(create_table_smlmast);
        Constant.showLog(create_table_detail);
        Constant.showLog(create_table_master);
        Constant.showLog(create_table_svd);
        Constant.showLog(create_questbank_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 7) {
            String str1 = "alter table " + Ticket_Detail_Table + " add " + TicketD_Id + " int";
            String str2 = "alter table " + Ticket_Detail_Table + " add " + TicketD_ClientAuto + " int";
            String str3 = "alter table " + Ticket_Detail_Table + " add " + TicketD_PointType + " text";
            String str4 = "alter table " + Ticket_Detail_Table + " add " + TicketD_CrDate1 + " text";
            db.execSQL(str1);
            db.execSQL(str2);
            db.execSQL(str3);
            db.execSQL(str4);
            Constant.showLog("Update Ticket Detail Table 7");
        }
        if (oldVersion < 8) {
            String str2 = "alter table " + SMLMAST_Table + " add " + SMLMAST_NickName + " text";
            String str3 = "alter table " + SMLMAST_Table + " add " + SMLMAST_OtherMobNo + " text";
            db.execSQL(str2);
            db.execSQL(str3);
            Constant.showLog("Update Ticket Detail Table 8");
        }
        if (oldVersion < 9) {
            db.execSQL(create_table_svd);
            Constant.showLog("Update Ticket Detail Table 9");
        }
        if (oldVersion < 10) {
            db.execSQL(create_questbank_table);
            Constant.showLog("Update Ticket Detail Table 10");
        }
        /*if(dbInterface!=null) {
            dbInterface.dbUpgraded();
        }*/

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.disableWriteAheadLogging();
    }

    public void addSMLMAST(SMLMASTClass custClass) {
        ContentValues cv = new ContentValues();
        cv.put(SMLMAST_Auto, custClass.getAuto());
        cv.put(SMLMAST_ClientID, custClass.getClientID());
        cv.put(SMLMAST_ClientName, custClass.getClientName());
        cv.put(SMLMAST_Mobile, custClass.getMobile());
        cv.put(SMLMAST_Email, custClass.getEmail());
        cv.put(SMLMAST_FTPLocation, custClass.getFTPLocation());
        cv.put(SMLMAST_FTPUser, custClass.getFTPUser());
        cv.put(SMLMAST_FTPPass, custClass.getFTPPass());
        cv.put(SMLMAST_FTPImgFolder, custClass.getFTPImgFolder());
        cv.put(SMLMAST_CustomerName, custClass.getCustomerName());
        cv.put(SMLMAST_GroupId, custClass.getGroupId());
        cv.put(SMLMAST_isHO, custClass.getIsHO());
        cv.put(SMLMAST_isHWapplicable, custClass.getIsHWapplicable());
        getWritableDatabase().insert(SMLMAST_Table, null, cv);
    }

    public void addTicketMaster(TicketMasterClass ticketMaster) {
        ContentValues cv = new ContentValues();
        cv.put(TicketM_Auto, ticketMaster.getAuto());
        cv.put(TicketM_Id, ticketMaster.getId());
        cv.put(TicketM_ClientAuto, ticketMaster.getClientAuto());
        cv.put(TicketM_ClientName, ticketMaster.getClientName());
        cv.put(TicketM_FinYr, ticketMaster.getFinyr());
        cv.put(TicketM_TicketNo, ticketMaster.getTicketNo());
        cv.put(TicketM_Particular, ticketMaster.getParticular());
        cv.put(TicketM_Subject, ticketMaster.getSubject());
        cv.put(TicketM_ImagePath, ticketMaster.getImagePAth());
        cv.put(TicketM_Status, ticketMaster.getStatus());
        cv.put(TicketM_CrBy, ticketMaster.getCrBy());
        cv.put(TicketM_CrDate, ticketMaster.getCrDate());
        cv.put(TicketM_CrTime, ticketMaster.getCrTime());
        cv.put(TicketM_ModBy, ticketMaster.getModBy());
        cv.put(TicketM_ModDate, ticketMaster.getModDate());
        cv.put(TicketM_ModTime, ticketMaster.getModTime());
        cv.put(TicketM_AssignTo, ticketMaster.getAssignTO());
        cv.put(TicketM_AssignDate, ticketMaster.getAssignTODate());
        cv.put(TicketM_AssignTime, ticketMaster.getAssignTOTime());
        cv.put(TicketM_Type, ticketMaster.getType());
        cv.put(TicketM_GenType, ticketMaster.getGenType());
        cv.put(TicketM_AssignBy, ticketMaster.getAssignBy());
        cv.put(TicketM_AssignByDate, ticketMaster.getAssignByDate());
        cv.put(TicketM_AssignByTime, ticketMaster.getAssignByTime());
        cv.put(TicketM_Branch, ticketMaster.getBranch());
        cv.put(TicketM_PointType, ticketMaster.getPointtype());
        cv.put(TicketM_ModDate1, ticketMaster.getModdate1());
        getWritableDatabase().insert(Ticket_Master_Table, null, cv);
    }

    public void addTicketMaster(List<TicketMasterClass> ticketMasterList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        int count = 0;
        for (TicketMasterClass ticketMaster : ticketMasterList) {
            cv.put(TicketM_Auto, ticketMaster.getAuto());
            cv.put(TicketM_Id, ticketMaster.getId());
            cv.put(TicketM_ClientAuto, ticketMaster.getClientAuto());
            cv.put(TicketM_ClientName, ticketMaster.getClientName());
            cv.put(TicketM_FinYr, ticketMaster.getFinyr());
            cv.put(TicketM_TicketNo, ticketMaster.getTicketNo());
            cv.put(TicketM_Particular, ticketMaster.getParticular());
            cv.put(TicketM_Subject, ticketMaster.getSubject());
            cv.put(TicketM_ImagePath, ticketMaster.getImagePAth());
            cv.put(TicketM_Status, ticketMaster.getStatus());
            cv.put(TicketM_CrBy, ticketMaster.getCrBy());
            cv.put(TicketM_CrDate, ticketMaster.getCrDate());
            cv.put(TicketM_CrTime, ticketMaster.getCrTime());
            cv.put(TicketM_ModBy, ticketMaster.getModBy());
            cv.put(TicketM_ModDate, ticketMaster.getModDate());
            cv.put(TicketM_ModTime, ticketMaster.getModTime());
            cv.put(TicketM_AssignTo, ticketMaster.getAssignTO());
            cv.put(TicketM_AssignDate, ticketMaster.getAssignTODate());
            cv.put(TicketM_AssignTime, ticketMaster.getAssignTOTime());
            cv.put(TicketM_Type, ticketMaster.getType());
            cv.put(TicketM_GenType, ticketMaster.getGenType());
            cv.put(TicketM_AssignBy, ticketMaster.getAssignBy());
            cv.put(TicketM_AssignByDate, ticketMaster.getAssignByDate());
            cv.put(TicketM_AssignByTime, ticketMaster.getAssignByTime());
            cv.put(TicketM_Branch, ticketMaster.getBranch());
            cv.put(TicketM_PointType, ticketMaster.getPointtype());
            cv.put(TicketM_ModDate1, ticketMaster.getModdate1());
            db.insert(Ticket_Master_Table, null, cv);
            count++;
        }
        Constant.showLog("Count " + count);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void addTicketDetail(TicketDetailClass ticketDetail) {
        ContentValues cv = new ContentValues();
        cv.put(TicketD_Auto, ticketDetail.getAuto());
        cv.put(TicketD_MastAuto, ticketDetail.getMastAuto());
        cv.put(TicketD_Description, ticketDetail.getDesc());
        cv.put(TicketD_CrBy, ticketDetail.getCrby());
        cv.put(TicketD_CrDate, ticketDetail.getCrDate());
        cv.put(TicketD_CrTime, ticketDetail.getCrTime());
        cv.put(TicketD_Type, ticketDetail.getType());
        cv.put(TicketD_GenType, ticketDetail.getGenType());
        cv.put(TicketD_Id, ticketDetail.getId());
        cv.put(TicketD_ClientAuto, ticketDetail.getClientAuto());
        cv.put(TicketD_PointType, ticketDetail.getPointType());
        cv.put(TicketD_CrDate1, ticketDetail.getCrDate1());
        getWritableDatabase().insert(Ticket_Detail_Table, null, cv);
    }

    public void addTicketDetail(List<TicketDetailClass> ticketDetailList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        int count = 0;
        for (TicketDetailClass ticketDetail : ticketDetailList) {
            cv.put(TicketD_Auto, ticketDetail.getAuto());
            cv.put(TicketD_MastAuto, ticketDetail.getMastAuto());
            cv.put(TicketD_Description, ticketDetail.getDesc());
            cv.put(TicketD_CrBy, ticketDetail.getCrby());
            cv.put(TicketD_CrDate, ticketDetail.getCrDate());
            cv.put(TicketD_CrTime, ticketDetail.getCrTime());
            cv.put(TicketD_Type, ticketDetail.getType());
            cv.put(TicketD_GenType, ticketDetail.getGenType());
            cv.put(TicketD_Id, ticketDetail.getId());
            cv.put(TicketD_ClientAuto, ticketDetail.getClientAuto());
            cv.put(TicketD_PointType, ticketDetail.getPointType());
            cv.put(TicketD_CrDate1, ticketDetail.getCrDate1());
            db.insert(Ticket_Detail_Table, null, cv);
            count++;
        }
        Constant.showLog("Count " + count);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void addFEEDBACK(List<FeedbackClass> fList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        for (FeedbackClass fClass : fList) {
            cv.put(FED_AUTO, fClass.getAuto());
            cv.put(FED_QUESTION, fClass.getQuestion());
            cv.put(FED_CAT1, fClass.getCat1());
            cv.put(FED_CAT2, fClass.getCat2());
            cv.put(FED_CAT3, fClass.getCat3());
            cv.put(FED_CAT4, fClass.getCat4());
            cv.put(FED_CAT5, fClass.getCat5());
            cv.put(FED_CAT6, fClass.getCat6());
            cv.put(FED_STATUS, fClass.getStatus());
            cv.put(FED_TYPE, fClass.getType());
            db.insert(Table_QuestBank, null, cv);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public int getSMLMASTMaxAuto() {
        int maxAuto = 0;
        String str = "Select max(" + SMLMAST_Auto + ") from " + SMLMAST_Table;
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            maxAuto = res.getInt(0);
        }
        res.close();
        return maxAuto;
    }

    public List<String> getDistinctBranch() {
        List<String> list = new ArrayList<>();
        String str = "select distinct " + SMLMAST_ClientID + " from " + SMLMAST_Table + " order by " + SMLMAST_ClientID;
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            do {
                list.add(res.getString(0));
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

    /*public int getMaxAutoId(String type){
        int autoId = 0;
        Cursor res;
        if(type.equals("C")) {
            String str = "select count("+TicketM_Id+") from "+Ticket_Master_Table;
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
    }*/

    public int getMaxAutoId(String type) {
        int autoId = 0;
        Cursor res;
        if (type.equals("C")) {
            String str = "select max(" + TicketM_Id + ") from " + Ticket_Master_Table;
            res = getWritableDatabase().rawQuery(str, null);
        } else {
            String str = "select max(" + TicketM_Auto + ") from " + Ticket_Master_Table;
            res = getWritableDatabase().rawQuery(str, null);
        }
        if (res.moveToFirst()) {
            autoId = res.getInt(0);
        }
        res.close();
        return autoId;
    }

    public int getAutoTD() {
        int autoId = 0;
        Cursor res;
        String str = "select max(" + TicketD_Auto + ") from " + Ticket_Detail_Table;
        res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            autoId = res.getInt(0);
        }
        res.close();
        return autoId;
    }

    public int getMaxCompleteAutoId(String type) {
        int autoId = 0;
        Cursor res;
        if (type.equals("C")) {
            String str = "select count(" + TicketM_Id + ") from " + Ticket_Master_Table + " where " + TicketM_Status + "='Closed'";
            res = getWritableDatabase().rawQuery(str, null);
        } else {
            String str = "select count(" + TicketM_Auto + ") from " + Ticket_Master_Table + " where " + TicketM_Status + "='Closed'";
            res = getWritableDatabase().rawQuery(str, null);
        }
        if (res.moveToFirst()) {
            autoId = res.getInt(0);
        }
        res.close();
        return autoId;
    }

    /*public int getAutoTD() {
        int autoId = 0;
        Cursor res;
        String str = "select max(" + TicketD_Auto + ") from " + Ticket_Detail_Table;
        res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            autoId = res.getInt(0);
        }
        res.close();
        return autoId;
    }*/

    public int getMaxAutoIdAsync(String type) {
        int autoId = 0;
        Cursor res;
        if (type.equals("C")) {
            String str = "select count(" + TicketM_Id + ") from " + Ticket_Master_Table;
            res = getWritableDatabase().rawQuery(str, null);
        } else {
            String str = "select max(" + TicketM_Auto + ") from " + Ticket_Master_Table;
            res = getWritableDatabase().rawQuery(str, null);
        }
        if (res.moveToFirst()) {
            autoId = res.getInt(0);
        }
        res.close();
        return autoId;
    }

    public int getMaxCompleteAutoIdAsync(String type) {
        int autoId = 0;
        Cursor res;
        if (type.equals("C")) {
            String str = "select count(" + TicketM_Id + ") from " + Ticket_Master_Table + " where " + TicketM_Status + "='Closed'";
            res = getWritableDatabase().rawQuery(str, null);
        } else {
            String str = "select count(" + TicketM_Auto + ") from " + Ticket_Master_Table + " where " + TicketM_Status + "='Closed'";
            res = getWritableDatabase().rawQuery(str, null);
        }
        if (res.moveToFirst()) {
            autoId = res.getInt(0);
        }
        res.close();
        return autoId;
    }

    public int getAutoTDAsync() {
        int autoId = 0;
        Cursor res;
        String str = "select max(" + TicketD_Auto + ") from " + Ticket_Detail_Table;
        res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            autoId = res.getInt(0);
        }
        res.close();
        return autoId;
    }

    public String getCount() {
        String finYr = Constant.finYr();
        String count = "0-0-0";
        /*String str = "select " +
                "(select count(" + TicketM_Auto + ") from " + Ticket_Master_Table + " where " + TicketM_PointType + "<>'I') as Total," +
                "(select count(" + TicketM_Auto + ") from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Closed','Cancel','ClientClosed') and " + TicketM_PointType + "<>'I') as Complete," +
                "(select count(" + TicketM_Auto + ") from " + Ticket_Master_Table + " where " + TicketM_Status + "<>'Closed' and " + TicketM_Status + "<>'Cancel' and " + TicketM_Status + "<>'ClientClosed' and " + TicketM_PointType + "<>'I') as Pending";*/
        String str = "select " +
                "(select count(" + TicketM_Auto + ") from " + Ticket_Master_Table + " where " + TicketM_PointType + "<>'I'  and  Status not in ('Closed','Cancel','ClientClosed')) as Total," +
                "(select count(" + TicketM_Auto + ") from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Closed','Cancel','ClientClosed') and " + TicketM_PointType + "<>'I'  and FinYr='"+finYr+"') as Complete," +
                "(select count(" + TicketM_Auto + ") from " + Ticket_Master_Table + " where " + TicketM_Status + "<>'Closed' and " + TicketM_Status + "<>'Cancel' and " + TicketM_Status + "<>'ClientClosed' and " + TicketM_PointType + "<>'I'  and FinYr='"+finYr+"') as Pending";
        Constant.showLog(str);
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            do {
                count = res.getString(0) + "^" + res.getString(1) + "^" + res.getString(2);
            } while (res.moveToNext());
        }
        res.close();
        return count;
    }

    public ArrayList<TicketMasterClass> getTicketMaster(int limit, String crby, String type) {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str;
        if (type.equals("E")) {
            if (limit != 0) {
                str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " not in ('Closed','Cancel','ClientClosed')" +
                        " order by " + TicketM_Auto + " desc limit " + limit;
            } else {
                str = "select * from " + Ticket_Master_Table + " where " + TicketM_CrBy + "='" + crby + "' or " + TicketM_AssignTo +
                        "='" + crby + "' or " + TicketM_AssignTo + "='NotAssigned' and " + TicketM_Status + "<>'Cancel' order by " +
                        TicketM_Auto + " desc";
            }
        } else {
            if (limit != 0) {
                /*str = "select * from " + Ticket_Master_Table + " where "+ TicketM_Status + "<>'Closed' and " + TicketM_Status + "<>'Cancel' and "
                        + TicketM_Status + "<>'ClientClosed' order by " + TicketM_Auto + " desc limit " + limit;*/
                str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " not in ('Closed','Cancel','ClientClosed') order by " + TicketM_Auto + " desc limit " + limit;
            } else {
                str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + "<>'Cancel' order by " + TicketM_Auto + " desc";
            }
        }
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
            } while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getPendingTicket(String isHWApplicable) {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        if (isHWApplicable.equals("S") || isHWApplicable.equals("SH")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Open','Pending','ReOpen') and " +
                    TicketM_PointType + "='S' order by " + TicketM_Auto + " desc";
        } else if (isHWApplicable.equals("H")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Open','Pending','ReOpen') and " +
                    TicketM_PointType + "='H' order by " + TicketM_Auto + " desc";
        }
        Constant.showLog("getPendingTicket Start");
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
                pendingTicketClass.setBranch(res.getString(res.getColumnIndex(DBHandler.TicketM_Branch)));
                pendingTicketClassList.add(pendingTicketClass);
            } while (res.moveToNext());
        }
        res.close();
        Constant.showLog("getPendingTicket Stop");
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getPendingTicketLM(String isHWApplicable, int page) {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        if (isHWApplicable.equals("S") || isHWApplicable.equals("SH")) {
            /*str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Open','Pending','ReOpen') and " +
                    TicketM_PointType + "='S' order by " + TicketM_Auto + " desc";*/

            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status +
                    " in ('Open','Pending','ReOpen') and " +
                    TicketM_PointType + "='S' order by " + TicketM_Auto + " desc LIMIT " + limit + " * (" + page + " - 1)";

           /* str = "select  * from tb_newstile  where news_id not in(select news_id from tb_newstile  LIMIT "
                    + 5 * (page - 1) + ")  LIMIT " + 5 * page;*/


        } else if (isHWApplicable.equals("H")) {
            /*str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Open','Pending','ReOpen') and " +
                    TicketM_PointType + "='H' order by " + TicketM_Auto + " desc";*/

            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status +
                    " in ('Open','Pending','ReOpen') and " +
                    TicketM_PointType + "='H' order by " + TicketM_Auto + " desc LIMIT " + limit + " * (" + page + " - 1)";
        }
        Constant.showLog(str);
        Constant.showLog("getPendingTicket Start");
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
                pendingTicketClass.setBranch(res.getString(res.getColumnIndex(DBHandler.TicketM_Branch)));
                pendingTicketClassList.add(pendingTicketClass);
            } while (res.moveToNext());
        }
        res.close();
        Constant.showLog("getPendingTicket Stop");
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getClosedTicket(String isHWApplicable) {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        if (isHWApplicable.equals("S") || isHWApplicable.equals("SH")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Closed','ClientClosed') and "
                    + TicketM_PointType + "='S' order by " + TicketM_Auto + " desc";
        } else if (isHWApplicable.equals("H")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Closed','ClientClosed') and "
                    + TicketM_PointType + "='H' order by " + TicketM_Auto + " desc";
        }

        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
            } while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getClosedTicketLM(String isHWApplicable, int page) {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        if (isHWApplicable.equals("S") || isHWApplicable.equals("SH")) {

            /*str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Closed','ClientClosed') and "
                    + TicketM_PointType + "='S' order by " + TicketM_Auto + " desc";*/

            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Closed','ClientClosed') and "
                    + TicketM_PointType + "='S' order by " + TicketM_Auto + " desc LIMIT " + limit + " * (" + page + " - 1)";

        } else if (isHWApplicable.equals("H")) {

            /*str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Closed','ClientClosed') and "
                    + TicketM_PointType + "='H' order by " + TicketM_Auto + " desc";*/

            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Closed','ClientClosed') and "
                    + TicketM_PointType + "='H' order by " + TicketM_Auto + " desc LIMIT " + limit + " * (" + page + " - 1)";
        }
        Constant.showLog(str);

        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
            } while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getHoldTicket(String isHWApplicable) {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        if (isHWApplicable.equals("S") || isHWApplicable.equals("SH")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Hold','hold') and "
                    + TicketM_PointType + "='S' order by " + TicketM_Auto + " desc";
        } else if (isHWApplicable.equals("H")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Hold','hold') and "
                    + TicketM_PointType + "='H' order by " + TicketM_Auto + " desc";
        }
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
            } while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getHoldTicketLM(String isHWApplicable, int page) {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        if (isHWApplicable.equals("S") || isHWApplicable.equals("SH")) {
            /*str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Hold','hold') and "
                    + TicketM_PointType + "='S' order by " + TicketM_Auto + " desc";*/
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Hold','hold') and "
                    + TicketM_PointType + "='S' order by " + TicketM_Auto + " desc LIMIT " + limit + " * (" + page + " - 1)";
        } else if (isHWApplicable.equals("H")) {
            /*str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Hold','hold') and "
                    + TicketM_PointType + "='H' order by " + TicketM_Auto + " desc";*/
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Hold','hold') and "
                    + TicketM_PointType + "='H' order by " + TicketM_Auto + " desc LIMIT " + limit + " * (" + page + " - 1)";
        }
        Constant.showLog(str);
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
            } while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getCancelTicket(String isHWApplicable) {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        if (isHWApplicable.equals("S") || isHWApplicable.equals("SH")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Cancel') and "
                    + TicketM_PointType + "='S' order by " + TicketM_Auto + " desc";
        } else if (isHWApplicable.equals("H")) {
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Cancel') and "
                    + TicketM_PointType + "='H' order by " + TicketM_Auto + " desc";
        }
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
            } while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getCancelTicketLM(String isHWApplicable, int page) {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        if (isHWApplicable.equals("S") || isHWApplicable.equals("SH")) {
            /*str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Cancel') and "
                    + TicketM_PointType + "='S' order by " + TicketM_Auto + " desc";*/
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Cancel') and "
                    + TicketM_PointType + "='S' order by " + TicketM_Auto + " desc LIMIT " + limit + " * (" + page + " - 1)";
        } else if (isHWApplicable.equals("H")) {
            /*str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Cancel') and "
                    + TicketM_PointType + "='H' order by " + TicketM_Auto + " desc";*/
            str = "select * from " + Ticket_Master_Table + " where " + TicketM_Status + " in ('Cancel') and "
                    + TicketM_PointType + "='H' order by " + TicketM_Auto + " desc LIMIT " + limit + " * (" + page + " - 1)";
        }
        Constant.showLog(str);
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
            } while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getHardwarePoint() {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        str = "select * from " + Ticket_Master_Table + " where " + TicketM_PointType + "='H' order by " +
                TicketM_Auto + " desc";

        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
            } while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getHardwarePointLM(int page) {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str = null;
        /*str = "select * from " + Ticket_Master_Table + " where " + TicketM_PointType + "='H' order by " +
                TicketM_Auto + " desc";*/
        str = "select * from " + Ticket_Master_Table + " where " + TicketM_PointType + "='H' order by " +
                TicketM_Auto + "  desc LIMIT " + limit + " * (" + page + " - 1)";
        Constant.showLog(str);
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
            } while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getInternalePoint() {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str;
        str = "select * from " + Ticket_Master_Table + " where " + TicketM_PointType + "='I' order by " +
                TicketM_Auto + " desc";

        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
            } while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public ArrayList<TicketMasterClass> getInternalePointLM(int page) {
        ArrayList<TicketMasterClass> pendingTicketClassList = new ArrayList<>();
        String str;
        str = "select * from " + Ticket_Master_Table + " where " + TicketM_PointType + "='I' order by " +
                TicketM_Auto + "  desc LIMIT " + limit + " * (" + page + " - 1)";
        Constant.showLog(str);
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
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
            } while (res.moveToNext());
        }
        res.close();
        return pendingTicketClassList;
    }

    public String getAutoFolder(String clienID) {
        String data = "0";
        String str = "select " + SMLMAST_Auto + "," + SMLMAST_FTPImgFolder + " from " + SMLMAST_Table + " where " + SMLMAST_GroupId +
                "=(select " + SMLMAST_GroupId + " from " + SMLMAST_Table + " where " + SMLMAST_ClientID + "='" + clienID + "') and " + SMLMAST_isHO + "='Y'";
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            data = res.getString(0) + "^" + res.getString(1);
        }
        res.close();
        return data;
    }

    public String getFolder(int clientAuto) {
        String data = "0";
        String str = "select " + SMLMAST_FTPImgFolder + " from " + SMLMAST_Table + " where " + SMLMAST_Auto + "=" + clientAuto;
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            data = res.getString(0);
        }
        res.close();
        return data;
    }

    public String getMobile(int clientAuto) {
        String data = "0";
        String str = "select " + SMLMAST_GroupId + " from " + SMLMAST_Table + " where " + SMLMAST_Auto + "=" + clientAuto;
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            data = res.getString(0);
        }
        res.close();
        String str1 = "select " + SMLMAST_Mobile + " from " + SMLMAST_Table + " where " + SMLMAST_GroupId + "=" + data + " and " + SMLMAST_FTPLocation + "<>'' and " + SMLMAST_FTPUser + "<>'' and " + SMLMAST_FTPPass + "<>''";
        Cursor res1 = getWritableDatabase().rawQuery(str1, null);
        if (res1.moveToFirst()) {
            data = res1.getString(0);
        }
        res1.close();
        return data;
    }

    public void updateTicketStatus(int auto, int id, int clientAuto, String finyr, String status, String clientName, String ticketno) {
        String date = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        ContentValues cv = new ContentValues();
        cv.put(TicketM_Status, status);
        cv.put(TicketM_ModBy, clientName);
        cv.put(TicketM_ModDate, date);
        cv.put(TicketM_ModTime, time);
        getWritableDatabase().update(Ticket_Master_Table, cv,
                TicketM_Auto + "=? and " + TicketM_Id + "=? and " + TicketM_ClientAuto + "=? and " +
                        TicketM_FinYr + "=? and " + TicketM_TicketNo + "=?",
                new String[]{String.valueOf(auto), String.valueOf(id), String.valueOf(clientAuto), finyr, ticketno});
    }

    public void updateTicketMaster(TicketMasterClass master) {
        ContentValues cv = new ContentValues();
        cv.put(TicketM_Status, master.getStatus());
        cv.put(TicketM_ModBy, master.getModBy());
        cv.put(TicketM_ModDate, master.getModDate());
        cv.put(TicketM_ModTime, master.getModTime());
        cv.put(TicketM_ModDate1, master.getModdate1());
        cv.put(TicketM_AssignTo, master.getAssignTO());
        cv.put(TicketM_AssignDate, master.getAssignTODate());
        cv.put(TicketM_AssignTime, master.getAssignTOTime());
        cv.put(TicketM_AssignBy, master.getAssignBy());
        cv.put(TicketM_AssignByDate, master.getAssignByDate());
        cv.put(TicketM_AssignByTime, master.getAssignByTime());

        getWritableDatabase().update(Ticket_Master_Table, cv,
                TicketM_Auto + "=? and " + TicketM_Id + "=? and " + TicketM_ClientAuto + "=? and " +
                        TicketM_FinYr + "=? and " + TicketM_TicketNo + "=?",
                new String[]{String.valueOf(master.getAuto()), String.valueOf(master.getId()),
                        String.valueOf(master.getClientAuto()), master.getFinyr(), master.getTicketNo()});
    }

    public void deleteTabel(String tableName) {
        getWritableDatabase().execSQL("delete from " + tableName);
    }

    public String getLatestModDate1() {
        String data = "0";
        String str = "SELECT max(" + TicketM_ModDate1 + ") FROM " + Ticket_Master_Table + " where " + TicketM_ModDate1 + "<>'null'";
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            data = res.getString(0);
        }
        res.close();
        return data;
    }

    public Cursor getAllMDTicket() {
        String str = "select * from " + Ticket_Master_Table + " order by " + TicketM_Auto + " desc";
        return getWritableDatabase().rawQuery(str, null);
    }

    public Cursor getTicketDetail(int auto, String type) {
        String str;
        if (type.equals("E")) {
            str = "select TicketMaster.Auto,TicketMaster.ClientAuto,SMLMAST.ClientId, TicketMaster.TicketNo, TicketMaster.Subject, TicketMaster.Status," +
                    "TicketDetail.Auto,TicketDetail.MastAuto,TicketDetail.Description, TicketDetail.CrBy, TicketDetail.CrDate," +
                    "TicketDetail.CrTime " +
                    "from TicketMaster,TicketDetail,SMLMAST " +
                    "where TicketMaster.Auto = TicketDetail.MastAuto and SMLMAST.Auto = TicketMaster.ClientAuto and SMLMAST.Auto = TicketDetail.ClientAuto " +
                    " and TicketDetail.Auto = (select MAX(TicketDetail.Auto) from TicketDetail where TicketDetail.MastAuto = TicketMaster.Auto) " +
                    " order by TicketDetail.Auto desc ";
        } else {
            str = "select TicketMaster.Auto,TicketMaster.ClientAuto,SMLMAST.ClientId, TicketMaster.TicketNo, TicketMaster.Subject, TicketMaster.Status," +
                    "TicketDetail.Auto,TicketDetail.MastAuto,TicketDetail.Description, TicketDetail.CrBy, TicketDetail.CrDate," +
                    "TicketDetail.CrTime " +
                    "from TicketMaster,TicketDetail,SMLMAST " +
                    "where TicketMaster.Auto = TicketDetail.MastAuto and SMLMAST.Auto = TicketMaster.ClientAuto and SMLMAST.Auto = TicketDetail.ClientAuto " +
                    " and TicketDetail.Auto = (select MAX(TicketDetail.Auto) from TicketDetail where TicketDetail.MastAuto = TicketMaster.Auto) " +
                    " and TicketMaster.ClientAuto=" + auto +
                    " order by TicketDetail.Auto desc ";
        }
        Constant.showLog(str);
        return getWritableDatabase().rawQuery(str, null);
    }

    public Cursor getParticularTicketDetail() {
        String str = "select * from " + Ticket_Detail_Table + " where " + TicketD_MastAuto + "=" + UpdateTicketActivity.auto + " order by " + TicketD_Auto + " asc";
        Constant.showLog(str);
        return getWritableDatabase().rawQuery(str, null);
    }

    public int getIDTD() {
        int autoId = 0;
        Cursor res;
        String str = "select max(" + TicketD_Id + ") from " + Ticket_Detail_Table + " where " + TicketD_MastAuto + "=" + UpdateTicketActivity.auto;
        res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            autoId = res.getInt(0);
        }
        res.close();
        return autoId;
    }

    public void updateNickName(String nickname, String omobno, String auto) {
        ContentValues cv = new ContentValues();
        cv.put(SMLMAST_NickName, nickname);
        cv.put(SMLMAST_OtherMobNo, omobno);
        getWritableDatabase().update(SMLMAST_Table, cv, SMLMAST_Auto + "=?", new String[]{auto});
    }

    public Cursor getReleaseNotes() {
        String str = "select * from " + SoftwareVersionDetail_Table +" order by "+SVD_Auto+" desc";
        return getWritableDatabase().rawQuery(str, null);
    }

    public int getSVDMax() {
        int autoId = 0;
        Cursor res;
        String str = "select max(" + SVD_Auto + ") from " + SoftwareVersionDetail_Table;
        res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            autoId = res.getInt(0);
        }
        res.close();
        return autoId;
    }

    public void addReleaseNote(List<ReleaseNoteClass> noteList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        for (ReleaseNoteClass note : noteList) {
            cv.put(SVD_Auto, note.getAuto());
            cv.put(SVD_Version, note.getVersionNo());
            cv.put(SVD_Desc, note.getDesc());
            cv.put(SVD_CrBy, note.getCrby());
            cv.put(SVD_CrDate, note.getCrDate());
            cv.put(SVD_CrTime, note.getCrTime());
            cv.put(SVD_ModBy, note.getModby());
            cv.put(SVD_ModDate, note.getModDate());
            cv.put(SVD_ModTime, note.getModTime());
            db.insert(SoftwareVersionDetail_Table, null, cv);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public int getClientAuto(String clienID) {
        int auto = 0;
        String str = "select " + SMLMAST_Auto + " from " + SMLMAST_Table + " where " + SMLMAST_ClientID + "='" + clienID + "'";
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {
            auto = res.getInt(0);
        }
        res.close();
        return auto;
    }

    public Cursor getFeedQuestions() {
        String str = "select distinct " + FED_AUTO + " from " + Table_QuestBank;
        Constant.showLog(str);
        return getWritableDatabase().rawQuery(str, null);
    }

    public String getQuestion(int auto) {
        String s = "";
        String str = "select  " + FED_QUESTION + " from " + Table_QuestBank + " where " + FED_AUTO + "=" + auto;
        Constant.showLog(str);
        Cursor res = getWritableDatabase().rawQuery(str, null);
        if (res.moveToFirst()) {

            s = res.getString(0);
        }
        res.close();
        return s;
    }

    public String getFedType1(String question) {
        String a = "";
        String str = "select " + FED_TYPE + " from " + Table_QuestBank + " where " + FED_QUESTION + "='" + question + "'";
        Constant.showLog(str);
        Cursor cursor = getWritableDatabase().rawQuery(str, null);
        if (cursor.moveToFirst()) {

            a = cursor.getString(0);
        }
        cursor.close();
        return a;
    }

    public Cursor getQuesOPTIONS(int auto) {
        String str = "select " + FED_CAT1 + "," + FED_CAT2 + "," + FED_CAT3 + "," + FED_CAT4 + "," + FED_CAT5 + "," + FED_CAT6 + " from " + Table_QuestBank + " where " + FED_AUTO + " = " + auto;
        Constant.showLog(str);
        return getWritableDatabase().rawQuery(str, null);
    }
}


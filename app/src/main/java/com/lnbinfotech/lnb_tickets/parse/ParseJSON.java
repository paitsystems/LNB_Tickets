package com.lnbinfotech.lnb_tickets.parse;

import android.content.Context;
import android.content.SharedPreferences;

import com.lnbinfotech.lnb_tickets.FirstActivity;
import com.lnbinfotech.lnb_tickets.R;
import com.lnbinfotech.lnb_tickets.constant.Constant;
import com.lnbinfotech.lnb_tickets.db.DBHandler;
import com.lnbinfotech.lnb_tickets.log.WriteLog;
import com.lnbinfotech.lnb_tickets.model.AssetClass;
import com.lnbinfotech.lnb_tickets.model.FeedbackClass;
import com.lnbinfotech.lnb_tickets.model.SMLMASTClass;
import com.lnbinfotech.lnb_tickets.model.TicketMasterClass;
import com.lnbinfotech.lnb_tickets.model.ShortDescClass;
import com.lnbinfotech.lnb_tickets.model.TicketDetailClass;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Created by lnb on 8/11/2016.

public class ParseJSON {

    private String json;
    private Context context;
    private DBHandler db;

    public ParseJSON(String _json){
        this.json = _json;
    }

    public ParseJSON(String _json, Context _context){
        this.json = _json;
        this.context = _context;
        db = new DBHandler(_context);
        FirstActivity.pref = context.getSharedPreferences(FirstActivity.PREF_NAME,Context.MODE_PRIVATE);
    }

    public String parseGetCountData() {
        String data = null;
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String total = jsonArray.getJSONObject(i).getString("Total");
                    String complete = jsonArray.getJSONObject(i).getString("Complete");
                    String pending = jsonArray.getJSONObject(i).getString("Pending");
                    String message = jsonArray.getJSONObject(i).getString("Message");
                    data = total + "^" + complete + "^" + pending + "^" + message;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeLog("ParseJSON_parseGetCountData_"+e.getMessage());
        }
        return data;
    }

    public String parseVersion() {
        String data = null;
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    data = jsonArray.getJSONObject(i).getString("mobversion");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeLog("ParseJSON_parseVersion_"+e.getMessage());
        }
        return data;
    }

    public ArrayList<ShortDescClass> parseShortDesc(){
        ArrayList<ShortDescClass> list = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    ShortDescClass descClass = new ShortDescClass();
                    descClass.setDesc(jsonArray.getJSONObject(i).getString("Particular"));
                    list.add(descClass);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            writeLog("ParseJSON_parseShortDesc_"+e.getMessage());
        }
        return list;
    }

    public int parseUserData() {
        //result = 0-fail, 1-customerUser, 2-lnbUser
        int result = 0;
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                int auto = 0, groupId = 0;
                String ClientID=null,ClientName=null,FTPUser=null,FTPPass=null,FTPFolder=null,
                        CustomerName=null, mobile="0", FTPLocation = null, type=null, isHWapplicable=null,
                        nickname=null, othermobno = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    auto = jsonArray.getJSONObject(i).getInt("Auto");
                    ClientID = jsonArray.getJSONObject(i).getString("ClientID");
                    ClientName = jsonArray.getJSONObject(i).getString("ClientName");
                    mobile = jsonArray.getJSONObject(i).getString("Mobile");
                    /*FTPLocation = jsonArray.getJSONObject(i).getString("FTPLocation");
                    FTPUser = jsonArray.getJSONObject(i).getString("FTPUser");
                    FTPPass = jsonArray.getJSONObject(i).getString("FTPPass");*/
                    FTPFolder = jsonArray.getJSONObject(i).getString("FTPImgFolder");
                    CustomerName = jsonArray.getJSONObject(i).getString("CustomerName");
                    type = jsonArray.getJSONObject(i).getString("type");
                    isHWapplicable = jsonArray.getJSONObject(i).getString("isHWapplicable");
                    groupId = jsonArray.getJSONObject(i).getInt("GroupId");
                    nickname = jsonArray.getJSONObject(i).getString("nickname");
                    othermobno = jsonArray.getJSONObject(i).getString("othermobno");
                    result = 1;
                }
                SharedPreferences.Editor editor = FirstActivity.pref.edit();
                editor.putInt(context.getString(R.string.pref_auto), auto);
                editor.putString(context.getString(R.string.pref_clientID), ClientID);
                editor.putString(context.getString(R.string.pref_ClientName), ClientName);
                editor.putString(context.getString(R.string.pref_mobno), mobile);
                /*if(FTPLocation!=null && !FTPLocation.equals("")) {
                    if(FTPLocation.length()>=6) {
                        FTPLocation = FTPLocation.substring(6, FTPLocation.length() - 1);
                    }else{
                        FTPLocation = "";
                    }
                }
                editor.putString(context.getString(R.string.pref_FTPLocation), FTPLocation);
                editor.putString(context.getString(R.string.pref_FTPUser), FTPUser);
                editor.putString(context.getString(R.string.pref_FTPPass), FTPPass);*/
                editor.putString(context.getString(R.string.pref_FTPLocation), Constant.ftp_adress);
                editor.putString(context.getString(R.string.pref_FTPUser), Constant.ftp_username);
                editor.putString(context.getString(R.string.pref_FTPPass), Constant.ftp_password);
                editor.putString(context.getString(R.string.pref_FTPImgFolder), FTPFolder);
                editor.putString(context.getString(R.string.pref_CustomerName), CustomerName);
                editor.putString(context.getString(R.string.pref_emptype), type);
                editor.putInt(context.getString(R.string.pref_groupid), groupId);
                editor.putString(context.getString(R.string.pref_isHWapplicable), isHWapplicable);
                if(nickname!=null) {
                    if (!nickname.equals("NA")) {
                        editor.putString(context.getString(R.string.pref_nickname), nickname);
                    }
                }
                editor.apply();
            }
        } catch (Exception e) {
            result = 0;
            e.printStackTrace();
            writeLog("ParseJSON_parseUserData_"+e.getMessage());
        }
        return result;
    }

    public int parseSMLMASTData() {
        //result = 0-fail, 1-customerUser, 2-lnbUser
        int result = 0;
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                //db.deleteTabel(DBHandler.SMLMAST_Table);
                for (int i = 0; i < jsonArray.length(); i++) {
                    SMLMASTClass custClass = new SMLMASTClass();
                    custClass.setAuto(jsonArray.getJSONObject(i).getInt("Auto"));
                    custClass.setClientID(jsonArray.getJSONObject(i).getString("ClientID"));
                    custClass.setClientName(jsonArray.getJSONObject(i).getString("ClientName"));
                    custClass.setMobile(jsonArray.getJSONObject(i).getString("Mobile"));
                    custClass.setFTPLocation(jsonArray.getJSONObject(i).getString("FTPLocation"));
                    custClass.setFTPUser(jsonArray.getJSONObject(i).getString("FTPUser"));
                    custClass.setFTPPass(jsonArray.getJSONObject(i).getString("FTPPass"));
                    custClass.setFTPImgFolder(jsonArray.getJSONObject(i).getString("FTPImgFolder"));
                    custClass.setCustomerName(jsonArray.getJSONObject(i).getString("CustomerName"));
                    custClass.setEmail(jsonArray.getJSONObject(i).getString("Email"));
                    custClass.setGroupId(jsonArray.getJSONObject(i).getInt("GroupId"));
                    custClass.setIsHO(jsonArray.getJSONObject(i).getString("isHO"));
                    custClass.setIsHWapplicable(jsonArray.getJSONObject(i).getString("isHWapplicable"));
                    db.addSMLMAST(custClass);
                    result = 1;
                }
            }
        } catch (Exception e) {
            result = 0;
            e.printStackTrace();
            writeLog("ParseJSON_parseCustomerData_"+e.getMessage());
        }
        return result;
    }

    public ArrayList<TicketMasterClass> parseAllTicket(){
        ArrayList<TicketMasterClass> list = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            Constant.showLog(""+jsonArray.length());
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    TicketMasterClass pendingTicketClass = new TicketMasterClass();
                    pendingTicketClass.setAuto(jsonArray.getJSONObject(i).getInt("auto"));
                    pendingTicketClass.setId(jsonArray.getJSONObject(i).getInt("id"));
                    pendingTicketClass.setClientAuto(jsonArray.getJSONObject(i).getInt("ClientAuto"));
                    pendingTicketClass.setClientName(jsonArray.getJSONObject(i).getString("ClientName"));
                    pendingTicketClass.setFinyr(jsonArray.getJSONObject(i).getString("finyr"));
                    pendingTicketClass.setTicketNo(jsonArray.getJSONObject(i).getString("ticketNo"));
                    pendingTicketClass.setParticular(jsonArray.getJSONObject(i).getString("Particular"));
                    pendingTicketClass.setSubject(jsonArray.getJSONObject(i).getString("Subject"));
                    pendingTicketClass.setImagePAth(jsonArray.getJSONObject(i).getString("ImagePAth"));
                    pendingTicketClass.setStatus(jsonArray.getJSONObject(i).getString("Status"));
                    pendingTicketClass.setCrBy(jsonArray.getJSONObject(i).getString("CrBy"));
                    pendingTicketClass.setCrDate(jsonArray.getJSONObject(i).getString("CrDate"));
                    pendingTicketClass.setCrTime(jsonArray.getJSONObject(i).getString("CrTime"));
                    pendingTicketClass.setModBy(jsonArray.getJSONObject(i).getString("ModBy"));
                    String moddate = jsonArray.getJSONObject(i).getString("ModDate");
                    pendingTicketClass.setModDate(moddate);
                    pendingTicketClass.setModTime(jsonArray.getJSONObject(i).getString("ModTime"));
                    pendingTicketClass.setAssignTO(jsonArray.getJSONObject(i).getString("AssignTo"));
                    pendingTicketClass.setAssignTODate(jsonArray.getJSONObject(i).getString("AssignDate"));
                    pendingTicketClass.setAssignTOTime(jsonArray.getJSONObject(i).getString("AssignTime"));
                    pendingTicketClass.setAssignBy(jsonArray.getJSONObject(i).getString("Assignby"));
                    pendingTicketClass.setAssignByDate(jsonArray.getJSONObject(i).getString("AssignbyDate"));
                    pendingTicketClass.setAssignByTime(jsonArray.getJSONObject(i).getString("AssignbyTime"));
                    pendingTicketClass.setType(jsonArray.getJSONObject(i).getString("type"));
                    pendingTicketClass.setGenType(jsonArray.getJSONObject(i).getString("GenType"));
                    pendingTicketClass.setBranch(jsonArray.getJSONObject(i).getString("Branch"));
                    pendingTicketClass.setPointtype(jsonArray.getJSONObject(i).getString("PointType"));
                    String moddate1 = "null";
                    if(!moddate.equals("null")) {
                        Date d = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).parse(moddate);
                        moddate1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(d);
                    }
                    pendingTicketClass.setModdate1(moddate1);
                    list.add(pendingTicketClass);
                    //db.addTicketMaster(pendingTicketClass);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            writeLog("ParseJSON_parseAllTicket_"+e.getMessage());
        }
        return list;
    }

    public ArrayList<TicketMasterClass> parseUpdatedTicket(){
        ArrayList<TicketMasterClass> list = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            Constant.showLog(""+jsonArray.length());
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    TicketMasterClass pendingTicketClass = new TicketMasterClass();
                    pendingTicketClass.setAuto(jsonArray.getJSONObject(i).getInt("auto"));
                    pendingTicketClass.setId(jsonArray.getJSONObject(i).getInt("id"));
                    pendingTicketClass.setClientAuto(jsonArray.getJSONObject(i).getInt("ClientAuto"));
                    pendingTicketClass.setClientName(jsonArray.getJSONObject(i).getString("ClientName"));
                    pendingTicketClass.setFinyr(jsonArray.getJSONObject(i).getString("finyr"));
                    pendingTicketClass.setTicketNo(jsonArray.getJSONObject(i).getString("ticketNo"));
                    pendingTicketClass.setParticular(jsonArray.getJSONObject(i).getString("Particular"));
                    pendingTicketClass.setSubject(jsonArray.getJSONObject(i).getString("Subject"));
                    pendingTicketClass.setImagePAth(jsonArray.getJSONObject(i).getString("ImagePAth"));
                    pendingTicketClass.setStatus(jsonArray.getJSONObject(i).getString("Status"));
                    pendingTicketClass.setCrBy(jsonArray.getJSONObject(i).getString("CrBy"));
                    pendingTicketClass.setCrDate(jsonArray.getJSONObject(i).getString("CrDate"));
                    pendingTicketClass.setCrTime(jsonArray.getJSONObject(i).getString("CrTime"));
                    pendingTicketClass.setModBy(jsonArray.getJSONObject(i).getString("ModBy"));
                    String moddate = jsonArray.getJSONObject(i).getString("ModDate");
                    pendingTicketClass.setModDate(moddate);
                    pendingTicketClass.setModTime(jsonArray.getJSONObject(i).getString("ModTime"));
                    pendingTicketClass.setAssignTO(jsonArray.getJSONObject(i).getString("AssignTo"));
                    pendingTicketClass.setAssignTODate(jsonArray.getJSONObject(i).getString("AssignDate"));
                    pendingTicketClass.setAssignTOTime(jsonArray.getJSONObject(i).getString("AssignTime"));
                    pendingTicketClass.setAssignBy(jsonArray.getJSONObject(i).getString("Assignby"));
                    pendingTicketClass.setAssignByDate(jsonArray.getJSONObject(i).getString("AssignbyDate"));
                    pendingTicketClass.setAssignByTime(jsonArray.getJSONObject(i).getString("AssignbyTime"));
                    pendingTicketClass.setType(jsonArray.getJSONObject(i).getString("type"));
                    pendingTicketClass.setGenType(jsonArray.getJSONObject(i).getString("GenType"));
                    pendingTicketClass.setBranch(jsonArray.getJSONObject(i).getString("Branch"));
                    pendingTicketClass.setPointtype(jsonArray.getJSONObject(i).getString("PointType"));
                    String moddate1 = "null";
                    if(!moddate.equals("null")) {
                        Date d = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).parse(moddate);
                        moddate1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(d);
                    }
                    pendingTicketClass.setModdate1(moddate1);
                    list.add(pendingTicketClass);
                    db.updateTicketMaster(pendingTicketClass);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            writeLog("ParseJSON_parseUpdateTicket_"+e.getMessage());
        }
        return list;
    }

    public ArrayList<TicketDetailClass> parseTicketDetail(){
        ArrayList<TicketDetailClass> list = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                //db.deleteTabel(DBHandler.Ticket_Detail_Table);
                for (int i = 0; i < jsonArray.length(); i++) {
                    TicketDetailClass ticketDetailClass = new TicketDetailClass();
                    ticketDetailClass.setAuto(jsonArray.getJSONObject(i).getInt("Auto"));
                    ticketDetailClass.setMastAuto(jsonArray.getJSONObject(i).getInt("MastAuto"));
                    ticketDetailClass.setDesc(jsonArray.getJSONObject(i).getString("Description"));
                    ticketDetailClass.setCrby(jsonArray.getJSONObject(i).getString("CrBy"));
                    String crdate1 = jsonArray.getJSONObject(i).getString("CrDate");
                    ticketDetailClass.setCrDate(crdate1);
                    ticketDetailClass.setCrTime(jsonArray.getJSONObject(i).getString("CrTime"));
                    ticketDetailClass.setType(jsonArray.getJSONObject(i).getString("Type"));
                    ticketDetailClass.setId(jsonArray.getJSONObject(i).getInt("Id"));
                    ticketDetailClass.setClientAuto(jsonArray.getJSONObject(i).getInt("ClientAuto"));
                    ticketDetailClass.setPointType(jsonArray.getJSONObject(i).getString("PointType"));

                    Date d = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH).parse(crdate1);
                    String crdate2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(d);
                    ticketDetailClass.setCrDate1(crdate2);

                    list.add(ticketDetailClass);

                }
                db.addTicketDetail(list);
            }
        }catch (Exception e){
            e.printStackTrace();
            writeLog("ParseJSON_parseTicketDetail_"+e.getMessage());
        }
        return list;
    }

    public String parseProdUsed() {
        String data = null;
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    data = jsonArray.getJSONObject(i).getString("prodUsed");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeLog("ParseJSON_parseProdUsed_"+e.getMessage());
        }
        return data;
    }

    public List<AssetClass> parseAssetInfo() {
        List<AssetClass> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    AssetClass asset = new AssetClass();
                    asset.setAuto(jsonArray.getJSONObject(i).getInt("Auto"));
                    asset.setId(jsonArray.getJSONObject(i).getInt("Id"));
                    asset.setClientAuto(jsonArray.getJSONObject(i).getInt("ClientAuto"));
                    asset.setMachineId(jsonArray.getJSONObject(i).getString("MachineId"));
                    asset.setLocation(jsonArray.getJSONObject(i).getString("Location"));
                    asset.setMachineType(jsonArray.getJSONObject(i).getString("MachineType"));
                    asset.setBrandName(jsonArray.getJSONObject(i).getString("BrandName"));
                    asset.setProcessor(jsonArray.getJSONObject(i).getString("Processor"));
                    asset.setRAM(jsonArray.getJSONObject(i).getString("RAM"));
                    asset.setHardDisk(jsonArray.getJSONObject(i).getString("HardDisk"));
                    asset.setOS(jsonArray.getJSONObject(i).getString("OS"));
                    asset.setSWInstalled(jsonArray.getJSONObject(i).getString("SWInstalled"));
                    asset.setLicenseSW(jsonArray.getJSONObject(i).getString("LicenseSW"));
                    asset.setImageName(jsonArray.getJSONObject(i).getString("ImageName"));
                    asset.setPrinterName(jsonArray.getJSONObject(i).getString("PrinterName"));
                    asset.setModel(jsonArray.getJSONObject(i).getString("Model"));
                    asset.setModelNo(jsonArray.getJSONObject(i).getString("ModelNo"));
                    asset.setPrinterType(jsonArray.getJSONObject(i).getString("PrinterType"));
                    asset.setRouterName(jsonArray.getJSONObject(i).getString("RouterName"));
                    asset.setRouterModel(jsonArray.getJSONObject(i).getString("RouterModel"));
                    asset.setRouterModelNo(jsonArray.getJSONObject(i).getString("RouterModelNo"));
                    asset.setRouterType(jsonArray.getJSONObject(i).getString("RouterType"));
                    asset.setOther(jsonArray.getJSONObject(i).getString("Other"));
                    asset.setCrBy(jsonArray.getJSONObject(i).getInt("CrBy"));
                    asset.setCrDate(jsonArray.getJSONObject(i).getString("CrDate"));
                    asset.setCrTime(jsonArray.getJSONObject(i).getString("CrTime"));
                    asset.setActive(jsonArray.getJSONObject(i).getString("Active"));
                    asset.setRemark(jsonArray.getJSONObject(i).getString("Remark"));
                    asset.setUserNm(jsonArray.getJSONObject(i).getString("UserNm"));
                    asset.setContactNo(jsonArray.getJSONObject(i).getString("ContactNo"));
                    list.add(asset);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeLog("ParseJSON_parseAssetInfo_"+e.getMessage());
        }
        return list;
    }

    public int parseQuestBank(){
        int ret = 0;
        List<FeedbackClass> list = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                DBHandler db = new DBHandler(context);
                for (int i = 0; i < jsonArray.length(); i++) {
                    FeedbackClass fClass = new FeedbackClass();
                    fClass.setAuto(jsonArray.getJSONObject(i).getInt("Auto"));
                    fClass.setQuestion(jsonArray.getJSONObject(i).getString("Question"));
                    fClass.setCat1(jsonArray.getJSONObject(i).getString("Opt1"));
                    fClass.setCat2(jsonArray.getJSONObject(i).getString("Opt2"));
                    fClass.setCat3(jsonArray.getJSONObject(i).getString("Opt3"));
                    fClass.setCat4(jsonArray.getJSONObject(i).getString("Opt4"));
                    fClass.setCat5(jsonArray.getJSONObject(i).getString("Opt5"));
                    fClass.setCat6(jsonArray.getJSONObject(i).getString("Opt6"));
                    fClass.setStatus(jsonArray.getJSONObject(i).getString("Status"));
                    fClass.setType(jsonArray.getJSONObject(i).getString("Type"));
                    list.add(fClass);
                }
                db.addFEEDBACK(list);
                ret = 1;
            }
        }catch (Exception e){
            ret = 0;
            e.printStackTrace();
            writeLog("parseFEEDBACK()_"+e.getMessage());
        }
        return ret;
    }

    public String isFeedBackRequired() {
        String data = null;
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    data = jsonArray.getJSONObject(i).getString("isFeedbackReq");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeLog("ParseJSON_isFeedBackRequired_"+e.getMessage());
        }
        return data;
    }

    public String getAccHead() {
        String data = "";
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    data = data + jsonArray.getJSONObject(i).getInt("Emp_Id") +"^";
                    data = data + jsonArray.getJSONObject(i).getString("EmpFullName")+"^";
                    data = data + jsonArray.getJSONObject(i).getString("Emp_Phno")+"^";
                    data = data + jsonArray.getJSONObject(i).getString("Emp_mobno")+"^";
                    data = data + jsonArray.getJSONObject(i).getString("EmpImgName")+"^";
                    data = data + jsonArray.getJSONObject(i).getString("EmailId");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeLog("ParseJSON_isFeedBackRequired_"+e.getMessage());
        }
        return data;
    }

    private void writeLog(String _data){
        new WriteLog().writeLog(context,_data);
    }

}

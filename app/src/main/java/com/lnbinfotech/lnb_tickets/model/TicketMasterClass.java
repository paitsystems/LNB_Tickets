package com.lnbinfotech.lnb_tickets.model;

//Created by lnb on 8/12/2017.

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TicketMasterClass implements Serializable{

    @SerializedName("finyr")
    public String finyr;
    @SerializedName("ticketNo")
    public String ticketNo;
    @SerializedName("Particular")
    public String Particular;
    @SerializedName("Subject")
    public String Subject;
    @SerializedName("ImagePAth")
    public String ImagePAth;
    @SerializedName("Status")
    public String Status;
    @SerializedName("CrBy")
    public String CrBy;
    @SerializedName("CrDate")
    public String CrDate;
    @SerializedName("CrTime")
    public String CrTime;
    @SerializedName("ModBy")
    public String ModBy;
    @SerializedName("ModDate")
    public String ModDate;
    @SerializedName("ModTime")
    public String ModTime;
    @SerializedName("type")
    public String type;
    @SerializedName("GenType")
    public String genType;
    @SerializedName("AssignTo")
    public String assignTO;
    @SerializedName("AssignDate")
    public String assignTODate;
    @SerializedName("AssignTime")
    public String assignTOTime;
    @SerializedName("Assignby")
    public String assignBy;
    @SerializedName("AssignbyDate")
    public String assignByDate;
    @SerializedName("AssignbyTime")
    public String assignByTime;
    @SerializedName("Branch")
    public String branch;
    @SerializedName("ClientName")
    public String clientName;
    @SerializedName("PointType")
    public String pointtype;
    @SerializedName("ModDate1")
    public String moddate1;
    @SerializedName("auto")
    public int auto;
    @SerializedName("id")
    public int id;
    @SerializedName("ClientAuto")
    public int clientAuto;

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getParticular() {
        return Particular;
    }

    public void setParticular(String particular) {
        Particular = particular;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getImagePAth() {
        return ImagePAth;
    }

    public void setImagePAth(String imagePAth) {
        ImagePAth = imagePAth;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCrBy() {
        return CrBy;
    }

    public void setCrBy(String crBy) {
        CrBy = crBy;
    }

    public String getCrDate() {
        return CrDate;
    }

    public void setCrDate(String crDate) {
        CrDate = crDate;
    }

    public String getCrTime() {
        return CrTime;
    }

    public void setCrTime(String crTime) {
        CrTime = crTime;
    }

    public int getAuto() {
        return auto;
    }

    public void setAuto(int auto) {
        this.auto = auto;
    }

    public String getFinyr() {
        return finyr;
    }

    public void setFinyr(String finyr) {
        this.finyr = finyr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getClientAuto() {
        return clientAuto;
    }

    public void setClientAuto(int clientAuto) {
        this.clientAuto = clientAuto;
    }

    public String getAssignTO() {
        return assignTO;
    }

    public void setAssignTO(String assignTO) {
        this.assignTO = assignTO;
    }

    public String getModBy() {
        return ModBy;
    }

    public void setModBy(String modBy) {
        ModBy = modBy;
    }

    public String getModDate() {
        return ModDate;
    }

    public void setModDate(String modDate) {
        ModDate = modDate;
    }

    public String getModTime() {
        return ModTime;
    }

    public void setModTime(String modTime) {
        ModTime = modTime;
    }

    public String getAssignTODate() {
        return assignTODate;
    }

    public void setAssignTODate(String assignTODate) {
        this.assignTODate = assignTODate;
    }

    public String getAssignTOTime() {
        return assignTOTime;
    }

    public void setAssignTOTime(String assignTOTime) {
        this.assignTOTime = assignTOTime;
    }

    public String getAssignBy() {
        return assignBy;
    }

    public void setAssignBy(String assignBy) {
        this.assignBy = assignBy;
    }

    public String getAssignByDate() {
        return assignByDate;
    }

    public void setAssignByDate(String assignByDate) {
        this.assignByDate = assignByDate;
    }

    public String getAssignByTime() {
        return assignByTime;
    }

    public void setAssignByTime(String assignByTime) {
        this.assignByTime = assignByTime;
    }

    public String getGenType() {
        return genType;
    }

    public void setGenType(String genType) {
        this.genType = genType;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPointtype() {
        return pointtype;
    }

    public void setPointtype(String pointtype) {
        this.pointtype = pointtype;
    }

    public String getModdate1() {
        return moddate1;
    }

    public void setModdate1(String moddate1) {
        this.moddate1 = moddate1;
    }
}

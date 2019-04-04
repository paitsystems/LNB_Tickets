package com.lnbinfotech.lnb_tickets.model;

// Created by lnb on 8/18/2017.

import com.google.gson.annotations.SerializedName;

public class TicketDetailClass {

    @SerializedName("Description")
    public String desc;
    @SerializedName("CrDate")
    public String crDate;
    @SerializedName("CrTime")
    public String crTime;
    @SerializedName("Type")
    public String type;
    @SerializedName("CrBy")
    public String crby;
    @SerializedName("GenType")
    public String genType;
    @SerializedName("PointType")
    public String pointType;
    @SerializedName("CrDate1")
    public String crDate1;
    @SerializedName("Auto")
    public int auto;
    @SerializedName("MastAuto")
    public int mastAuto;
    @SerializedName("Id")
    public int id;
    @SerializedName("ClientAuto")
    public int clientAuto;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCrDate() {
        return crDate;
    }

    public void setCrDate(String crDate) {
        this.crDate = crDate;
    }

    public String getCrTime() {
        return crTime;
    }

    public void setCrTime(String crTime) {
        this.crTime = crTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAuto() {
        return auto;
    }

    public void setAuto(int auto) {
        this.auto = auto;
    }

    public int getMastAuto() {
        return mastAuto;
    }

    public void setMastAuto(int mastAuto) {
        this.mastAuto = mastAuto;
    }

    public String getCrby() {
        return crby;
    }

    public void setCrby(String crby) {
        this.crby = crby;
    }

    public String getGenType() {
        return genType;
    }

    public void setGenType(String genType) {
        this.genType = genType;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientAuto() {
        return clientAuto;
    }

    public void setClientAuto(int clientAuto) {
        this.clientAuto = clientAuto;
    }

    public String getCrDate1() {
        return crDate1;
    }

    public void setCrDate1(String crDate1) {
        this.crDate1 = crDate1;
    }
}

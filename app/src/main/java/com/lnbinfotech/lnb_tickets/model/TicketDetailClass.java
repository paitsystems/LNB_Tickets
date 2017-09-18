package com.lnbinfotech.lnb_tickets.model;

// Created by lnb on 8/18/2017.

public class TicketDetailClass {

    public String desc, crDate, crTime, type,crby;
    public int auto, mastAuto;

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
}

package com.lnbinfotech.lnb_tickets.model;

//Created by lnb on 8/28/2017.

public class SMLMASTClass {

    public int Auto, groupId;
    public String ClientID;
    public String ClientName;
    public String Mobile;
    public String Email;
    public String FTPLocation;
    public String FTPUser;
    public String FTPPass;
    public String FTPImgFolder;
    public String CustomerName;
    public String isHO;

    public String getIsHWapplicable() {
        return isHWapplicable;
    }

    public void setIsHWapplicable(String isHWapplicable) {
        this.isHWapplicable = isHWapplicable;
    }

    public String isHWapplicable;

    public int getAuto() {
        return Auto;
    }

    public void setAuto(int auto) {
        Auto = auto;
    }

    public String getClientID() {
        return ClientID;
    }

    public void setClientID(String clientID) {
        ClientID = clientID;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFTPLocation() {
        return FTPLocation;
    }

    public void setFTPLocation(String FTPLocation) {
        this.FTPLocation = FTPLocation;
    }

    public String getFTPUser() {
        return FTPUser;
    }

    public void setFTPUser(String FTPUser) {
        this.FTPUser = FTPUser;
    }

    public String getFTPPass() {
        return FTPPass;
    }

    public void setFTPPass(String FTPPass) {
        this.FTPPass = FTPPass;
    }

    public String getFTPImgFolder() {
        return FTPImgFolder;
    }

    public void setFTPImgFolder(String FTPImgFolder) {
        this.FTPImgFolder = FTPImgFolder;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getIsHO() {
        return isHO;
    }

    public void setIsHO(String isHO) {
        this.isHO = isHO;
    }
}

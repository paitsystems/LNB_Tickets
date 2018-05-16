package com.lnbinfotech.lnb_tickets.model;

//Created by ANUP on 18-04-2018.


import java.io.Serializable;

public class AssetClass implements Serializable{

    private int Auto,Id,CrBy,ClientAuto;
    private String MachineId,Location,MachineType,BrandName,Processor,RAM,HardDisk,OS,SWInstalled,LicenseSW,Remark,ImageName,PrinterName,Model,ModelNo,
            PrinterType,RouterName,RouterModel,RouterModelNo,RouterType,Other,CrDate,CrTime,Active,UserNm,ContactNo;

    public int getAuto() {
        return Auto;
    }

    public void setAuto(int auto) {
        Auto = auto;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getCrBy() {
        return CrBy;
    }

    public void setCrBy(int crBy) {
        CrBy = crBy;
    }

    public int getClientAuto() {
        return ClientAuto;
    }

    public void setClientAuto(int clientAuto) {
        ClientAuto = clientAuto;
    }

    public String getMachineId() {
        return MachineId;
    }

    public void setMachineId(String machineId) {
        MachineId = machineId;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getMachineType() {
        return MachineType;
    }

    public void setMachineType(String machineType) {
        MachineType = machineType;
    }

    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String brandName) {
        BrandName = brandName;
    }

    public String getProcessor() {
        return Processor;
    }

    public void setProcessor(String processor) {
        Processor = processor;
    }

    public String getRAM() {
        return RAM;
    }

    public void setRAM(String RAM) {
        this.RAM = RAM;
    }

    public String getHardDisk() {
        return HardDisk;
    }

    public void setHardDisk(String hardDisk) {
        HardDisk = hardDisk;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getSWInstalled() {
        return SWInstalled;
    }

    public void setSWInstalled(String SWInstalled) {
        this.SWInstalled = SWInstalled;
    }

    public String getLicenseSW() {
        return LicenseSW;
    }

    public void setLicenseSW(String licenseSW) {
        LicenseSW = licenseSW;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getPrinterName() {
        return PrinterName;
    }

    public void setPrinterName(String printerName) {
        PrinterName = printerName;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getModelNo() {
        return ModelNo;
    }

    public void setModelNo(String modelNo) {
        ModelNo = modelNo;
    }

    public String getPrinterType() {
        return PrinterType;
    }

    public void setPrinterType(String printerType) {
        PrinterType = printerType;
    }

    public String getRouterName() {
        return RouterName;
    }

    public void setRouterName(String routerName) {
        RouterName = routerName;
    }

    public String getRouterModel() {
        return RouterModel;
    }

    public void setRouterModel(String routerModel) {
        RouterModel = routerModel;
    }

    public String getRouterModelNo() {
        return RouterModelNo;
    }

    public void setRouterModelNo(String routerModelNo) {
        RouterModelNo = routerModelNo;
    }

    public String getRouterType() {
        return RouterType;
    }

    public void setRouterType(String routerType) {
        RouterType = routerType;
    }

    public String getOther() {
        return Other;
    }

    public void setOther(String other) {
        Other = other;
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

    public String getActive() {
        return Active;
    }

    public void setActive(String active) {
        Active = active;
    }

    public String getUserNm() {
        return UserNm;
    }

    public void setUserNm(String userNm) {
        UserNm = userNm;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }
}

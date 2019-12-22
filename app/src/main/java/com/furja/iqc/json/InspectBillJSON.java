package com.furja.iqc.json;

import com.furja.common.Qrcode;
import com.furja.common.User;
import com.furja.iqc.beans.ReferDetail;

import java.util.List;

public class InspectBillJSON {
    private String userName;
    private String userPass;
    private String FMaterialNumber;
    private List<ApplyCheckOrder> FItemDetail;
    private String FUsePolicy;
    private int FInspectQty;
    private List<Qrcode> Qrcode;
    private List<ReferDetail> FReferDetail;
    private String FUnitNumber;
    private String FQcScheme;
    private String FSourceOrgNumber;
    private String FCurrentOrgNumber;
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFQcScheme() {
        return FQcScheme;
    }

    public void setFQcScheme(String FQcScheme) {
        this.FQcScheme = FQcScheme;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getFCurrentOrgNumber() {
        return FCurrentOrgNumber;
    }

    public void setFCurrentOrgNumber(String FCurrentOrgNumber) {
        this.FCurrentOrgNumber = FCurrentOrgNumber;
    }

    public String getFMaterialNumber() {
        return FMaterialNumber;
    }

    public String getFUnitNumber() {
        return FUnitNumber;
    }

    public void setFUnitNumber(String FUnitNumber) {
        this.FUnitNumber = FUnitNumber;
    }

    public void setFMaterialNumber(String FMaterialNumber) {
        this.FMaterialNumber = FMaterialNumber;
    }

    public String getFSourceOrgNumber() {
        return FSourceOrgNumber;
    }

    public void setFSourceOrgNumber(String FSourceOrgNumber) {
        this.FSourceOrgNumber = FSourceOrgNumber;
    }

    public int getFInspectQty() {
        return FInspectQty;
    }

    public void setFInspectQty(int FInspectQty) {
        this.FInspectQty = FInspectQty;
    }

    public List<ReferDetail> getFReferDetail() {
        return FReferDetail;
    }

    public void setFReferDetail(List<ReferDetail> FReferDetail) {
        this.FReferDetail = FReferDetail;
    }

    public List<ApplyCheckOrder> getFItemDetail() {
        return FItemDetail;
    }

    public void setFItemDetail(List<ApplyCheckOrder> FItemDetail) {
        this.FItemDetail = FItemDetail;
    }

    public String getFUsePolicy() {
        return FUsePolicy;
    }

    public void setFUsePolicy(String FUsePolicy) {
        this.FUsePolicy = FUsePolicy;
    }

    public List<com.furja.common.Qrcode> getQrcode() {
        return Qrcode;
    }

    public void setQrcode(List<com.furja.common.Qrcode> qrcode) {
        Qrcode = qrcode;
    }

    public void setUser(User user) {
        if(user!=null){
            userName=user.getUserName();
            userPass=user.getPassword();
        }
    }
}

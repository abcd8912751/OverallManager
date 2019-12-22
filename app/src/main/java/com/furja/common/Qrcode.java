package com.furja.common;

import static com.furja.utils.Utils.intOf;

public class Qrcode {
    private String value;
    private int qty;
    private String tag;
    private String FBillNo;
    private int FMaterialID;
    private String FUserID;
    private int FID;
    private int FEntryID;
    private int FSourceID;
    private int FSourceEntryID;
    public Qrcode() {
    }
    public Qrcode(String userid) {
        this.FUserID=userid;
    }
    public Qrcode(String value, int qty, String tag, int FMaterialID) {
        this.value = value;
        this.qty = qty;
        this.tag = tag;
        this.FMaterialID = FMaterialID;
    }

    public int getFMaterialID() {
        return FMaterialID;
    }

    public int getFSourceID() {
        return FSourceID;
    }

    public void setFSourceID(int FSourceID) {
        this.FSourceID = FSourceID;
    }

    public int getFSourceEntryID() {
        return FSourceEntryID;
    }

    public void setFSourceEntryID(int FSourceEntryID) {
        this.FSourceEntryID = FSourceEntryID;
    }

    public void setFMaterialID(int FMaterialID) {
        this.FMaterialID = FMaterialID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFUserID() {
        return FUserID;
    }

    public void setFUserID(String FUserID) {
        this.FUserID = FUserID;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getFID() {
        return FID;
    }

    public void setFID(int FID) {
        this.FID = FID;
    }

    public int getFEntryID() {
        return FEntryID;
    }

    public void setFEntryID(int FEntryID) {
        this.FEntryID = FEntryID;
    }

    public String getFBillNo() {
        return FBillNo;
    }

    public void setFBillNo(String FBillNo) {
        this.FBillNo = FBillNo;
    }

}

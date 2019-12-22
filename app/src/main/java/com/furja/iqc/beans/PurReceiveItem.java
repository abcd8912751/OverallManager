package com.furja.iqc.beans;

public class PurReceiveItem {
    private int seq;        //行号
    private int interID;    //收料通知单内码
    private int entryID;    //收料通知单表体ID
    private String barcode;
    private String qcSchemeNumber;
    private String sampleSchemeNumber;


    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getInterID() {
        return interID;
    }

    public void setInterID(int interID) {
        this.interID = interID;
    }

    public int getEntryID() {
        return entryID;
    }

    public void setEntryID(int entryID) {
        this.entryID = entryID;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getQcSchemeNumber() {
        return qcSchemeNumber;
    }

    public void setQcSchemeNumber(String qcSchemeNumber) {
        this.qcSchemeNumber = qcSchemeNumber;
    }

    public String getSampleSchemeNumber() {
        return sampleSchemeNumber;
    }

    public void setSampleSchemeNumber(String sampleSchemeNumber) {
        this.sampleSchemeNumber = sampleSchemeNumber;
    }
}

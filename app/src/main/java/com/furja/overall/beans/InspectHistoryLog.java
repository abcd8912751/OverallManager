package com.furja.overall.beans;

import java.util.ArrayList;
import java.util.List;
public class InspectHistoryLog {
    private String FDocumentStatus;
    private String FInspectBillNo;
    private String FInstockBillNo;
    private String FMrbBillNo;
    private double FInspectQty;
    private String FUnitName;
    private String FReceiveBillno;
    private String FInspectResult;
    private String FUsePolicy;
    private double FInstockQty;
    private double FReturnQty;
    private double FReceiveQty;
    private int FInspectBillID;
    private String FSupplier;
    private String FMaterialName;
    private String FInspectDate;
    private String FInstockDate;
    private String FMrbDate;
    private String FReceiveBiller;
    private String FInstockBiller;
    private String FMrbBiller;
    private List<InspectHistoryLog> items;

    public InspectHistoryLog() {
    }

    public InspectHistoryLog(InspectHistoryLog log) {
        this.FDocumentStatus=log.getFDocumentStatus();
        this.FInspectBillID=log.getFInspectBillID();
        this.FInspectBillNo=log.getFInspectBillNo();
        this.FInspectQty=log.getFInspectQty();
    }

    public String getFDocumentStatus() {
        return FDocumentStatus;
    }

    public void setFDocumentStatus(String FDocumentStatus) {
        this.FDocumentStatus = FDocumentStatus;
    }

    public String getFInspectDate() {
        return FInspectDate;
    }

    public void setFInspectDate(String FInspectDate) {
        this.FInspectDate = FInspectDate;
    }

    public String getFInstockDate() {
        String date = FInstockDate;
        if(date!=null&&date.length()>15)
            date=date.substring(0,16);
        return date;
    }

    public void setFInstockDate(String FInstockDate) {
        this.FInstockDate = FInstockDate;
    }

    public String getFMrbDate() {
        String date = FMrbDate;
        if(date!=null&&date.length()>15)
            date=date.substring(0,16);
        return date;
    }

    public void setFMrbDate(String FMrbDate) {
        this.FMrbDate = FMrbDate;
    }

    public String getFInspectBillNo() {
        return FInspectBillNo;
    }

    public int getFInspectBillID() {
        return FInspectBillID;
    }

    public void setFInspectBillID(int FInspectBillID) {
        this.FInspectBillID = FInspectBillID;
    }

    public void setFInspectBillNo(String FInspectBillNo) {
        this.FInspectBillNo = FInspectBillNo;
    }

    public String getFInstockBillNo() {
        return FInstockBillNo;
    }

    public List<InspectHistoryLog> getItems() {
        return items;
    }

    public void setItems(List<InspectHistoryLog> items) {
        this.items = items;
    }

    public void setFInstockBillNo(String FInstockBillNo) {
        this.FInstockBillNo = FInstockBillNo;
    }

    public String getFReceiveBiller() {
        return FReceiveBiller;
    }

    public String getFMrbBiller() {
        return FMrbBiller;
    }

    public void setFMrbBiller(String FMrbBiller) {
        this.FMrbBiller = FMrbBiller;
    }

    public void setFReceiveBiller(String FReceiveBiller) {
        this.FReceiveBiller = FReceiveBiller;
    }

    public String getFInstockBiller() {
        return FInstockBiller;
    }

    public void setFInstockBiller(String FInstockBiller) {
        this.FInstockBiller = FInstockBiller;
    }

    public String getFMrbBillNo() {
        return FMrbBillNo;
    }

    public void setFMrbBillNo(String FMrbBillNo) {
        this.FMrbBillNo = FMrbBillNo;
    }

    public String getFSupplier() {
        return FSupplier;
    }

    public void setFSupplier(String FSupplier) {
        this.FSupplier = FSupplier;
    }

    public String getFMaterialName() {
        return FMaterialName;
    }

    public void setFMaterialName(String FMaterialName) {
        this.FMaterialName = FMaterialName;
    }

    public double getFReceiveQty() {
        return FReceiveQty;
    }

    public void setFReceiveQty(double FReceiveQty) {
        this.FReceiveQty = FReceiveQty;
    }

    public double getFInspectQty() {
        return FInspectQty;
    }

    public void setFInspectQty(double FInspectQty) {
        this.FInspectQty = FInspectQty;
    }

    public String getFUnitName() {
        return FUnitName;
    }

    public void setFUnitName(String FUnitName) {
        this.FUnitName = FUnitName;
    }

    public String getFReceiveBillno() {
        return FReceiveBillno;
    }

    public void setFReceiveBillno(String FReceiveBillno) {
        this.FReceiveBillno = FReceiveBillno;
    }

    public String getFInspectResult() {
        return FInspectResult;
    }

    public void setFInspectResult(String FInspectResult) {
        this.FInspectResult = FInspectResult;
    }

    public String getFUsePolicy() {
        return FUsePolicy;
    }

    public void setFUsePolicy(String FUsePolicy) {
        this.FUsePolicy = FUsePolicy;
    }

    public double getFInstockQty() {
        return FInstockQty;
    }

    public void setFInstockQty(double FInstockQty) {
        this.FInstockQty = FInstockQty;
    }

    public double getFReturnQty() {
        return FReturnQty;
    }

    public void setFReturnQty(double FReturnQty) {
        this.FReturnQty = FReturnQty;
    }
    public void addItem(InspectHistoryLog log){
        if(items==null)
            items = new ArrayList<>();
        items.add(log);
    }


}

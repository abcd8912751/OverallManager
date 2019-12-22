package com.furja.iqc.beans;

import com.furja.common.FIDBean;
import com.furja.iqc.json.NewQCList;

public class ReferDetail {
    /**
     * FSrcBillType : PUR_ReceiveBill
     * FSrcBillNo : CGSL000021
     * FSrcInterId : 100023
     * FSrcEntryId : 100035
     * FSrcEntrySeq : 1
     * FOrderType : {"FID":"PUR_PurchaseOrder"}
     * FOrderBillNo : CGDD000022
     * FOrderId : 100023
     * FOrderEntryId : 100042
     * FOrderEntrySeq : 1
     */
    private String FSrcBillType;
    private String FSrcBillNo;
    private int FSrcInterId;
    private int FSrcEntryId;
    private int FSrcEntrySeq;
    private FIDBean FOrderType;
    private String FOrderBillNo;
    private int FOrderId;
    private int FOrderEntryId;
    private int FOrderEntrySeq;

    public ReferDetail() {
    }

    public ReferDetail(String srcBillType) {
        this.FSrcBillType=srcBillType;
    }

    public String getFSrcBillType() {
        return FSrcBillType;
    }

    public void setFSrcBillType(String FSrcBillType) {
        this.FSrcBillType = FSrcBillType;
    }

    public String getFSrcBillNo() {
        return FSrcBillNo;
    }

    public void setFSrcBillNo(String FSrcBillNo) {
        this.FSrcBillNo = FSrcBillNo;
    }

    public int getFSrcInterId() {
        return FSrcInterId;
    }

    public void setFSrcInterId(int FSrcInterId) {
        this.FSrcInterId = FSrcInterId;
    }

    public int getFSrcEntryId() {
        return FSrcEntryId;
    }

    public void setFSrcEntryId(int FSrcEntryId) {
        this.FSrcEntryId = FSrcEntryId;
    }

    public int getFSrcEntrySeq() {
        return FSrcEntrySeq;
    }

    public void setFSrcEntrySeq(int FSrcEntrySeq) {
        this.FSrcEntrySeq = FSrcEntrySeq;
    }

    public FIDBean getFOrderType() {
        return FOrderType;
    }

    public void setFOrderType(FIDBean FOrderType) {
        this.FOrderType = FOrderType;
    }

    public String getFOrderBillNo() {
        return FOrderBillNo;
    }

    public void setFOrderBillNo(String FOrderBillNo) {
        this.FOrderBillNo = FOrderBillNo;
    }

    public int getFOrderId() {
        return FOrderId;
    }

    public void setFOrderId(int FOrderId) {
        this.FOrderId = FOrderId;
    }

    public int getFOrderEntryId() {
        return FOrderEntryId;
    }

    public void setFOrderEntryId(int FOrderEntryId) {
        this.FOrderEntryId = FOrderEntryId;
    }

    public int getFOrderEntrySeq() {
        return FOrderEntrySeq;
    }

    public void setFOrderEntrySeq(int FOrderEntrySeq) {
        this.FOrderEntrySeq = FOrderEntrySeq;
    }


    public void convertQcData(NewQCList.QCDataBean qcDataBean) {
        setFSrcBillNo(qcDataBean.getApplyOrderBillNo());
        setFSrcInterId(qcDataBean.getApplyOrderID());
        setFSrcEntryId(qcDataBean.getApplyOrderEntryID());
        setFSrcEntrySeq(qcDataBean.getApplyOrder_Row());
        setFOrderBillNo(qcDataBean.getFOrderBillNo());
        setFOrderId(qcDataBean.getFOrderId());
        setFOrderEntryId(qcDataBean.getFOrderEntryId());
        setFOrderEntrySeq(qcDataBean.getFOrderEntrySeq());
        setFOrderType(new FIDBean(qcDataBean.getFOrderType()));
    }
}

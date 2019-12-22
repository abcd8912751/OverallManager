package com.furja.iqc.json;

import java.util.ArrayList;
import java.util.List;

public class PolicyItem {

    /**
     * FPolicyStatus :
     * FPolicyQty : 1100
     * FBasePolicyQty : 1100
     * FUsePolicy : 接受
     * FSerialId : {"FNUMBER":""}
     * FIsCheck : false
     * FIsDefectProcess : false
     * FCanSale : false
     * FIsMRBReview : false
     * FIsReturn : false
     * FIsRelatedDefect : false
     * FMRBReviewStatus :
     * FPcsNumber :
     * FMemo1 :
     * FMRBEntryId : 0
     */

    private String FPolicyStatus;
    private int FPolicyQty;
    private int FBasePolicyQty;
    private String FUsePolicy;
    private String FIsCheck="false";
    private String FIsDefectProcess="false";
    private String FCanSale="false";
    private String FIsMRBReview="false";
    private String FIsReturn="false";
    private String FIsRelatedDefect="false";
    private String FMRBReviewStatus;
    private String FPcsNumber;
    private String FMemo1;
    private int FMRBEntryId;

    public PolicyItem(){
    }
    public PolicyItem(String policy, int policyQty){
        this.FUsePolicy=policy;
        this.FPolicyQty=policyQty;
        this.FBasePolicyQty=policyQty;
    }

    public String getFPolicyStatus() {
        return FPolicyStatus;
    }

    public void setFPolicyStatus(String FPolicyStatus) {
        this.FPolicyStatus = FPolicyStatus;
    }

    public int getFPolicyQty() {
        return FPolicyQty;
    }

    public void setFPolicyQty(int FPolicyQty) {
        this.FPolicyQty = FPolicyQty;
    }

    public int getFBasePolicyQty() {
        return FBasePolicyQty;
    }

    public void setFBasePolicyQty(int FBasePolicyQty) {
        this.FBasePolicyQty = FBasePolicyQty;
    }

    public String getFUsePolicy() {
        return FUsePolicy;
    }

    public void setFUsePolicy(String FUsePolicy) {
        this.FUsePolicy = FUsePolicy;
    }

    public String getFIsCheck() {
        return FIsCheck;
    }

    public void setFIsCheck(String FIsCheck) {
        this.FIsCheck = FIsCheck;
    }

    public String getFIsDefectProcess() {
        return FIsDefectProcess;
    }

    public void setFIsDefectProcess(String FIsDefectProcess) {
        this.FIsDefectProcess = FIsDefectProcess;
    }

    public String getFCanSale() {
        return FCanSale;
    }

    public void setFCanSale(String FCanSale) {
        this.FCanSale = FCanSale;
    }

    public String getFIsMRBReview() {
        return FIsMRBReview;
    }

    public void setFIsMRBReview(String FIsMRBReview) {
        this.FIsMRBReview = FIsMRBReview;
    }

    public String getFIsReturn() {
        return FIsReturn;
    }

    public void setFIsReturn(String FIsReturn) {
        this.FIsReturn = FIsReturn;
    }

    public String getFIsRelatedDefect() {
        return FIsRelatedDefect;
    }

    public void setFIsRelatedDefect(String FIsRelatedDefect) {
        this.FIsRelatedDefect = FIsRelatedDefect;
    }

    public String getFMRBReviewStatus() {
        return FMRBReviewStatus;
    }

    public void setFMRBReviewStatus(String FMRBReviewStatus) {
        this.FMRBReviewStatus = FMRBReviewStatus;
    }

    public String getFPcsNumber() {
        return FPcsNumber;
    }

    public void setFPcsNumber(String FPcsNumber) {
        this.FPcsNumber = FPcsNumber;
    }

    public String getFMemo1() {
        return FMemo1;
    }

    public void setFMemo1(String FMemo1) {
        this.FMemo1 = FMemo1;
    }

    public int getFMRBEntryId() {
        return FMRBEntryId;
    }

    public void setFMRBEntryId(int FMRBEntryId) {
        this.FMRBEntryId = FMRBEntryId;
    }

    public List<PolicyItem> toList() {
        List<PolicyItem> lst= new ArrayList<>();
        lst.add(this);
        return lst;
    }
}

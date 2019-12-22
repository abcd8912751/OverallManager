package com.furja.iqc.beans;

import android.text.TextUtils;

/**
 * 检验原因,正常或不正常
 */

public class CheckReason {
    private boolean pass;
    private String reasonCode;      //原因代码
    private String reason;          //原因
    private String reasonNum;       //相应原因的数目

    /**
     * 根据是否pass来设定不合格原因
     * @param isPass
     */
    public CheckReason(boolean isPass)
    {
        this.pass=isPass;
        if(isPass)
        {
            this.reasonNum="0";
            this.reasonCode ="0";
            this.reason="";
        }
        else
        {
            this.reasonNum="11";
            this.reasonCode ="A054";
            this.reason="物料信息不匹配";
        }

    }



    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        if(!TextUtils.isEmpty(reasonNum)&&reasonNum.equals("0"))
            this.reasonCode="0";
        else
            this.reasonCode = reasonCode;
    }

    public String getReasonNum() {
        return reasonNum;
    }

    public void setReasonNum(String reasonNum) {
        this.reasonNum = reasonNum;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

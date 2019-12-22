package com.furja.verify.json;

/**
 * 释放结果的Json
 */

public class ReleaseResultJson {

    /**
     * ErrCode : 100
     * ErrMsg : 数据提交成功!
     * ErrData : true
     */

    private int ErrCode;
    private String ErrMsg;
    private boolean ErrData;

    public int getErrCode() {
        return ErrCode;
    }

    public void setErrCode(int ErrCode) {
        this.ErrCode = ErrCode;
    }

    public String getErrMsg() {
        return ErrMsg;
    }

    public void setErrMsg(String ErrMsg) {
        this.ErrMsg = ErrMsg;
    }

    public boolean isErrData() {
        return ErrData;
    }

    public void setErrData(boolean ErrData) {
        this.ErrData = ErrData;
    }
}

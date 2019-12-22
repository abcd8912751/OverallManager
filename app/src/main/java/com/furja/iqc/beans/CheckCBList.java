package com.furja.iqc.beans;

/**
 * Created by zhangmeng on 2018/6/20.
 */

public class CheckCBList {

    /**
     * IsCheck : 0
     */

    private String IsCheck;

    public String getIsCheck() {
        return IsCheck;
    }

    public void setIsCheck(String IsCheck) {
        this.IsCheck = IsCheck;
    }
    public int getCheckCode()
    {
        return Integer.valueOf(IsCheck);
    }
}

package com.furja.iqc.beans;

/**
 * 主要设置请求次数的,默认超出三次即为over将Toast网络异常
 */

public class RequestResult {
    private int requestCount;
    private int limit;
    public RequestResult()
    {
        initValue();
    }
    public boolean isOverTimes()
    {
        if(requestCount<limit)
            return false;
        return true;
    }

    /**
     * 请求失败将请求次数加1
     */
    public void error()
    {
        this.requestCount=requestCount+1;
    }

    /**
     * 初始化数值
     */
    public void initValue()
    {
        this.limit=3;
        this.requestCount=0;
    }
}

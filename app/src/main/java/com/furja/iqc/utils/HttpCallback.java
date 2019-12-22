package com.furja.iqc.utils;

/*
*Http提交的回执
*/
public interface HttpCallback<T> {
    public void onSuccess(T object);
    public void onFail(String errorMsg);
}

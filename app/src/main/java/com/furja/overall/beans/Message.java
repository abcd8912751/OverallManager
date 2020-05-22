package com.furja.overall.beans;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;


public class Message<T> implements Serializable{
    @JSONField(serialize=false)
    private static final long   serialVersionUID    = 1L;
    private int code;   //100为用户,102为push的消息
    private String msg;     //对于code的解释
    private T data;

    public Message(int code, String msg)
    {
        this(code,msg,null);
    }

    public Message(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

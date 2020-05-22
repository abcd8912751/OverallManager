package com.furja.overall.beans;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * 使用Jackson解析
 */
public class PushMessage implements Serializable {
    @JSONField(serialize = false)
    private static final long   serialVersionUID    = 1L;
    public String packageName;
    public String pushMsg;
    public String title;
    public PushMessage() {

    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPushMsg() {
        return pushMsg;
    }

    public void setPushMsg(String pushMsg) {
        this.pushMsg = pushMsg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

package com.furja.common;

import android.os.Build;
import android.text.TextUtils;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 操作员使用员工号数据库进行登录审核
 */
public class User {
    private String userName;
    private String userId;
    private String password;
    @JSONField(serialize = false)
    private boolean isChecker;
    private String packageName;
    private List<CloudUserWithOrg> data;
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public List<CloudUserWithOrg> getData() {
        return data;
    }

    public void setData(List<CloudUserWithOrg> data) {
        this.data = data;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public void formatUserId(String info)
    {
        String string[]= info.split(",");
        setUserId(string[0]);
    }


    @Override
    public String toString() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return "    用户名:" + userName + System.lineSeparator()
                    + System.lineSeparator() +
                    "    ID: " + userId;
        }
        else
            return super.toString();
    }

    public String getUserId() {
        if(TextUtils.isEmpty(userId))
            return "0";
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }



    public boolean isChecker() {
        return isChecker;
    }

    public void setChecker(boolean checker) {
        isChecker = checker;
    }


}

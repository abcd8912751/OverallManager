package com.furja.verify;

import android.content.Context;

import com.furja.common.User;
import com.furja.utils.Utils;
import com.furja.common.Preferences;

/**
 * 主Application
 */

public class VerifyProductApp  {
    private static volatile Context context;
    private static User user;


    public static User getUser() {
        return user;
    }




    public static Context getContext() {
        return Utils.getContext();
    }


    /**
     * 将员工用户信息存储
     * @param user
     */
    public static void setUserAndSave(User user) {
        VerifyProductApp.user = user;
        if(user !=null)
            Preferences.saveUser(user);
    }

}

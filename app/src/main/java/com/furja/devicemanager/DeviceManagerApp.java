package com.furja.devicemanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.furja.common.Preferences;
import com.furja.devicemanager.databases.DaoMaster;
import com.furja.devicemanager.databases.DaoSession;
import com.furja.devicemanager.services.AppPushService;
import com.furja.overall.FurjaApp;
import com.furja.common.User;
import com.furja.utils.Utils;

/**
 * 主Application
 */
public class DeviceManagerApp {
    private static  Context context;
    private static User user;
    private static AppPushService pushService;
    ServiceConnection serviceConnection;
    public static DeviceManagerApp getInstance()
    {
        return new DeviceManagerApp();
    }
    public DeviceManagerApp()
    {
        context= FurjaApp.getContext();
//        bindPushServices();
    }

    public static AppPushService getPushService() {
        return pushService;
    }

    public static void setPushService(AppPushService pushService) {
        DeviceManagerApp.pushService = pushService;
    }







    /**
     * 绑定推送服务
     */
    private void bindPushServices()
    {
        Intent intent=new Intent(context, AppPushService.class);
        context.startService(intent);
        serviceConnection=new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                AppPushService.PushBinder binder=
                        (AppPushService.PushBinder)iBinder;
                pushService=binder.getPushService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                pushService=null;
                bindPushServices();
            }
        };
        context.bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
    }



    public static Context getContext() {
        return context;
    }


    /**
     * 将员工用户信息存储
     * @param user
     */
    public static void setUserAndSave(User user) {
        DeviceManagerApp.user = user;
        if(user!=null)
            Preferences.saveUser(user);
        else
            FurjaApp.getInstance()
                    .setUserAndSave(null);
    }



    public static DaoSession getDaoSession()
    {
        DaoMaster daoMaster=Utils.getDaoMaster();
        return daoMaster.newSession();
    }

    public static User getUser() {
        return user;
    }

    public static boolean isChecker()
    {
        if(user==null)
            return false;
        return user.isChecker();
    }


    public static String getUserId() {
        if(user==null)
            return "0";
        return user.getUserId();
    }



    public  void unbindService()
    {
        if(serviceConnection!=null&&context!=null)
            context.unbindService(serviceConnection);
    }


}

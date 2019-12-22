package com.furja.alertsop.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.furja.utils.Utils.showLog;

/**
 * 网络切换接收
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private static boolean isInnerNet=true;
    private static Context context;
    private static NetworkChangeReceiver networkChangeReceiver;
    public static void init(Context applicationContext)
    {
        context=applicationContext;
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getInstance();
        context.registerReceiver(networkChangeReceiver,intentFilter);
    }
    public static NetworkChangeReceiver getInstance()
    {
        if(networkChangeReceiver==null)
            networkChangeReceiver=new NetworkChangeReceiver();
        return networkChangeReceiver;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            showLog("网络不可用");
        }else{
            pingAndSetCurNet();
        }
    }

    /**
     * ping 指定IP返回成功与否,此为linux下的方法
     * @param host
     * @return
     */
    public  boolean ping(String host) {
        String line = null;
        Process process = null;
        int pingCount=1;
        String command = "ping -c " + pingCount + " " + host;
        boolean isSuccess = false;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                return false;
            }
            int status = process.waitFor();
            if (status == 0) {
                isSuccess = true;
            } else {
                isSuccess = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return isSuccess;
    }




    public static boolean isInnerNet() {
        return isInnerNet;
    }

    public static void setIsInnerNet(boolean isInnerNet) {
        NetworkChangeReceiver.isInnerNet = isInnerNet;
    }
    public static void unregister()
    {
        if(context!=null&&networkChangeReceiver!=null)
            context.unregisterReceiver(networkChangeReceiver);
    }

    public void pingAndSetCurNet() {
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ping("192.168.10.5")||!ping("119.75.217.109");
            }})
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isInnerNet) throws Exception {
                        NetworkChangeReceiver.isInnerNet=isInnerNet;
                        showLog("网络状态变化为内网:"+isInnerNet);
                    }
                });
    }
}

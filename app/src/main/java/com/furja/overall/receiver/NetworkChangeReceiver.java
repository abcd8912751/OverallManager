package com.furja.overall.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.furja.utils.Constants;
import com.furja.utils.SharpBus;

import java.util.concurrent.Callable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.furja.utils.Constants.TAG_GOT_NETWORK;
import static com.furja.utils.Utils.showLog;

/**
 * 网络切换接收
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private static Context context;
    private static NetworkChangeReceiver networkChangeReceiver;
    private static boolean oldFlag = true;  //尚未获取到准确网络环境时为false
    private static boolean isInnerNet=true;
    private NetWorkListener netWorkListener;    //网络切换时调用其方法通知
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
        if(networkInfo==null||!networkInfo.isAvailable())
            showLog("网络不可用");
        else
            pingAndSetCurNet();
    }

    /**
     * ping 指定IP返回成功与否
     * @param host
     * @return
     */
    public static boolean ping(String host) {
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


    public static void unregister() {
        if(context!=null&&networkChangeReceiver!=null)
            context.unregisterReceiver(networkChangeReceiver);
    }

    public static boolean isInnerNet() {
        return isInnerNet;
    }

    public static void setIsInnerNet(boolean isInnerNet) {
        NetworkChangeReceiver.isInnerNet = isInnerNet;
        Constants.isInnerNet=isInnerNet;
    }

    /**
     * ping 一下公司内网服务器,判断是否在内网
     */
    public  void pingAndSetCurNet()
    {
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ping("192.168.10.5");   //ping百度的IP
            }})
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isInnerNet) throws Exception {
                        if(oldFlag){
                            SharpBus.getInstance().post(TAG_GOT_NETWORK,TAG_GOT_NETWORK);
                        }
                        oldFlag=false;
                        Log.e("NetworkChange","网络状态变化为内网:"+isInnerNet);
                        setIsInnerNet(isInnerNet);
                        Constants.isInnerNet=isInnerNet;
                        toRemedy();
                    }
                });
    }

    /**
     * 当网络恢复正常时进行一些补救操作->比如上传因网络异常未被上传的数据
     */
    private  void toRemedy() {

        if(netWorkListener!=null)
            netWorkListener.onChange(isInnerNet);
    }
    public static boolean isOldFlag() {
        return oldFlag;
    }
    public interface NetWorkListener
    {
        void onChange(boolean isInnerNet);
    }
}

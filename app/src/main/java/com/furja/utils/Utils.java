package com.furja.utils;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSON;
import com.furja.common.BadMaterialLogDao;
import com.furja.common.BadTypeConfigDao;
import com.furja.common.DaoSession;
import com.furja.common.Preferences;
import com.furja.common.DaoMaster;
import com.furja.common.QMAGroupData;
import com.furja.common.QMAGroupDataDao;
import com.furja.overall.FurjaApp;
import com.furja.overall.R;
import com.furja.common.BadMaterialLog;
import com.furja.common.BadTypeConfig;
import com.furja.overall.json.BadTypeConfigJson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.RequestBody;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.furja.utils.Constants.LOG_TAG;
import static com.furja.utils.Constants.SYNCOVER_BADTYPE_CONFIG;
import static com.furja.verify.utils.InneURL.FURJA_BADTYPEBASIC_URL;

/**
 * 显示Log或Toast
 */

public class Utils {
    public static Random random=new Random();
    public static NotificationManager notificationManager;
    private static  Context context;
    private static DaoMaster daoMaster;
    private static DaoMaster.DevOpenHelper helper;
    /**
     * 打印Log
     * @param msg
     */
    public static void showLog(String msg) {
        Log.i(Constants.LOG_TAG,msg);
    }

    /**
     * 打印Log
     * @param msg
     */
    public static void showError(String msg) {
        Log.e(Constants.LOG_TAG,msg);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dp(float pxValue) {
        return (pxValue / Resources.getSystem().getDisplayMetrics().density);
    }
    /**
     * 显示Toast
     * @param msg
     */
    public static void showLongToast(final String msg) {
        showLog(msg);
        new Thread(){
            public void run(){
                try {
                    Looper.prepare();
                    Context context= FurjaApp.getContext();
                    if (Build.VERSION.SDK_INT>24)
                        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
                    else
                        AnimToast.makeText(context,msg).setDelay(2).show();
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 显示Toast
     * @param msg
     */
    public static void showToast(final String msg) {
        showLog(msg);
        new Thread(){
            public void run(){
                try {
                    Looper.prepare();
                    Context context= FurjaApp.getContext();
                    if (Build.VERSION.SDK_INT>24)
                        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                    else
                        AnimToast.makeText(context,msg).show();
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static String getIntentAction(Intent intent){
        String action=null;
        if(intent!=null)
            action=intent.getAction();
        if(action==null)
            action="";
        else
            action = action.toUpperCase();
        return action;
    }


    /**
     * 将 BadMaterialLog 数据存至本地
     * @param badMaterialLog
     */
    public static void saveToLocal(BadMaterialLog badMaterialLog) {
        DaoSession daoSession= FurjaApp.getDaoSession();
        BadMaterialLogDao dao=daoSession.getBadMaterialLogDao();
        dao.save(badMaterialLog);
        daoSession.clear();
    }

    /**
     * 将 BadMaterialLog 数据存至本地并上传
     * @param badMaterialLog
     */
    public static void saveAndUpload(BadMaterialLog badMaterialLog) {
        saveToLocal(badMaterialLog);
        badMaterialLog.uploadToRemote();
    }

    public static <T>String textOf(T value){
        return value==null?"":value.toString();
    }


    /**
     * 同步本地异常类型至本地数据库
     * fromTag为请求同步的界面提供的Tag
     */
    public static void syncBadTypeConfig(final boolean isReset) {
        String getBadtypeUrl= FURJA_BADTYPEBASIC_URL;
        final DaoSession daoSession= FurjaApp.getDaoSession();
        final BadTypeConfigDao typeConfigDao=daoSession.getBadTypeConfigDao();
        if(isReset)
            typeConfigDao.deleteAll();
        else {
            List<BadTypeConfig> configs=
                    typeConfigDao.loadAll();
            if(configs!=null&&configs.size()>0) {
                SharpBus.getInstance().post(SYNCOVER_BADTYPE_CONFIG,true);
                return;
            }
        }
        OkHttpUtils
                .get()
                .url(getBadtypeUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        SharpBus.getInstance().post(SYNCOVER_BADTYPE_CONFIG,"false");
                        showLog(e.toString());
                    }
                    @Override
                    public void onResponse(String responce, int i) {
                        showLog("获取异常类型基础信息成功，正在处理数据");
                        BadTypeConfigJson badTypeConfigJson
                                = JSON.parseObject(responce,BadTypeConfigJson.class);
                        if(badTypeConfigJson.getErrCode()!=110) {
                            Observable.just(badTypeConfigJson.getErrData())
                                    .observeOn(Schedulers.io())
                                    .subscribe(new Consumer<List<BadTypeConfigJson.ErrDataBean>>() {
                                        @Override
                                        public void accept(List<BadTypeConfigJson.ErrDataBean> errDataBeans) throws Exception {
                                            for(BadTypeConfigJson.ErrDataBean configBean:errDataBeans)
                                            {
                                                BadTypeConfig config=
                                                        new BadTypeConfig((long) configBean.getBadTypeID(),configBean.getSourceType(),configBean.getBadTypeInfo()+"",configBean.getBadTypeDetail());
                                                typeConfigDao.insertOrReplace(config);
                                            }
                                            SharpBus.getInstance().post(SYNCOVER_BADTYPE_CONFIG,true);
                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * 获取设备ID
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceID(){
        String deviceID=Preferences.getDeviceID();
        if(!TextUtils.isEmpty(deviceID))
            return deviceID;
        try {
            TelephonyManager telephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceID=telephonyMgr.getDeviceId();
         } catch (Exception e) {
        }
        if(!TextUtils.isEmpty(deviceID))
            deviceID= "imei -> "+deviceID;
        else {
            try {
                deviceID=Settings.Secure.getString(context.getContentResolver(), "android_id");
                if(!TextUtils.isEmpty(deviceID))
                    deviceID= "android_id -> "+deviceID;
                else
                    deviceID= "serialnumber -> "+android.os.Build.SERIAL;
             } catch (Exception e) {
            }
        }
        if(TextUtils.isEmpty(deviceID)) {
            String ipAddress=getIPAddress();
            if(!TextUtils.isEmpty(ipAddress))
                deviceID = "ip -> " + ipAddress;
        }
        else
            Preferences.saveDeviceID(deviceID);
        return deviceID;
    }

    public static String getIPAddress() {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        String ipAddress="";
        if(networkInfo!=null&&networkInfo.isAvailable()){
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {    // 当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                                return inetAddress.getHostAddress();
                        }
                    }
                  } catch (Exception e) {
                }
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {    // 当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                ipAddress = intIP2StringIP(wifiInfo.getIpAddress());    // 得到IPV4地址
            }
        }
        return ipAddress;
    }


    /**
     * 将得到的int类型的IP转换为String类型
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 后台上传数据使用
     */
    public static void toUploadBackground() {
        try {
            Observable.fromCallable(new Callable<List<BadMaterialLog>>() {
                @Override
                public List<BadMaterialLog> call() throws Exception {
                    List<BadMaterialLog> badMaterialLogs =queryNotUploadLog();
                    return badMaterialLogs;
                }}) .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Consumer<List<BadMaterialLog>>() {
                        @Override
                        public void accept(List<BadMaterialLog> badMaterialLogs) throws Exception {
                            for(BadMaterialLog badLog: badMaterialLogs) {
                                badLog.uploadToRemoteBackGround();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 配置WebView,使其可直接打开网页并使用JS
     * @param webView
     */
    public static void initWebView(WebView webView){
        if(webView==null)
            return;
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setBackgroundColor(0);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAllowFileAccess(true);
    }

    /**
     * 禁用输入法即只允许条码扫码
     */
    public static void disableShowInput(EditText edit_mainBarCode){
        EditText editText = edit_mainBarCode;
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus",boolean.class);
            method.setAccessible(true);
            method.invoke(editText,false);
          }catch (Exception e) {
        }
        try {
            method = cls.getMethod("setSoftInputShownOnFocus",boolean.class);
            method.setAccessible(true);
            method.invoke(editText,false);
          }
        catch (Exception e) {
        }
    }

    /**
     * 从数据索取未上传的Log进行上传
     * @return
     */
    public static List<BadMaterialLog> queryNotUploadLog() {
        DaoSession daoSession= FurjaApp.getDaoSession();
        BadMaterialLogDao dao=daoSession.getBadMaterialLogDao();
        List<BadMaterialLog> badLogs,
                uploadLogs=new ArrayList<BadMaterialLog>();
        badLogs=dao.loadAll();
        if(badLogs!=null) {
            for(BadMaterialLog badlog:badLogs) {
                if (badlog.isUploaded()|| TextUtils.isEmpty(badlog.getMaterialISN()))
                    delete(badlog);
                else
                    uploadLogs.add(badlog);
            }
        }
        showLog("待上传的数据条数:"+uploadLogs.size());
        return uploadLogs;
    }

    public static void delete(BadMaterialLog badMaterialLog) {
        try {
            DaoSession daoSession= FurjaApp.getDaoSession();
            BadMaterialLogDao dao=daoSession.getBadMaterialLogDao();
            dao.delete(badMaterialLog);
            showLog("被删除记录的ISN是:"+badMaterialLog.getMaterialISN());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setContext(Context context) {
        Utils.context = context;
        notificationManager
                = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    public static DaoMaster getDaoMaster() {
        if(daoMaster==null)
            initDaoMaster();
        return daoMaster;
    }


    public static Context getContext() {
        return context;
    }

    public static   void initDaoMaster() {
        helper = new DaoMaster.DevOpenHelper(context, "OverallManager.db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);//获取唯一DaoMaster对象
    }

    public static void closeDb() {
        if(helper!=null)
            helper.close();
    }



    /**
     * 截取二维码所示地址的后12位作为条码
     */
    public static String formatBarCode(String barCodeUrl) {
        try {
            barCodeUrl= URLEncoder.encode(barCodeUrl,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(barCodeUrl==null||barCodeUrl.length()<1)
            return "空";
        else
            return barCodeUrl;
    }


    /**
     *推送通知,ID标识区分如下:
     * 点检计划是计划ID*10+1;保养计划是计划ID*10+2;维修相关是其工单ID*10+3
     */
    public static void showNotification(int id, String text, String title, String info, Intent intent)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context,"planOver")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(false)
                        .setTicker(info+":"+title)
                        .setContentInfo(info);
        NotificationCompat.BigTextStyle bigTextStyle
                =new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(text);
        mBuilder.setStyle(bigTextStyle);
        PendingIntent activityIntent
                =PendingIntent.getActivity(context,0,intent,FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(activityIntent);
        notificationManager.notify(id, mBuilder.build());
    }



    public static void cancel(int id) {
        notificationManager.cancel(id);
    }
    /**
     * 打印Log
     * @param msg
     */
    public static <T> void showLog(T msg)
    {
        Log.i(LOG_TAG,msg+"");
    }


    public static String getDateString(Date date)
    {
        if(date==null)
            date=new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat
                =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(date);
    }



    public static DaoSession getDaoSession() {
        return FurjaApp.getDaoSession();
    }



    public static void insert(QMAGroupData groupData) {
        DaoSession daoSession=getDaoSession();
        QMAGroupDataDao dao
                =daoSession.getQMAGroupDataDao();
        dao.insertOrReplace(groupData);
    }

    /**
     * 生成 n 以内的随机整数值并转为字符串
     * @param n
     * @return
     */
    public static int getRandomInt(int n) {
        if(n<= 0)
            return 0;
        return random.nextInt(n);
    }


    public static  <T> double doubleOf(T str){
        if (str == null)
            return 0;
        else{
            String string=str+"";
            try {
                if(!string.equals("."))
                    return Double.valueOf(string);
            } catch (Exception e) { }
            return 0;
        }
    }

    /**
     * 判断List是否为空
     * @param list
     * @return
     */
    public static boolean isEmpty(List list){
        return list==null||list.isEmpty();
    }

    public static  <T> int intOf(T str){
        return (int) doubleOf(str);
    }

    public static RequestBody getRequestBody(Object object) {
        String json= JSON.toJSONString(object);
        return RequestBody
                .create(okhttp3.MediaType.parse("application/json;charset=utf-8"),json);
    }

    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) FurjaApp.getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels; //横屏时使用高度
    }
}

package com.furja.overall;

import android.accounts.NetworkErrorException;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.furja.common.CloudUserWithOrg;
import com.furja.common.DaoSession;
import com.furja.overall.beans.Message;
import com.furja.overall.services.ChatService;
import com.furja.utils.HttpsUtils;
import com.furja.utils.MyCrashHandler;
import com.furja.common.Preferences;
import com.furja.common.User;
import com.furja.common.DaoMaster;
import com.furja.common.DaoSession;
import com.furja.overall.receiver.NetworkChangeReceiver;
import com.furja.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.sentry.Sentry;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.furja.utils.Constants.LOG_TAG;
import static com.furja.utils.MyCrashHandler.postError;
import static com.furja.utils.Utils.getDeviceID;
import static com.furja.utils.Utils.getIPAddress;
import static com.furja.utils.Utils.initToastHandler;
import static com.furja.utils.Utils.showLog;


/**
 * 主Application
 */

public class FurjaApp extends Application {
    private static User user;
    private static FurjaApp instance;
    private static CloudUserWithOrg cloudUser;
    private static  Context context;
    ChatService chatService;
    ServiceConnection serviceConnection;



    @Override
    public void onCreate() {
        super.onCreate();
        lazyInit();
    }

    /**
     * 延迟1S懒加载
     */
    private void lazyInit() {
        context = getApplicationContext();
        instance = (FurjaApp)context;
        Observable.timer(2L,TimeUnit.SECONDS)
                .subscribe(i->{
                    initSomeFrameWork();
                });
    }

    /**
     * 获取用户名称
     * @return
     */
    public static String getUserName() {
        if(user!=null) {
            return user.getUserName();
        }
        return "";
    }

    /**
     * 注册网络状态变化的监听Receiver
     */
    private void registerNetWorkListener() {
        NetworkChangeReceiver.init(this);
    }

    /**
     * 登录成功后将员工用户信息存储,并视图连接至服务器
     * @param user
     */
    public static void setUserAndSave(User user) {
        FurjaApp.user = user;
        if(user!=null) {
            Preferences.saveUser(user);
            List<CloudUserWithOrg> userData = user!=null?user.getData():null;
            if(userData!=null&&userData.size()>0)
                FurjaApp.setCloudUser(userData.get(0));
        }
    }

    /**
     * 注销账户
     */
    public void logout() {
        if(user!=null&&chatService!=null)  {
            Message<User> message=new Message<>(101,"注销账号",user);
            chatService.sendMsg(message);
        }
    }

    /**
     * 初始化使用的开源库
     */
    private void initSomeFrameWork() {
        HttpsUtils.SSLParams sslParams
                = HttpsUtils.getSslSocketFactory(null, null, null);
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor(LOG_TAG))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier(hostnameVerifier)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
        MyCrashHandler.init(context);  //这个不要在启动时运行,延迟加载更合适,否则会死机
        registerNetWorkListener();
        Utils.initToastHandler();
        RxJavaPlugins.setErrorHandler(error->{
            error.printStackTrace();
        });
    }

    public static FurjaApp getInstance() {
        return instance;
    }
    public static User getUser() {
        return user;
    }

    public static CloudUserWithOrg getCloudUser() {
        return cloudUser;
    }

    public static void setCloudUser(CloudUserWithOrg cloudUser) {
        FurjaApp.cloudUser = cloudUser;
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 程序终止时解注册 网络状态监听器
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        NetworkChangeReceiver.unregister();
        if(serviceConnection!=null)
            unbindService(serviceConnection);
    }

    public static DaoSession getDaoSession() {
        DaoMaster daoMaster=Utils.getDaoMaster();
        return daoMaster.newSession();
    }

    /**
     * APP异常时记录日志并重启
     */
    public void restartApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Observable.just(launchIntent)
                .delay(1,TimeUnit.SECONDS)
                .subscribe(new Consumer<Intent>() {
                    @Override
                    public void accept(Intent intent) throws Exception {
                        startActivity(intent);
                        android.os.Process
                                .killProcess(android.os.Process.myPid());
                    }
                },error->{
                    error.printStackTrace();
                });
    }


    /**
     * 绑定推送服务
     */
    private void bindChatServices() {
        Observable.just(new Intent(this, ChatService.class))
                .observeOn(Schedulers.single())
                .subscribe(intent -> {
                    if(serviceConnection==null) {
                        serviceConnection=new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                                ChatService.ChatBinder binder=
                                        (ChatService.ChatBinder)iBinder;
                                chatService=binder.getService();
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName componentName) {
                                chatService=null;
                                bindChatServices();
                            }
                        };
                    }
                    bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
                });
    }

}

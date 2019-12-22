package com.furja.overall.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.furja.overall.beans.PushMessage;
import com.furja.overall.beans.Message;
import com.furja.overall.FurjaApp;
import com.furja.common.User;
import com.furja.utils.Utils;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static com.furja.devicemanager.utils.Constants.CHAT_PUSH_URL;
import static com.furja.utils.Utils.showLog;


/**
 * 后台通讯服务,用于推送消息
 */
public class ChatService extends Service{
    volatile WebSocket chatSocket;
    WebSocketListener webSocketListener;
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags,int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 发送消息
     * @param msg
     */
    public void sendMsg(Message msg)
    {
        if(chatSocket!=null)
            chatSocket.send(JSON.toJSONString(msg));
    }

    /**
     * 关闭连接
     * @param msg
     */
    public void close(Message msg)
    {
        if(chatSocket!=null)
            chatSocket.close(1000,"注销");
    }

    /**
     * 初始化连接WebSocket
     */
    public void init()
    {
        webSocketListener=new WebSocketListener() {
            int id=0;
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                if (chatSocket != null) {
                    synchronized (this){
                        if(chatSocket!=null)    //double heck
                        {
                            chatSocket.close(1000, "关闭先前的链接");
                        }
                    }
                }
                chatSocket = webSocket;
                User user= FurjaApp.getUser();
                Message<User> msg
                        =new Message<User>(100,"登录",user);
                sendMsg(msg);
                showLog("成功连接至服务器");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                showLog("收到消息:"+text);
                executeMessage(text);
            }

            private void executeMessage(String  text) {
                Observable.fromCallable(new Callable<PushMessage>() {
                    @Override
                    public PushMessage call() throws Exception {
                        id++;   //收到一条消息加一
                        PushMessage msg
                                =JSON.parseObject(text,PushMessage.class);
                        return msg;
                    }
                }).subscribeOn(Schedulers.io())
                        .subscribe(pushMessage-> {
                            Utils.showNotification(id,pushMessage.getPushMsg(),
                                    pushMessage.getTitle(),"推送",getLauncherIntent());
                            }
                        ,throwable -> {
                            showLog(throwable.getMessage());
                                });
            }
            private Intent getLauncherIntent()
            {
                Intent launchIntent =
                        getPackageManager().getLaunchIntentForPackage(getPackageName());
                return launchIntent;
            }
            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                showLog("连接关闭,原因是:"+reason);
                setChatSocket(null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                if (chatSocket != null) {
                    synchronized (this) {
                        if(chatSocket!=null)
                        {
                            chatSocket.close(1000, "连接失败关闭");
                            setChatSocket(null);
                            showLog("连接失败,原因是:"+t.getClass());
                        }
                    }
                }
                connectSocket();    //连接失败后重连
            }
        };
        io.reactivex.Observable.timer(1, TimeUnit.SECONDS, Schedulers.newThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        connectSocket();
                    }
                });

    }




    /**
     * 连接Socket
     */
    private  void connectSocket()
    {
        Request request
                = new Request.Builder().url(CHAT_PUSH_URL).build();
        try {
            OkHttpClient okHttpClient= new OkHttpClient.Builder()
                    .readTimeout(1, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(1, TimeUnit.SECONDS)//设置写的超时时间
                    .connectTimeout(1, TimeUnit.SECONDS)//设置连接超时时间
                    .pingInterval(3,TimeUnit.MINUTES)   //3分钟定时发送心跳包
                    .build();
            okHttpClient.newWebSocket(request,webSocketListener);
            okHttpClient.dispatcher()
                    .executorService().shutdown();
            } catch (Exception e) {
        }
    }

    public WebSocket getChatSocket() {
        return chatSocket;
    }

    public void setChatSocket(WebSocket chatSocket) {
        this.chatSocket = chatSocket;
    }

    public boolean isOnline()
    {
        return chatSocket!=null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ChatService.ChatBinder();
    }
    /**
     *Service Binder
     */
    public class ChatBinder extends Binder
    {
        public ChatService getService()
        {
            return ChatService.this;
        }
    }
}

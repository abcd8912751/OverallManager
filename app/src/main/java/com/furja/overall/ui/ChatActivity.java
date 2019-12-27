package com.furja.overall.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.furja.overall.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static com.furja.utils.Constants.CHAT_PUSH_URL;
import static com.furja.utils.Utils.showLog;

/**
 * 聊天的活动
 */

public class ChatActivity extends AppCompatActivity{
    @BindView(R.id.text_chat)
    TextView chat_text;
    @BindView(R.id.content_chat)
    TextView chat_content;
    @BindView(R.id.transfer_chat)
    TextView chat_transfer;
    int count=0;
    List<String> unsentMsgs;
    SimpleDateFormat simpleDateFormat
            =new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
    boolean connected;
    volatile WebSocket chatSocket;
    WebSocketListener webSocketListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        unsentMsgs=new ArrayList<String>();
        init();

    }
    @OnClick(R.id.transfer_chat)
    public void onTransfer(View view)
    {
        if(chatSocket!=null)
            chatSocket.close(1000,"关掉");
    }

    /**
     * 连接Socket
     */
    private  void connectSocket(){
        Request request
                = new Request.Builder().url(CHAT_PUSH_URL).build();
        try {
            OkHttpClient okHttpClient= new OkHttpClient.Builder()
                    .readTimeout(1, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(1, TimeUnit.SECONDS)//设置写的超时时间
                    .connectTimeout(1, TimeUnit.SECONDS)//设置连接超时时间
                    .pingInterval(1,TimeUnit.MINUTES)   //1分钟定时发送心跳包
                    .build();
            okHttpClient.newWebSocket(request,webSocketListener);
            okHttpClient.dispatcher()
                    .executorService().shutdown();
        } catch (Exception e) {
        }
    }

    @OnClick(R.id.text_chat)
    public void onClick(View view)
    {
        String msg="我又回来了->"+count;
        sendChatMsg(msg);
        count++;
    }

    public void init()
    {
        webSocketListener=new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                if (chatSocket != null) {
                    synchronized (this){
                        if(chatSocket!=null)
                        {
                            chatSocket.close(1000, "关闭先前的链接");
                            showLog("1我还活着");
                        }
                    }
                }
                chatSocket = webSocket;
                chatSocket.send("用户16396");
                connected = true;
                showLog("成功连接至服务器");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                showLog("收到服务器消息:"+text);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message="";
                        if(!TextUtils.isEmpty(chat_content.getText()))
                            message=chat_content.getText()
                                    +System.getProperty("line.separator");
                        chat_content.setText(message+text);
                    }
                });
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                showLog("连接关闭,原因是:"+reason);
                connected =false;
                setChatSocket(null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                showLog("连接失败,原因是:"+t.getClass());
                connected =false;
                if (chatSocket != null) {
                    synchronized (this) {
                        if(chatSocket!=null)
                        {
                            chatSocket.close(1000, "连接失败关闭");
                            showLog("2我还活着");
                            setChatSocket(null);
                        }
                    }
                }
                connectSocket();
            }
        };
        
    }

    /**
     * 发送聊天消息
     * @param msg
     */
    private void sendChatMsg(final String msg) {
        synchronized (this)
        {
            if(chatSocket!=null)
            {
                showLog(msg+".发送状态>"+chatSocket.send(msg));
            }
            else
            {
                showLog("正在重连服务器");
                connectSocket();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(chatSocket!=null)
            chatSocket.close(1000,"我也舍不得");
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public WebSocket getChatSocket() {
        return chatSocket;
    }

    public void setChatSocket(WebSocket chatSocket) {
        this.chatSocket = chatSocket;
    }
}

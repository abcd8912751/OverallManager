package com.furja.utils;

import android.content.Context;

import com.furja.overall.FurjaApp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import io.sentry.Sentry;
import io.sentry.SentryClientFactory;
import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.event.User;

import static com.furja.utils.Utils.getDeviceID;
import static com.furja.utils.Utils.getIPAddress;
import static com.furja.utils.Utils.showLog;

/**
 * 未捕捉异常的日志上传
 */

public class MyCrashHandler implements UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        postError(ex);
        FurjaApp.getInstance().restartApp();
    }

    public static void postError(Throwable ex){
        User sentryUser = new User(getDeviceID(), FurjaApp.getUserName(), getIPAddress(), "");
        Sentry.getContext().setUser(sentryUser);
        Sentry.capture(ex);
    }

    /**
     * 须延迟加载,否则卡白屏
     * @param applicationContext
     */
    public static void init(Context applicationContext){
        MyCrashHandler myCrashHandler = new MyCrashHandler();
        Thread.setDefaultUncaughtExceptionHandler(myCrashHandler);
        SentryClientFactory factory=new AndroidSentryClientFactory(applicationContext);
        Sentry.init("http://0d81a4cf6b4a40bd90044bd6c45c089b@192.168.8.202:9000/3", factory);
    }

    public static String getStackTraceInfo(final Throwable throwable) {
        String trace = "";
        try {
            Writer writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer);
            throwable.printStackTrace(pw);
            trace = writer.toString();
            pw.close();
        } catch (Exception e) {
            return "";
        }
        return trace;
    }


}

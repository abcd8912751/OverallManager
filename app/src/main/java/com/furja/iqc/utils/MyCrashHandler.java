package com.furja.iqc.utils;

import android.content.Context;

import com.furja.overall.FurjaApp;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import okhttp3.Call;

import static com.furja.utils.Constants.EXCEPTION_LOG_URL;
import static com.furja.utils.Utils.showLog;


/**
 * 未捕捉异常的日志上传
 */
public class MyCrashHandler implements UncaughtExceptionHandler {
    private Context context;
    public static MyCrashHandler crashHandler;
    public static MyCrashHandler getInstance() {
        if(crashHandler==null)
            crashHandler=new MyCrashHandler(FurjaApp.getContext());
        return crashHandler;
    }
    public MyCrashHandler(Context context1)
    {
        this.context=context1;
    }
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String string="VerifyElectronicAPP>"+getStackTraceInfo(ex);
        showLog(string);
        if(context==null)
            return;
//        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(launchIntent);
        try {
            Thread.sleep(1000);
//            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private  String getStackTraceInfo(final Throwable throwable) {
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
        showLog(trace);
        return trace;
    }

    public  void upload(String errorDetail)
    {
        try {
            OkHttpUtils.post()
                    .url(EXCEPTION_LOG_URL)
                    .addParams("errorLog",errorDetail)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void uploadError(Throwable ex)
    {
        upload(getStackTraceInfo(ex));
    }
}

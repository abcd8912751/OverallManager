package com.furja.iqc.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.furja.overall.R;
import com.furja.iqc.json.AutoUpdateJson;
import com.furja.utils.RetrofitBuilder;
import com.furja.utils.RetryWhenUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;


/**
 * 自动更新工具
 */

public class AutoUpdateUtils {
    private Context context;
    private boolean backGround;
    private static String FURJA_BCWEB_URL
            ="http://192.168.10.5:5050/bcwebservice/";

    public AutoUpdateUtils(Context uiContext,boolean isbackGround)
    {
        this.context=uiContext;
        this.backGround=isbackGround;
    }
    /**
     * 获取 当前安装APP的VersionCode
     * @return
     */
    public  int getVerCode() {
        int verCode = -1;
        try
        {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        }
        catch (PackageManager.NameNotFoundException e) {
            showLog(e.getMessage());
        }
        return verCode;
    }

    /**
     * 检查更新
     */
    public void checkUpdate() {
        UpdateHelper helper
                =   RetrofitBuilder
                .getHelperByUrl(FURJA_BCWEB_URL,UpdateHelper.class);
        helper.getVersionInfo()
                .retryWhen(new RetryWhenUtils())
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, List<AutoUpdateJson>>() {
                    @Override
                    public List<AutoUpdateJson> apply(ResponseBody responseBody) throws Exception {
                        String bodyString=getBodyAString(responseBody);
                        return JSON
                                .parseArray(bodyString,AutoUpdateJson.class);
                    }
                    @NonNull
                    private String getBodyAString(ResponseBody response) {
                        Reader in=response.charStream();
                        StringBuffer stringBuffer=new StringBuffer();
                        int bufferSize = 1024;
                        char[] buffer = new char[bufferSize];
                        for (; ; ) {
                            int rsz = 0;
                            try {
                                rsz = in.read(buffer, 0, buffer.length);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (rsz < 0)
                                break;
                            stringBuffer.append(buffer, 0, rsz);
                        }
                        return stringBuffer.toString();
                    }
                })
                .subscribe(new Consumer<List<AutoUpdateJson>>() {
                    @Override
                    public void accept(List<AutoUpdateJson> autoUpdateJsons) throws Exception {
                        for (AutoUpdateJson json : autoUpdateJsons) {
                            if (getPackageName().equals(json.getPackageName())) {
                                showLog("找到了报名一致的ApkUrl");
                                executeUpdateJson(json);
                                return;
                            }
                        }
                        showNoUpdateToast();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        if(throwable instanceof JSONException)
                        {
                            showLog("没有适合的数据");
                        }
                        showNoUpdateToast();
                    }
                });
    }

    private void showNoUpdateToast() {
        if(!backGround)
            showToast("已经是最新版本");
    }


    /**
     * 处理 服务器返回的与本包相符的 AutoUpdateJson 数据
     * @param json
     */
    private void executeUpdateJson(AutoUpdateJson json) {
        double serverVerName
                =Double.valueOf(json.getVersionName());
        int serverVerCode=json.getVersionCode();
        if(serverVerCode>=getVerCode())
        {
            if(serverVerCode>getVerCode())
            {
                showUpdateDialog(json);
            }
            else if(serverVerName>getVerName())
            {
                showUpdateDialog(json);
            }
            else
                showNoUpdateToast();
        }
        else
            showNoUpdateToast();
    }

    /**
     * 显示 更新对话框
     * @param json
     */
    private void showUpdateDialog(final AutoUpdateJson json)
    {
        Looper.prepare();
        MaterialDialog.Builder builder
                =new MaterialDialog.Builder(context);
        builder.title("检测到新版本");
        builder.content(json.getUpdateInfo());
        builder.positiveText("立即更新")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.cancel();
                        if(!TextUtils.isEmpty(json.getLatestApkUrl()))
                            downloadApkFromUrl(json.getLatestApkUrl());
                    }
                }).negativeText("下次再说");
        MaterialDialog dialog=builder.build();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
        Looper.loop();
    }

    /**
     * 从服务器下载 Apk
     * @param latestApkUrl
     */
    private void downloadApkFromUrl(final String latestApkUrl)
    {
        String path=Environment.getExternalStorageDirectory().getPath()+"/Download";
        String apkName=getNameFromUrl(latestApkUrl);
        File file=new File(path,apkName);
        if(file.exists())
        {
            showLog("指定文件已经下载完成");
//            toUpdateApk(file);
//            return;
        }
        final MaterialDialog downLoadDialog
                =new MaterialDialog.Builder(context)
                .progress(false,100,false)
                .title("下载完成后自行安装")
                .autoDismiss(false)
                .build();
        downLoadDialog.show();
        OkHttpUtils.get()
                .url(latestApkUrl)
                .build()
                .execute(new FileCallBack(path,apkName) {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        showToast("下载异常");
                        downLoadDialog.cancel();
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        showLog("下载完成:"+response.getAbsolutePath());
                        downLoadDialog.cancel();
                        toUpdateApk(response);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        int progressValue=(int) (progress*100);
                        if(progressValue!=downLoadDialog.getCurrentProgress())
                            downLoadDialog.setProgress(progressValue);
                    }
                });
    }

    /**
     * 从下载的Url里获取文件名
     * @param latestApkUrl
     * @return
     */
    private String getNameFromUrl(String latestApkUrl) {
        String fileName="latest.apk";
        if(!TextUtils.isEmpty(latestApkUrl))
        {
            int index=latestApkUrl.lastIndexOf("/");
            if(index>-1)
                fileName=latestApkUrl
                        .substring(index+1,latestApkUrl.length());
        }
        return fileName;
    }

    /**
     * 安装最新的APK,仅限于Android7.0以下
     */
    private void toUpdateApk(File file)
    {
        if(file==null||!file.exists())
        {
            showLog("更新");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 获取包名
     * @return
     */
    private String getPackageName()
    {
        return context.getPackageName();
    }

    /**
     * 获取当前安装APP的版本名
     * @return VersionName
     */
    public  double getVerName() {
        String verName = "";
        double versionName=0;
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
            versionName=Double.valueOf(verName);
        } catch (Exception e) {
            showLog(e.getMessage());
        }
        return versionName;
    }

    /**
     * 更新的 Helper
     */
    interface UpdateHelper
    {
        @GET("apk/AutoUpdate/version.txt")
        Observable<ResponseBody> getVersionInfo();
    }
}

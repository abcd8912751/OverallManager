package com.furja.alertsop.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.furja.overall.R;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;

import okhttp3.RequestBody;

import static com.furja.utils.Constants.LOG_TAG;

/**
 * 常用工具类
 */

public class Utils {

    /**
     * 抖动自己
     * @param view
     */
    public static void shakeOwnSelf(View view)
    {
        Animation shakeAnimation
                = new TranslateAnimation(0, 4, 0, 0);
        //设置一个循环加速器，使用传入的次数就会出现摆动的效果。
        shakeAnimation.setInterpolator(new CycleInterpolator(2));
        shakeAnimation.setDuration(500);
        view.startAnimation(shakeAnimation);
    }

    public static RequestBody getRequestBody(Object object)  {
        String json= JSON.toJSONString(object);
        return RequestBody
                .create(okhttp3.MediaType.parse("application/json;charset=utf-8"),json);
    }

    public static MaterialDialog showWaitingDialog(Context context) {
        return showWaitingDialog(context,"        正在提交数据...");
    }
    public static MaterialDialog showWaitingDialog(Context context, String content) {
        MaterialDialog.Builder builder
                =new MaterialDialog.Builder(context);
        MaterialDialog dialog
                =builder
                .iconRes(R.mipmap.ic_wait)
                .title("请稍候")
                .content(content)
                .contentColorRes(R.color.white)
                .titleColorRes(R.color.white)
                .cancelable(false)
                .build();
        dialog.show();
        Window window=dialog.getWindow();
        WindowManager.LayoutParams p =window.getAttributes();  //获取对话框当前的参数值
        p.dimAmount=0.2f;
        p.alpha=30;
        window.setAttributes(p);
        window.setBackgroundDrawableResource(R.drawable.shape_wxdialog_bg);
        return dialog;
    }

    public static String exceptionToString(Exception e)
    {
        try {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            printWriter.close();
            return writer.toString();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return e.toString();
    }


    public static int getSampleSizeByProduction(double totalProduction) {
        if(totalProduction>10000)
            return 315;
        if(totalProduction>3200)
            return 200;
        if(totalProduction>1200)
            return 125;
        if(totalProduction>500)
            return 80;
        if(totalProduction>280)
            return 50;
        if(totalProduction>150)
            return 32;
        if(totalProduction>90)
            return 20;
        if(totalProduction>50)
            return 13;
        if(totalProduction>25)
            return 8;
        if(totalProduction>15)
            return 5;
        if(totalProduction>8)
            return 3;
        else if(totalProduction>0)
            return 2;
            else
                return 0;
    }
}

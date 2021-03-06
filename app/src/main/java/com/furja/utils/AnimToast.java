package com.furja.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.furja.overall.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * 带动画效果的Toast
 */
public class AnimToast {
    View toastView;
    WindowManager windowManager;
    boolean isShowing;
    WindowManager.LayoutParams layoutParams;
    long delay = 1;
    public static AnimToast makeText(Context context, String text) {
        AnimToast result = new AnimToast(context, text);
        return result;
    }

    private AnimToast(Context context, String text){
        isShowing=false;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //通过Toast实例获取当前android系统的默认Toast的View布局
        toastView= LayoutInflater.from(context)
                .inflate(R.layout.cusom_toast_layout,null);
        TextView textView=toastView.findViewById(R.id.toast_msg);
        textView.setText(text);
        //设置布局参数
        setParams();
    }


    private void setParams() {
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.windowAnimations = R.style.AnimHorizontal;//设置进入退出动画效果
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        layoutParams.gravity = Gravity.START| Gravity.BOTTOM;
        layoutParams.y = 250;
        layoutParams.alpha=0.9f;
    }


    public void show(){
        if(!isShowing){//如果Toast没有显示，则开始加载显示
            isShowing = true;
            windowManager.addView(toastView, layoutParams);//将其加载到windowManager上
            Observable.just(windowManager)
                    .delay(delay, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        if(ViewCompat.isAttachedToWindow(toastView))
                            windowManager.removeView(toastView);
                        isShowing = false;
                    },throwable -> {
                        throwable.printStackTrace();
                    });
        }
    }


    public long getDelay() {
        return delay;
    }

    public AnimToast setDelay(long delay) {
        this.delay = delay;
        return this;
    }
}

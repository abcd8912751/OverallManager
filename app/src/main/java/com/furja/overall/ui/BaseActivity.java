package com.furja.overall.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.alibaba.fastjson.JSON;
import com.furja.common.CloudUserWithOrg;
import com.furja.common.Preferences;
import com.furja.common.User;
import com.furja.overall.FurjaApp;
import com.furja.utils.MyCrashHandler;
import com.furja.utils.RetrofitBuilder;
import com.furja.utils.RetrofitHelper;
import com.furja.utils.RetryWhenUtils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.sentry.Sentry;

import static android.view.KeyEvent.KEYCODE_F7;
import static com.furja.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.utils.Constants.VERTX_TEST_URL;
import static com.furja.utils.Constants.getVertxUrl;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 *基础Activity,手势控制 返回
 */

public class BaseActivity extends AppCompatActivity {
    private static Object TAG_MARGIN_ADDED="TAG_MARGIN_ADDED";
    float lastY,lastX;
    int screenY;
    int screenX;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isFisrtStart())
            return;
        screenX=getScreenWidth();
        screenY=getScreenHeight();
    }

    /**
     * 更改系统状态栏颜色
     */
    public void changeStatusColor() {
    }


    public  int getScreenWidth() {
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels; //横屏时使用高度
    }

    public  int getScreenHeight() {
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels; //横屏时使用高度
    }

    /**
     * 静默登录
     */
    public void silentLogin(){
        if(FurjaApp.getUser()==null) {
            String password = Preferences.getPassword();
            String username = Preferences.getUsername();
            if (!TextUtils.isEmpty(username))
                login(username, password);
            else
                jumpToLogin();
        }
    }


    public  void login(String userName, String password) {
        RetrofitHelper helper= RetrofitBuilder.getHelperByUrl(getVertxUrl());
        helper.login(userName,password)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWhenUtils.create())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->{
                    if(response.getCode()>0){
                        User user =new User(userName,password);
                        showLog(JSON.toJSONString(user)+"->登录成功");
                        String result=response.getResult();
                        if(!TextUtils.isEmpty(result)) {
                            try {
                                List<CloudUserWithOrg> data=JSON.parseArray(result,CloudUserWithOrg.class);
                                user.setData(data);
                             } catch (Exception e) {
                            }
                        }
                        FurjaApp.setUserAndSave(user);
                        Preferences.saveAutoLogin(true);
                        switchToHome();
                    }
                    else {
                        showToast(response.getResult());
                        jumpToLogin();
                    }
                },error->{
                    error.printStackTrace();
                    showToast(INTERNET_ABNORMAL);
                    jumpToLogin();
                });
    }

    protected void jumpToLogin(){
        if(this instanceof LoginActivity)
            resumeView();
        else {
            Intent intent= new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    protected void resumeView() {
    }

    protected void switchToHome() {

    }

    /**
     * 是否是第一次启动,如果不是直接finish
     */
    protected boolean isFisrtStart() {
        if (!this.isTaskRoot()) {   // 判断当前activity是不是所在任务栈的根
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction(); //因为重新配置数据时会跳转至这一界面故判断action
                if (Intent.ACTION_MAIN.equals(action)) {
                    showLog("任务栈根");
                    finish();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int offsetX=this.screenX/4;
        int offsetY=this.screenY/5;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastY = motionEvent.getRawY();
                lastX = motionEvent.getRawX();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float currY = motionEvent.getRawY();
                float currX = motionEvent.getRawX();
                if(lastY-currY>screenY/5){
                    if(screenY-lastY<100) {
                        showSheetDialog();
                        return true;
                    } else if(lastY - currY > offsetY){
                        pageDown();
                        return super.dispatchTouchEvent(motionEvent);
                    }
                }
                else if (currY - lastY > offsetY) {
                    pageUp();
                    return super.dispatchTouchEvent(motionEvent);
                } else if(currX-lastX>offsetX){
                    pushToRight();
                    return true;
                } else if(lastX-currX>offsetX){
                    pushToLeft();
                    return true;
                }
                break;
        }
        try {
            return super.dispatchTouchEvent(motionEvent);
        }
        catch (Exception e){
            Sentry.capture(e);
            return true;
        }
    }

    protected void pushToLeft() {


    }

    protected void pushToRight() {
        onBackPressed();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event!=null) {
            int keyCode = event.getKeyCode();
            if(keyCode == KeyEvent.KEYCODE_BRIGHTNESS_DOWN
                    || keyCode == KeyEvent.KEYCODE_BRIGHTNESS_UP
                    || keyCode == KEYCODE_F7) {
                focusBarCode();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    protected void focusBarCode() {
    }

    protected void pageDown(){

    }

    protected void pageUp(){

    }

    protected void showSheetDialog() {

    }
}



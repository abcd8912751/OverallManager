package com.furja.overall.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.furja.overall.FurjaApp;
import com.furja.overall.R;
import com.furja.common.Preferences;
import com.furja.utils.Constants;
import com.furja.utils.SharpBus;
import com.furja.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 启动配置页
 */

public class SplashActivity extends BaseActivity {
    @BindView(R.id.image_setting_splash)
    ImageView image_Setting;
    @BindView(R.id.splash_operatorLabel)
    TextView loadingLabel;
    @BindView(R.id.splash_startLoginBtn)
    Button startLoginBtn;
    @BindView(R.id.splash_switchOperatorBtn)
    Button switchOperatorBtn;
    int callCount=0;
    boolean isResetConfig;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        Intent fromIntent=getIntent();
        isResetConfig =false;
        if(fromIntent!=null)
        {
            if(!TextUtils.isEmpty(fromIntent.getAction())
                    &&fromIntent.getAction().equals(Constants.RESET_CONFIG))
                isResetConfig =true;
        }
        Utils.showLog("isReset:->"+isResetConfig);
        if(!isResetConfig)
        {
//            checkFisrtStart();
            if(FurjaApp.getUser()!=null)
            {
                if(Preferences.getSourceType()!= Constants.TYPE_BADLOG_EMPTY)
                {
                    Utils.showLog(getClass()+"阻止不了我去LogBad");
                    toLogBad();
                }
            }

        }
        else
        {
            Utils.showLog("重置配置");
            syncLogConfigAndLogin();
        }

    }

    /**
     * 是否是第一次启动,如果不是直接finish
     */
    private void checkFisrtStart() {
        if (!this.isTaskRoot()) { // 判断当前activity是不是所在任务栈的根
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                }
            }
        }
    }

    /**
     * 从服务器同步异常类型配置至本地数据库
     */
    private void syncLogConfigAndLogin() {
        rotateSettingImage();
        SharpBus.getInstance().register(Constants.SYNCOVER_BADTYPE_CONFIG)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {
                        Utils.showLog(getClass()+"->"+value.toString());
                        if(value.toString().contains("true"))
                        {
                            callCount=0;
                            image_Setting.clearAnimation();
                            loadingLabel.setVisibility(View.GONE);
                            toLogBad();
                        }
                        else
                        {
                            callCount++;
                            if(callCount<3)
                                Utils.syncBadTypeConfig(isResetConfig);
                            else
                            {
                                image_Setting.clearAnimation();
                                loadingLabel.setVisibility(View.GONE);
                                toLogBad();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        Utils.syncBadTypeConfig(isResetConfig);
    }

    /**
     * 旋转Setting的 ImageView
     */
    private void rotateSettingImage() {
        Animation circle_anim = AnimationUtils.loadAnimation(this, R.anim.anim_round_rotate);
        if (circle_anim != null) {
            image_Setting.startAnimation(circle_anim);  //开始动画
        }
    }

    /**
     * 跳转至记录异常的界面
     */
    private void toLogBad() {
        Intent intent
                =new Intent(SplashActivity.this, BadLogActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转页面登录
     */
    private void switchToLogin() {
        Intent intent
                =new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}

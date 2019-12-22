package com.furja.devicemanager.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.furja.overall.R;
import com.furja.common.Preferences;
import com.furja.devicemanager.services.AlarmReceiver;
import com.furja.devicemanager.services.AppPushService;
import com.furja.devicemanager.ui.plan.PlanListActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * 主界面RadioButton计划 对应的Fragment
 */

public class PlanFragmentDev extends DevBaseFragment {

    String barCode;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_plan,container,false);
        ButterKnife.bind(this,view);

        fragChangeListener.hideDeviceInfo();
        return view;
    }

    @OnClick({R.id.text_inspectPlan,R.id.text_manatainPlan,
                R.id.text_ownDataSync,R.id.text_intervalMinute})
    public void onClick(View view)
    {
        Intent intent=new Intent(getContext(), PlanListActivity.class);
        switch (view.getId())
        {
            case R.id.text_inspectPlan:
                //点检计划
                showLog("点检计划采集");
                intent.setData(Uri.parse("InspectPlanFragment"));
                startActivity(intent);
                break;
            case R.id.text_manatainPlan:
                //保养计划
                showLog("保养计划采集");
                intent.setData(Uri.parse("MaintainPlanFragment"));
                startActivity(intent);
                break;
            case R.id.text_ownDataSync:
                //数据同步
                showLog("数据同步");
                Intent intent1=new Intent(mContext, AlarmReceiver.class);
                intent1.setAction("com.furja.devicemanager.services.ALARM");
                mContext.sendBroadcast(intent1);
                showToast("数据同步完成");
                break;
            case R.id.text_intervalMinute:
                setAlarmIntoDialog();
                break;
        }
    }

    /**
     * 在对话框里设置时间
     */
    private void setAlarmIntoDialog() {
        new MaterialDialog.Builder(mContext)
                .title("设置数据定时同步间隔,单位为分钟")
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("默认为5分钟", Preferences.getAlarmInterval()+"", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if(!TextUtils.isEmpty(input))
                        {
                            String string=input.toString();
                            int i=Integer.valueOf(string);
                            if(i!= Preferences.getAlarmInterval())
                            {
                                Preferences.saveAlarmInterval(string);
                                Intent intent
                                        =new Intent(mContext, AppPushService.class);
                                mContext.startService(intent);
                                showLog("设定闹钟间隔");
                            }
                        }
                        dialog.cancel();
                    }
                }).build().show();
    }


    @Override
    boolean isEmptyBarCode() {
        return false;
    }

    @Override
    void setCurBarCode(String barCode) {
        this.barCode=barCode;
    }

    @Override
    void resume() {
        fragChangeListener.hideDeviceInfo();
    }
}

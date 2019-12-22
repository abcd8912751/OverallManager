package com.furja.devicemanager.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.furja.devicemanager.DeviceManagerApp;
import com.furja.devicemanager.beans.DeviceAccount;
import com.furja.common.Preferences;
import com.furja.devicemanager.utils.JSONParser;
import com.furja.devicemanager.view.ClearableEditTextWithIcon;
import com.furja.overall.ui.LoginActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.furja.overall.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static com.furja.devicemanager.utils.Constants.FURJA_GET_DEVICEACCOUNT;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

public class DevManageActivity extends AppCompatActivity implements DevBaseFragment.FragmentChangedListener {
    @BindView(R.id.edit_barCode)
    ClearableEditTextWithIcon barCode_Edit;
    @BindView(R.id.text_deviceInfo)
    TextView deviceInfo_text;
    @BindView(R.id.radio_spotCheck)
    RadioButton radio_spotCheck;
    @BindView(R.id.radio_maintain)
    RadioButton radio_maintain;
    @BindView(R.id.radio_repair)
    RadioButton radio_repair;
    @BindView(R.id.radio_plan)
    RadioButton radio_plan;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    private DevBaseFragment tempFragment;
    private FragmentManager fm;
    private String barCode;
    private int requestCount=0;
    private List<DevBaseFragment> devBaseFragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dev);
        ButterKnife.bind(this);
        InputOverListener listener=new InputOverListener();
        barCode_Edit.setOnEditorActionListener(listener);
        barCode_Edit.setOnKeyListener(listener);

//        barCode_Edit.disableShowInput();

        initFragment();
        changeFragment(0);
    }




    private void initFragment()
    {
        fm=getSupportFragmentManager();
        devBaseFragments =new ArrayList<DevBaseFragment>();
        InspectFragmentDev inspectFragment = new InspectFragmentDev();
        MaintainFragmentDev maintainFragment = new MaintainFragmentDev();
        RepairFragmentDev repairFragment=new RepairFragmentDev();
        PlanFragmentDev planFragment = new PlanFragmentDev();
        devBaseFragments.add(inspectFragment);
        devBaseFragments.add(maintainFragment);
        devBaseFragments.add(repairFragment);
        devBaseFragments.add(planFragment);
    }


    /**
     * 切换Fragment
     * @param to
     * @param tag
     */
    @Override
    public void transferFragment(DevBaseFragment to, String tag)
    {
        DevBaseFragment from=tempFragment;
        FragmentTransaction fts = fm.beginTransaction();
        if(from!=null)
        {
            if(from==to)
                return;
            fts.hide(from).commit();
        }
        this.tempFragment=to;
        if(!devBaseFragments.contains(to))
            devBaseFragments.add(to);
        if(!TextUtils.isEmpty(barCode))
            tempFragment.setCurBarCode(barCode);
        showFragment(to, tag);
    }

    @Override
    public void transferFragment(int position) {
        transferFragment(devBaseFragments.get(position),"SCENE_"+position);
    }

    @Override
    public void setDisplayHomeAsUpEnable(boolean show) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(show);
    }

    @Override
    public void setRadioGroupVisibility(int visibility) {
        radioGroup.setVisibility(visibility);
    }

    /**
     * 设备信息右侧显示扫描框框
     */
    public void showScanDrawable()
    {
        Drawable scanDrawable
                =getResources().getDrawable(R.mipmap.ic_scan_right);
        deviceInfo_text
                .setCompoundDrawablesWithIntrinsicBounds(null,null,scanDrawable,null);
        deviceInfo_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Drawable drawableRight = deviceInfo_text.getCompoundDrawables()[2];
                 if (drawableRight != null && event.getX() > view.getWidth() - view.getPaddingRight() - drawableRight.getIntrinsicWidth()) {
                     showLog("点击了扫描按钮");
                     showBarCodeEdit();
                 }
                return false;
            }
        });


    }

    /**
     * 显示Fragment
     * @param to
     * @param tag
     */
    private void showFragment(DevBaseFragment to, String tag) {
        FragmentTransaction fts = fm.beginTransaction();
        if(to.isAdded())
        {
            fts.show(to);
        }
        else
        {
            fts.add(R.id.content_frame, to,tag);
        }
        fts.commit();
    }

    /**
     * 上传数据后reset视图
     */
    @Override
    public void onUploadSuccess() {
        showBarCodeEdit();
        deviceInfo_text.setText("设备信息");
    }

    /**
     * 条码录入框体输入完成监听
     */
    public class InputOverListener implements View.OnKeyListener,TextView.OnEditorActionListener {

        private long lastTimeMillis=System.currentTimeMillis();    //上次校验条码的时间
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (System.currentTimeMillis() - lastTimeMillis > 1000)
                    lastTimeMillis = System.currentTimeMillis();
                else
                    return false;
                if (TextUtils.isEmpty(barCode_Edit.getText()))
                    return false;
                else
                {
                    String input_BarCode
                            =barCode_Edit.getText()
                            .toString();
                    setBarCode(input_BarCode);
                    requestDeviceInfo();
                }
            }
            return false;
        }

        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
        {
            if (i == EditorInfo.IME_ACTION_DONE) {
                if (System.currentTimeMillis() - lastTimeMillis > 1000)
                    lastTimeMillis = System.currentTimeMillis();
                else
                    return false;
                if (TextUtils.isEmpty(barCode_Edit.getText()))
                    return false;
                else
                {
                    String input_BarCode=barCode_Edit.getText()
                            .toString();
                    setBarCode(input_BarCode);
                    requestDeviceInfo();
                }
            }
            return false;
        }
    }

    /**
     * 保存条码录入框录入的数据
     * @param curBarCode
     */
    public void setBarCode(String curBarCode) {
        curBarCode=curBarCode.toUpperCase();    //大写并去除回车换行符
        curBarCode=curBarCode.replace("\n","");
        curBarCode=curBarCode.replace("\r","");
        this.barCode=curBarCode;
        barCode_Edit.clearFocus();
        showToast("获取设备信息");
    }

    /**
     * 以barCode为参数请求设备信息
     */
    private void requestDeviceInfo()
    {
        OkHttpUtils
                .get()
                .url(FURJA_GET_DEVICEACCOUNT)
                .addParams("deviceNumber", barCode)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                        if(++requestCount<3)
                            requestDeviceInfo();
                        else
                            showToast("网络异常请重试");
                    }

                    @Override
                    public void onResponse(final String respon, int i) {
                        showLog(getClass()+respon);
                        requestCount=0;
                        Observable.fromCallable(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                try {
                                    String response= JSONParser.parserJSON(respon);
                                    DeviceAccount infoJson
                                            = JSON.parseObject(response,DeviceAccount.class);
                                    setBarCode(infoJson.getFID()+"");
                                    return infoJson.toString();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    showToast("没有找到相应设备");
                                    return "";
                                }}})
                                .subscribe(new Consumer<Object>() {
                                    @Override
                                    public void accept(Object s) throws Exception {
                                        String deviceInfo=s.toString();
                                        if(TextUtils.isEmpty(deviceInfo))
                                        {
                                            return;
                                        }
                                        deviceInfo_text
                                                .setText(deviceInfo);
                                        showScanDrawable();
                                        barCode_Edit.setVisibility(View.GONE);
                                        if (tempFragment != null)
                                            tempFragment.setCurBarCode(barCode);
                                    }
                                });
                        }
                });
    }

    /**
     * RadioButton点击监听
     */
    @OnClick({R.id.radio_spotCheck,R.id.radio_maintain,R.id.radio_repair,R.id.radio_plan})
    public void onClick(View v)
    {
        int position=0;
        switch (v.getId())
        {
            case R.id.radio_maintain:
                position=1;
                break;
            case R.id.radio_repair:
                position=2;
                break;
            case R.id.radio_plan:
                position=3;
                break;
        }
        changeFragment(position);
    }



    /**
     * 根据选中的position切换视图
     * @param position
     */
    private void changeFragment(int position)
    {
        if(devBaseFragments ==null)
            return;
        updateDrawableTop(position);
        if(devBaseFragments.size()>position)
        {
            transferFragment(position);
            if(position==3)
                hideDeviceInfo();
        }
    }


    /**
     * 隐藏设备条码框及信息
     */
    @Override
    public void hideDeviceInfo()
    {
        if(tempFragment instanceof InspectFragmentDev
            ||tempFragment instanceof MaintainFragmentDev)
            return;
        barCode_Edit.setVisibility(View.GONE);
        deviceInfo_text.setVisibility(View.GONE);
    }


    /**
     * 显示设备信息
     */
    @Override
    public void showDeviceInfo()
    {
        if(tempFragment instanceof PlanFragmentDev)
            return;
        showLog("显示设备信息");
        if(deviceInfo_text.getVisibility()!=View.VISIBLE)
        {
            deviceInfo_text.setVisibility(View.VISIBLE);
        }
        if(TextUtils.isEmpty(barCode))
        {
            showBarCodeEdit();
        }
        else
        {
            try{
                int i= Integer.valueOf(barCode);
            }
            catch(Exception e)
            {   //barCode中尚未
                showBarCodeEdit();
            }
        }
    }

    /**
     * 显示设备条码输入框
     */
    public void showBarCodeEdit()
    {
        barCode_Edit.setVisibility(View.VISIBLE);
        barCode_Edit.setText("");
        deviceInfo_text
                .setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
    }


    /**
     * 更新 RadioButton 视图
     */
    public void updateDrawableTop(int position)
    {
        resetDrawableTop();
        int color
                = getResources().getColor(R.color.radioText_color);
        switch (position)
        {
            case 0:
                radio_spotCheck.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.ic_spot_check_select,0,0);
                radio_spotCheck.setTextColor(color);
                break;
            case 1:
                radio_maintain.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.ic_maintain_select,0,0);
                radio_maintain.setTextColor(color);
                break;
            case 2:
                radio_repair.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.ic_repair_select,0,0);
                radio_repair.setTextColor(color);
                break;
            case 3:
                radio_plan.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.ic_plan_select,0,0);
                radio_plan.setTextColor(color);
                break;
        }
    }

    /**
     * 将RadioButton的Drawable设置为初始值
     */
    private void resetDrawableTop() {
        radio_spotCheck.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.ic_spot_check,0,0);
        radio_maintain.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.ic_maintain,0,0);
        radio_repair.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.ic_repair,0,0);
        radio_plan.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.ic_radio_plan,0,0);
        radio_spotCheck.setTextColor(Color.BLACK);
        radio_maintain.setTextColor(Color.BLACK);
        radio_repair.setTextColor(Color.BLACK);
        radio_plan.setTextColor(Color.BLACK);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==0)
        {
            barCode_Edit.setText("");
            barCode_Edit.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_dev, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            showLog("点击home");
            onBackPressed();
        }
        else if(item.getItemId()==R.id.action_logOut)
        {
            Preferences.saveAutoLogin(false);
            DeviceManagerApp.setUserAndSave(null);
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 返回上级的处理拦截
     */
    @Override
    public void onBackPressed() {
        showLog("返回了");
        if(tempFragment!=null)
        {
            if(tempFragment instanceof RepairOrderFragmentDev ||
                    tempFragment instanceof ApplyRepairFragmentDev)
            {
                transferFragment(2);
                return;
            }
            else if(tempFragment instanceof RepairDetailFragmentDev)
            {
                DevBaseFragment fragment=
                        (DevBaseFragment) fm.findFragmentByTag("RepairOrderFragmentDev");
                if(fragment!=null)
                {
                    transferFragment(fragment,"RepairOrderFragmentDev");
                    return;
                }
                else
                    showLog("维修工单Fragment丢失");
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

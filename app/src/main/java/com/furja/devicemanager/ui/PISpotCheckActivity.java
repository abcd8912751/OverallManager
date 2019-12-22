package com.furja.devicemanager.ui;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.furja.devicemanager.DeviceManagerApp;
import com.furja.overall.R;
import com.furja.devicemanager.databases.PlasticInjectCheck;
import com.furja.devicemanager.databases.PlasticInjectCheckChild;
import com.furja.devicemanager.databases.PlasticInjectCheckDao;
import com.furja.devicemanager.json.DeviceInfoJson;
import com.furja.devicemanager.json.PISpotCheckJSON;
import com.furja.devicemanager.json.PISpotCheckListJson;
import com.furja.devicemanager.presenter.PIViewPagerPresenter;
import com.furja.devicemanager.utils.Utils;
import com.furja.devicemanager.view.ClearableEditTextWithIcon;
import com.furja.devicemanager.beans.SpotCheckItem;
import com.furja.devicemanager.view.NonSlideViewPager;
import com.furja.devicemanager.view.PIViewPagerAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static com.furja.devicemanager.utils.Constants.FURJA_DEVICE_INFO_GET;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * 注塑车间开机点检的Activity
 */
public class PISpotCheckActivity extends AppCompatActivity implements PIViewPagerPresenter.ScanListener{
    @BindView(R.id.edit_barCode)
    ClearableEditTextWithIcon barCode_edit;
    @BindView(R.id.text_deviceInfo)
    TextView deviceInfo_text;
    @BindView(R.id.pi_spotCheckPager)
    NonSlideViewPager pi_spotCheckPager;
    @BindView(R.id.upload_spotCheck)
    Button upload_btn;
    private String deviceDesp =""; //设备描述
    private String deviceCode ="";  //设备条码
    PIViewPagerAdapter checkAdapter;
    int requestCount=0;   //请求清单的次数
    private boolean isPass;     //是否通过点检
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi_spotcheck);
        ButterKnife.bind(this);
        checkAdapter=new PIViewPagerAdapter(this);
        pi_spotCheckPager.setAdapter(checkAdapter);
        requestList();
        isPass=false;   //默认没有通过点检
        barCode_edit.disableShowInput();
    }

    /**
     * ViewPager及上传按钮点击监听
     * @param view
     */
    @OnClick({R.id.upload_spotCheck})//R.id.pi_spotCheckPager,
    void onClick(View view)
    {
        showLog("点了呀");
        boolean isFinish=checkFinishOrFresh();
        if(view.getId()==R.id.upload_spotCheck)
        {
            if(isFinish)    //弹出对话框以选择是否通过点检
            {
                showConfirmPassDialog();
            }
            else            //弹出未点检条目列表
            {
                showUnCheckedListDialog();
            }
        }

    }

    /**
     * 选择是否通过开机点检
     */
    private void showConfirmPassDialog() {
        new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .iconRes(R.mipmap.ic_confirm_left)
                .title("确认")
                .content("这台机器是否通过点检")
                .positiveText("通过")
                .negativeText("不通过")
                .positiveFocus(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setPass(true);
                        uploadRecord();
                        dialog.cancel();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setPass(false);
                        uploadRecord();
                        dialog.cancel();
                    }
                }).show();
    }

    /**
     * 显示没有 录入检查内容对应条码或没有填写必填项的检查列表
     */
    private void showUnCheckedListDialog() {
        List<SpotCheckItem>
                spotCheckItems=checkAdapter.getSpotCheckItems();
        if(spotCheckItems==null)
        {
            showLog("获取到空列表");
            return;
        }
        else
            showLog("获取到列表");
        List<String>
                unCheckItems=new ArrayList<String>();
        for(int i=0;i<spotCheckItems.size();i++)
        {
            SpotCheckItem item=spotCheckItems.get(i);
            {
                if(!item.isChecked())
                    unCheckItems.add((i+1)+":"+item.getCheck_Title());
            }
        }
        showLog("unCheckItems.size:"+unCheckItems.size());
        new MaterialDialog.Builder(this)
                .title("未点检条目清单")
                .items(unCheckItems)
                .dividerColorRes(R.color.colorBottomBg)
                .show();
    }

    /**
     * 获取清单
     */
    public void requestList()
    {
        OkHttpUtils
                .get()
                .url(FURJA_DEVICE_INFO_GET)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        requestCount++;
                        if(requestCount<3)
                            requestList();
                        else
                            showToast("网络异常,请返回重试");
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        showLog(s);
                        requestCount=0;
                        try
                        {
                            PISpotCheckListJson checkListJson
                                    = JSON.parseObject(s,PISpotCheckListJson.class);
                            if(checkListJson==null)
                                return;
                            if(checkListJson.getErrCode()==100)
                            {
                                List<PISpotCheckListJson.ErrDataBean> errDataBeans
                                        =checkListJson.getErrData();
                                List<SpotCheckItem> mData=new ArrayList<SpotCheckItem>();
                                for(PISpotCheckListJson.ErrDataBean dataBean:errDataBeans)
                                {
                                    mData.add(new SpotCheckItem(dataBean.getFTitle(),
                                            Integer.valueOf(dataBean.getFCheckType()),dataBean.getFId()));
                                }
                                checkAdapter.setNewData(mData);
                                checkFinishOrFresh();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }


    /**
     * 校验并上传数据
     */
    private void uploadRecord()
    {
        List<SpotCheckItem> spotCheckItems
                = checkAdapter.getSpotCheckItems();
        if (spotCheckItems == null)
        {
            showLog("spotCheckItems是空的");
            return;
        }
        uploadToRemote(spotCheckItems);
    }



    /**
     * 上传数据
     * @param spotCheckItems
     */
    private void uploadToRemote(List<SpotCheckItem> spotCheckItems)
    {
        PlasticInjectCheckDao dao
                = DeviceManagerApp.getDaoSession().getPlasticInjectCheckDao();
        long interID=dao.count();       //以interID作为关联创建2张表
        String personelID=DeviceManagerApp.getUser().getUserId();
        Date date=new Date(System.currentTimeMillis());
        PlasticInjectCheck PIcheck
                =new PlasticInjectCheck(null,interID, deviceCode,personelID,isPass,false,date);
        List<PlasticInjectCheckChild> PICheckChilds
                =new ArrayList<PlasticInjectCheckChild>();
        for(SpotCheckItem item:spotCheckItems)
        {
            PlasticInjectCheckChild injectCheck
                    =new PlasticInjectCheckChild(interID);
            injectCheck.setSpotCheckItem(item);
            PICheckChilds.add(injectCheck);
            Utils.saveInLocal(injectCheck);
        }
        dao.saveInTx(PIcheck);
        PISpotCheckJSON checkJSON=new PISpotCheckJSON();
        checkJSON.setPICheckChilds(PICheckChilds);
        checkJSON.setPiCheck(PIcheck);
        checkJSON.createJsonByPIcheck();
        checkJSON.uploadWithJson(new PISpotCheckJSON.UploadListener()
        {
            @Override
            public void onSuccess() {
                showToast("上传完成,继续点检");
                spotCheckNext();
            }
            @Override
            public void onFail() {
                showToast("网络异常,将在后台自动重试,继续点检");
            }

        });
    }

    /**
     * 上传成功,继续点检下一设备
     */
    @Override
    public void spotCheckNext() {
        setDeviceCode("");
        barCode_edit.setText("");
        barCode_edit.setHint(R.string.hint_barCode);
        deviceInfo_text.setText("设备信息");
        pi_spotCheckPager.setVisibility(View.GONE);
        upload_btn.setVisibility(View.GONE);
    }

    /**
     * 清空条码录入框体的内容
     */
    @Override
    public void clearBarCodeEdit() {
        barCode_edit.setText("");
    }

    /**
     *让条码录入框获取焦点
     */
    @Override
    public void focusOnBarCodeEdit() {
        barCode_edit.requestFocus();
    }

    @Override
    public void switchToItem(int position) {
        pi_spotCheckPager.setCurrentItem(position,false);
    }

    @Override
    public void setScanOverLister(PIViewPagerAdapter.ScanOverListener lister) {
        barCode_edit.setOnEditorActionListener(lister);
        barCode_edit.setOnKeyListener(lister);
    }

    @Override
    public CharSequence getScanedBarCode() {
        return barCode_edit.getText();
    }

    @Override
    public Context getContext() {
        return getBaseContext();
    }

    /**
     * 以barCode为参数请求设备信息
     */
    public void requestDeviceInfo(final String barCode) {
        OkHttpUtils
                .get()
                .url(FURJA_DEVICE_INFO_GET)
                .addParams("FBarCode", barCode)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                        if(++requestCount<3)
                            requestDeviceInfo(barCode);
                        else
                            showToast("网络异常请重试");
                    }

                    @Override
                    public void onResponse(final String response, int i) {
                        showLog(getClass()+response);
                        requestCount=0;
                        Observable.fromCallable(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                try
                                {
                                    DeviceInfoJson infoJson
                                            = JSON.parseObject(response,DeviceInfoJson.class);
                                    if(infoJson.getErrCode()!=100)
                                        return "";
                                    DeviceInfoJson.ErrDataBean bean=infoJson.getErrData().get(0);
                                    return bean.toString();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return "";
                                }}})
                                .subscribe(new Consumer<Object>() {
                                    @Override
                                    public void accept(Object s) throws Exception {
                                        String deviceInfo=s.toString();
                                        if(TextUtils.isEmpty(deviceInfo))
                                        {
                                            showToast("找不到条码相应信息,请核对");
                                            return;
                                        }
                                        setDeviceCode(barCode);
                                        deviceInfo_text
                                                .setText(deviceInfo);
                                        barCode_edit.setHint("请录入点检内容条码");
                                        barCode_edit.setText("");
                                    }
                                });
                    }
                });
    }

    @Override
    public void showViewPager() {
        if(pi_spotCheckPager.getVisibility()!=View.VISIBLE)
        {
            pi_spotCheckPager.setVisibility(View.VISIBLE);
            upload_btn.setVisibility(View.VISIBLE);
        }
        checkFinishOrFresh();
    }

    @Override
    public int getCurrentItem() {
        return pi_spotCheckPager.getCurrentItem();
    }

    @Override
    public void freshViewPager() {
        checkAdapter.notifyDataSetChanged();
        checkFinishOrFresh();
    }

    /**
     * 获取当前机器条码,如果是""则未扫描设备条码不予点检
     * @return
     */
    @Override
    public String getDeviceBarCode() {
        return getDeviceCode();
    }

    /**
     * 校验是否点检完成或更新相应进度
     */
    public boolean checkFinishOrFresh()
    {
       int checkedNum=0,totalNum=19;
       List<SpotCheckItem> spotCheckItems
                        =checkAdapter.getSpotCheckItems();
       String progress="已点检 ";
       if(spotCheckItems!=null)
       {
           for(SpotCheckItem item:spotCheckItems)
           {
               if(item.isChecked())
                   checkedNum++;
           }
       }
       showLog("checkedNum:"+checkedNum);
       if(checkedNum==totalNum)
       {
           upload_btn.setText("");
           upload_btn.setBackgroundResource(R.mipmap.ic_upload_src);
           return true;
       }
       else
       {
           upload_btn.setText(progress+checkedNum+"/ "+totalNum);
           upload_btn.setBackgroundResource(R.mipmap.ic_upload_src_bg);
           return false;
       }
    }



    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public boolean isPass() {
        return isPass;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }

}

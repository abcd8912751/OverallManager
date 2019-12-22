package com.furja.verify.presenter;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.furja.overall.R;
import com.furja.verify.beans.CheckState;
import com.furja.verify.contract.ProductVerifyContract;
import com.furja.verify.json.JYProductJson;
import com.furja.verify.utils.InneURL;
import com.furja.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static com.furja.verify.utils.Constants.FURJA_VERIFYJY_URL;
import static com.furja.verify.utils.Constants.isInnerNet;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * 连接ProductVerify活动与之View的桥
 */

public class JYProductVerifyPresenter implements ProductVerifyContract.Presenter {
    ProductVerifyContract.View productVerifyView;
    JYProductJson.ErrDataBean dataBean;
    long lastTimeMillis;    //上次校验条码的时间
    int requestCount;   //请求次数
    String mainBarCode; //主机即成品条码
    CheckState checkState;  //检验状态
    public JYProductVerifyPresenter(ProductVerifyContract.View view)
    {
        this.productVerifyView=view;
        checkState=new CheckState();
        checkState.reset();requestCount=0;
        setInputOverListener();
        lastTimeMillis=System.currentTimeMillis();
    }

    /**
     * 给每个条码输入框注册结束监听
     */
    private void setInputOverListener()
    {
        InputOverListener listener=new InputOverListener();
        productVerifyView.setEditorActionListener(listener);
        productVerifyView.setKeyListener(listener);
    }

    /**
     * KeyListener和OnEditorActionListener监听
     */
    public class InputOverListener implements View.OnKeyListener,TextView.OnEditorActionListener{
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER)
            {
                if(System.currentTimeMillis()-lastTimeMillis>1000)
                    lastTimeMillis=System.currentTimeMillis();
                else
                    return false;
                String input=productVerifyView.getTextString(view.getId());
                showLog("有Key指令"+view.getId()+":"+input+"尾");
                TextView textView=(TextView)view;
                if(view.getId()== R.id.edit_mainBarCode)
                {
                    if(input!=null)
                    {
                        input=input.replace("\n","");
                        input=input.replace("\r","");
                    }
                    mainBarCode=input;
                    input= Utils.formatBarCode(input);
                    requestInfo(input);
                }
                else
                    verifyByBarCode(input,textView);
            }
            return false;
        }
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
        {
            if (i == EditorInfo.IME_ACTION_DONE)
            {
                if(System.currentTimeMillis()-lastTimeMillis>1000)
                    lastTimeMillis=System.currentTimeMillis();
                else
                    return false;
                String input=productVerifyView.getTextString(textView.getId());
                showLog("有Done指令"+textView.getId()+":"+input);
                if(input!=null)
                {
                    input=input.replace("\n","");
                    input=input.replace("\r","");
                }
				if(textView.getId()== R.id.edit_mainBarCode)
                {
                    mainBarCode=input;
                    input= Utils.formatBarCode(input);
                    requestInfo(input);
                }
                else
                    verifyByBarCode(input,textView);
            }
            return false;
        }

        /**
         * 获取条码尾
         * @param string
         * @return
         */
        public String getTailString(String string)
        {
            if(TextUtils.isEmpty(string)||string.length()<12)
                return "无";
            return string.substring(string.length()-12);
        }

        /**
         * 与 已获取到的产品信息校验
         * @param input
         */
        private void verifyByBarCode(String input,TextView view)
        {
            int viewId=view.getId();
            showLog(input+"来检查了:"+view.getId());
            if(dataBean==null)
            {
                return;
            }
            switch (viewId)
            {
                case R.id.edit_colorBox:
                    if(input.equals(dataBean.getColorBoxCode())&&input.equals(mainBarCode))
                    {
                        if(checkState.isCheckedColorBox())
                            return;
                        checkState.setCheckedColorBox(true);
                        view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_verfity_right,0);
                        verifyFinish();
                    }
                    else
                    {
                        view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_verfity_error,0);
                        productVerifyView.showVerifyDialog("NG");
                    }
                    break;
                case R.id.edit_outBox:
                    if(input.equals(dataBean.getOutBoxCode()))
                    {
                        if(checkState.isCheckedOutBox())
                            return;
                        checkState.setCheckedOutBox(true);
                        view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_verfity_right,0);
                        verifyFinish();
                    }
                    else
                    {
                        view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_verfity_error,0);
                        productVerifyView.showVerifyDialog("NG");
                    }
                    break;
                case R.id.edit_batteryBarCode1:
                case R.id.edit_batteryBarCode2:
                    if(input.equals(dataBean.getBatteryBagOne()))
                    {
                        showLog("电池包条码正确");
                        if(checkState.isCheckedBattery1())
                            return;
                        checkState.setCheckedBattery1(true);
                        view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_verfity_right,0);
                        verifyFinish();
                    }
                    else
                        if(input.equals(dataBean.getBatteryBagTwo()))
                        {
                            showLog("条码正确");
                            if(checkState.isCheckedBattery2())
                                return;
                            checkState.setCheckedBattery2(true);
                            view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_verfity_right,0);
                            verifyFinish();
                        }
                        else
                        {
                            view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_verfity_error,0);
                            productVerifyView.showVerifyDialog("NG");
                        }
                    break;
            }
        }

        /**
         * 以成品条码 获取产品信息
         * @param barCode
         */
        private void requestInfo(final String barCode)
        {
            String verifyJYUrl=FURJA_VERIFYJY_URL;
            if(isInnerNet)
                verifyJYUrl= InneURL.FURJA_VERIFYJY_URL;
            productVerifyView.showProductInfo("正在获取成品信息..");
            OkHttpUtils
                    .get()
                    .url(verifyJYUrl)
                    .addParams("masterCode", barCode)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i)
                        {
                            e.printStackTrace();
                            requestCount++;
                            if(requestCount<3)
                                requestInfo(barCode);
                            else
                            {
                                productVerifyView.
                                        showProductInfo("网络或接口异常");
                            }
                        }
                        /**
                         * response
                         * @param response
                         * @param i
                         */
                        @Override
                        public void onResponse(String response, int i) {
                            showLog(getClass()+response);
                            requestCount=0;
                            try
                            {
                                JYProductJson jyProductJson
                                        = JSON.parseObject(response,JYProductJson.class);
                                if(jyProductJson!=null)
                                {
                                    Observable.just(jyProductJson)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<JYProductJson>() {
                                                @Override
                                                public void accept(JYProductJson jyProductjson) throws Exception {
                                                    List<JYProductJson.ErrDataBean> beans=jyProductjson.getErrData();
                                                    if(beans==null||beans.isEmpty())
                                                    {
                                                        productVerifyView.
                                                                showProductInfo("没有找到符合条件的内容");
                                                        return;
                                                    }
                                                    dataBean=beans.get(0);	//只取第一个寻找到的bean
                                                    if(dataBean==null)
                                                    {
                                                        productVerifyView.
                                                                showProductInfo("条码信息不存在");
                                                        return;
                                                    }
                                                    productVerifyView.hideBarCodeEditor();
                                                    productVerifyView.
                                                            showProductInfo(dataBean.toString());
                                                    if(TextUtils.isEmpty(dataBean.getBatteryBagOne()))
                                                    {
                                                        checkState.minusVerifyNum();
                                                    }
                                                    if(TextUtils.isEmpty(dataBean.getBatteryBagTwo()))
                                                    {
                                                        productVerifyView.hideBattery2Editor();
                                                        checkState.minusVerifyNum();
                                                    }
                                                    if(checkState.getVerifyNum()==0)
                                                    {
                                                        productVerifyView.showBarCodeEditor();
                                                    }
                                                }
                                            });
                                }
                                else
                                    showLog("条码不存在");
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                showToast("没有找到物料,需重输");
                            }
                        }
                    });
        }
    }


    /**
     * 判断是否校验完成
     */
    private void verifyFinish() {
        if(checkState.verifyFinish())
            productVerifyView.showVerifyDialog("OK");
    }


    /**
     * 清除结果
     */
    public void cleanDataBean()
    {
        this.dataBean=null;
        checkState.reset();
    }
}

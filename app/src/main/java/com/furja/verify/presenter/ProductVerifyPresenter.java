package com.furja.verify.presenter;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.furja.overall.R;
import com.furja.verify.contract.ProductVerifyContract;
import com.furja.verify.json.MaterialJson;
import com.furja.verify.utils.InneURL;
import com.furja.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static com.furja.verify.utils.Constants.FURIA_BARCODEINFO_URL;
import static com.furja.verify.utils.Constants.isInnerNet;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * 连接ProductVerify活动与之View的桥
 */

public class ProductVerifyPresenter implements ProductVerifyContract.Presenter {

    ProductVerifyContract.View productVerifyView;
    MaterialJson.ErrDataBean dataBean;
    int requestCount;
    int verifiedNum;  //校验成功的数目
    int verifyNum;  //待校验的电池包数目
    long lastTimeMillis;    //上次校验条码的时间
    String mainBarCode;
    public ProductVerifyPresenter(ProductVerifyContract.View view)
    {
        this.productVerifyView=view;
        this.requestCount=0;
        this.verifyNum=2;
        this.verifiedNum=0;
        setInputOverListener();
        lastTimeMillis=System.currentTimeMillis();
    }

    /**
     * 给每个条码输入框注册结束监听
     */
    private void setInputOverListener() {
        InputOverListener listener=new InputOverListener();
        productVerifyView.setEditorActionListener(listener);
        productVerifyView.setKeyListener(listener);
    }


    public class InputOverListener implements View.OnKeyListener,TextView.OnEditorActionListener{

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER)
            {
                showLog("有Key指令"+view.getId());
                if(System.currentTimeMillis()-lastTimeMillis>1000)
                    lastTimeMillis=System.currentTimeMillis();
                else
                    return false;
                String input=productVerifyView.getTextString(view.getId());

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
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == EditorInfo.IME_ACTION_DONE)
            {
                if(System.currentTimeMillis()-lastTimeMillis>1000)
                    lastTimeMillis=System.currentTimeMillis();
                else
                    return false;
                showLog("有Done指令"+textView.getId());
                String input=productVerifyView.getTextString(textView.getId());
                if(textView.getId()== R.id.edit_mainBarCode)
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
            if(input!=null)
            {
                input=input.replace("\n","");
                input=input.replace("\r","");
            }
            showLog("来检查了:"+view.getId());
            if(dataBean==null)
            {
                return;
            }
            if(view.getId()==R.id.edit_batteryBarCode1)
            {
                if(input.equals(dataBean.getFBatteryBarCodeNO1()))
                {
                    showLog("电池包条码正确");
                    view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_verfity_right,0);
                    verifyFinish();
                }
                else
                {
                    view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_verfity_error,0);
                    productVerifyView.showVerifyDialog("NG");
                }
            }
            else
            {
                if(input.equals(dataBean.getFBatteryBarCodeNO2()))
                {
                    showLog("条码正确");
                    view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_verfity_right,0);
                    verifyFinish();
                }
                else
                {
                    view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_verfity_error,0);
                    productVerifyView.showVerifyDialog("NG");
                }
            }
        }

        /**
         * 以成品条码 获取产品信息
         * @param barCode
         */
        private void requestInfo(final String barCode)
        {
            String barCodeUrl=FURIA_BARCODEINFO_URL;
            if(isInnerNet)
                barCodeUrl= InneURL.FURIA_BARCODEINFO_URL;
            productVerifyView.showProductInfo("正在获取成品信息..");
            OkHttpUtils
                    .get()
                    .url(barCodeUrl)
                    .addParams("Barcode", barCode)
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

                        @Override
                        public void onResponse(String response, int i) {
                            showLog(getClass()+response);
                            requestCount=0;
                            try
                            {
                                MaterialJson materialJson
                                        = JSON.parseObject(response,MaterialJson.class);
                                if(materialJson!=null)
                                {
                                    Observable.just(materialJson)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<MaterialJson>() {
                                                @Override
                                                public void accept(MaterialJson materialJson) throws Exception {
                                                    dataBean=materialJson.getErrData();
                                                    if(dataBean==null)
                                                    {
                                                        productVerifyView.
                                                                showProductInfo("条码信息不存在");
                                                        return;
                                                    }
                                                    productVerifyView.hideBarCodeEditor();
                                                    showLog(dataBean.toString(getTailString(mainBarCode)));
                                                    productVerifyView.
                                                            showProductInfo(dataBean.toString(getTailString(mainBarCode)));
                                                    if(TextUtils.isEmpty(dataBean.getFBatteryBarCodeNO1()))
                                                    {
                                                        verifyNum--;
                                                    }
                                                    if(TextUtils.isEmpty(dataBean.getFBatteryBarCodeNO2()))
                                                    {
                                                        productVerifyView.hideBattery2Editor();
                                                        verifyNum--;
                                                    }
                                                    if(verifyNum==0)
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
        if((++verifiedNum)==verifyNum)
            productVerifyView.showVerifyDialog("OK");
    }

    /**
     * 清除结果
     */
    public void cleanDataBean()
    {
        this.dataBean=null;
        setVerifyNum(2);
        setVerifiedNum(0);
    }

    public void setVerifyNum(int verifyNum) {
        this.verifyNum = verifyNum;
    }

    public void setVerifiedNum(int verifiedNum) {
        this.verifiedNum = verifiedNum;
    }


}

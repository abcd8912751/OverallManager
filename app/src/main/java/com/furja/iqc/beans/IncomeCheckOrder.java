package com.furja.iqc.beans;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.furja.iqc.NetworkChangeReceiver;
import com.furja.iqc.utils.HttpCallback;
import com.furja.iqc.utils.HttpLooperInterceptor;
import com.furja.iqc.utils.MyCrashHandler;
import com.furja.utils.Utils;
import com.furja.utils.RetrofitHelper;
import com.furja.utils.SharpBus;
import com.furja.iqc.utils.StringFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.furja.utils.Constants.FURJA_QCWEBINNER_URL;
import static com.furja.utils.Constants.FURJA_QCWEBOUTER_URL;
import static com.furja.utils.Constants.LOG_TAG;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * 来料检验单GreenDao基类
 */
public class IncomeCheckOrder {
    private Long id;
    private boolean uploaded;
    private String CodeBar;
    private String QCEntryData;
    private String FItemId;
    private String Status;
    private String uploadResult;


    public IncomeCheckOrder(String codeBar,String entryData,String fitemID)
    {
        this.id = null;
        this.uploaded = false;
        this.CodeBar = codeBar;
        this.QCEntryData = entryData;
        this.FItemId = fitemID;
        this.uploadResult="";
        setStatus("");
    }
    public IncomeCheckOrder(Long id, boolean uploaded, String CodeBar, String QCEntryData,
            String FItemId, String Status, String uploadResult) {
        this.id = id;
        this.uploaded = uploaded;
        this.CodeBar = CodeBar;
        this.QCEntryData = QCEntryData;
        this.FItemId = FItemId;
        this.Status = Status;
        this.uploadResult = uploadResult;
    }

    public IncomeCheckOrder() {
    }

    /**
     * 使用OkHttpUtils上传
     */
    public  void upload(final HttpCallback<IncomeCheckOrder> httpCallback)
    {
        final SharpBus sharpBus = SharpBus.getInstance();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLooperInterceptor(LOG_TAG))
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.MINUTES)
                .retryOnConnectionFailure(false)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
        showLog("提交检验单");
        String QCWEB_URL= FURJA_QCWEBOUTER_URL;
        if(NetworkChangeReceiver.isInnerNet())
            QCWEB_URL= FURJA_QCWEBINNER_URL;
        OkHttpUtils.post()
                .url(QCWEB_URL+"SendQCListByTable")
                .params(getUploadMap())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int i) {
                        e.printStackTrace();
                        httpCallback.onFail("网络异常请稍后重试");
                    }
                    @Override
                    public void onResponse(String body, int i) {
                        if(TextUtils.isEmpty(body))
                        {
                            httpCallback.onFail("网络异常请稍后重试");
                            return;
                        }
                        showLog(CodeBar+"的结果:"+body);
                        setUploadResult(body);
                        setUploaded(true);
                        if(body.equals("99")||body.contains("异常"))
                        {
                            body="生成检验单出错,请重试";
                            httpCallback.onFail(body);
                        }
                        else
                        {
                            showToast(body);
                            httpCallback.onSuccess(IncomeCheckOrder.this);
                        }
                    }
                });
    }

    /**
     * 使用Retrofit上传数据
     * @param httpCallback
     */
    public void uploadWithRetrofit(final HttpCallback<IncomeCheckOrder> httpCallback)
    {
        String QCWEB_URL= FURJA_QCWEBOUTER_URL;
        if(NetworkChangeReceiver.isInnerNet())
            QCWEB_URL= FURJA_QCWEBINNER_URL;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLooperInterceptor(LOG_TAG))
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(80, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                //其他配置
                .build();
        Retrofit retrofit
                =new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(StringFactory.create())
                .baseUrl(QCWEB_URL)
                .build();
        RetrofitHelper helper
                =retrofit.create(RetrofitHelper.class);
        String codebar=getCodeBar();
        if(codebar.contains("T"))
            codebar= Utils.getSubstring(getCodeBar());
        Call<String> postOrder
                =helper.postApplyPurchaseOrder(codebar,
                getQCEntryData(),getFItemId(),Status);
        postOrder.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body=response.body();
                if(response==null
                        || TextUtils.isEmpty(body))
                {
                    httpCallback.onFail("网络异常请稍后重试");
                    return;
                }
                showLog(CodeBar+"的检验结果: "+body);
                setUploadResult(body);
                setUploaded(true);
                if(body.equals("99"))
                {
                    body="生成检验单出错,请重试";
                    MyCrashHandler.getInstance()
                            .upload("VerifyElectronicAPP:>"+getUploadMap().toString());
                    httpCallback.onFail(body);
                }
                else
                {
                    httpCallback.onSuccess(IncomeCheckOrder.this);
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                httpCallback.onFail("网络异常请稍后重试");
            }
        });
    }

    @JSONField(serialize = false)
    public Map<String,String> getUploadMap()
    {
        Map<String,String> params
                =new HashMap<String,String>();
        String codebar=getCodeBar();
        if(codebar.endsWith("T"))
            codebar= Utils.getSubstring(getCodeBar());
        params.put("CodeBar",codebar);
        params.put("QCEntryData",getQCEntryData());
        params.put("FItemId",getFItemId());
        params.put("status",getStatus());
        return params;
    }


    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public boolean getUploaded() {
        return this.uploaded;
    }
    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }
    public String getCodeBar() {
        return this.CodeBar;
    }
    public void setCodeBar(String CodeBar) {
        this.CodeBar = CodeBar;
    }
    public String getQCEntryData() {
        return this.QCEntryData;
    }
    public void setQCEntryData(String QCEntryData) {
        this.QCEntryData = QCEntryData;
    }
    public String getFItemId() {
        return this.FItemId;
    }
    public void setFItemId(String FItemId) {
        this.FItemId = FItemId;
    }
    public String getUploadResult() {
        return this.uploadResult;
    }
    public void setUploadResult(String uploadResult) {
        this.uploadResult = uploadResult;
    }

    public String getStatus() {
        return this.Status;
    }
    public void setStatus(String Status) {
        this.Status = Status;
    }

}

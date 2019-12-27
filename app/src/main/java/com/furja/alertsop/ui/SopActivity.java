package com.furja.alertsop.ui;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.furja.alertsop.contract.InjectionLogContract;
import com.furja.alertsop.presenter.InjectionLogPresenter;
import com.furja.overall.R;
import com.furja.alertsop.beans.MaterialInfo;
import com.furja.alertsop.beans.ProduceNoAndModel;
import com.furja.utils.HttpsUtils;
import com.furja.utils.RetrofitBuilder;
import com.furja.utils.RetrofitHelper;
import com.furja.utils.RetryWhenUtils;
import com.furja.utils.SharpBus;
import com.furja.utils.TextInputListener;
import com.furja.alertsop.view.SopRecyclerAdapter;
import com.furja.alertsop.view.WrapLinearLayoutManager;
import com.furja.overall.ui.BaseActivity;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

import static android.view.KeyEvent.KEYCODE_F7;
import static com.furja.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.utils.Constants.LOG_TAG;
import static com.furja.utils.Constants.NODATA_AVAILABLE;
import static com.furja.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.utils.Constants.VERTX_INNER_URL;
import static com.furja.utils.Constants.getHttpsUrl;
import static com.furja.utils.TextInputListener.INPUT_ERROR;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

public class SopActivity extends BaseActivity implements InjectionLogContract.View {
    InjectionLogPresenter presenter;
    @BindView(R.id.text_materialName)
    TextView textMaterialName;
    @BindView(R.id.text_materialModel)
    TextView textMaterialModel;
    @BindView(R.id.text_produceNo)
    TextView textProduceNo;
    @BindView(R.id.edit_barCode)
    AppCompatEditText editBarCode;
    @BindView(R.id.materialInfo)
    ConstraintLayout materialInfoView;
    @BindView(R.id.recycler_sop)
    RecyclerView recyclerSop;
    private MaterialInfo materialInfo;
    SopRecyclerAdapter sopAdapter;
    String barCode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sop);
        ButterKnife.bind(this);
        presenter = new InjectionLogPresenter(this);
        initView();
        initGlide();
    }

    private void initView() {
        TextInputListener.bind(editBarCode);
        WrapLinearLayoutManager wrapLinearLayoutManager
                = new WrapLinearLayoutManager(this);
        recyclerSop.setLayoutManager(wrapLinearLayoutManager);
        DividerItemDecoration itemDecoration
                = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            itemDecoration.setDrawable(getDrawable(R.drawable.shape_divider));
        recyclerSop.addItemDecoration(itemDecoration);
        sopAdapter = new SopRecyclerAdapter(R.layout.recycler_sop_item);
        sopAdapter.bindToRecyclerView(recyclerSop);
        sopAdapter.setEmptyView(R.layout.empty_sop_layout,recyclerSop);
        listenSharpBus();
    }

    private void listenSharpBus() {
        SharpBus.getInstance().register(TAG_SCAN_BARCODE,this, String.class)
                .as(AutoDispose.<String>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(scanString -> {
                    if (scanString.equals(INPUT_ERROR)
                          || scanString.equals(INTERNET_ABNORMAL)
                            || scanString.equals(NODATA_AVAILABLE)) {
                        editBarCode.setEnabled(true);
                        editBarCode.setText("");
                        showToast(scanString);
                     } else if(editBarCode.isEnabled()) {
                        showToast("正在获取相关信息及图片");
                        editBarCode.setEnabled(false);
                        barCode = scanString;
                        presenter.acquireMaterialInfo(scanString);
                    }
                });
    }

    @Override
    public void resetView() {
        materialInfoView
                .setVisibility(View.GONE);
        enableBarCodeInput();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event!=null) {
            int keyCode = event.getKeyCode();
            if(keyCode == KeyEvent.KEYCODE_BRIGHTNESS_DOWN
                    || keyCode == KeyEvent.KEYCODE_BRIGHTNESS_UP
                    || keyCode == KEYCODE_F7) {
                if(editBarCode.isEnabled()) {
                    resetView();
                    editBarCode.requestFocus();
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    private void enableBarCodeInput() {
        editBarCode.setText("");
        editBarCode.setEnabled(true);
        editBarCode.setVisibility(View.VISIBLE);
    }
    @Override
    public void showMaterialInfo(MaterialInfo materialInfo) {
        this.materialInfo=materialInfo;
        if (materialInfo != null) {
            editBarCode.setEnabled(true);
            editBarCode.setText("");
            editBarCode.setVisibility(View.GONE);
            materialInfoView.setVisibility(View.VISIBLE);
            textMaterialName.setText(materialInfo.getMaterialName());
            textMaterialModel.setText(materialInfo.getNorm());
            textProduceNo.setText(materialInfo.getProduceNo());
            List<String> newUrls = new ArrayList<String>(),urls=materialInfo.getUrls();
            for (String url:urls) {
                url=url.replace("www.nbfurja.com","192.168.8.46");
                newUrls.add(url);
            }
            sopAdapter.setNewData(newUrls);
            acquireProduceModel(materialInfo.getProduceNo());
            acquireProduceInfo(materialInfo.getProduceNo());
            acquireFurInfo();
        }
    }

    /**
     * 获取客户型号
     * @param produceNo
     */
    private void acquireProduceModel(String produceNo) {
        RetrofitHelper helper
                = RetrofitBuilder.getHelperByUrl(VERTX_INNER_URL, RetrofitHelper.class);
        helper.getProduceModel(produceNo)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenUtils())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if(response.getCode()>0){
                        List<ProduceNoAndModel> result=response.getResult();
                        if(result!=null&&result.size()>0)
                            textProduceNo.setText(result.get(0).toString());
                    }
                },error->{
                    error.printStackTrace();
                });
    }

    /**
     * 获取生产单号的有关信息
     */
    public void acquireProduceInfo(String produceNo){
        RetrofitHelper helper
                = RetrofitBuilder.getHelperByUrl(getHttpsUrl(), RetrofitHelper.class);
        helper.getFurInfo(produceNo)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenUtils())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(materialJson -> {
                    MaterialInfo info=new MaterialInfo(materialJson);
                    List<String> newUrls = new ArrayList<String>(),urls=info.getUrls();
                    newUrls.add("https://192.168.8.46:7070/Image/notice/2.jpg");
                    for (String url:urls) {
                        url=url.replace("www.nbfurja.com","192.168.8.46");
                        newUrls.add(url);
                    }
                    if(urls.size()>0)
                        sopAdapter.addData(newUrls);
                    else
                        showLog("无相应的变更信息");
                },throwable ->{
                    throwable.printStackTrace();
                });
    }

    public void acquireFurInfo() {
        RetrofitHelper helper
                = RetrofitBuilder.getHelperByUrl(getHttpsUrl(), RetrofitHelper.class);
        if(barCode==null||barCode.length()<5)
            return;
        String scanString=barCode.substring(0, barCode.length()-4);
        helper.getFurInfo(scanString)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenUtils())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(materialJson -> {
                    MaterialInfo info=new MaterialInfo(materialJson);
                    List<String> newUrls = new ArrayList<String>(),urls=info.getUrls();
                    newUrls.add("https://192.168.8.46:7070/Image/notice/1.jpg");
                    for (String url:urls) {
                        url=url.replace("www.nbfurja.com","192.168.8.46");
                        newUrls.add(url);
                    }
                    if(urls.size()>0)
                        sopAdapter.addData(newUrls);

                },throwable ->{
                    if(throwable instanceof NetworkErrorException)
                        showToast(INTERNET_ABNORMAL);
                });
    }





    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public Context getContext() {
        return this;
    }

    protected void initGlide(){
        HttpsUtils.SSLParams sslParams
                = HttpsUtils.getSslSocketFactory(null, null, null);
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor(LOG_TAG))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier(hostnameVerifier)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                //其他配置
                .build();
        Glide.get(getApplicationContext()).getRegistry().replace(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(okHttpClient));
        new Thread(()->{
            Glide.get(getApplicationContext()).clearDiskCache();
        }).start();
    }
}

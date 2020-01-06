package com.furja.presenter;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import com.furja.common.MaterialInfo;
import com.furja.common.ProduceNoAndModel;
import com.furja.common.WrapLinearLayoutManager;
import com.furja.contract.SopOnlineContract;
import com.furja.iqc.ui.adapter.SopRecyclerAdapter;
import com.furja.overall.R;
import com.furja.utils.RetrofitBuilder;
import com.furja.utils.RetrofitHelper;
import com.furja.utils.RetryWhenUtils;
import com.furja.utils.SharpBus;
import com.furja.utils.TextInputListener;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import static com.furja.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.utils.Constants.NODATA_AVAILABLE;
import static com.furja.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.utils.Constants.getBaseUrl;
import static com.furja.utils.Constants.getHttpsUrl;
import static com.furja.utils.Constants.getVertxUrl;
import static com.furja.utils.TextInputListener.INPUT_ERROR;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

public class SopOnlinePresenter implements SopOnlineContract.Presenter {
    Context context;
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
    private SopOnlineContract.View sopView;
    SopRecyclerAdapter sopAdapter;
    String barCode;
    SharpBus sharpBus;
    public SopOnlinePresenter(View rootView, LifecycleOwner lifeOwner) {
        ButterKnife.bind(this,rootView);
        this.context=rootView.getContext();
        initView();
        listenSharpBus(lifeOwner);
    }

    private void initView() {
        TextInputListener.bind(editBarCode);
        WrapLinearLayoutManager wrapLinearLayoutManager
                = new WrapLinearLayoutManager(context);
        recyclerSop.setLayoutManager(wrapLinearLayoutManager);
        DividerItemDecoration itemDecoration
                = new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(context,R.drawable.shape_divider));
        recyclerSop.addItemDecoration(itemDecoration);
        sopAdapter = new SopRecyclerAdapter(R.layout.recycler_sop_item);
        sopAdapter.bindToRecyclerView(recyclerSop);
        sopAdapter.setEmptyView(R.layout.empty_sop_layout,recyclerSop);
    }

    /**
     * 监听SharpBus事件
     * @param lifeOwner
     */
    private void listenSharpBus(LifecycleOwner lifeOwner) {
        sharpBus = SharpBus.getInstance();
        sharpBus.register(TAG_SCAN_BARCODE,this, String.class)
                .as(AutoDispose.<String>autoDisposable(AndroidLifecycleScopeProvider.from(lifeOwner)))
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
                        acquireMaterialInfo(scanString);
                    }
                });
    }

    /**
     * 获取物料信息
     * @param scanString
     */
    public void acquireMaterialInfo(String scanString) {
        RetrofitHelper helper
                = RetrofitBuilder.getHelperByUrl(getBaseUrl());
        helper.getMaterialJson(scanString)
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenUtils())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(materialJson -> {
                    MaterialInfo info=new MaterialInfo(materialJson);
                    showMaterialInfo(info);
                },throwable ->{
                    if(throwable instanceof NetworkErrorException)
                        sharpBus.post(TAG_SCAN_BARCODE,INTERNET_ABNORMAL);
                    else
                        sharpBus.post(TAG_SCAN_BARCODE,NODATA_AVAILABLE);
                });
    }

    public void resetView() {
        materialInfoView.setVisibility(View.GONE);
        enableBarCodeInput();
    }

    private void enableBarCodeInput() {
        editBarCode.setText("");
        editBarCode.setEnabled(true);
        editBarCode.setVisibility(View.VISIBLE);
    }

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
        RetrofitHelper helper= RetrofitBuilder.getHelperByUrl(getVertxUrl());
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
                = RetrofitBuilder.getHelperByUrl(getHttpsUrl());
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

    /**
     * 获取物料
     */
    public void acquireFurInfo() {
        RetrofitHelper helper= RetrofitBuilder.getHelperByUrl(getHttpsUrl());
        if(barCode==null||barCode.length()<6)
            return;
        String scanString=barCode.substring(0, barCode.length()-5);
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

    public void focusBarcodeInput() {
        if(editBarCode.isEnabled()) {
            resetView();
            editBarCode.requestFocus();
        }
    }
}

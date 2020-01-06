package com.furja.iqc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.furja.common.CloudUserWithOrg;
import com.furja.common.JustCallBack;
import com.furja.common.RecyclerBottomSheetFragment;
import com.furja.common.WrapLinearLayoutManager;
import com.furja.iqc.presenter.InspectItemDetailPresenter;
import com.furja.utils.Utils;
import com.furja.overall.FurjaApp;
import com.furja.overall.R;
import com.furja.common.BaseHttpResponse;
import com.furja.iqc.beans.ReferDetail;
import com.furja.iqc.json.InspectBillJSON;
import com.furja.iqc.json.ApplyCheckOrder;
import com.furja.iqc.json.NewQCList;
import com.furja.common.Qrcode;
import com.furja.utils.RetryWhenUtils;
import com.furja.overall.ui.BaseActivity;
import com.furja.utils.RetrofitBuilder;
import com.furja.utils.RetrofitHelper;
import com.furja.utils.SharpBus;
import com.furja.common.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.furja.iqc.ui.LinearDialogFragment.DIALOG_SOP_ONLINE;
import static com.furja.utils.Constants.EXTRA_NOREASON_NUMBER;
import static com.furja.utils.Constants.EXTRA_QCLIST_DATA;
import static com.furja.utils.Constants.getVertxUrl;
import static com.furja.utils.Utils.intOf;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showLongToast;
import static com.furja.utils.Utils.showToast;
import static com.furja.utils.Utils.textOf;

/**
 *检验项目实际操作块
 */
public class InspectItemDetailActivity extends BaseActivity implements InspectItemDetailPresenter.ProjectDetailView{
    WrapLinearLayoutManager layoutManager;
    SharpBus sharpBus
            = SharpBus.getInstance();
    InspectItemDetailPresenter detailPresenter;
    long lastPressTime;
    List<NewQCList.QCDataBean> qcDataBeans;
    LinearDialogFragment webviewFragment,sopOnlineFragment;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_item);
        lastPressTime =System.currentTimeMillis();
        View view=findViewById(android.R.id.content);
        detailPresenter =new InspectItemDetailPresenter(view, InspectItemDetailActivity.this);
        TextView textComplete=findViewById(R.id.text_complete);
        textComplete.setOnClickListener(v->{
            if(detailPresenter.isEmpty()) {
                showToast("无可用检验项目");
                return;
            }
            List<ApplyCheckOrder> orders =detailPresenter.getmDatas();
            int index=0;
            boolean qualified=true;
            for(ApplyCheckOrder order:orders){
                if(!order.hasCheck()) {
                    showToast("存在尚未检验的定量分析项目");
                    detailPresenter.setPositionAndValue(index);
                    detailPresenter.generateSpinnerItems(index);
                    return;
                }
                if(qualified&&!order.isQualified(true))
                    qualified=false;
                index++;
            }
            showConfirmDialog(qualified);
        });
        analyseIntent(getIntent());
        fragmentManager = getSupportFragmentManager();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 生成确认对话框
     */
    private void showConfirmDialog(boolean qualified) {
        MaterialDialog.Builder builder=new MaterialDialog.Builder(this)
                .negativeText("取消").positiveText("确认")
                .title("生成检验单").autoDismiss(false)
                .content("确定数据无误并予以提交?")
                .onPositive((materialDialog, dialogAction) -> {
                    materialDialog.getActionButton(DialogAction.POSITIVE).setVisibility(View.GONE);
                    materialDialog.getActionButton(DialogAction.NEGATIVE).setVisibility(View.GONE);
                    materialDialog.setContent("正在提交...");
                    materialDialog.setCancelable(false);
                    generateAndUploadJson("A",materialDialog);  //接收为A
                }).onNegative((dialog, which) -> {
                    dialog.cancel();
                });
        if(!qualified) {
            builder.neutralText("让步接收").onNeutral((materialDialog, dialogAction)->{
                materialDialog.getActionButton(DialogAction.POSITIVE).setVisibility(View.GONE);
                materialDialog.getActionButton(DialogAction.NEUTRAL).setVisibility(View.GONE);
                materialDialog.getActionButton(DialogAction.NEGATIVE).setVisibility(View.GONE);
                materialDialog.setContent("正在提交...");
                materialDialog.setCancelable(false);
                generateAndUploadJson("B",materialDialog);  //让步接收的Policy是B
            }).positiveText("直接生成").onPositive((materialDialog, dialogAction) -> {
                materialDialog.getActionButton(DialogAction.POSITIVE).setVisibility(View.GONE);
                materialDialog.getActionButton(DialogAction.NEGATIVE).setVisibility(View.GONE);
                materialDialog.getActionButton(DialogAction.NEUTRAL).setVisibility(View.GONE);
                materialDialog.setContent("正在提交...");
                materialDialog.setCancelable(false);
                generateAndUploadJson("F",materialDialog);  //判退为A
            });
        }
        builder.show().getWindow()
                .setBackgroundDrawableResource(R.drawable.shape_dialog_bg);

    }

    /**
     * 启动 不良项目序列的Activity
     */
    @Override
    public void startQMAGroupDataUI(int viewID) {
        Intent intent =new Intent(this, QMAGroupActivity.class);
        showLog("去记录");
        startActivityForResult(intent,viewID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if(data!=null) {
                String text=data.getStringExtra(EXTRA_NOREASON_NUMBER);
                switch (requestCode) {
                    case 1:
                        detailPresenter
                                    .valueItemByEditText(R.id.item_edit1,text);
                        break;
                    case 3:
                        detailPresenter
                                    .valueItemByEditText(R.id.item_edit3,text);
                        break;
                    case 5:
                        detailPresenter
                                    .valueItemByEditText(R.id.item_edit5,text);
                        break;
                }
                smoothToPostion(detailPresenter.getCurrentPosition());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 生成并上传检验单
     */
    private void generateAndUploadJson(String policy,final MaterialDialog materialDialog) {
        Observable.fromCallable(new Callable<InspectBillJSON>() {
            @Override
            public InspectBillJSON call() throws Exception {
                User user= FurjaApp.getUser();
                InspectBillJSON inspectBillJSON=new InspectBillJSON();
                String fuserid="0",currentOrgNumber="";
                if(user!=null) {
                    fuserid = user.getUserName();
                    inspectBillJSON.setUser(user);
                    CloudUserWithOrg cloudUserWithOrg = FurjaApp.getCloudUser();
                    if(cloudUserWithOrg!=null)
                        currentOrgNumber=cloudUserWithOrg.getFOrgNumber();
                }
                inspectBillJSON.setFCurrentOrgNumber(currentOrgNumber);
                List<Qrcode> qrcodes = new ArrayList<>();
                List<ReferDetail> referDetails=new ArrayList<>();
                NewQCList.QCDataBean topBean=qcDataBeans.get(0);
                int materialID = topBean.getMaterialID(),inspectQty=0;
                for(NewQCList.QCDataBean qcDataBean:qcDataBeans){
                    int qty=intOf(qcDataBean.getApplyQcNum());
                    Qrcode qrcode=new Qrcode(fuserid);
                    qrcode.setValue(qcDataBean.getBarcode());
                    qrcode.setQty(qty);
                    qrcode.setTag("生成来料检验单");
                    qrcode.setFSourceID(qcDataBean.getApplyOrderID());
                    qrcode.setFSourceEntryID(qcDataBean.getApplyOrderEntryID());
                    qrcode.setFMaterialID(qcDataBean.getMaterialID());
                    qrcodes.add(qrcode);
                    ReferDetail referDetail=new ReferDetail("PUR_ReceiveBill");
                    referDetail.convertQcData(qcDataBean);
                    referDetails.add(referDetail);
                    inspectQty+=qty;
                }
                inspectBillJSON.setQrcode(qrcodes);
                inspectBillJSON.setFMaterialNumber(topBean.getMaterialNumber());
                inspectBillJSON.setFUsePolicy(policy);
                inspectBillJSON.setFInspectQty(inspectQty);
                inspectBillJSON.setFReferDetail(referDetails);
                inspectBillJSON.setFUnitNumber(topBean.getFUnitNumber());
                inspectBillJSON.setFQcScheme(topBean.getQcScheme());
                inspectBillJSON.setFSourceOrgNumber(topBean.getFSourceOrgNumber());
                inspectBillJSON.setFSupplierNumber(topBean.getSupplyNumber());
                List<ApplyCheckOrder> orders=new ArrayList<>(),
                        oldOrders=detailPresenter.getmDatas();
                for(ApplyCheckOrder order:oldOrders){
                    order.generateValue(inspectQty);
                    orders.add(order);
                }
                inspectBillJSON.setFItemDetail(orders);
                return inspectBillJSON;
            }
        }).subscribeOn(Schedulers.io())
          .flatMap(new Function<InspectBillJSON, ObservableSource<BaseHttpResponse<String>>>() {
            @Override
            public ObservableSource<BaseHttpResponse<String>> apply(InspectBillJSON inspectBillJSON) throws Exception {
                RetrofitHelper helper= RetrofitBuilder.getHelperByUrl(getVertxUrl());
                return helper.postInspectBill(Utils.getRequestBody(inspectBillJSON));
            }
        }).retryWhen(RetryWhenUtils.create())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->{
                    materialDialog.cancel();
                    if(response.getCode()>0) {
                        showToast(response.getResult());
                        backToParentActivity();
                    }else
                        showLongToast(response.getResult());
                },error->{
                    error.printStackTrace();
                    showToast("网络异常,请重试");
                    materialDialog.cancel();
                });
    }

    /**
     * 返回父级 Activity
     */
    @Override
    public void backToParentActivity() {
        Intent intent=new Intent(this, InspectIncomingActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 处理接受的Intent,将其携带的数据加载至RecyclerView
     * @param intent
     */
    private void analyseIntent(Intent intent) {
        if(intent!=null) {
            Observable.fromCallable(new Callable<List<ApplyCheckOrder>>() {
                @Override
                public List<ApplyCheckOrder> call() throws Exception {
                    NewQCList qcList=intent.getParcelableExtra(EXTRA_QCLIST_DATA);
                    if(qcList==null){
                        showLog("没有质检数据");
                        backToParentActivity();
                        return null;
                    }
                    qcDataBeans=qcList.getQCData();
                    List<NewQCList.QCEntryDataBean> databeans = qcList.getQCEntryData();
                    if(databeans==null||databeans.isEmpty()) {
                        showLog("没有质检项目数据");
                        backToParentActivity();
                        return null;
                    }
                    List<NewQCList.QCValueBean> valueBeans =qcList.getQCValueData();
                    List<ApplyCheckOrder> orders =new ArrayList<ApplyCheckOrder>();
                    NewQCList.QCDataBean qcDataBean= qcDataBeans.get(0);
                    int i=0,valueSize=valueBeans.size();
                    for(NewQCList.QCEntryDataBean dataBean:databeans) {
                        ApplyCheckOrder order=new ApplyCheckOrder(dataBean,i);
                        if(i<valueSize&&dataBean.isFKeyInspect())
                            order.fillCheckValue(valueBeans.get(i));
                        order.setSampleScheme(dataBean.getFSampleSchemeId1());
                        order.setFInspectItemId(dataBean.getFInspectItemId());
                        order.setFInspectMethodId(dataBean.getFInspectMethodId());
                        order.setFInspectBasisId(dataBean.getFInspectBasisId());
                        order.setFTargetValB(dataBean.getFTargetValB());
                        order.setFInspectInstrumentId(dataBean.getFInspectInstrumentId());
                        order.setFKeyInspect(dataBean.isFKeyInspect());
                        orders.add(order);
                        i++;
                    }
                    if(textOf(qcDataBean.getMaterialName()).contains("电源线"))
                        detailPresenter.setHasSixValue(true);
                    return orders;
                }
            }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orders->{
                    if(orders!=null)
                        detailPresenter.setmDatas(orders);
                });
        }
    }

    /**
     *移动到某个位置
     *@param position
     */
    public void smoothToPostion(int position) {
        detailPresenter.setPositionAndValue(position);
    }

    @Override
    public void onBackPressed() {
        long currTime=System.currentTimeMillis();
        if(currTime-lastPressTime>2000) {
            showToast("再来一次将放弃本次记录");
            lastPressTime=currTime;
        }
        else
            backToParentActivity();
    }

    @Override
    protected void pushToRight() {
        if(webviewFragment==null)
            webviewFragment = new LinearDialogFragment();
        String tag="InspectDetailWebFragment";
        webviewFragment.show(fragmentManager,tag);
    }

    @Override
    protected void pushToLeft() {
        if(sopOnlineFragment==null)
            sopOnlineFragment = new LinearDialogFragment(DIALOG_SOP_ONLINE);
        String tag="InspectDetailSopOnline";
        sopOnlineFragment.show(fragmentManager,tag);
    }

    @Override
    protected void showSheetDialog() {
        RecyclerBottomSheetFragment<ApplyCheckOrder> sheetFragment=
                new RecyclerBottomSheetFragment<>(detailPresenter.getmDatas());
        sheetFragment.setClickCallBack(new JustCallBack() {
            @Override
            public void onComplete(String res) {
                int position=intOf(res);
                detailPresenter.setPositionAndValue(position);
                detailPresenter.generateSpinnerItems(position);
                if(!sheetFragment.isHidden())
                    sheetFragment.dismiss();
            }
        });
        sheetFragment.setTitle("检验项目清单");
        sheetFragment.show(fragmentManager,"InspectItemDetailActivitySheet");
    }

    @Override
    protected void pageDown() {
        detailPresenter.hideSoftInput();
        detailPresenter.navToNext();
    }

    @Override
    protected void pageUp() {
        detailPresenter.hideSoftInput();
        detailPresenter.navToPrev();
    }
}

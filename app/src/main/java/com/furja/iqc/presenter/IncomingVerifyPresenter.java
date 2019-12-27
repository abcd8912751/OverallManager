package com.furja.iqc.presenter;

import com.alibaba.fastjson.JSONException;
import com.furja.iqc.NetworkChangeReceiver;
import com.furja.overall.FurjaApp;
import com.furja.overall.R;
import com.furja.iqc.json.NewQCList;
import com.furja.utils.RetrofitBuilder;
import com.furja.utils.RetrofitHelper;
import com.furja.utils.RetryWhenUtils;
import com.furja.utils.SharpBus;
import com.furja.utils.TextInputListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.sentry.Sentry;

import static com.furja.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.utils.Constants.SERVER_ABNORMAL;
import static com.furja.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.utils.Constants.VERTX_OUTER_URL;
import static com.furja.utils.Constants.VERTX_TEST_URL;
import static com.furja.utils.Constants.getVertxUrl;
import static com.furja.utils.MyCrashHandler.postError;
import static com.furja.utils.TextInputListener.INPUT_ERROR;
import static com.furja.utils.Utils.getSubstring;
import static com.furja.utils.Utils.intOf;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showLongToast;
import static com.furja.utils.Utils.showToast;

/**
 *电源线检验APP的桥
 */
public class IncomingVerifyPresenter {
    LineVerifyView verifyView;
    SharpBus sharpBus;
    List<NewQCList.QCEntryDataBean> QcEntryDataBeans;   //检验项目
    List<NewQCList.QCDataBean> QcDataBeans;   //检验项目
    String MaterialInfo;    //报检数量,物料代码,最后一次录入
    List<String> barCodes;      //截去后四位的条码
    NewQCList qcList;
    long lastMillis;
    public IncomingVerifyPresenter(LineVerifyView view) {
        this.verifyView=view;
        sharpBus = SharpBus.getInstance();
        registerObserver();
        resetFieldData();
    }

    /**
     * 注册观察者
     */
    private void registerObserver() {
        sharpBus.register(TAG_SCAN_BARCODE)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        String input=o.toString();
                        if(input.contains(INPUT_ERROR)) {
                            showToast(INPUT_ERROR);
                            verifyView.showBarCodeEditor();
                        }
                        else {
                            for(String barCode:barCodes)
                                if(input.contains(barCode)) {
                                    verifyView.showBarCodeEditor();
                                    showToast("该条码已扫描过");
                                    return;
                                }
                            long currTimeMillis=System.currentTimeMillis();
                            if(currTimeMillis-lastMillis<500){
                                showToast("你太勤奋了,我跟不上");
                                verifyView.showBarCodeEditor();
                                return;
                            } lastMillis=currTimeMillis;
                            String inputBarCodes=input;
                            if(!barCodes.isEmpty())
                                inputBarCodes=getCommaString("")+","+input;
                            barCodes.add(input);
                            checkAndRequestQclist(inputBarCodes);
                            verifyView.hideBarCodeEditor();
                        }
                    }
                });
    }

    /**
     * 获取检验项目
     * @param barCode
     */
    public void checkAndRequestQclist(final String barCode){
        String QCWEB_URL = getVertxUrl();
        RetrofitHelper helper = RetrofitBuilder.getHelperByUrl(QCWEB_URL);
        helper.getQcList(barCode, FurjaApp.getUserName())
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWhenUtils.create())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->{
                    showLog("取得QcList");
                    if(response.getCode()>0) {
                        try {
                            NewQCList list=response.getResult();
                            List<NewQCList.QCDataBean> dataList=list.getQCData();
                            if(dataList!=null&&!dataList.isEmpty()) {
                                NewQCList.QCDataBean bean=dataList.get(0);
                                verifyView.showBarCodeEditor();
                                qcList=list;
                                executeQcList();
                            }
                        } catch (Exception e){
                            removeErrorBarcode(barCode);
                            postError(e);
                        }
                    }
                    else {
                        removeErrorBarcode(barCode);
                        showLongToast(response.getMessage());
                        verifyView.showBarCodeEditor();
                        verifyView.setEmptyView(R.layout.scan_empty_view);
                    }
                },error->{
                    error.printStackTrace();
                    postError(error);
                    if(error instanceof JSONException)
                        showToast(SERVER_ABNORMAL);
                    else
                        showToast(INTERNET_ABNORMAL);
                    verifyView.setEmptyView(R.layout.offline_empty_view);
                    verifyView.showBarCodeEditor();
                    removeErrorBarcode(barCode);
                });
    }

    private void removeErrorBarcode(String barCode){
        String removeItem="";
        for(String code:barCodes) {
            if(code.contains(barCode)) {
                removeItem=code;
                break;
            }
        }
        barCodes.remove(removeItem);
        verifyView.setEmptyView(R.layout.scan_empty_view);
    }

    /**
     * 清除观察者
     */
    public void unregisterObserver() {
        sharpBus.unregister(TAG_SCAN_BARCODE);
        showLog("解注册观察者");
    }


    private void executeQcList() {
        List<NewQCList.QCEntryDataBean> entryDataBeans
                =qcList.getQCEntryData();
        setQcEntryDataBeans(entryDataBeans);
        QcDataBeans =qcList.getQCData();
        NewQCList.QCDataBean dataBean=QcDataBeans.get(0);
        setMaterialInfo(dataBean.toString());
        verifyView.showMaterialInfo(getMaterialInfo()+getBarCodeStr());
    }

    /**
     * 将请检数量加起来
     * @return
     */
    public String getBarCodeStr() {
        if(QcDataBeans.isEmpty())
            return "";
        String barString =System.getProperty("line.separator")+"     请检数量:      ";
        int actReceQty=0;
        for (NewQCList.QCDataBean dataBean:QcDataBeans)
            actReceQty += intOf(dataBean.getApplyQcNum());
        return barString+actReceQty;
    }

    /**
     * 将barcodes以逗号分割
     * @param barString
     * @return
     */
    private String getCommaString(String barString) {
        for(int i=0;i<barCodes.size();i++) {
            String barCode=barCodes.get(i);
            if(i!=barCodes.size()-1)
                barString += barCode+",";
            else
                barString += barCode;
        }
        return barString;
    }


    /**
     * 重置字段、变量
     */
    public void resetFieldData() {
        setQcEntryDataBeans(null);
        setMaterialInfo(null);
        qcList=null;
        barCodes=new ArrayList<>();
        QcDataBeans=new ArrayList<>();
        QcEntryDataBeans=new ArrayList<>();
    }

    public String getMaterialInfo() {
        return MaterialInfo;
    }

    public void setMaterialInfo(String materialInfo) {
        MaterialInfo = materialInfo;
    }

    public List<String> getBarCodes() {
        List<String> codes=new ArrayList<String>();
        for(String code:barCodes)
            codes.add(code);
        return codes;
    }

    public void setBarCodes(List<String> barCodes) {
        this.barCodes = barCodes;
    }

    public List<NewQCList.QCEntryDataBean> getQcEntryDataBeans() {
        return QcEntryDataBeans;
    }

    public void setQcEntryDataBeans(List<NewQCList.QCEntryDataBean> qcEntryDataBeans) {
        QcEntryDataBeans = qcEntryDataBeans;
    }


    public NewQCList getQcList() {
        return qcList;
    }

    public List<NewQCList.QCDataBean> getQcDataBeans() {
        return QcDataBeans;
    }

    public List<NewQCList.QCValueBean> getQcValueData() {
        if(qcList==null||qcList.getQCValueData()==null)
            return Collections.emptyList();
        else
            return qcList.getQCValueData();
    }

    public void setQcDataBeans(List<NewQCList.QCDataBean> qcDataBeans) {
        QcDataBeans = qcDataBeans;
    }

    public interface LineVerifyView {
        void hideBarCodeEditor();
        void showBarCodeEditor();
        void setEmptyView(int layoutID);
        void resetView();
        void showMaterialInfo(String infos);
    }
}

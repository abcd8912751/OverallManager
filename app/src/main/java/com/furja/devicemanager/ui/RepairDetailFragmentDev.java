package com.furja.devicemanager.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.furja.devicemanager.DeviceManagerApp;
import com.furja.overall.R;
import com.furja.devicemanager.beans.RepairOrderItem;
import com.furja.devicemanager.beans.RepairProjectItem;
import com.furja.devicemanager.json.RepairCheckJson;
import com.furja.devicemanager.json.RepairRecordJson;
import com.furja.devicemanager.presenter.RepairDetailPresenter;
import com.furja.devicemanager.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static android.app.Activity.RESULT_OK;
import static com.furja.devicemanager.DeviceManagerApp.isChecker;
import static com.furja.devicemanager.utils.Constants.CODE_REPAIR_DETAIL;
import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_CHECK;
import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_MYAPPLY;
import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_MYREPAIR;
import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_REPAIR;
import static com.furja.devicemanager.utils.Constants.FURJA_SEND_REPAIRCHECK;
import static com.furja.devicemanager.utils.Constants.FURJA_SEND_REPAIRRECORD;
import static com.furja.devicemanager.utils.Constants.KEY_PROJECT_ITEMS;
import static com.furja.devicemanager.utils.Utils.isUploadSuccess;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * RadioButton维修 对应的Fragment
 */
public class RepairDetailFragmentDev extends DevBaseFragment implements RepairDetailPresenter.RepairFragmentView {
    @BindView(R.id.text_faultDescription)
    TextView text_faultDescription;
    @BindView(R.id.edit_stopDuration)
    EditText edit_stopDuration;
    @BindView(R.id.edit_repairDuration)
    EditText edit_repairDuration;
    @BindView(R.id.edit_repairFee)
    EditText edit_repairFee;
    @BindView(R.id.edit_repairNote)
    EditText edit_repairNote;
    @BindView(R.id.label_deviceState)
    TextView text_deviceState;
    @BindView(R.id.content_evaluate)
    TextView content_evaluate;

    @BindView(R.id.evaluate_layout)
    LinearLayout evaluateLayout;
    @BindView(R.id.recycler_repairItem)
    RecyclerView recycler_repairItem;
    @BindView(R.id.btn_upload_repair)
    Button btn_upload;
    RepairOrderItem cur_orderItem;
    private String barCode;
    private boolean isCatched=false;    //是否已经接单
    private int errorCount=0;           //请求数据失败次数
    private int filterCode=0;
    RepairDetailPresenter repairDetailPresenter;
    List<RepairProjectItem> projectItemList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_repair_detail, container, false);
        ButterKnife.bind(this, view);
        text_faultDescription.setMovementMethod(ScrollingMovementMethod.getInstance());//textResult滚动
        recycler_repairItem
                .setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        repairDetailPresenter =new RepairDetailPresenter(this);
        if(cur_orderItem!=null)
            repairDetailPresenter.setRepairWOID(cur_orderItem.getFRepairWOID()+"");
        if(filterCode>3||DeviceManagerApp.isChecker())
            setNonEdit();
        else
            setEditAvailable();



        return view;
    }



    /**
     * 检验本工单是否是我提交的
     * @return
     */
    private boolean isMyOrder() {

        return true;
    }

    public String getFormatDouble(double d)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(d);
    }

    @Override
    boolean isEmptyBarCode() {
        return TextUtils.isEmpty(this.barCode);
    }

    @Override
    void setCurBarCode(String barCode) {
        this.barCode=barCode;
    }

    @Override
    void resume() {
        if(fragChangeListener!=null)
        {
            fragChangeListener.hideDeviceInfo();
            fragChangeListener.setDisplayHomeAsUpEnable(true);
            fragChangeListener.setRadioGroupVisibility(View.GONE);
        }
        if(cur_orderItem!=null)
        {
            text_faultDescription
                    .setText(getFaultDescription());
            showRepairValue();
            switch(cur_orderItem.getFRepairState())
            {
                case FILTER_REPAIRORDER_CHECK:
                    btn_upload.setText("验收并审核");
                    break;
                case FILTER_REPAIRORDER_MYAPPLY:
                case FILTER_REPAIRORDER_MYREPAIR:
                    btn_upload.setVisibility(View.GONE);
                    break;
            }
            if(cur_orderItem.getFCheckStatus()==1)
                showEvaluateLayout();
        }
        if(isChecker())
        {
            btn_upload.setText(R.string.check_button_label);
        }
        setUploadEnableOrNot();
    }

    /**
     * 上传成功后重塑View
     * 这里仅供查看
     */
    private void resetView() {

    }

    private void showEvaluateLayout() {
        evaluateLayout.setVisibility(View.VISIBLE);
        if(cur_orderItem.getFRepairCheckResult()!=1)
            text_deviceState.setText("设备状态: 异常");
        content_evaluate.setText(cur_orderItem.getFRepairCheckNote());
    }

    /**
     * 显示维修费用、维修工时及停机工时的值
     */
    private void showRepairValue() {
        if(cur_orderItem.getFRepairFee()==0
                ||cur_orderItem.getFRepairManhour()==0)
        {
            edit_repairDuration
                    .setText(getFormatDouble(cur_orderItem.getFPlanRepairManHour()));
            edit_stopDuration
                    .setText(getFormatDouble(cur_orderItem.getFPlanStopManHour()));
            edit_repairFee
                    .setText(getFormatDouble(cur_orderItem.getFPlanRepairFee()));
        }
        else
        {
            edit_repairDuration
                    .setText(getFormatDouble(cur_orderItem.getFRepairManhour()));
            edit_stopDuration
                    .setText(getFormatDouble(cur_orderItem.getFStopManhour()));
            edit_repairFee
                    .setText(getFormatDouble(cur_orderItem.getFRepairFee()));
            edit_repairNote
                    .setText(cur_orderItem.getFRepairRecord());
        }
    }


    @Override
    public void setUploadEnableOrNot() {
        if(inputHasNull())
            btn_upload.setEnabled(false);
        else
            btn_upload.setEnabled(true);
    }
    @Override
    public boolean isCatched() {
        return isCatched;
    }



    @Override
    public int getRepairState() {
        if(cur_orderItem!=null)
            return cur_orderItem.getFRepairState();
        return 0;
    }


    @Override
    public void setRecyclerAdapter(RepairDetailPresenter.RepairItemAdapter adapter) {
        recycler_repairItem.setAdapter(adapter);
        adapter.bindToRecyclerView(recycler_repairItem);
    }

    @Override
    public void switchActivity(Intent intent) {
        intent.setClass(mContext,ProjectRecordActivity.class);
        startActivityForResult(intent,CODE_REPAIR_DETAIL);
    }

    @Override
    public void addTextWatcher(TextWatcher textWatcher) {
        edit_repairNote.addTextChangedListener(textWatcher);
    }


    /**
     * 检测各个输入框是否为空
     * @return
     */
    public boolean inputHasNull()
    {
        if(TextUtils.isEmpty(edit_repairNote.getText()))
            return true;
        return false;
    }

    @OnClick(R.id.btn_upload_repair)
    public void onClick(View view)
    {
        showLog("点击上传");
        if(inputHasNull())
        {
            showToast("请填写维修记录");
            return;
        }
        if(cur_orderItem.getFRepairState()==FILTER_REPAIRORDER_CHECK)
        {
            showEvaluateDialog();
            return;
        }
        showConfirmDialog();
    }


    /**
     * 显示确认上传/审核的对话框
     */
    private void showConfirmDialog()
    {
        String label="确认数据无误予以提交";

        new MaterialDialog.Builder(mContext)
                .title(label)
                .negativeText("取消")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (cur_orderItem.getFRepairState())
                        {
                            case FILTER_REPAIRORDER_CHECK:
                                showLog("验收");
                                showEvaluateDialog();
                                break;
                            case FILTER_REPAIRORDER_REPAIR:
                                showLog("维修");
                                buildAndUploadRecord();
                                break;
                        }
                        dialog.cancel();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.cancel();
                    }
                }).build().show();
    }

    /**
     * 上传
     */
    private void buildAndUploadRecord() {
        RepairRecordJson json=buildRepairRecord();
        uploadRepairJson(json);
    }



    /**
     *显示评价的窗口
     */
    private void showEvaluateDialog() {
        final MaterialDialog dialog=
                new MaterialDialog.Builder(mContext)
                        .title("验证及评价")
                        .customView(R.layout.dialog_evaluatecontent,false)
                        .build();
        View view= dialog.getCustomView();
        final EditText evaluate=view.findViewById(R.id.evaluate_input);
        final RadioButton overButton=view.findViewById(R.id.repair_over);

        dialog.setActionButton(DialogAction.POSITIVE,R.string.submit_button_label);
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String evaluateString=
                        "评分(满分5分):"+5;
                if(!TextUtils.isEmpty(evaluate.getText()))
                    evaluateString
                            =evaluateString+";评价如下:"+evaluate.getText().toString();
                RepairCheckJson checkJson
                        =new RepairCheckJson();
                if(projectItemList!=null)
                    checkJson.setProjectItemList(projectItemList);
                if(overButton.isChecked())
                {
                    checkJson.setFRepairCheckResult(1);
                }
                else
                    checkJson.setFRepairCheckResult(0);
                checkJson.setFNote(evaluateString);
                loadAndUploadCheckJson(checkJson);
                dialog.cancel();
            }
        });
        dialog.show();
        //默认五星且验收通过
        overButton.setChecked(true);
    }

    /**
     * 装载并上传 验收记录
     * @param checkJson
     */
    private void loadAndUploadCheckJson(RepairCheckJson checkJson) {
        String curDate= Utils.getCurDate();
        String personnelID= DeviceManagerApp.getUserId();
        int userID=Integer.valueOf(personnelID);
        checkJson.setFBillDate(curDate);
        checkJson.setFBillerID(userID);
        checkJson.setFBillID("");
        checkJson.setFID(0);
        checkJson.setFBillStatus(1);
        checkJson.setFCheckDate(curDate);
        checkJson.setFCheckerID(userID);
        checkJson.setFClassTypeID(1002119);
        checkJson.setFRepairCheckerID(userID);
        checkJson.setFRepairCheckDate(curDate);
        try
        {
            if(cur_orderItem!=null)
            {
                checkJson.setFDeviceID(cur_orderItem.getFDeviceID());
                checkJson
                        .setFRepairWOBillID(cur_orderItem.getFRepairWOBillID());
                checkJson.setFRepairWOID(cur_orderItem.getFRepairWOID());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        uploadCheckJson(checkJson);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CODE_REPAIR_DETAIL &&resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                String projectStrings
                        =data.getStringExtra(KEY_PROJECT_ITEMS);
                projectItemList
                        = JSON.parseArray(projectStrings,RepairProjectItem.class);
                showLog(projectItemList.size()+"<已保存维修项目:"+projectStrings);
                repairDetailPresenter.setRepairProjectItems(projectItemList);
            }
        }
    }

    /**
     * 以页面内容生成RepairRcord
     */
    private RepairRecordJson buildRepairRecord() {
        String stopDuration=getTextString(edit_stopDuration);
        String repairDuration=getTextString(edit_repairDuration);
        String repairFee=getTextString(edit_repairFee);
        String personnelID= DeviceManagerApp.getUserId();
        String curDate=Utils.getCurDate();
        int billerID=Integer.valueOf(personnelID);
        RepairRecordJson recordJson=new RepairRecordJson();
        recordJson.setFBillerID(billerID);
        recordJson.setFCheckerID(billerID);
        recordJson.setFBillStatus(1);
        recordJson.setFBeginDate(curDate);
        recordJson.setFEndDate(curDate);
        recordJson.setFBillDate(curDate);
        recordJson.setFCheckDate(curDate);
        recordJson.setFID(0);
        recordJson.setFBillID("");
        recordJson.setFRepairID(0);
        recordJson.setFRepairNumber("");
        recordJson.setFRepairName("");
        recordJson.setFStopManhour(getDoubleofString(stopDuration));
        recordJson.setFRepairFee(getDoubleofString(repairFee));
        recordJson.setFRepairManhour(getDoubleofString(repairDuration));
        recordJson.setFNote(edit_repairNote.getText().toString());
        if(cur_orderItem!=null)
        {
            recordJson.setFTimeUnit(cur_orderItem.getFTimeUnit());
            recordJson.setFRepairCatg(cur_orderItem.getFRepairCatg());
            recordJson.setFEntrust(cur_orderItem.getFEntrust());
            recordJson.setFDeviceID(cur_orderItem.getFDeviceID());
            recordJson.setFEntrust(cur_orderItem.getFEntrust());
            recordJson.setFRepairSubcntr(cur_orderItem.getFRepairSubcntr());
            recordJson.setFCurrency(cur_orderItem.getFCurrency());
            recordJson.setFRepairWOID(cur_orderItem.getFRepairWOID());
            recordJson.setFRepairWOBillID(cur_orderItem.getFRepairWOBillID());
            recordJson.setFPlanRepairFee(cur_orderItem.getFPlanRepairFee());
            recordJson.setFPlanRepairManHour(cur_orderItem.getFPlanRepairManHour());
            recordJson.setFPlanStopManHour(cur_orderItem.getFPlanStopManHour());
            recordJson.setFRequire(cur_orderItem.getFRequire());
            recordJson.setFRepairContent(cur_orderItem.getFRepairContent());
        }
        recordJson.setFClassTypeID(1002118);
        if(projectItemList!=null)
            recordJson.setItems(projectItemList);
        return recordJson;
    }



    /**
     * 上传 维修验收记录
     * @param checkJson
     */
    private void uploadCheckJson(final RepairCheckJson checkJson) {
        final String json=JSON.toJSONString(checkJson);
        showLog("RepairCheckJson:"+json);
        OkHttpUtils
                .postString()
                .url(FURJA_SEND_REPAIRCHECK)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if(++errorCount<3)
                            uploadCheckJson(checkJson);
                        else
                            showToast("上传失败请重试");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        showLog("维修验收上传结果:"+s);
                        errorCount=0;
                        if(isUploadSuccess(s))
                        {
                            showToast("上传成功");
                            resetView();
                        }
                    }
                });
    }

    //上传时可以只上传RepairWOID,和相应的维修内容，服务器根据ID提取共同字段并补充入维修记录
    /**
     * 以json格式上传 维修记录
     */
    private void uploadRepairJson(final RepairRecordJson record) {
        String json
                =JSON.toJSONString(record);
        showLog("RepairRecordJson:"+json);
        OkHttpUtils
                .postString()
                .url(FURJA_SEND_REPAIRRECORD)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if(++errorCount<3)
                            uploadRepairJson(record);
                        else
                            showToast("网络异常请重试");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        showLog("维修记录上传结果:"+s);
                        if(isUploadSuccess(s))
                        {
                            showToast("上传成功");
                        }
                    }
                });
    }

    /**
     * 让所有编辑框均不可编辑
     */
    public void setNonEdit()
    {
        setEditable(false);
    }

    /**
     * 让所有编辑框可编辑
     */
    public void setEditAvailable()
    {
        setEditable(true);
    }

    /**
     * 设置页面编辑栏是否可用
     * @param isEditable
     */
    public void setEditable(boolean isEditable)
    {
        edit_repairDuration.setFocusableInTouchMode(isEditable);
        edit_repairDuration.setFocusable(isEditable);
        edit_stopDuration.setFocusableInTouchMode(isEditable);
        edit_stopDuration.setFocusable(isEditable);
        edit_repairFee.setFocusableInTouchMode(isEditable);
        edit_repairFee.setFocusable(isEditable);
    }

    /**
     * 拿到EditText的文本值
     * @param editText
     * @return
     */
    public String getTextString(EditText editText)
    {
        return editText.getText().toString();
    }
    public double getDoubleofString(String doubles)
    {
        if(TextUtils.isEmpty(doubles))
            return 0;
        else
            return Double.valueOf(doubles);

    }

    public RepairOrderItem getCur_orderItem() {
        return cur_orderItem;
    }

    public void setCur_orderItem(RepairOrderItem cur_orderItem) {
        this.cur_orderItem = cur_orderItem;

    }


    public String getFaultDescription()
    {
        String desp="",line_separator=System.getProperty("line.separator");
        if(cur_orderItem!=null)
        {
            desp="维修单号:"+cur_orderItem.getFRepairWOBillID()+line_separator
                    +"维修类别:"+cur_orderItem.getFRepairGrade()+line_separator
                    +"维修内容:"+cur_orderItem.getFRepairContent()+line_separator
                    +"维修要求:"+cur_orderItem.getFRequire()+line_separator
                    +"使用部门:"+cur_orderItem.getFSettingPlace();
        }
        return desp;
    }

    public int getFilterCode() {
        return filterCode;
    }

    public void setFilterCode(int filterCode) {
        this.filterCode = filterCode;
    }
}

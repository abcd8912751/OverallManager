package com.furja.devicemanager.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.furja.devicemanager.DeviceManagerApp;
import com.furja.overall.R;
import com.furja.devicemanager.beans.RepairProjectItem;
import com.furja.devicemanager.json.RepairWoJson;
import com.furja.devicemanager.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static android.app.Activity.RESULT_OK;
import static com.furja.devicemanager.utils.Constants.CODE_REPAIR_RECORD;
import static com.furja.devicemanager.utils.Constants.FURJA_SEND_REPAIRWO;
import static com.furja.devicemanager.utils.Constants.KEY_PROJECT_ITEMS;
import static com.furja.devicemanager.utils.Utils.isUploadSuccess;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 *故障报修界面,K3里新增设备维修工单
 */

public class ApplyRepairFragmentDev extends DevBaseFragment {
    int devCode=0;                 //设备条码
    @BindView(R.id.edit_repairContent)
    EditText edit_repairContent;    //维修内容
    @BindView(R.id.edit_repairRequire)
    EditText edit_repairRequire;    //维修要求
    @BindView(R.id.edit_stopDuration)
    EditText edit_stopDuration;     //停机用时
    @BindView(R.id.edit_repairDuration)
    EditText edit_repairDuration;   //维修用时
    @BindView(R.id.edit_planRepairFee)
    EditText edit_planRepairFee;   //计划维修费用
    @BindView(R.id.edit_repairNote)
    EditText edit_repairNote;   //维修备注
    @BindView(R.id.begin_content)
    TextView beginContent;
    @BindView(R.id.end_content)
    TextView endContent;
    @BindView(R.id.repair_item)
    TextView repairItem;    //维修项目
    List<RepairProjectItem> projectItemList;
    private int errCount=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view
                =inflater.inflate(R.layout.frag_apply_repair,container,false);
        ButterKnife.bind(this,view);
        setRegionDate();
        return view;
    }

    /**
     *设置 保养 的 时间
     */
    private void setRegionDate() {
        Date date=new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat
                =new SimpleDateFormat("yyyy/MM/dd");
        String dateString=simpleDateFormat.format(date);
        beginContent.setText(dateString);
        endContent.setText(dateString);
    }

    @Override
    boolean isEmptyBarCode() {
        return false;
    }

    @Override
    void setCurBarCode(String barCode) {
        try {
            this.devCode=Integer.valueOf(barCode);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            this.devCode=0;  //默认测试设备
        }
    }

    @Override
    void resume() {
        fragChangeListener.setRadioGroupVisibility(View.GONE);
        fragChangeListener.setDisplayHomeAsUpEnable(true);
    }


    void resetView() {
        edit_repairNote.setText("");
        edit_planRepairFee.setText("");
        edit_repairDuration.setText("");
        edit_stopDuration.setText("");
        edit_repairRequire.setText("");
        edit_repairContent.setText("");
        if(projectItemList!=null)
            projectItemList.clear();
    }

    @OnClick({R.id.begin_content,R.id.end_content})
    public void onSetDate(View view)
    {
        Calendar calendar=Calendar.getInstance();
        DateSetListener setListener=new DateSetListener(view);
        DatePickerDialog datePickerDialog
                =new DatePickerDialog(mContext, setListener,calendar.get(Calendar.YEAR)
                ,calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public class DateSetListener implements DatePickerDialog.OnDateSetListener
    {
        private int viewID;
        public DateSetListener(View view)
        {
            this.viewID=view.getId();
        }
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
        {
            String dateString=i+formatDayOrMonth(i1+1)+formatDayOrMonth(i2);
            if(viewID==R.id.begin_content)
            {
                beginContent.setText(dateString);
            }
            else
                endContent.setText(dateString);
        }
        public String formatDayOrMonth(int i)
        {
            if(i<10)
                return "-0"+i;
            else
                return "-"+i;
        }
    }

    /**
     * 编辑维修项目
     * @param view
     */
    @OnClick({R.id.repair_item,R.id.btn_applyRepair})
    public void addOrApplyRepair(View view)
    {
        switch (view.getId())
        {
            case R.id.repair_item:
                toRecordRepair();
                break;
            case R.id.btn_applyRepair:
                if(!inputHasNull())
                    showConfirmDialog();
                break;
        }
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
                        applyAndCheckRepairWO();
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
     * 去记录维修项目
     */
    private void toRecordRepair() {
        Intent intent=new Intent(mContext,ProjectRecordActivity.class);
        intent.setData(Uri.parse("addRepairItem"));
        if(projectItemList!=null
                &&!projectItemList.isEmpty())
        {
            intent.putExtra(KEY_PROJECT_ITEMS, JSON.toJSONString(projectItemList));
        }
        startActivityForResult(intent, CODE_REPAIR_RECORD);
    }


    private boolean inputHasNull() {
        if(TextUtils.isEmpty(edit_repairContent.getText()))
        {
            edit_repairContent.requestFocus();
            showToast("请输入维修内容");
            return true;
        }
        if(TextUtils.isEmpty(edit_repairRequire.getText()))
        {
            edit_repairRequire.requestFocus();
            showToast("请输入维修要求");
            return true;
        }
        if(TextUtils.isEmpty(edit_planRepairFee.getText()))
        {
            edit_planRepairFee.requestFocus();
            showToast("请输入计划维修费用");
            return true;
        }
        if(projectItemList==null||projectItemList.isEmpty())
        {
            showToast("请记录维修项目");
            toRecordRepair();
            return true;
        }
        return false;
    }

    /**
     * 报修并审核工单
     */
    private void applyAndCheckRepairWO() {
        String curDate= Utils.getCurDate();
        RepairWoJson repairWoJson=new RepairWoJson();
        repairWoJson.setFID(0);
        repairWoJson.setFClassTypeID(1002117);
        repairWoJson.setFBillID("");
        repairWoJson.setFBillStatus(1);     //单据状态为1即审核
        repairWoJson.setFRepairPlanID(0);
        repairWoJson.setFRepairApplyBillID("");
        repairWoJson.setFRepairPlanBillID("");
        repairWoJson.setFDeviceID(devCode);
        repairWoJson.setFRepairNumber("");
        repairWoJson.setFRepairName("");
        repairWoJson.setFPlanBeginDate(beginContent.getText().toString());
        repairWoJson.setFPlanEndDate(endContent.getText().toString());
        repairWoJson.setFEndDate(null);
        repairWoJson.setFBeginDate(null);
        repairWoJson.setFRepairContent(edit_repairContent.getText().toString());
        repairWoJson.setFRequire(edit_repairRequire.getText().toString());
        repairWoJson.setFTimeUnit(11082);           //代表小时
        repairWoJson.setFPlanStopManHour(getDoubleValue(edit_stopDuration));
        repairWoJson.setFPlanRepairManHour(getDoubleValue(edit_repairDuration));
        repairWoJson.setFNote(getEditString(edit_repairNote));
        String userID= DeviceManagerApp.getUserId();
        int billerID=Integer.valueOf(userID);
        repairWoJson.setFBillerID(billerID);
        repairWoJson.setFCheckerID(billerID);
        repairWoJson.setFRepairID(0);
        repairWoJson.setFRepairApplyID(0);
        repairWoJson.setFBillDate(curDate);
        repairWoJson.setFCheckDate(curDate);
        repairWoJson.setFBeginDate(curDate);
        repairWoJson.setFEndDate(curDate);
        repairWoJson.setFRepairCatg(6);         //维修类别
        repairWoJson.setFEntrust(0);            //是否外包
        repairWoJson.setFRepairSubcntr(0);
        repairWoJson.setFCurrency(1);           //人民币
        repairWoJson.setFPlanRepairFee(getDoubleValue(edit_planRepairFee));
        repairWoJson.setFRepairFee(0);
        repairWoJson.setFStopManhour(0);
        repairWoJson.setFPlanRepairManHour(0);
        repairWoJson.setItems(projectItemList);
        uploadWithJson(repairWoJson);
    }

    private void uploadWithJson(final RepairWoJson repairWoJson) {
        String json=JSON.toJSONString(repairWoJson);
        showLog(getClass()+json);
        OkHttpUtils
                .postString()
                .url(FURJA_SEND_REPAIRWO)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if(++errCount<3)
                        {
                            uploadWithJson(repairWoJson);
                        }
                        else
                            showToast("网络异常请重试");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        errCount=0;
                        showLog(s);
                        if(isUploadSuccess(s))
                        {
                            showToast("上传成功");
                            resetView();
                        }
                    }

                });
    }

    @NonNull
    private String getEditString(EditText editText) {
        if(TextUtils.isEmpty(editText.getText()))
            return "";
        else
            return editText.getText().toString();
    }

    /**
     * 获取时长输入框的Double值
     * @param editDuration
     * @return
     */
    private double getDoubleValue(EditText editDuration) {
        CharSequence charSequence=editDuration.getText();
        if(TextUtils.isEmpty(charSequence))
            return 0;
        else
            return Double.valueOf(charSequence.toString());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CODE_REPAIR_RECORD &&resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                String projectStrings
                        =data.getStringExtra(KEY_PROJECT_ITEMS);
                projectItemList
                        = JSON.parseArray(projectStrings,RepairProjectItem.class);
                showLog(projectItemList.size()+"<已保存维修项目:"+projectStrings);
            }
        }
    }
}

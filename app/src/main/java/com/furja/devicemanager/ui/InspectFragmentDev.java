package com.furja.devicemanager.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.furja.devicemanager.DeviceManagerApp;
import com.furja.overall.R;
import com.furja.devicemanager.beans.DetailItem;
import com.furja.devicemanager.beans.InspectPlan;
import com.furja.devicemanager.beans.InspectProjectItem;
import com.furja.devicemanager.json.InspectRecordJson;
import com.furja.devicemanager.json.CheckRecordJson;
import com.furja.devicemanager.utils.JSONParser;
import com.furja.devicemanager.utils.Utils;
import com.furja.devicemanager.view.BasePlanAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;
import static android.app.Activity.RESULT_OK;
import static com.furja.devicemanager.DeviceManagerApp.isChecker;
import static com.furja.devicemanager.utils.Constants.CODE_INSPECT_RECORD;
import static com.furja.devicemanager.utils.Constants.FURJA_GET_INSPECTPLAN;
import static com.furja.devicemanager.utils.Constants.FURJA_SEND_CHECKRECORD;
import static com.furja.devicemanager.utils.Constants.FURJA_SEND_INSPECTRECORD;
import static com.furja.devicemanager.utils.Constants.KEY_PROJECT_ITEMS;
import static com.furja.devicemanager.utils.Constants.STATE_PLAN_INTIME;
import static com.furja.devicemanager.utils.Constants.STATE_PLAN_NEEDCHECK;
import static com.furja.devicemanager.utils.Constants.STATE_PLAN_NOTPASSCHECK;
import static com.furja.devicemanager.utils.Utils.isUploadSuccess;
import static com.furja.devicemanager.utils.Utils.managerNotification;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 *设备点检的Fragment
 */

public class InspectFragmentDev extends DevBaseFragment {
    @BindView(R.id.btn_spotcheck)
    Button upload_Btn;
    @BindView(R.id.recycler_spotcheck)
    RecyclerView spotcheck_Recycler;
    BasePlanAdapter adapter;    //计划内容的RecyclerView适配器
    private String barCode;
    @BindView(R.id.begin_content)
    TextView beginContent;
    @BindView(R.id.end_content)
    TextView endContent;
    InspectPlan inspectPlan;
    List<InspectProjectItem> inspectProjectItems;
    String[] planTitles=new String[]{"设备名称","设备代码","规格型号","所在部门",
            "点检计划名称","点检计划周期","计划开始日期","计划截止日期","最近点检日期","下次点检日期","当日计划状态"};
    int requestCount=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_spotcheck,container,false);
        ButterKnife.bind(this,view);
        adapter=new BasePlanAdapter(null);
        DividerItemDecoration itemDecoration
                =new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL);
        spotcheck_Recycler.addItemDecoration(itemDecoration);
        spotcheck_Recycler.setLayoutManager(new LinearLayoutManager(mContext));
        spotcheck_Recycler.setAdapter(adapter);
        inspectProjectItems=new ArrayList<>();
        setRegionDate();
        if(isChecker())
        {
            upload_Btn.setText(R.string.check_button_label);
        }
        if(inspectPlan!=null)
        {
            this.barCode=inspectPlan.getFDeviceID()+"";
            showPlanList(true);
        }
        return view;
    }

    /**
     * 请求 点检计划
     */
    private void requestInspectPlan() {
        showLog("获取点检计划");
        OkHttpUtils
                .get()
                .url(FURJA_GET_INSPECTPLAN)
                .addParams("devCode",barCode)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if(++requestCount<3)
                            requestInspectPlan();
                        else
                            showToast("网络异常请重试");
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            requestCount=0;
                            s= JSONParser.parserJSON(s);
                            if(s.contains("},"))    //说明有多个计划需要生成JSONArray
                            {
                                List<InspectPlan> planList
                                    =JSON.parseArray(s,InspectPlan.class);
                                inspectPlan
                                    =planList.get(0);
                            }
                            else
                                inspectPlan
                                        =JSON.parseObject(s,InspectPlan.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast("无对应的点检计划");
                            upload_Btn.setEnabled(false);
                            resetView();
                            return;
                        }
                        if(inspectPlan==null)
                        {
                            showLog(getClass()+s);
                            upload_Btn.setEnabled(false);
                            showToast("无对应的点检计划");
                            resetView();
                            return;
                        }
                        upload_Btn.setEnabled(true);
                        showPlanList(false);
                    }
                });
    }

    /**
     * 显示点检计划详情
     */
    private void showPlanList(boolean showDeviceInfo) {
        List<DetailItem> detailItems
                =new ArrayList<DetailItem>();
        List<String> details=inspectPlan.toStringList();
        int addend=4;
        if(showDeviceInfo)
            addend=0;   //如果显示设备信息则不设间隔
        for(int index=0;index+addend<planTitles.length;index++)
        {
            DetailItem item=new DetailItem(planTitles[index+addend],details.get(index+addend));
            detailItems.add(item);
        }
        adapter.setNewData(detailItems);

        setButtonView();
    }

    /**
     * 根据计划状态设置上传按钮的视图
     */
    private void setButtonView() {
        upload_Btn.setText("上传点检记录");
        if(inspectPlan.getFPlanState()==STATE_PLAN_NEEDCHECK)
        {
            upload_Btn.setText("审核");
            if(DeviceManagerApp.isChecker())
                upload_Btn.setEnabled(true);
            else
                upload_Btn.setEnabled(false);
        }
        else if(inspectPlan.getFPlanState()==STATE_PLAN_INTIME)
        {
            upload_Btn.setText("计划如期执行,无需重复操作");
            upload_Btn.setEnabled(false);
        }
        else if(DeviceManagerApp.isChecker())
        {
            upload_Btn.setEnabled(false);
        }
    }

    /**
     *设置 点检 的 时间区间
     */
    private void setRegionDate() {
        Date date=new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat
                =new SimpleDateFormat("yyyy/MM/dd");
        String dateString
			=simpleDateFormat.format(date);
        beginContent.setText(dateString);
        endContent.setText(dateString);
    }

    /**
     * 设置
     * @param view
     */
    @OnClick({R.id.btn_spotcheck,R.id.spotcheck_item})
    public void onClick(View view)
    {
        if (isEmptyBarCode())
            return;
        switch (view.getId())
        {
            case R.id.btn_spotcheck:
                if(inspectProjectItems.isEmpty())
                {
                    if(isChecker())
                        showToast("需先审核点检项目");
                    else
                        showToast("需先记录点检项目");
                    toRecordInspect();
                    return;
                }
                //上传操作
                showConfirmDialog();
                break;
            case R.id.spotcheck_item:
                toRecordInspect();
                break;
        }
    }


    /**
     * 显示确认上传/审核的对话框
     */
    private void showConfirmDialog()
    {
        final boolean isChecker=DeviceManagerApp.isChecker();
        String label="确认数据无误予以上传",
                negativeText="取消",positiveText="确定";
        if(isChecker)
        {
            label="确认数据无误予以通过审核";
            negativeText="不通过";
            positiveText="通过";
        }

        new MaterialDialog.Builder(mContext)
                .title(label)
                .negativeText(negativeText)
                .positiveText(positiveText)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(!isChecker)
                        {
                            InspectRecordJson json=new InspectRecordJson(inspectPlan);
                            json.setFBeginDate(beginContent.getText().toString());
                            json.setFEndDate(endContent.getText().toString());
                            if(inspectProjectItems!=null&&!inspectProjectItems.isEmpty())
                            {
                                int FID=inspectProjectItems.get(0).getFID();
                                json.setFID(FID);
                            }
                            json.setInspectProjectItems(inspectProjectItems);
                            uploadWithJson(JSON.toJSONString(json));
                        }
                        else
                        {
                            CheckRecordJson checkRecordJson
                                    =buildCheckRecord();
                            if(checkRecordJson!=null)
                            {
                                checkRecordJson.setFCheckPass(1);
                                checkRecord(checkRecordJson);
                            }

                        }
                        dialog.cancel();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.cancel();
                        if(isChecker)
                        {
                            CheckRecordJson checkRecordJson
                                    =buildCheckRecord();
                            if(checkRecordJson!=null)
                            {
                                checkRecordJson.setFCheckPass(0);
                                checkRecordJson.setFBillstatus(0);
                                checkRecord(checkRecordJson);
                            }
                        }
                    }
                }).build().show();
    }


    /**
     * 审核点检记录
     */
    public void checkRecord(final CheckRecordJson checkjson)
    {
            OkHttpUtils
                    .postString()
                    .url(FURJA_SEND_CHECKRECORD)
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(JSON.toJSONString(checkjson))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            if(++requestCount<3)
                                checkRecord(checkjson);
                            else
                                showToast("网络异常请重试");
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            requestCount=0;
                            if(isUploadSuccess(s))
                            {
                                showToast("审核完成");
                                if(checkjson.getFBillstatus()==1)
                                    inspectPlan.setFPlanState(STATE_PLAN_INTIME);
                                else
                                    inspectPlan.setFPlanState(STATE_PLAN_NOTPASSCHECK);
                                managerNotification(inspectPlan);
                                if(fragChangeListener!=null)
                                    fragChangeListener.onUploadSuccess();
                                resetView();
                            }
                        }
                    });
    }

    /**
     * 创建CheckRecordJson
     * @return
     */
    public CheckRecordJson buildCheckRecord()
    {
        int FID=inspectProjectItems.get(0).getFID();    //获取的是点检记录的FID
        if(FID==0)
        {
            showToast("无当日需审核的点检记录");
            return null;
        }
        int checkerID= Integer.valueOf(DeviceManagerApp.getUserId());
        String checkDate= Utils.getCurDate();
        CheckRecordJson json
                =new CheckRecordJson();
        json.setFID(FID);
        if(inspectPlan!=null)
            json.setFPlanID(inspectPlan.getFID());
        json.setFBillstatus(1);
        json.setFCheckDate(checkDate);
        json.setFCheckerID(checkerID);
        json.setFBillType(1);
        return json;
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
            String dateString=i+formatDayOrMonth(1+i1)+formatDayOrMonth(i2);
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
     * 对每一个点检项目开始点检
     */
    private void toRecordInspect() {
        if(inspectPlan==null)
        {
            showToast("无相关点检计划");
            return;
        }
        Intent intent
                =new Intent(mContext, ProjectRecordActivity.class);
        intent.putExtra("planID",""+inspectPlan.getFID());       //保养计划ID
        intent.setData(Uri.parse("InspectItem"));
        if(inspectProjectItems!=null
                &&!inspectProjectItems.isEmpty())
        {
            intent.putExtra(KEY_PROJECT_ITEMS,JSON.toJSONString(inspectProjectItems));
        }
        startActivityForResult(intent, CODE_INSPECT_RECORD);
    }


    public InspectPlan getInspectPlan() {
        return inspectPlan;
    }

    public void setInspectPlan(InspectPlan inspectPlan) {
        this.inspectPlan = inspectPlan;
    }


    /**
     * 判断是否是空BarCode
     * @return
     */
    @Override
    boolean isEmptyBarCode() {
        if(TextUtils.isEmpty(barCode))
        {
            showToast("需录入设备条码");
            return true;
        }
        return false;
    }

    private void uploadWithJson(final String json)
    {
        showLog("上传>"+json);
        OkHttpUtils
                .postString()
                .url(FURJA_SEND_INSPECTRECORD)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i)
                    {
                        if(++requestCount>2)
                        {
                            e.printStackTrace();
                            showToast("网络异常请重试");
                        }
                        else
                            uploadWithJson(json);
                    }
                    @Override
                    public void onResponse(String response, int i)
                    {
                        requestCount=0;
                        if(isUploadSuccess(response))
                        {
                            showToast("上传成功");
                            inspectPlan.setFPlanState(STATE_PLAN_NEEDCHECK);
                            managerNotification(inspectPlan);
                            if(fragChangeListener!=null)
                                fragChangeListener.onUploadSuccess();
                            resetView();
                        }
                        else
                            showLog("上传失败");
                    }
                });
    }

    /**
     * 重置视图
     */
    private void resetView() {
        inspectPlan=null;
        inspectProjectItems=Collections.EMPTY_LIST;
        adapter.setNewData(Collections.EMPTY_LIST);
    }

    /**
     * 对本页表单的部分控件进行判空处理
     * @return
     */
    private boolean checkFormHasNull()
    {
        if (isEmptyBarCode())
            return true;
        return false;
    }

    @Override
    void setCurBarCode(String barCode) {
        setBarCode(barCode);
//        showToast("获取该设备的点检计划");
        requestInspectPlan();
    }

    @Override
    void resume() {
        if(fragChangeListener!=null)
            fragChangeListener.showDeviceInfo();
    }


    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CODE_INSPECT_RECORD
                &&resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                String projectStrings
                        =data.getStringExtra(KEY_PROJECT_ITEMS);
                inspectProjectItems
                        =JSON.parseArray(projectStrings,InspectProjectItem.class);
                showLog("已保存点检项目:"+projectStrings);
            }
        }
    }
}

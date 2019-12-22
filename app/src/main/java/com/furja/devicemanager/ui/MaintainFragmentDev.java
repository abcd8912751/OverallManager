package com.furja.devicemanager.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.furja.devicemanager.DeviceManagerApp;
import com.furja.overall.R;
import com.furja.devicemanager.beans.DetailItem;
import com.furja.devicemanager.beans.MaintainPlan;
import com.furja.devicemanager.beans.MaintainProjectItem;
import com.furja.devicemanager.json.MaintainRecordJson;
import com.furja.devicemanager.json.CheckRecordJson;
import com.furja.devicemanager.utils.JSONParser;
import com.furja.devicemanager.utils.Utils;
import com.furja.devicemanager.view.BasePlanAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static android.app.Activity.RESULT_OK;
import static com.furja.devicemanager.DeviceManagerApp.isChecker;
import static com.furja.devicemanager.utils.Constants.CODE_MAINTAIN_RECORD;
import static com.furja.devicemanager.utils.Constants.FURJA_GET_MAINTAINPLAN;
import static com.furja.devicemanager.utils.Constants.FURJA_SEND_CHECKRECORD;
import static com.furja.devicemanager.utils.Constants.FURJA_SEND_MAINTAINRECORD;
import static com.furja.devicemanager.utils.Constants.KEY_PROJECT_ITEMS;
import static com.furja.devicemanager.utils.Constants.STATE_PLAN_INTIME;
import static com.furja.devicemanager.utils.Constants.STATE_PLAN_NEEDCHECK;
import static com.furja.devicemanager.utils.Constants.STATE_PLAN_NOTPASSCHECK;
import static com.furja.devicemanager.utils.Utils.isUploadSuccess;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;
/**
 * RadioButton维护 对应的Fragment
 */
public class MaintainFragmentDev extends DevBaseFragment {
    @BindView(R.id.label_maintainLevel)
    TextView text_maintainLevel;
    @BindView(R.id.btn_maintain)
    Button btn_maintain;
    @BindView(R.id.recycler_maintain)
    RecyclerView maintain_Recycler;
    BasePlanAdapter adapter;
    @BindView(R.id.begin_content)
    TextView beginContent;
    @BindView(R.id.end_content)
    TextView endContent;
    String[] planTitles
            =new String[]{"设备名称","设备代码","规格型号","所在部门","保养计划周期",
                "计划开始日期","计划结束日期","最近保养日期","下次保养日期","当日计划状态"};
    private String barCode;
    private String cur_planID;
    private int requestCount=0;
    private List<MaintainPlan> planList;
    private List<MaintainProjectItem> maintainProjectItems;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_maintain,container,false);
        ButterKnife.bind(this,view);
        adapter=new BasePlanAdapter(null);
        DividerItemDecoration itemDecoration
                =new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL);
        maintain_Recycler.addItemDecoration(itemDecoration);
        maintain_Recycler.setLayoutManager(new LinearLayoutManager(mContext));
        maintain_Recycler.setAdapter(adapter);
        setRegionDate();
        if(isChecker())
        {
            btn_maintain.setText(R.string.check_button_label);
        }
        if(planList==null)
        {
            planList=new ArrayList<MaintainPlan>();
        }
        else
        {
            showMaintainPlanByIndex(0,true);
            try {
                this.barCode=planList.get(0).getFDeviceID()+"";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    /**
     * 请求保养计划数据
     */
    private void requestMaintainPlan()
    {
        if(isEmptyBarCode())
        {
            showToast("请录入设备条码");
            return;
        }
        OkHttpUtils
                .get()
                .url(FURJA_GET_MAINTAINPLAN)
                .addParams("devCode",barCode)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        showLog(e.toString());
                        if (++requestCount<3)
                            requestMaintainPlan();
                        else
                            showToast("网络异常请稍候重试");
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        requestCount=0;
                        try {
                            s= JSONParser.parserArray(s);
                            planList= JSON.parseArray(s,MaintainPlan.class);

                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast("无对应的保养计划");
                            resetView();
                        }
                        if(planList!=null&&!planList.isEmpty())
                            showPlanList();
                        else
                        {
                            btn_maintain.setEnabled(false);
                            showToast("无对应的保养计划");
                            resetView();
                        }
                    }
                });
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
     *  从服务器获取到保养计划后装载UI
     */
    private void showPlanList() {
        btn_maintain.setEnabled(true);
        showMaintainPlanByIndex(0, false);
    }

    private void showMaintainPlanByIndex(int index, boolean
                                         showDeviceInfo) {
        MaintainPlan plan=planList.get(index);
        if(plan.getFID().equals(getCur_planID()))
            return;
        setCur_planID(plan.getFID());
        int addend=4;
        if(showDeviceInfo)
            addend=0;   //如果显示设备信息则不设间隔
        maintainProjectItems=null;      //更换保养计划后重置保养项目Item
        text_maintainLevel.setText("保养类别:"+plan.getFMaintainType());
        String[] strings=plan.toStringArray();
        List<DetailItem> items=new ArrayList<>();
        for(int i=0;i+addend<planTitles.length;i++)
        {
            DetailItem detailItem=new DetailItem(planTitles[i+addend],strings[i+addend]);
            items.add(detailItem);
        }
        adapter.setNewData(items);
        setButtonView(plan);
    }

    /**
     *根据MaintainPlan的状态设定状态
     * @param plan
     */
    private void setButtonView(MaintainPlan plan) {
        btn_maintain.setText("上传保养记录");
        if(plan.getFPlanState()==STATE_PLAN_NEEDCHECK)
        {
            btn_maintain.setText("审核");
            if(DeviceManagerApp.isChecker())
                btn_maintain.setEnabled(true);
            else
                btn_maintain.setEnabled(false);
        }
        else if(plan.getFPlanState()==STATE_PLAN_INTIME)
        {
            btn_maintain.setText("计划如期执行,无需重复操作");
            btn_maintain.setEnabled(false);
        }
        else if(DeviceManagerApp.isChecker())
        {
            btn_maintain.setEnabled(false);
        }
    }

    /**
     * 点击保养级别或点击上传保养记录
     * @param view
     */
    @OnClick({R.id.label_maintainLevel,R.id.btn_maintain})
    public void onClick(View view)
    {
        if(isEmptyBarCode())
        {
            showToast("请先录入设备条码");
            return;
        }
        if(view.getId()==R.id.label_maintainLevel)
            showLevelPopupWindow();
        else
        {   //提交保养记录
            if(maintainProjectItems==null||maintainProjectItems.isEmpty())
            {
                if(DeviceManagerApp.isChecker())
                    showToast("请点击保养项目进行审核");
                else
                    showToast("请点击保养项目进行记录");
                toRecordMaintain();
                return;
            }
            showConfirmDialog();
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
                        MaintainPlan plan = getCurMaintainPlan();
                        if (plan != null)
                        {
                            if(isChecker)
                            {
                                CheckRecordJson json=buildCheckRecord();
                                if(json!=null)
                                {
                                    json.setFCheckPass(1);
                                    checkRecord(json);
                                }
                            }
                            else
                            {
                                MaintainRecordJson json
                                        = new MaintainRecordJson(plan);
                                json.setFBeginDate(beginContent.getText().toString());
                                json.setFEndDate(endContent.getText().toString());
                                if(maintainProjectItems!=null&&!maintainProjectItems.isEmpty())
                                {
                                    int FID=maintainProjectItems.get(0).getFID();
                                    json.setFID(FID);
                                }
                                json.setProjectItems(maintainProjectItems);
                                postMaintainRecord(JSON.toJSONString(json));
                            }
                        }
                        dialog.cancel();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(isChecker)
                        {
                            CheckRecordJson json=buildCheckRecord();
                            if(json!=null)
                            {
                                json.setFCheckPass(0);
                                json.setFBillstatus(0);
                                checkRecord(json);
                            }
                        }
                        dialog.cancel();
                    }
                }).build().show();
    }

    /**
     * 审核保养记录
     */
    private void checkRecord(final CheckRecordJson json) {
        final MaintainPlan plan=getCurMaintainPlan();
        if(plan.getFPlanState()!=STATE_PLAN_NEEDCHECK)
        {
            showToast("该计划无需要审核的保养记录");
        }
        else
        {
            OkHttpUtils
                    .postString()
                    .url(FURJA_SEND_CHECKRECORD)
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .content(JSON.toJSONString(json))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            if(++requestCount<3)
                                checkRecord(json);
                            else
                                showToast("网络异常请重试");
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            requestCount=0;
                            if(isUploadSuccess(s))
                            {
                                showToast("审核完成");
                                if(json.getFBillstatus()==1)
                                    plan.setFPlanState(STATE_PLAN_INTIME);
                                else
                                    plan.setFPlanState(STATE_PLAN_NOTPASSCHECK);
                                Utils.managerNotification(plan);
                                resetView();
                                if(fragChangeListener!=null)
                                    fragChangeListener.onUploadSuccess();
                            }
                        }
                    });
        }
    }

    @Nullable
    private CheckRecordJson buildCheckRecord() {
        int FID=maintainProjectItems.get(0).getFID();    //获取的是保养记录的FID
        if(FID==0)
        {
            showToast("无当日保养记录");
            return null;
        }
        int checkerID= Integer.valueOf(DeviceManagerApp.getUserId());
        String checkDate= Utils.getCurDate();
        CheckRecordJson json
                =new CheckRecordJson();
        json.setFID(FID);
        try {
            if(!TextUtils.isEmpty(cur_planID))
                json.setFPlanID(Integer.valueOf(cur_planID));
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.setFBillstatus(1);
        json.setFCheckDate(checkDate);
        json.setFCheckerID(checkerID);
        json.setFBillType(2);
        return json;
    }

    private void postMaintainRecord(final String json) {
        showLog("MaintainRecordJson:>"+JSON.toJSONString(json));
        OkHttpUtils
                .postString()
                .url(FURJA_SEND_MAINTAINRECORD)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if(++requestCount<2)
                        {
                            postMaintainRecord(json);
                        }
                        else
                            showToast("网络异常请重试");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        requestCount=0;
                        showLog(getClass()+s);
                        if(isUploadSuccess(s))
                        {
                            showToast("上传成功");
                            MaintainPlan plan=getCurMaintainPlan();
                            plan.setFPlanState(STATE_PLAN_NEEDCHECK);
                            Utils.managerNotification(plan);
                            resetView();
                            if(fragChangeListener!=null)
                                fragChangeListener.onUploadSuccess();
                        }
                    }


                });
    }

    /**
     * 获取当前保养计划
     * @return
     */
    @Nullable
    private MaintainPlan getCurMaintainPlan() {
        MaintainPlan plan=null;
        if(planList.isEmpty())
            return plan;
        for(MaintainPlan maintainPlan:planList)
        {
            if(maintainPlan.getFID().equals(cur_planID))
            {
                plan=maintainPlan;
                break;
            }
        }
        return plan;
    }

    /**
     * 重置视图，重新录入设备条码以
     */
    private void resetView() {
        text_maintainLevel.setText("保养类别");
        adapter.setNewData(null);
    }

    /**
     * 点击保养项目或备件耗材时使用
     * @param
     */
    @OnClick({R.id.maintain_item,R.id.spare_parts})
    public void onRecordMaintain(View view)
    {
        if(isEmptyBarCode())
        {
            showToast("请先录入设备条码");
            return;
        }
        if(TextUtils.isEmpty(cur_planID))
        {
            showToast("无可选保养计划");
            return;
        }
        toRecordMaintain();
    }

    /**
     * 去记录保养项目
     */
    private void toRecordMaintain() {
        Intent intent=new Intent(mContext, ProjectRecordActivity.class);
        intent.putExtra("planID",cur_planID);       //保养计划ID
        intent.setData(Uri.parse("MaintainItem"));
        if(maintainProjectItems!=null
                &&!maintainProjectItems.isEmpty())
        {
            intent.putExtra(KEY_PROJECT_ITEMS, JSON.toJSONString(maintainProjectItems));
        }
        startActivityForResult(intent, CODE_MAINTAIN_RECORD);
    }

    /**
     * 显示选择 保养级别的 PopupWindow
     */
    private void showLevelPopupWindow()
    {
        final ListPopupWindow listPopupWindow=new ListPopupWindow(mContext);
        List<String> candidateStrings=new ArrayList<String>();
        if(planList==null||planList.isEmpty())
        {
            return;
        }
        for(MaintainPlan plan:planList)
            candidateStrings.add(plan.getFMaintainType());
        ArrayAdapter adapter
                =new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,
                candidateStrings);
        listPopupWindow.setAnchorView(text_maintainLevel);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        listPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                showMaintainPlanByIndex(position, false);
                listPopupWindow.dismiss();
            }
        });
        listPopupWindow.show();
    }


    /**
     * 本页面二维码为空
     * @return
     */
    @Override
    boolean isEmptyBarCode() {
        return TextUtils.isEmpty(barCode);
    }

    /**
     * 设置二维码
     * @param barCode
     */
    @Override
    void setCurBarCode(String barCode) {
        this.barCode=barCode;
        requestMaintainPlan();
    }

    public String getCur_planID() {
        return cur_planID;
    }

    public void setCur_planID(String cur_planID) {
        this.cur_planID = cur_planID;
    }

    public List<MaintainPlan> getPlanList() {
        return planList;
    }

    public void setPlanList(List<MaintainPlan> planList) {
        this.planList = planList;
    }
    @Override
    public void resume() {
        if(fragChangeListener!=null)
            fragChangeListener.showDeviceInfo();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&requestCode==CODE_MAINTAIN_RECORD)
        {
            if(data!=null)
            {
                String projectStrings
                        =data.getStringExtra(KEY_PROJECT_ITEMS);
                maintainProjectItems
                        =JSON.parseArray(projectStrings,MaintainProjectItem.class);
                showLog("已保存保养项目:"+projectStrings);
            }
        }
    }
}

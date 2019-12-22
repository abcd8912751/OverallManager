package com.furja.devicemanager.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.furja.overall.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_CHECK;
import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_MYAPPLY;
import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_MYREPAIR;
import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_NOTEND;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * 维修模块
 */

public class RepairFragmentDev extends DevBaseFragment {
    public String barCode;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_repair,container,false);
        ButterKnife.bind(this,view);
        showLog("Dev天亮了");

        return view;
    }

    @OnClick({R.id.text_applyRepair,R.id.text_repairOrder,R.id.text_repairCheck
              ,R.id.text_ownRepairReport,R.id.text_ownRepairRecord })
    public void onOptionClick(View view)
    {
        Intent intent=new Intent();
        switch (view.getId())
        {
                //报修
            case R.id.text_applyRepair:
                if(isEmptyBarCode())
                {
                    showToast("请先录入设备条码");
                    return;
                }
                else
                {
                    ApplyRepairFragmentDev applyRepairFragment
                            =new ApplyRepairFragmentDev();
                    fragChangeListener
                            .transferFragment(applyRepairFragment,"ApplyRepairFragmentDev");
                }
                break;
                //维修工单
            case R.id.text_repairOrder:
                RepairOrderFragmentDev repairOrderFragment
                        =new RepairOrderFragmentDev();
                repairOrderFragment.setFilterItemID(FILTER_REPAIRORDER_NOTEND);
                fragChangeListener
                        .transferFragment(repairOrderFragment,"RepairOrderFragmentDev");
                break;
                //维修验收
            case R.id.text_repairCheck:
                RepairOrderFragmentDev repairCheckFragment
                        =new RepairOrderFragmentDev();
                repairCheckFragment.setFilterItemID(FILTER_REPAIRORDER_CHECK);
                fragChangeListener
                        .transferFragment(repairCheckFragment,"RepairOrderFragmentDev");
                break;
            //我的报修记录
            case R.id.text_ownRepairReport:
                RepairOrderFragmentDev repairReportFragment
                        =new RepairOrderFragmentDev();
                repairReportFragment.setFilterItemID(FILTER_REPAIRORDER_MYAPPLY);
                fragChangeListener
                        .transferFragment(repairReportFragment,"RepairOrderFragmentDev");
                break;
            //我的维修记录
            case R.id.text_ownRepairRecord:
                RepairOrderFragmentDev repairRecordFragment
                        =new RepairOrderFragmentDev();
                repairRecordFragment.setFilterItemID(FILTER_REPAIRORDER_MYREPAIR);
                fragChangeListener
                        .transferFragment(repairRecordFragment,"RepairOrderFragmentDev");
                break;
        }
    }



    @Override
    boolean isEmptyBarCode() {
        return TextUtils.isEmpty(barCode);
    }

    @Override
    void setCurBarCode(String barCode) {
        this.barCode=barCode;
    }

    @Override
    void resume() {
        fragChangeListener.showDeviceInfo();
        fragChangeListener.setDisplayHomeAsUpEnable(false);
        fragChangeListener.setRadioGroupVisibility(View.VISIBLE);
    }


    void resetView() {
        setCurBarCode("");
    }
}

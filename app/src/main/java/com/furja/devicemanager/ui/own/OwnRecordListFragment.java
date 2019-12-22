package com.furja.devicemanager.ui.own;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.overall.R;
import com.furja.devicemanager.beans.RecordDetailItem;
import com.furja.devicemanager.beans.RepairState;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.furja.utils.Utils.showLog;

/**
 * 点击 我的报修/维修/派工/验证记录时跳转至此，将显示相应维修单列表List
 */

public class OwnRecordListFragment extends BaseRecordFragment {
    @BindView(R.id.recycler_recordList)
    RecyclerView recycler_recordList;
    @BindView(R.id.refresh_ownRecord)
    SmartRefreshLayout refreshLayout;
    RecordListAdapter recordListAdapter;
    String recordTag="";
    List<RecordDetailItem> detailItems;
    int requestCount=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_ownrecord_list,container,false);
        ButterKnife.bind(this,view);
        recordListAdapter = new RecordListAdapter(R.layout.ownrecord_list_item);
        recycler_recordList.setLayoutManager(new LinearLayoutManager(mContext));
        recordListAdapter.bindToRecyclerView(recycler_recordList);
        recordListAdapter.setEmptyView(R.layout.load_repairorder_view);
        detailItems=new ArrayList<>();
        refreshLayout.setRefreshHeader(new MaterialHeader(mContext).setShowBezierWave(false));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

            }
        });
        //子项目点击事件
        recordListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                showLog("点击了:"+position);
                if(!detailItems.isEmpty())
                {
                    RecordDetailItem item=detailItems.get(position);
                    fragCommunionListener.viewDetail(item);
                }
            }
        });
        return view;
    }
    /**
     *Recycleview的适配器
     */
    public class RecordListAdapter extends BaseQuickAdapter<RecordDetailItem,BaseViewHolder>
    {
        RepairState repairState=new RepairState();
        public RecordListAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, RecordDetailItem item) {
            helper.setText(R.id.order_title,item.getDeviceName());
            helper.setText(R.id.order_deviceSettingPlace,"设备条码: "+item.getDeviceCode());
            String recordTime=item.getRepairDate();
            if(recordTag.contains("report"))
                recordTime=item.getReportDate();
            else if(recordTag.contains("dispatch"))
                recordTime=item.getDispatchDate();
                else if(recordTag.contains("confirmList"))
                    recordTime=item.getConfirmDate();
            helper.setText(R.id.recordRepairDate,"记录时间: "+recordTime);
            helper.setText(R.id.order_state," "+item.getRepairState());
            Resources resources=helper.itemView.getResources();
            int stateColor= Color.BLACK;
            switch (repairState.getStateIndex(item.getRepairState()))
            {
                case 0:
                    stateColor=resources.getColor(R.color.radioText_color);
                    break;
                case 1:
                    stateColor=resources.getColor(R.color.color_AccentBtn);
                    break;
                case 2:
                    stateColor=resources.getColor(R.color.color_stroke);
                    break;
                case 3:
                    stateColor=resources.getColor(R.color.holo_purple);
                    break;
                case 4:
                    stateColor=resources.getColor(R.color.holo_text);
                    break;
            }
            helper.setTextColor(R.id.order_state,stateColor);
            
        }
    }




    public List<RecordDetailItem> getDetailItems() {
        return detailItems;
    }

    public void setDetailItems(List<RecordDetailItem> detailItems) {
        this.detailItems = detailItems;
    }

    public String getRecordTag() {
        return recordTag;
    }

    public void setRecordTag(String recordTag) {
        this.recordTag = recordTag;
    }
}

package com.furja.devicemanager.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.furja.devicemanager.DeviceManagerApp;
import com.furja.overall.R;
import com.furja.devicemanager.beans.RepairOrderItem;
import com.furja.devicemanager.utils.JSONParser;
import com.furja.devicemanager.view.RepairOrderAdapter;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_CHECK;
import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_MYAPPLY;
import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_MYREPAIR;
import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_NOTEND;
import static com.furja.devicemanager.utils.Constants.FILTER_REPAIRORDER_REPAIR;
import static com.furja.devicemanager.utils.Constants.FURJA_GET_REPAIRORDERLIST;
import static com.furja.utils.Utils.showLog;
import static com.furja.devicemanager.view.MyDecoration.VERTICAL_LIST;

/**
 * 维修单Fragment
 */
public class RepairOrderFragmentDev extends DevBaseFragment {
    @BindView(R.id.recycler_repairOrder)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_repairOrder)
    SmartRefreshLayout refreshLayout;
    RepairOrderAdapter repairOrderAdapter;
    int requestCount=0;
    int filterItemID;
    List<RepairOrderItem> curItems=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_repair_order, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    /**
     * 加载数据
     */
    private void initData() {
        repairOrderAdapter
                =new RepairOrderAdapter();
        recyclerView.setAdapter(repairOrderAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        repairOrderAdapter.bindToRecyclerView(recyclerView);
        repairOrderAdapter.setEmptyView(R.layout.load_repairorder_view);
        //下拉刷新模块
        refreshLayout.setRefreshHeader(new MaterialHeader(mContext).setShowBezierWave(false));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                requestOrderList();
            }
        });
        repairOrderAdapter
                .setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        RepairDetailFragmentDev repairDetailFragment = new RepairDetailFragmentDev();
                        repairDetailFragment
                                .setCur_orderItem(repairOrderAdapter.getData().get(position));
                        repairDetailFragment.setFilterCode(filterItemID);
                        fragChangeListener.transferFragment(repairDetailFragment,"SCENE_4");
                    }
                });
    }

    /**
     * 请求维修单
     */
    private void requestOrderList() {
        OkHttpUtils
                .get()
                .url(FURJA_GET_REPAIRORDERLIST)
                .addParams("filterCode",filterItemID+"")
                .addParams("userID", DeviceManagerApp.getUserId())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        requestCount++;
                        if(requestCount<2)
                            requestOrderList();
                        else {
                            e.printStackTrace();
                            repairOrderAdapter.setUpFetching(false);
                            repairOrderAdapter.setEmptyView(R.layout.offline_empty_view,recyclerView);
                        }
                        refreshLayout.finishRefresh();
                    }

                    @Override
                    public void onResponse(String s, int i)
                    {
                        try
                        {
                            refreshLayout.finishRefresh();
                            requestCount=0;
                            s= JSONParser.parserArray(s);
                            List<RepairOrderItem> items
                                    = JSON.parseArray(s,RepairOrderItem.class);
                            setCurItems(items);
                            if(items==null||items.isEmpty())
                            {
                                showLog("未获取到维修单");
                                repairOrderAdapter.setNewData(items);
                                repairOrderAdapter.setEmptyView(R.layout.empty_view,recyclerView);
                            }
                            else
                            {
                                DividerItemDecoration decoration
                                        =new DividerItemDecoration(mContext,VERTICAL_LIST);
                                recyclerView.addItemDecoration(decoration);
                                repairOrderAdapter.setNewData(items);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            repairOrderAdapter.setEmptyView(R.layout.empty_view,recyclerView);
                        }
                    }
                });
    }





    /**
     * 显示筛选对话框
     */
    private void showFilterDialog() {
        showLog("筛选");
        AlertDialog.Builder builder
                =new AlertDialog.Builder(mContext);
        builder.setTitle("设置筛选条件");
        CharSequence[] charSequences=new CharSequence[]{"维修派工","我要维修","正在维修","维修验证","不作筛选"};
        builder.setSingleChoiceItems(charSequences, filterItemID, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                filterItemID=i;
                effectFilter(i);
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    /**
     * 使筛选器生效
     * @param i
     */
    private void effectFilter(int i)
    {
        List<RepairOrderItem> items
                =getCurItems(),newData=new ArrayList<RepairOrderItem>();
        showLog("筛选:"+i);
        switch (i)
        {
            case FILTER_REPAIRORDER_REPAIR:
                for(RepairOrderItem item:items)
                {
                    if(item.getFRepairState()==FILTER_REPAIRORDER_REPAIR)
                        newData.add(item);
                }
                break;
            case FILTER_REPAIRORDER_CHECK:
                for(RepairOrderItem item:items)
                {
                    if(item.getFRepairState()==FILTER_REPAIRORDER_CHECK)
                        newData.add(item);
                }
                break;
            case FILTER_REPAIRORDER_NOTEND: //工单未验收就会流到这里
                newData=items;
                break;
            case FILTER_REPAIRORDER_MYAPPLY:
                for(RepairOrderItem item:items)
                {
                    if(item.getFRepairState()==FILTER_REPAIRORDER_CHECK)
                        newData.add(item);
                }
                break;
            case FILTER_REPAIRORDER_MYREPAIR:

                break;
        }
        repairOrderAdapter.setNewData(newData);
        if(newData.isEmpty())
            repairOrderAdapter.setEmptyView(R.layout.empty_view);
    }


    public int getFilterItemID() {
        return filterItemID;
    }

    public void setFilterItemID(int filterItemID) {
        this.filterItemID = filterItemID;
    }

    @Override
    boolean isEmptyBarCode() {
        return false;
    }

    @Override
    void setCurBarCode(String barCode) {
    }

    @Override
    void resume() {
        fragChangeListener.hideDeviceInfo();
        fragChangeListener.setDisplayHomeAsUpEnable(true);
        fragChangeListener.setRadioGroupVisibility(View.GONE);
        requestOrderList();
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public List<RepairOrderItem> getCurItems() {
        return curItems;
    }

    public void setCurItems(List<RepairOrderItem> curItems) {
        this.curItems = curItems;
    }



}

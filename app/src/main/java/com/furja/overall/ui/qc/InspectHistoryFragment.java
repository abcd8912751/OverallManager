package com.furja.overall.ui.qc;

import android.accounts.NetworkErrorException;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.common.BaseFragment;
import com.furja.common.JustCallBack;
import com.furja.common.RecyclerBottomSheetFragment;
import com.furja.common.WrapLinearLayoutManager;
import com.furja.iqc.json.ApplyCheckOrder;
import com.furja.overall.FurjaApp;
import com.furja.overall.R;
import com.furja.overall.beans.InspectHistoryLog;
import com.furja.utils.RetrofitBuilder;
import com.furja.utils.RetrofitHelper;
import com.furja.utils.RetryWhenUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import org.json.JSONException;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.sentry.Sentry;
import static com.furja.utils.Constants.INTERNET_ABNORMAL;
import static com.furja.utils.Constants.SERVER_ABNORMAL;
import static com.furja.utils.Constants.VERTX_TEST_URL;
import static com.furja.utils.Constants.getVertxUrl;
import static com.furja.utils.Utils.intOf;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;
import static com.furja.utils.Utils.textOf;
import static com.scwang.smartrefresh.layout.constant.RefreshState.Refreshing;

public class InspectHistoryFragment extends BaseFragment {
    RecyclerView recyclerView;
    SmartRefreshLayout refreshLayout;
    InspectHistoryAdapter historyAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_inspecthistory, container, false);
        recyclerView=root.findViewById(R.id.recycler_view);
        refreshLayout=root.findViewById(R.id.refresh_recycler);
        recyclerView.setLayoutManager(WrapLinearLayoutManager.wrap(mContext));
        historyAdapter =new InspectHistoryAdapter(R.layout.layout_inspecthistory_item);
        historyAdapter.bindToRecyclerView(recyclerView);
        refreshLayout.setRefreshHeader(new MaterialHeader(mContext).setShowBezierWave(false));
        refreshLayout.setOnRefreshListener(refreshlayout->{
            acquireInspectHistory();
        });
        historyAdapter.setEmptyView(R.layout.loading_empty_view,recyclerView);
        acquireInspectHistory();
        historyAdapter.setOnItemClickListener((adapter, view, position) -> {
            showSheetDialog(historyAdapter.getItem(position));
        });
        return root;
    }


    private void acquireInspectHistory() {
        RetrofitHelper helper= RetrofitBuilder.getHelperByUrl(getVertxUrl());
        helper.getInspectHistory(FurjaApp.getUserName())
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWhenUtils.create())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->{
                    if(response.getCode()>0)
                        historyAdapter.setNewData(response.getResult());
                    else {
                        showToast(response.getMessage());
                        historyAdapter.setEmptyView(R.layout.nodata_empty_view, recyclerView);
                    }
                    if(refreshLayout.getState()==Refreshing)
                        refreshLayout.finishRefresh();
                },error->{
                    if(error instanceof JSONException)
                        showToast(SERVER_ABNORMAL);
                    else {
                        showToast(INTERNET_ABNORMAL);
                        error.printStackTrace();
                    }
                    if(refreshLayout.getState()==Refreshing)
                        refreshLayout.finishRefresh();
                    historyAdapter.setEmptyView(R.layout.empty_offline_layout,recyclerView);
                });
    }

    private void showSheetDialog(InspectHistoryLog item) {
        RecyclerBottomSheetFragment<InspectHistoryLog> sheetFragment=
                new RecyclerBottomSheetFragment<>(item.getItems());
        sheetFragment.setTitle("检验详情");
        sheetFragment.show(getFragmentManager(),"InspectHistoryFragmentSheet");
    }


    public static class InspectHistoryAdapter extends BaseQuickAdapter<InspectHistoryLog, BaseViewHolder>{

        public InspectHistoryAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, InspectHistoryLog item) {
            helper.setText(R.id.item_title,item.getFMaterialName());
            helper.setText(R.id.item_supplier,item.getFSupplier());
            helper.setText(R.id.item_content,"检验单号: "+item.getFInspectBillNo()+" 检验数量: "+item.getFInspectQty()+item.getFUnitName());
            String inspectDate=textOf(item.getFInspectDate());
            String inspectResult=item.getFInspectResult();
            if(inspectDate.length()>18)
                inspectDate=inspectDate.substring(0,19);
            helper.setText(R.id.item_note,"检验时间: "+inspectDate);
            helper.setText(R.id.chip_policy,item.getFUsePolicy());
            Resources resources = helper.itemView.getResources();
            TextView itemStatus = helper.getView(R.id.item_status);
            itemStatus.setText(item.getFInspectResult());
            if("合格".equals(inspectResult))
                itemStatus.setTextColor(resources.getColor(R.color.colorAccent));
            else
                itemStatus.setTextColor(Color.MAGENTA);
        }
    }
}

package com.furja.devicemanager.ui.own;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.furja.overall.R;
import com.furja.devicemanager.beans.RecordDetailItem;
import com.furja.devicemanager.view.RecyclerDetailAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.furja.devicemanager.view.MyDecoration.VERTICAL_LIST;

/**
 * 单个维修单的详情
 */

public class OwnRepairDetailFragment extends BaseRecordFragment {
    @BindView(R.id.recycler_repairDetail)
    RecyclerView recyclerView;
    @BindView(R.id.evaluate_layout)
    LinearLayout evaluateLayout;
    @BindView(R.id.content_evaluate)
    TextView evaluateContent;
    RecyclerDetailAdapter recyclerAdapter;
    RecordDetailItem detailItem;
    //在这里加上保养计划的实例
    boolean isInitFinished=false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.frag_repair_comment,container,false);
        ButterKnife.bind(this,view);

        recyclerAdapter=new RecyclerDetailAdapter(R.layout.repairdetail_layout,true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration decoration
                =new DividerItemDecoration(mContext,VERTICAL_LIST);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(recyclerAdapter);
        isInitFinished=true;
        evaluateContent.setMovementMethod(ScrollingMovementMethod.getInstance());//textResult滚动


        if(detailItem !=null)
        {
            recyclerAdapter.setNewData(detailItem.testStringList());
            showEvaluateLayout();
        }
        return view;
    }


    public RecordDetailItem getDetailItem() {
        return detailItem;
    }

    public void setDetailItem(final RecordDetailItem detailItem) {
        this.detailItem = detailItem;
//        showEvaluateLayout();
    }

    private void showEvaluateLayout() {
        if(detailItem==null||!isInitFinished)
            return;
        if(TextUtils.isEmpty(detailItem.getConfirmPersonnel()))
            evaluateLayout.setVisibility(View.GONE);
        else {
            if(!TextUtils.isEmpty(detailItem.getEvaluateDetail()))
                evaluateContent.setText(detailItem.getEvaluateDetail());
            else
                evaluateContent.setText("所评星级如下所示：");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(detailItem !=null)
        {
            recyclerAdapter.setNewData(detailItem.toStringList());
            showEvaluateLayout();
        }

    }




}

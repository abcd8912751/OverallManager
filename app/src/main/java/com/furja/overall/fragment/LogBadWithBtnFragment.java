package com.furja.overall.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.furja.common.BaseFragment;
import com.furja.overall.R;
import com.furja.overall.beans.WorkOrderInfo;
import com.furja.contract.LogBadWithBnContract;
import com.furja.presenter.BadLogWithBtnPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.furja.utils.Utils.showLog;

/**
 * Created by zhangmeng on 2017/1/3.
 */

public class LogBadWithBtnFragment extends BaseFragment implements LogBadWithBnContract.View {
    @BindView(R.id.recycler_marker)
    RecyclerView markerRecyclerView;
    //上述MarkerRecyclerView与数据交互使用的presenter
    private BadLogWithBtnPresenter mBadLogWithBtnPresenter;
    @BindView(R.id.btn_redo_btnFrag)
    ImageButton redo_button;
    @BindView(R.id.btn_undo_btnFrag)
    ImageButton undo_button;
    @BindView(R.id.btn_submit_btnFrag)
    ImageButton submit_button;
    @BindView(R.id.btn_edit_btnFrag)
    ImageButton edit_button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_badlogwithbtn,container,false);
        ButterKnife.bind(this,view);

        markerRecyclerView.setLayoutManager(new GridLayoutManager(mContext,4));
        mBadLogWithBtnPresenter =new BadLogWithBtnPresenter(this);


        return view;
    }


    @Override
    public void setRecyclerAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        markerRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setButtonClickListener(View.OnClickListener buttonClickListener) {
        redo_button.setOnClickListener(buttonClickListener);
        undo_button.setOnClickListener(buttonClickListener);
        edit_button.setOnClickListener(buttonClickListener);
        submit_button.setOnClickListener(buttonClickListener);
    }

    //转移焦点
    @Override
    public void changeFocus() {
        undo_button.performClick();
    }


    public void syncAndUpdateBtnBadData(WorkOrderInfo workOrderInfo) {
        if(mBadLogWithBtnPresenter!=null)
            mBadLogWithBtnPresenter.syncAndUpdateData(workOrderInfo);
    }


    /**
     * 将工单信息及
     */
    public void syncData()
    {
        mBadLogWithBtnPresenter.syncData();
        showLog("数据同步完成");
    }

}

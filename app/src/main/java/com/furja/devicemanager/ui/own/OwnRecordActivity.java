package com.furja.devicemanager.ui.own;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;

import com.furja.overall.R;
import com.furja.devicemanager.beans.RecordDetailItem;

/**
 * 点击 我的报修/维修/派工/验证记录时跳转至此，将显示符合条件的列表及Detail
 */

public class OwnRecordActivity extends AppCompatActivity implements BaseRecordFragment.FragCommunionListener {
    OwnRecordListFragment recordListFragment;
    OwnRepairDetailFragment detailFragment;
    RecordDetailItem detailItem;
    boolean isInDetailFragView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_record);
        recordListFragment=new OwnRecordListFragment();
        detailFragment=new OwnRepairDetailFragment();
        Intent intent=getIntent();

        if(intent!=null)
        {
            String tag=intent.getDataString();//通过tag确定是通过哪个入口进入
            if(!TextUtils.isEmpty(tag))
            {
                detailItem=new RecordDetailItem();
                viewDetail(detailItem);
//                showViewByTag(tag);
            }
        }

    }

    /**
     * tag为1,2,3,4,详见Constants类
     * @param tag
     */
    private void showViewByTag(String tag) {
        int tagIndex=Integer.valueOf(tag);
        switch (tagIndex)
        {
            case 1:
                recordListFragment.setRecordTag("reportList");
                setBarTitle("我的报修记录");
                break;
            case 2:
                recordListFragment.setRecordTag("repairList");
                setBarTitle("我的维修记录");
                break;
            case 3:
                recordListFragment.setRecordTag("dispatchList");
                setBarTitle("我的派工记录");
                break;
            case 4:
                recordListFragment.setRecordTag("confirmList");
                setBarTitle("我的验证记录");
                break;
        }
        transferFragment(null,recordListFragment);
    }





    /**
     * 切换Fragment
     * @param to
     */
    public void transferFragment(BaseRecordFragment from, BaseRecordFragment to)
    {
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction fts = fm.beginTransaction();
        if(from!=null)
        {
            if(from==to)
                return;
            if(from.isAdded())
                fts.hide(from);
        }
        if(to.isAdded())
        {
            fts.show(to);
        }
        else
        {
            String tag="OwnRecordListFragment";
            if(to instanceof OwnRepairDetailFragment)
                tag="OwnRepairDetailFragment";
            fts.add(R.id.record_frame, to,tag);
        }
        fts.commit();
    }

    @Override
    public void viewDetail(RecordDetailItem detailItem) {
        detailFragment.setDetailItem(detailItem);
        transferFragment(recordListFragment,detailFragment);
        isInDetailFragView=true;
    }

    @Override
    public void backToList() {
        transferFragment(detailFragment,recordListFragment);
        setIsDetailFragView(false);
    }

    /**
     * 设置状态栏标题
     * @param title
     */
    public void setBarTitle(String title)
    {
        getSupportActionBar().setTitle(title);
    }

    /**
     * 回退方法截停
     */
    @Override
    public void onBackPressed() {
        if(isInDetailFragView())
            backToList();
        else
            super.onBackPressed();
    }


    public boolean isInDetailFragView() {
        return isInDetailFragView;
    }

    public void setIsDetailFragView(boolean IDetailFragView) {
        isInDetailFragView = IDetailFragView;
    }


}

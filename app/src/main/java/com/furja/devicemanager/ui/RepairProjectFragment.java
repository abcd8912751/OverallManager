package com.furja.devicemanager.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.furja.overall.R;
import com.furja.devicemanager.beans.RepairProjectItem;
import com.furja.devicemanager.utils.Constants;
import com.furja.devicemanager.view.RepairProjectAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.furja.utils.Utils.showLog;

/**
 * 维修项目的 Fragment
 */
public class RepairProjectFragment extends Fragment{
    @BindView(R.id.recycler_recordList)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_ownRecord)
    SmartRefreshLayout refreshLayout;
    RepairProjectAdapter adapter;
    private Context context;
    private List<RepairProjectItem> items;
    private int projectSign;    //用于判断添加还是编辑维修项目以及是否是验收项目
    private int initPosition=0;   //初始化进入List的位置
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_ownrecord_list, container, false);
        ButterKnife.bind(this, view);
        adapter
                =new RepairProjectAdapter(R.layout.recyclerview_layout,null);
        recyclerView
                .setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
        showLog("initPosition:"+initPosition);
        DividerItemDecoration    itemDecoration
                    =new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
        Drawable drawable
                    =context.getResources().getDrawable(R.drawable.item_drawable);
        itemDecoration.setDrawable(drawable);
        recyclerView.addItemDecoration(itemDecoration);
        if(items!=null)
            adapter.setNewData(items);
        if(initPosition<adapter.getItemCount())
            recyclerView.smoothScrollToPosition(initPosition);
        adapter.setProjectSign(projectSign);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnabled(false);
        refreshLayout.setEnableLoadMore(false);
        return view;
    }


    /**
     * 当前的项目名称是否为空或已保存到List<RepairProjectItem>
     * @return
     */
    private boolean hasComplexOrNull() {
//        DetailItem item=recyclerAdapter.getItem(0);
//        String projectName=item.getDetail();
//        if(TextUtils.isEmpty(projectName))
//            return true;
//        for(RepairProjectItem projectItem:items)
//        {
//            if(projectItem.getFProjectName().equals(item.getDetail()))
//                return true;
//        }
        return false;
    }





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
    }

    /**
     * 获取项目
     * @return
     */
    public List<RepairProjectItem> getItems() {
        items=adapter.getItems();
        if (items == null)
            return Collections.EMPTY_LIST;
        return items;
    }

    public void setItems(List<RepairProjectItem> items) {
        this.items = items;
    }



    public int getProjectSign() {
        return projectSign;
    }

    public void setProjectSign(int projectSign) {
        this.projectSign = projectSign;
    }

    public int getInitPosition() {
        return initPosition;
    }

    public void setInitPosition(int initPosition) {
        this.initPosition = initPosition;
    }
}
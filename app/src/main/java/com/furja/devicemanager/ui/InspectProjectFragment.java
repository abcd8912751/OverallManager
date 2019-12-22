package com.furja.devicemanager.ui;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.devicemanager.DeviceManagerApp;
import com.furja.overall.R;
import com.furja.devicemanager.beans.InspectProjectItem;
import com.furja.devicemanager.utils.JSONParser;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.furja.devicemanager.utils.Constants.FURJA_GET_INSPECTPROJECT;
import static com.furja.utils.Utils.showLog;

/**
 * 点检项目
 */
public class InspectProjectFragment extends Fragment {
    private Context context;
    private String planID;
    @BindView(R.id.recycler_recordList)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_ownRecord)
    SmartRefreshLayout refreshLayout;
    private int requestCount=0;
    RecyclerAdapter recyclerAdapter;
    private List<InspectProjectItem> items;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.frag_ownrecord_list,container,false);
        ButterKnife.bind(this,view);
        recyclerAdapter=new RecyclerAdapter(R.layout.inspect_project_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.bindToRecyclerView(recyclerView);
        refreshLayout.setEnabled(false);
        refreshLayout.setEnableRefresh(false);
        DividerItemDecoration dividerItemDecoration
                =new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        if(getItems().isEmpty())
        {
            getAndShowInspectProject();
        }
        else
        {
            showLog("传递来的:"+JSON.toJSONString(items));
            recyclerAdapter.setNewData(items);
        }
        return view;
    }




    /**
     * 获取并显示 点检项目
     */
    private void getAndShowInspectProject() {
        recyclerAdapter.setEmptyView(R.layout.empty_layout);
        if(TextUtils.isEmpty(planID))
        {
            showLog("没有获取到可用的保养计划ID");
            return;
        }

        OkHttpUtils
                .get()
                .url(FURJA_GET_INSPECTPROJECT)
//                .url("http://192.168.10.92:8378/getInspectProject")
                .addParams("FID",planID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        showLog(e.toString());
                        if(++requestCount<3)
                            getAndShowInspectProject();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        showLog(getClass()+s);requestCount=0;
                        try {
                            s= JSONParser.parserArray(s);
                            items
                                    = JSON.parseArray(s,InspectProjectItem.class);
                            if(items!=null)
                                recyclerAdapter.setNewData(items);
                            else
                            {
                                recyclerAdapter.setEmptyView(R.layout.empty_view);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            recyclerAdapter.setEmptyView(R.layout.empty_view);
                        }
                    }
                });
    }

    public void setPlanID(String planID) {
        this.planID = planID;
        showLog("planID:"+planID);
    }

    public List<InspectProjectItem> getItems() {
        if(items==null)
            return Collections.EMPTY_LIST;
        return items;
    }

    public void setItems(List<InspectProjectItem> items) {
        this.items = items;
    }


    private class RecyclerAdapter extends BaseQuickAdapter<InspectProjectItem,BaseViewHolder>
    {
        public RecyclerAdapter(int layoutResId) {
            super(layoutResId);
        }
        @Override
        protected void convert(BaseViewHolder helper, final InspectProjectItem item) {
            helper.setText(R.id.projectyName_content,item.getFProjectName());
            helper.setText(R.id.projectAsk_content,item.getFInspectRequire());
            helper.setText(R.id.project_remarkContent,item.getFFNote());
            final EditText inspectRecord_input=helper.getView(R.id.inspectRecord_input);
            if(!TextUtils.isEmpty(item.getFInspectRecord()))
                helper.setText(R.id.inspectRecord_input,item.getFInspectRecord());
            if(inspectRecord_input!=null)
            {
                if(!DeviceManagerApp.isChecker())
                {
                    inspectRecord_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if(!TextUtils.isEmpty(editable))
                                item.setFInspectRecord(editable.toString());
                        }
                    });
                    inspectRecord_input
                            .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View view, boolean b) {
                                    if(!b)
                                    {
                                        item
                                            .setFInspectRecord(inspectRecord_input
                                                    .getText().toString());
                                    }
                                }
                            });
                    inspectRecord_input.setFocusableInTouchMode(true);
                    inspectRecord_input.setFocusable(true);
                }
                else
                {
                    inspectRecord_input.setFocusableInTouchMode(false);
                    inspectRecord_input.setFocusable(false);
                }
            }

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.context=getContext();
    }
}

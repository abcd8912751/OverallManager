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
import com.furja.devicemanager.beans.MaintainProjectItem;
import com.furja.devicemanager.utils.JSONParser;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import java.util.Collections;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.furja.devicemanager.utils.Constants.FURJA_GET_MAINTAINPROJECT;
import static com.furja.utils.Utils.showLog;

/**
 * 保养项目进入的Fragment
 */

public class MaintainProjectFragment extends Fragment {
    private Context context;
    private String planID;
    @BindView(R.id.recycler_recordList)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_ownRecord)
    SmartRefreshLayout refreshLayout;
    RecyclerAdapter recyclerAdapter;
    private int requestCount=0;
    private List<MaintainProjectItem> items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.frag_ownrecord_list,container,false);
        ButterKnife.bind(this,view);
        recyclerAdapter=new RecyclerAdapter(R.layout.maintain_project_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(recyclerAdapter);
        refreshLayout.setEnabled(false);
        refreshLayout.setEnableRefresh(false);
        DividerItemDecoration dividerItemDecoration
                =new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerAdapter.bindToRecyclerView(recyclerView);
        if(getItems().isEmpty())
            getAndShowMaintainProject();
        else
            recyclerAdapter.setNewData(items);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.context=getContext();
    }

    public List<MaintainProjectItem> getItems() {
        if(items==null)
            return Collections.EMPTY_LIST;
        return items;
    }

    public void setItems(List<MaintainProjectItem> items) {
        this.items = items;
    }


    private class RecyclerAdapter extends BaseQuickAdapter<MaintainProjectItem,BaseViewHolder>
    {
        public RecyclerAdapter(int layoutResId) {
            super(layoutResId);
        }
        @Override
        protected void convert(BaseViewHolder helper, final MaintainProjectItem item) {
            helper.setText(R.id.projectyName_content,separateText(item.getProjectName()));
            helper.setText(R.id.projectAsk_content,separateText(item.getProjectAsk()));
            helper.setText(R.id.project_remarkContent,item.getProjectRemark());
            if(item.getStopDuration()!=0)
                helper.setText(R.id.stopDuration_input,item.getStopDuration()+"");
            if(item.getMaintainDuration()!=0)
                helper.setText(R.id.maintainDuration_input,item.getMaintainDuration()+"");
            if(!TextUtils.isEmpty(item.getFMaintainRecord()))
                helper.setText(R.id.maintainRecord_input,item.getFMaintainRecord());
            EditText stopDuration=helper.getView(R.id.stopDuration_input);
            EditText maintainDuration=helper.getView(R.id.maintainDuration_input);
            final EditText maintainRecord=helper.getView(R.id.maintainRecord_input);
            if(DeviceManagerApp.isChecker())
            {
                stopDuration.setFocusable(false);
                stopDuration.setFocusableInTouchMode(false);
                maintainDuration.setFocusableInTouchMode(false);
                maintainDuration.setFocusable(false);
                maintainRecord.setFocusableInTouchMode(false);
                maintainRecord.setFocusable(false);
            }
            else
            {
                stopDuration.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!TextUtils.isEmpty(editable.toString()))
                            item.setStopDuration(Double.valueOf(editable.toString()));
                        else
                            item.setStopDuration(0);
                    }
                });
                maintainDuration.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!TextUtils.isEmpty(editable.toString()))
                            item.setMaintainDuration(Double.valueOf(editable.toString()));
                        else
                            item.setMaintainDuration(0);
                    }
                });
                maintainRecord.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!TextUtils.isEmpty(editable))
                            item.setFMaintainRecord(editable.toString());
                    }
                });
                maintainRecord
                        .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                if(!b)
                                {
                                    item
                                            .setFMaintainRecord(maintainRecord
                                                    .getText().toString());
                                }
                            }
                        });
            }
        }

        /**
         * 字符串以空格区分,到了一定字长自动换行
         * @param content
         * @return
         */
        private String separateText(String content) {
            StringBuffer stringBuffer=new StringBuffer();
            String line_separater=System.getProperty("line.separator");
            int step=10,i,length=content.length();
            if(length<step+1)
                return content;
            for(i=0;i<length;i=i+step)
            {
                int limit=i+step;
                if(limit>length)
                    limit=length;
                String string=content.substring(i, limit);
                if(i==0)
                    stringBuffer.append(string);
                else
                    stringBuffer.append(line_separater).append(string);
            }
            return stringBuffer.toString();
        }
    }

    /**
     * 获取并显示保养项目
     */
    public void getAndShowMaintainProject()
    {
        recyclerAdapter.setEmptyView(R.layout.empty_layout);
        if(TextUtils.isEmpty(getPlanID()))
        {
            showLog("没有获取到可用的保养计划ID");
            return;
        }
        OkHttpUtils
                .get()
                .url(FURJA_GET_MAINTAINPROJECT)
//                .url("http://192.168.10.92:8378/getMaintainProject")
                .addParams("FID",planID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        showLog(e.toString());
                        if(++requestCount<3)
                            getAndShowMaintainProject();
                    }
                    @Override
                    public void onResponse(String s, int i) {
                        showLog(getClass()+s);requestCount=0;
                        try {
                            s= JSONParser.parserArray(s);
                            List<MaintainProjectItem> items
                                    =JSON.parseArray(s,MaintainProjectItem.class);
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

    public String getPlanID() {
        return planID;
    }
    public void setPlanID(String planID) {
        this.planID = planID;
    }

    /**
     * 获取保养项目集
     * @return
     */
    public List<MaintainProjectItem> getProjectItems()
    {
        List<MaintainProjectItem> items
                =recyclerAdapter.getData();
        if(items!=null)
            return items;
        return Collections.<MaintainProjectItem>emptyList();
    }
}

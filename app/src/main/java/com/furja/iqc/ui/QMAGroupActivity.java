package com.furja.iqc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.common.DaoSession;
import com.furja.overall.R;
import com.furja.common.QMAGroupData;
import com.furja.common.QMAGroupDataDao;
import com.furja.iqc.json.QMAGroupBean;
import com.furja.utils.RetryWhenUtils;
import com.furja.iqc.view.MyAutoAdapter;
import com.furja.iqc.view.WrapLinearLayoutManager;
import com.furja.utils.RetrofitBuilder;
import com.furja.utils.RetrofitHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.schedulers.Schedulers;

import static com.furja.utils.Constants.EXTRA_NOREASON_NUMBER;
import static com.furja.utils.Constants.getVertxUrl;
import static com.furja.utils.Utils.getDaoSession;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * 不良原因及不良原因数
 */
public class QMAGroupActivity extends AppCompatActivity {
    @BindView(R.id.recycler_QMAGroup)
    RecyclerView recyclerView;
    @BindView(R.id.product_no)
    AppCompatAutoCompleteTextView autoTextView;
    QMAGroupAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qmap);
        ButterKnife.bind(this);
        WrapLinearLayoutManager layoutManager
                =new WrapLinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new QMAGroupAdapter(null);
        recyclerView.setAdapter(adapter);
        adapter.bindToRecyclerView(recyclerView);
        DividerItemDecoration itemDecoration
                =new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        MyAutoAdapter myAutoAdapter=new MyAutoAdapter(this,R.layout.simple_list_item);
        autoTextView.setAdapter(myAutoAdapter);
        autoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QMAGroupData seleteData
                        = (QMAGroupData) parent.getItemAtPosition(position);
                Intent intent
                        =new Intent();
                intent.putExtra(EXTRA_NOREASON_NUMBER,seleteData.toString());
                setResult(RESULT_OK,intent);
                showLog("选择了:"+seleteData.toString());
                finish();
            }
        });
        DaoSession daoSession=getDaoSession();
        QMAGroupDataDao dao
                =daoSession.getQMAGroupDataDao();
        List<QMAGroupData> groupDatas
                =dao.loadAll();
        if(groupDatas==null||groupDatas.isEmpty())
            freshNoReasonList();
        else {
            adapter.setNewData(groupDatas);
            daoSession.clear();
        }

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                QMAGroupData seleteData=
                        (QMAGroupData) adapter.getItem(position);
                Intent intent
                        =new Intent();
                intent.putExtra(EXTRA_NOREASON_NUMBER,seleteData.toString());
                setResult(RESULT_OK,intent);
                showLog("选择了:"+seleteData.toString());
                finish();
            }
        });
    }

    private void freshNoReasonList() {
        showToast("正在获取不良原因清单");
        RetrofitHelper helper= RetrofitBuilder.getHelperByUrl(getVertxUrl());
        helper.getQMAGroupInfo()
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWhenUtils.create())
                .subscribe(response->{
                    if(response.getCode()>0){
                        List<QMAGroupBean> lst=response.getResult();
                        final DaoSession daoSession=getDaoSession();
                        QMAGroupDataDao dao
                                =daoSession.getQMAGroupDataDao();
                        dao.deleteAll();
                        for(QMAGroupBean bean:lst){
                            QMAGroupData groupData
                                    =new QMAGroupData(null,bean.getFNumber(),bean.getFName());
                            dao.insertOrReplace(groupData);
                        }
                        List<QMAGroupData> groupDatas
                                =dao.loadAll();
                        recyclerView.post(()->{
                            adapter.setNewData(groupDatas);
                        });
                        daoSession.clear();
                    }
                    else
                        showToast("缺少不良原因基础数据");
                },error->{
                    error.printStackTrace();
                });
    }


    private class QMAGroupAdapter extends BaseQuickAdapter<QMAGroupData,BaseViewHolder> {

        public QMAGroupAdapter(@Nullable List data) {
            super(R.layout.simple_list_item,data);
        }

        @Override
        protected void convert(BaseViewHolder helper, QMAGroupData item) {
            helper.setText(R.id.simple_text_item,item.getFNumber()+" "+item.getFName());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater()
                .inflate(R.menu.menu_fresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_fresh)
            freshNoReasonList();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent intent
                =new Intent();
        intent.putExtra(EXTRA_NOREASON_NUMBER,"");
        setResult(RESULT_OK,intent);
        showLog("选择了:");
        finish();
    }
}

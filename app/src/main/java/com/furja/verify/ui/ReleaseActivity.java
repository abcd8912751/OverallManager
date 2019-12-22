package com.furja.verify.ui;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.furja.devicemanager.databases.DaoSession;
import com.furja.overall.FurjaApp;
import com.furja.overall.R;
import com.furja.verify.json.ProductNoListJson;
import com.furja.verify.json.ReleaseResultJson;
import com.furja.verify.model.ProductNumber;
import com.furja.verify.model.ProductNumberDao;
import com.furja.verify.utils.InneURL;
import com.furja.verify.view.MyAutoAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

import static com.furja.overall.FurjaApp.getDaoSession;
import static com.furja.verify.utils.Constants.FURIA_RELEASE_URL;
import static com.furja.verify.utils.Constants.FURJA_SCDHINFO_URL;
import static com.furja.verify.utils.Constants.isInnerNet;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

public class ReleaseActivity extends AppCompatActivity {
    AppCompatAutoCompleteTextView productNo_text;
    TextView product_info;
    int requestCount=0;
    int releaseCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);

        productNo_text=(AppCompatAutoCompleteTextView)findViewById(R.id.product_no);
        product_info=(TextView)findViewById(R.id.product_info);
        MyAutoAdapter myAutoAdapter=new MyAutoAdapter(this,R.layout.simple_list_item);
        productNo_text.setAdapter(myAutoAdapter);
        restoreLocalDbFirst();
        Button btn_submit
                =(Button)findViewById(R.id.button_release);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                hideKeyBoard();
                CharSequence charSequence
                        =productNo_text.getText();
                if(!TextUtils.isEmpty(charSequence))
                {
                    final String string
                            =charSequence.toString();
                    AlertDialog.Builder builder
                            =new AlertDialog.Builder(ReleaseActivity.this);
                    builder.setTitle("           释放单号");
                    builder.setMessage("确认释放生产单号:  "+string);
                    builder.setPositiveButton("释放", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            releaseProductByNo(string);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog alertDialog=builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
                showLog("请输入生产单号");
            }
        });

    }

    public void hideKeyBoard()
    {
        showLog("触发了我隐藏软键盘的想法");
        InputMethodManager imm = (InputMethodManager)productNo_text.getContext() .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null&&imm.isActive())
            imm.hideSoftInputFromWindow(productNo_text.getWindowToken(), 0);
    }

    private void restoreLocalDbFirst() {
        DaoSession daoSession=getDaoSession();
        ProductNumberDao dao=daoSession.getProductNumberDao();
        List<ProductNumber> productNumbers=dao.loadAll();
        if(productNumbers==null||productNumbers.size()==0)
            restoreLocalDb();
    }

    /**
     * 释放单号
     * @param productNo
     */
    private void releaseProductByNo(final String productNo) {
        String url=FURIA_RELEASE_URL;
        if(isInnerNet)
            url= InneURL.FURIA_RELEASE_URL;
        showLog("将释放单号->"+productNo);
        OkHttpUtils
                .get()
                .url(url)
                .addParams("FSCDNO",productNo)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        releaseCount++;
                        if(releaseCount<3)
                            releaseProductByNo(productNo);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        ReleaseResultJson resultJson
                                =JSON.parseObject(s,ReleaseResultJson.class);
                        if(resultJson==null)
                            showLog("返回异常");
                        else
                        {
                            if(resultJson.getErrCode()==100)
                            {
                                showToast("释放成功");
                                productNo_text.setText("");
                            }
                            else
                                if(resultJson.getErrCode()==110)
                                    showToast("释放失败");
                                else
                                {
                                    showToast(resultJson.getErrMsg());
                                }
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_release, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id==R.id.action_updateProductNo)
        {
            restoreLocalDb();
        }
        return super.onOptionsItemSelected(item);
    }

    public void restoreLocalDb()
    {
        String infoUrl=FURJA_SCDHINFO_URL;
        if(isInnerNet)
            infoUrl=InneURL.FURJA_SCDHINFO_URL;
        showToast("开始更新工作单号列表");
        productNo_text.setHint("正在更新工单列表,请稍候");
        productNo_text.setEnabled(false);
        OkHttpUtils.
                get()
                .url(infoUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i)
                    {
                        e.printStackTrace();
                        if(++requestCount>3)
                            restoreLocalDb();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        ProductNoListJson json
                                = JSON.parseObject(s, ProductNoListJson.class);
                        if (json == null)
                        {
                            showLog("获取生产单号异常");
                            return;
                        }
                        List<ProductNoListJson.BCSCDHBean> bcscdhBeans
                                =json.getBCSCDH();
                        if(bcscdhBeans==null)
                        {
                            showLog("获取生产单号为空");
                            return;
                        }
                        if(bcscdhBeans.size()>0)
                            executeResponse(bcscdhBeans);
                    }
                });

    }

    public void executeResponse(final List<ProductNoListJson.BCSCDHBean> beans)
    {
        //判断response是否携带有效信息携带了就清除本地数据,重新导入
        DaoSession daoSession
                = FurjaApp.getDaoSession();
        final ProductNumberDao dao
                =daoSession.getProductNumberDao();
        dao.deleteAll();
        showLog("工单列表记录条数:"+beans.size());
        io.reactivex.Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for(ProductNoListJson.BCSCDHBean bean:beans)
                {
                    ProductNumber productNumber
                            =new ProductNumber(null,bean.getProductNo());
                    dao.saveInTx(productNumber);
                }
                return "saveFinish";
            }})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        productNo_text.setEnabled(true);
                        productNo_text.setHint(R.string.hint_productNo);
                    }
                });
    }

}

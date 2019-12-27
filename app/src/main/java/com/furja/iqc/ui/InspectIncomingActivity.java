package com.furja.iqc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.furja.common.Preferences;
import com.furja.common.WrapLinearLayoutManager;
import com.furja.overall.FurjaApp;
import com.furja.overall.R;
import com.furja.iqc.json.NewQCList;
import com.furja.iqc.presenter.IncomingVerifyPresenter;
import com.furja.iqc.ui.adapter.LineItemAdapter;
import com.furja.utils.TextInputListener;
import com.furja.overall.ui.BaseActivity;
import com.furja.overall.ui.LoginActivity;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebCreator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.furja.utils.Constants.EXTRA_QCDATA_BEAN;
import static com.furja.utils.Constants.EXTRA_QCENTRY_DATA;
import static com.furja.utils.Constants.EXTRA_QCLIST_DATA;
import static com.furja.utils.Constants.EXTRA_QCVALUE_DATA;
import static com.furja.utils.Utils.showLog;

/**
 * 电源线检验的活动
 */
public class InspectIncomingActivity extends BaseActivity implements IncomingVerifyPresenter.LineVerifyView{
    @BindView(R.id.input_mainBarCode)
    AppCompatEditText edit_mainBarCode;
    @BindView(R.id.btn_confirm)
    Button confirmButton;
    @BindView(R.id.recycler_partItem)
    RecyclerView recyclerView;
    @BindView(R.id.qcsheme_info)
    TextView qcscheme_info;
    IncomingVerifyPresenter presenter;
    LineItemAdapter lineItemAdapter;
    View emptyWebView;
    AgentWeb agentWeb;
    boolean showIQCBoard=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_incoming);
        ButterKnife.bind(this);
        lineItemAdapter=new LineItemAdapter(R.layout.check_item_layout);
        recyclerView.setLayoutManager(WrapLinearLayoutManager.wrap(this));
        recyclerView.setAdapter(lineItemAdapter);
        lineItemAdapter.bindToRecyclerView(recyclerView);
        presenter =new IncomingVerifyPresenter(this);
        View.OnClickListener listener =v->{ toLogProject();  };
        confirmButton.setOnClickListener(listener);
        qcscheme_info.setMovementMethod(ScrollingMovementMethod.getInstance());
        DividerItemDecoration itemDecoration
                =new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        edit_mainBarCode.requestFocus();
        initWebView();
        TextInputListener.bind(edit_mainBarCode);
    }


    /**
     * 针对检验项目赋值
     */
    private void toLogProject() {
        NewQCList qcList =presenter.getQcList();
        if(qcList==null) {
            showLog("暂未获得质检方案不予执行");
            return;
        }
        if(presenter.getBarCodes().isEmpty()) {
            showLog("无有效的条形码");
            return;
        }
        Intent intent=new Intent(InspectIncomingActivity.this, InspectItemDetailActivity.class);
        intent.putExtra(EXTRA_QCLIST_DATA,qcList);
        startActivity(intent);
        finish();
    }

    private void initWebView() {
        emptyWebView =View.inflate(this,R.layout.layout_websurf,null);
        LinearLayout linearLayout= emptyWebView.findViewById(R.id.linear_layout);
        String url="http://192.168.8.46:8118/FJAPIManage/views/FJ_QCAutoDispatch/FJ_QCAutoDispatchForInNetWorkForFChecker.html?FCheckerName=";
        String userName = FurjaApp.getUserName();
        url = url +userName;
        agentWeb = AgentWeb.with(this)
                .setAgentWebParent(linearLayout, -1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//传入AgentWeb的父控件。
                .useDefaultIndicator(-1, 2)//设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(url);
        lineItemAdapter.setEmptyView(emptyWebView);
        qcscheme_info.setText("派单看板");
        WebCreator webCreator= agentWeb.getWebCreator();
        final WebView webView= webCreator.getWebView();
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(showIQCBoard&&webView!=null)
                    webView.onTouchEvent(event);
                return showIQCBoard;
            }
        });
        showIQCBoard=true;
    }


    public void hideBarCodeEditor() {
        edit_mainBarCode.setText("");
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null&&imm.isActive())
            imm.hideSoftInputFromWindow(edit_mainBarCode.getWindowToken(), 0);
        edit_mainBarCode.setHint(R.string.onloading_prompt);
    }

    public void showBarCodeEditor() {
        edit_mainBarCode.setText("");
        edit_mainBarCode.setHint(R.string.hint_scan_barCode);
        //隐藏输入法
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null&&imm.isActive())
            imm.hideSoftInputFromWindow(edit_mainBarCode.getWindowToken(), 0);
        edit_mainBarCode.requestFocus();
    }

    @Override
    public void setEmptyView(int layoutID) {
        if(layoutID==R.layout.scan_empty_view) {
            if(lineItemAdapter.isEmpty())
                reloadWebView();
        }
        else {
            if(lineItemAdapter.isEmpty())
                lineItemAdapter.setEmptyView(layoutID, recyclerView);
            showIQCBoard=false;
        }
    }

    private void reloadWebView() {
        qcscheme_info.setText("派单看板");
        lineItemAdapter.setEmptyView(emptyWebView);
        showIQCBoard=true;
        if(agentWeb!=null)
            agentWeb.getUrlLoader().reload();
    }

    @Override
    public void resetView() {
        showBarCodeEditor();
        lineItemAdapter.setNewData(null);
        confirmButton.setVisibility(View.GONE);
        presenter.resetFieldData();
        reloadWebView();
    }

    @Override
    public void showMaterialInfo(String infos) {
        if(TextUtils.isEmpty(infos)||infos.contains("null"))
            infos="";
        List<NewQCList.QCDataBean> beans = presenter.getQcDataBeans();
        lineItemAdapter.setNewData(beans);
        //判断状态:
        if(beans.isEmpty())
            confirmButton.setVisibility(View.GONE);
        else{
            NewQCList.QCDataBean dataBean = beans.get(0);
            List<NewQCList.QCValueBean> valueBeans=presenter.getQcValueData();
            if(valueBeans.isEmpty())
                confirmButton.setBackgroundResource(R.drawable.shape_button_default);
            else
                confirmButton.setBackgroundResource(R.drawable.shape_button_confirm);
            confirmButton.setVisibility(View.VISIBLE);
            infos=infos+"  , 单数: "+beans.size();
        }
        qcscheme_info.setText(infos);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_reset)
            resetView();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void focusBarCode() {
        edit_mainBarCode.setText("");
        edit_mainBarCode.requestFocus();
    }

    private void switchUser() {
        Preferences.saveAutoLogin(false);
        FurjaApp.setUserAndSave(null);
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

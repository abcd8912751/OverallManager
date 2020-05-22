package com.furja.overall.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.furja.overall.R;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

import static com.furja.utils.Constants.EXTRA_WEBVIEW_TITLE;
import static com.furja.utils.Constants.EXTRA_WEBVIEW_URL;

public class WebSurfActivity extends BaseActivity {
    AgentWeb mAgentWeb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_websurf);
        analyseIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void analyseIntent() {
        Intent intent=getIntent();
        String url="";
        if(intent!=null) {
            url = intent.getStringExtra(EXTRA_WEBVIEW_URL);
            String title=intent.getStringExtra(EXTRA_WEBVIEW_TITLE);
            if(!TextUtils.isEmpty(title))
                setTitle(title);
        }
        LinearLayout linearLayout=findViewById(R.id.linear_layout);
        if(TextUtils.isEmpty(url))
            url="http://192.168.8.46:8378";
        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent((LinearLayout) linearLayout, -1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//传入AgentWeb的父控件。
                .useDefaultIndicator(-1, 2)//设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(url);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAgentWeb!=null)
            mAgentWeb.destroy();
    }
}

package com.furja.overall.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.furja.overall.R;
import com.furja.overall.beans.NaviItem;
import com.furja.utils.AutoUpdateUtils;
import com.furja.overall.view.NaviRecyclerAdapter;
import com.furja.verify.ui.ProductVerifyActivity;
import com.furja.fixturemanager.ui.WorkFixActivity;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.furja.utils.Utils.showLog;

/**
 * 导航页
 */
public class NaviActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    @BindView(R.id.recycler_navi)
    RecyclerView recycler_navi;
    NaviRecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);
        ButterKnife.bind(this);

        recyclerAdapter=new NaviRecyclerAdapter(R.layout.navigation_item);
        GridLayoutManager layoutManager
                =new GridLayoutManager(this,2);
        recycler_navi.setLayoutManager(layoutManager);
        recycler_navi.setAdapter(recyclerAdapter);
        recyclerAdapter.bindToRecyclerView(recycler_navi);
        initNaviView();
        loadRecyclerData();

        //检查更新
        AutoUpdateUtils updateUtils
                =new AutoUpdateUtils(this,true);
        updateUtils.checkUpdate();
    }


    private void loadRecyclerData() {
        List<NaviItem>  naviItems
                =new ArrayList<NaviItem>();
        NaviItem naviItem=new NaviItem();
        naviItem.setaClass(BadLogActivity.class);
        naviItem.setTitle("质检助手");
        naviItem.setIconID(R.mipmap.qc_launcher);
        naviItems.add(naviItem);
        NaviItem naviItem1=new NaviItem();
        naviItem1.setaClass(ProductVerifyActivity.class);
        naviItem1.setIconID(R.mipmap.verify_jy_launcher);
        naviItem1.setTitle("成品校验");
        naviItems.add(naviItem1);
        naviItem1=new NaviItem();
        naviItem1.setaClass(ChartActivity.class);
        naviItem1.setIconID(R.mipmap.ic_chart_launcher);
        naviItem1.setTitle("异常报表");
        naviItems.add(naviItem1);
        naviItem=new NaviItem();
        naviItem.setaClass(ChatActivity.class);
        naviItem.setTitle("聊天模块");
        naviItem.setIconID(R.mipmap.ic_chat_launcher);
        naviItems.add(naviItem);
        naviItem=new NaviItem();
        naviItem.setaClass(WorkFixActivity.class);
        naviItem.setTitle("工装管理");
        naviItem.setIconID(R.mipmap.workfix_launcher);
        naviItems.add(naviItem);
        recyclerAdapter.setNewData(naviItems);
        recyclerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                NaviItem item=recyclerAdapter.getItem(position);
                if(item.getTitle().contains("更新"))
                {

                }
                else{
                    Intent intent=new Intent(NaviActivity.this,item.getaClass());
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 加载导航视图
     */
    private void initNaviView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.collapseActionView();
    }



    public String getVerInfo()
    {
        String versionInfo=System.getProperty("line.separator");
        try {
            PackageInfo packageInfo=getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            versionInfo+="    版本号: "+
                    packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionInfo;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}

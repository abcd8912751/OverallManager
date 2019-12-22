package com.furja.devicemanager.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.furja.overall.R;
import com.furja.devicemanager.beans.MaintainProjectItem;
import com.furja.devicemanager.beans.RepairProjectItem;
import com.furja.devicemanager.beans.InspectProjectItem;
import com.furja.devicemanager.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.furja.devicemanager.DeviceManagerApp.isChecker;
import static com.furja.devicemanager.utils.Constants.KEY_PROJECT_ITEMS;
import static com.furja.devicemanager.utils.Constants.TAG_ADD_PROJECT;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * 点击点检项目/保养项目或维修项目时跳入的Activity
 */
public class ProjectRecordActivity extends AppCompatActivity {
    MaintainProjectFragment maintainProjectFragment;
    InspectProjectFragment inspectProjectFragment;
    RepairProjectFragment repairProjectFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_record);
        analyseIntent(getIntent());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * 解析Intent
     * @param intent
     */
    private void analyseIntent(Intent intent)
    {
        if(intent==null)
            return;
        String planID=intent.getStringExtra("planID");
        String uriData=intent.getDataString();
        String itemStrings=intent.getStringExtra(KEY_PROJECT_ITEMS);
        if(uriData.contains("MaintainItem"))
        {
            showMaintainProject(planID, itemStrings);
        }
        else if(uriData.contains("InspectItem"))
            {
                showInspectProject(planID, itemStrings);
            }
            else if(uriData.contains("addRepairItem"))
                {
                    toAddRepairProject(itemStrings);
                }
                else if(uriData.contains("editRepairItem"))
                    {
                        repairProjectFragment
                                =new RepairProjectFragment();
                        if(!TextUtils.isEmpty(itemStrings))
                        {
                            List<RepairProjectItem> items
                                    =JSON.parseArray(itemStrings,RepairProjectItem.class);
                            repairProjectFragment.setItems(items);
                            repairProjectFragment.setProjectSign(Constants.TAG_EDIT_PROJECT);
                            try {
                                String[] datas=uriData.split(":");
                                String position=datas[1];
                                showLog("position:"+position);
                                repairProjectFragment.setInitPosition(Integer.valueOf(position));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        viewFragment(repairProjectFragment);
                    }
    }

    private void toAddRepairProject(String itemStrings) {
        repairProjectFragment
                =new RepairProjectFragment();
        List<RepairProjectItem> items
                =new ArrayList<RepairProjectItem>();
        if(!TextUtils.isEmpty(itemStrings))
        {
            items
                = JSON.parseArray(itemStrings,RepairProjectItem.class);
        }
        items.add(new RepairProjectItem());
        repairProjectFragment.setInitPosition(items.size()-1);
        repairProjectFragment.setItems(items);
        repairProjectFragment.setProjectSign(Constants.TAG_ADD_PROJECT);
        viewFragment(repairProjectFragment);
    }

    private void showInspectProject(String planID, String itemStrings) {
        inspectProjectFragment=new InspectProjectFragment();
        inspectProjectFragment.setPlanID(planID);
        if(!TextUtils.isEmpty(itemStrings))
        {
            List<InspectProjectItem> items
                    = JSON.parseArray(itemStrings,InspectProjectItem.class);
            inspectProjectFragment.setItems(items);
        }
        viewFragment(inspectProjectFragment);
    }

    private void showMaintainProject(String planID, String itemStrings) {
        maintainProjectFragment =new MaintainProjectFragment();
        maintainProjectFragment.setPlanID(planID);
        if(!TextUtils.isEmpty(itemStrings))
        {
            List<MaintainProjectItem> items
                    = JSON.parseArray(itemStrings,MaintainProjectItem.class);
            maintainProjectFragment.setItems(items);
        }
        viewFragment(maintainProjectFragment);
    }

    /**
     * 切换Fragment
     * @param to
     */
    public void viewFragment(Fragment to)
    {
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction fts = fm.beginTransaction();
        if(to.isAdded())
        {
            fts.show(to);
        }
        else
        {
            String tag="MaintainProjectFragment";
            String title="保养项目";
            if(to instanceof SparePartsFragment)
            {
                tag="SparePartsFragment";
                title="备件耗用";
            }
            else if(to instanceof InspectProjectFragment)
            {
                tag="InspectProjectFragment";
                title="点检项目";
            }
            else if(to instanceof RepairProjectFragment)
            {
                tag="RepairProjectFragment";
                title="维修项目";
            }
            setBarTitle(title);
            fts.add(R.id.record_frame, to,tag);
        }
        fts.commit();
    }

    /**
     * 设置状态栏标题
     * @param title
     */
    public void setBarTitle(String title)
    {
        getSupportActionBar().setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return backToPrevious(item.getItemId());
    }

    /**
     * 返回上级
     * @param itemID
     * @return
     */
    private boolean backToPrevious(int itemID) {
        Intent intent=new Intent();
        boolean canBackPrevious=true;
        if(inspectProjectFragment!=null)
        {
            //说明是进入点检模块
            showLog("点检模块");
            List<InspectProjectItem> items=
                    inspectProjectFragment.getItems();
            if(items!=null&&!isChecker())
            {
                for(InspectProjectItem projectItem:items)
                {
                    if(TextUtils.isEmpty(projectItem.getFInspectRecord()))
                    {
                        showToast("点检项目中点检记录不能为空");
                        return true;
                    }
                }
            }
            intent.putExtra(KEY_PROJECT_ITEMS, JSON.toJSONString(items));
        }
        else if(maintainProjectFragment!=null)
            {
                //说明是进入保养项目模块
                List<MaintainProjectItem> items=
                        maintainProjectFragment.getProjectItems();
                if(items!=null&&!isChecker())
                {
                    for(MaintainProjectItem maintainProjectItem:items)
                    {
                        if(TextUtils.isEmpty(maintainProjectItem.getFMaintainRecord()))
                        {
                            showToast("保养项目中保养记录不能为空");
                            return true;
                        }
                    }
                }
                intent.putExtra(KEY_PROJECT_ITEMS, JSON.toJSONString(items));
            }
            else if(repairProjectFragment!=null)
            {
                //说明是进入维修项目模块
                List<RepairProjectItem> items
                        =repairProjectFragment.getItems();
                if(items!=null)
                    items.clear();
                for(RepairProjectItem projectItem:repairProjectFragment.getItems())
                {
                    if(repairProjectFragment.getProjectSign()!=TAG_ADD_PROJECT)
                    {
                        if(TextUtils.isEmpty(projectItem.getFFactValue()))
                        {
                            canBackPrevious=false;
                            if(itemID!=android.R.id.home)
                            {
                                showToast("维修项目实际值不可为空");
                            }
                            else
                            {
                                showConfirmDialog();
                            }
                            return true;
                        }
                    }
                    if(TextUtils.isEmpty(projectItem.getFProjectName()))
                    {
                        showToast("名称为空的维修项目将不予记录");
                    }
                    else
                        items.add(projectItem);
                }
                if(canBackPrevious)
                {
                    intent.putExtra(KEY_PROJECT_ITEMS, JSON.toJSONString(items));
                }
            }
        setResult(RESULT_OK,intent);
        finish();
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            return backToPrevious(android.R.id.home);
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回确认对话框
     */
    private void showConfirmDialog() {
        new MaterialDialog.Builder(this)
                .title("维修项目实际值为空")
                .content("要舍弃本次编辑吗")
                .positiveText("确定")
                .negativeText("取消")
                .autoDismiss(true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setResult(RESULT_OK);
                        finish();
                    }
                }).show();
    }




}

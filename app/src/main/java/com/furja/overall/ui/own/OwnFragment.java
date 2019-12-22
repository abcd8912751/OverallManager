package com.furja.overall.ui.own;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.furja.common.CloudUserWithOrg;
import com.furja.common.Preferences;
import com.furja.common.User;
import com.furja.common.WrapLinearLayoutManager;
import com.furja.overall.FurjaApp;
import com.furja.overall.R;
import com.furja.common.BaseFragment;
import com.furja.overall.beans.PreferenceItem;
import com.furja.overall.ui.LoginActivity;
import com.furja.utils.AutoUpdateUtils;

import java.util.ArrayList;
import java.util.List;

import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.textOf;

public class OwnFragment extends BaseFragment {

    private OwnViewModel ownViewModel;
    PreferenceAdapter adapter;
    RecyclerView recyclerView;
    TextView textView;
    AppCompatSpinner spinnerOrg;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ownViewModel=ViewModelProviders.of(this).get(OwnViewModel.class);
        View root = inflater.inflate(R.layout.fragment_own, container, false);
        recyclerView=root.findViewById(R.id.recycler_preference);
        recyclerView.setLayoutManager(WrapLinearLayoutManager.wrap(mContext));
        textView=root.findViewById(R.id.text_userName);
        spinnerOrg=root.findViewById(R.id.spinner_org);
        initView();
        return root;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        PreferenceItem item=new PreferenceItem("检查更新");
        PreferenceItem item1=new PreferenceItem("切换账号");
        PreferenceItem item2=new PreferenceItem("关于软件");
        List<PreferenceItem> items = new ArrayList<>();
        items.add(item);items.add(item1);items.add(item2);
        adapter=new PreferenceAdapter(items);
        adapter.bindToRecyclerView(recyclerView);
        adapter.setOnItemClickListener((adapter1, view, position) ->{
            PreferenceItem item3=adapter.getItem(position);
            switch (item3.getTitle()){
                case "检查更新":
                    new AutoUpdateUtils(mContext).checkUpdate();
                    break;
                case "切换账号":
                    Intent intent=new Intent(mContext, LoginActivity.class);
                    Preferences.saveAutoLogin(false);
                    FurjaApp.setUserAndSave(null);
                    startActivity(intent);
                    getActivity().finish();
                    break;
                case "关于软件":
                    MaterialDialog dialog=new MaterialDialog.Builder(mContext)
                            .title("软件介绍")
                            .positiveText("了解")
                            .content("富佳移动云集成,当前版本:1.0")
                            .show();
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog_bg);
                    break;
            }
        });
        textView.setText(FurjaApp.getUserName());
        textView.setOnClickListener(v->{
            if(FurjaApp.getUser()==null)
                switchToLogin();
        });
        User user=FurjaApp.getUser();
        List<CloudUserWithOrg> userData = user!=null?user.getData():null;
        if(userData==null||userData.isEmpty())
            spinnerOrg.setVisibility(View.INVISIBLE);
        else {
            ArrayAdapter<CloudUserWithOrg> adapter=new ArrayAdapter<>(mContext,R.layout.simple_list_item,userData);
            spinnerOrg.setAdapter(adapter);
            spinnerOrg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CloudUserWithOrg item=(CloudUserWithOrg)parent.getItemAtPosition(position);
                    FurjaApp.setCloudUser(item);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            spinnerOrg.setVisibility(View.VISIBLE);
            FurjaApp.setCloudUser(adapter.getItem(0));
        }
    }


    public static class PreferenceAdapter extends BaseQuickAdapter<PreferenceItem, BaseViewHolder>{

        public PreferenceAdapter(@Nullable List<PreferenceItem> data) {
            super(R.layout.layout_preference_item,data);
        }

        @Override
        protected void convert(BaseViewHolder helper, PreferenceItem item) {
            helper.setText(R.id.item_title,item.getTitle());
            helper.setText(R.id.item_summary,textOf(item.getSummary()));
        }
    }
}
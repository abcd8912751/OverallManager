package com.furja.devicemanager.ui;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.furja.utils.Utils.showLog;

/**
 * 基础Fragment
 */

public abstract class DevBaseFragment extends Fragment {
    protected Context mContext;
    protected FragmentChangedListener fragChangeListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext=getContext();
    }

    abstract boolean isEmptyBarCode();

    abstract void setCurBarCode(String barCode);

    /**
     * 当Fragment状态改变时通过该接口调动MainActivity
     */
    interface FragmentChangedListener {
        void onUploadSuccess();        //上传成功后
        void hideDeviceInfo();  //隐藏设备信息
        void showDeviceInfo();  //显示设备信息
        void transferFragment(DevBaseFragment to, String tag);
        void transferFragment(int position);
        void setDisplayHomeAsUpEnable(boolean show);    //状态栏显示返回键
        void setRadioGroupVisibility(int visibility);   //隐藏RadioGroup
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try
        {
            fragChangeListener =(FragmentChangedListener)context;
        }
        catch (Exception e) {
        }
    }

    /**
     * 切换Fragment 重塑视图,二维码编辑栏
     */
    abstract void resume();


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
        {
            resume();
            showLog("hiddenChange");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resume();
    }
}

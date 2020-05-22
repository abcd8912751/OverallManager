package com.furja.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.furja.overall.ui.LoginActivity;
import com.furja.utils.Constants;
import com.furja.utils.SharpBus;

/**
 * Created by zhangmeng on 2017/12/3.
 */

public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext=getContext();
    }

    public void switchToLogin() {
        Intent intent=new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public  int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels; //横屏时使用高度
    }
}

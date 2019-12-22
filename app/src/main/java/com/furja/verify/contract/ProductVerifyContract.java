package com.furja.verify.contract;

import android.widget.TextView;

/**
 * Created by zhangmeng on 2017/12/18.
 */

public interface ProductVerifyContract {

    interface View {
        void showProductInfo(String text);
        void resetView();
        String getTextString(int viewId);
        void setKeyListener(android.view.View.OnKeyListener listener);
        void setEditorActionListener(TextView.OnEditorActionListener listener);
        String getProductInfo();
        void showVerifyDialog(String title);
        void hideBattery2Editor();
        void hideBarCodeEditor();
        void showBarCodeEditor();
    }

    interface Presenter {
    }
}

package com.furja.overall.beans;

import com.furja.common.Preferences;
import com.furja.utils.Constants;

/**
 * Created by zhangmeng on 2017/12/8.
 */

public class EmptyWorkScene implements WorkScene {
    @Override
    public String getDialogContent() {
        return "";
    }

    @Override
    public String getDialogTitle() {
        return "选择工作场景";
    }

    @Override
    public void selectSwitchYes() {
        Preferences.saveSourceType(Constants.TYPE_BADLOG_WITHBTN+"");
    }

    @Override
    public void selectSwitchNo() {
        Preferences.saveSourceType(Constants.TYPE_BADLOG_WITHKEY+"");
    }

    @Override
    public String getYesButtonLabel() {
        return Constants.BUTTON_FRAGMENT_TITLE;
    }

    @Override
    public String getNoButtonLabel() {
        return Constants.KEY_FRAGMENT_TITLE;
    }
}

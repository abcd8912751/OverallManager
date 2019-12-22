package com.furja.alertsop.contract;

import android.widget.BaseAdapter;
import android.widget.ListView;

import com.furja.alertsop.beans.WorkOrderInfo;

/**
 * Created by zhangmeng on 2017/12/3.
 */

public interface WorkOrderContract {
    interface Model {
        public int getItemCount();
        public String getTitle(int position);
        public String getContent(int position);
    }

    interface View {
        public void setListAdapter(BaseAdapter baseAdapter);
        public ListView getAdapterView();
        public void syncAndUpdateBadData();
        public void setSelection(int position);

        void requestFocus();

        void clearFocus();

        void onBackPress();
    }

    interface Presenter {
        public void setListAdapter();
        public WorkOrderInfo getWorkOrderInfo();
    }
}

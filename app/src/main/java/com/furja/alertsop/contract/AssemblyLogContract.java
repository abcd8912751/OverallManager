package com.furja.alertsop.contract;

import android.content.Context;

import com.furja.alertsop.beans.BadLogEntry;
import com.furja.alertsop.beans.MaterialInfo;

import java.util.List;

public interface AssemblyLogContract {
    interface Model {
    }

    interface View {
        List<BadLogEntry> getDatas();
        Context getContext();
        void showMaterialInfo(MaterialInfo materialInfo);
        void resetView();
        void performAction(int id);
    }

    interface Presenter {
        void resetFieldData();
    }
}

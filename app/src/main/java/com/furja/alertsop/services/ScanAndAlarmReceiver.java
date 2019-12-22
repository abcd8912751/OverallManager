package com.furja.alertsop.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.furja.utils.SharpBus;

import java.util.Calendar;

import static com.furja.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.utils.Utils.showLog;

/**
 * 定时任务接收器
 */
public class ScanAndAlarmReceiver extends BroadcastReceiver {
    int requestCount=0;
    int errorCount=0;
    int requestOrderCount=0;
    @Override
    public void onReceive(Context context, Intent intent) {
        showLog("接收到广播");
        if(intent!=null) {
            analyseIntent(intent);
        }
    }

    /**
     * 解析Intent
     * @param intent
     */
    private void analyseIntent(Intent intent) {
        String action=intent.getAction();
        if(!TextUtils.isEmpty(action)){
            if(action.contains("barcode")){
                String barcode=intent.getStringExtra("BARCODE");
                if(barcode!=null&&!barcode.isEmpty()) {
                    barcode = barcode.toUpperCase();
                    barcode = barcode.replace("\n", "");
                    barcode = barcode.replace("\r", "");
                    SharpBus.getInstance().post(TAG_SCAN_BARCODE, barcode);
                }
            }
        }
    }





    public static long getTriggerMillis(){
        Calendar calendar = Calendar.getInstance();
        Calendar triggerCal = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        triggerCal.set(Calendar.HOUR_OF_DAY,7);
        triggerCal.set(Calendar.MINUTE,30);
        if (!calendar.before(triggerCal)) {
            triggerCal.set(Calendar.HOUR_OF_DAY,19);
            triggerCal.set(Calendar.MINUTE,30);
            if (!calendar.before(triggerCal)) {
                triggerCal.set(Calendar.HOUR_OF_DAY,7);
                triggerCal.add(Calendar.DAY_OF_MONTH,1);
            }
        }
        return triggerCal.getTimeInMillis();
    }
}

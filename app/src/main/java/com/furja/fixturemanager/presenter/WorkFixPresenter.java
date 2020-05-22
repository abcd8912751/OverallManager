package com.furja.fixturemanager.presenter;

import com.furja.utils.SharpBus;
import com.furja.utils.TextInputListener;
import com.furja.fixturemanager.contract.WorkFixContract;

import static com.furja.utils.Constants.TAG_SCAN_BARCODE;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

public class WorkFixPresenter implements WorkFixContract.Presenter {
    SharpBus sharpBus=SharpBus.getInstance();
    public WorkFixPresenter() {
        sharpBus.register(TAG_SCAN_BARCODE,this,String.class)
                .subscribe((scanString->{
                    if(scanString.contains(TextInputListener.INPUT_ERROR))
                        showToast(scanString);
                    else
                        showLog("收到:"+scanString);
                }));
    }

    /**
     *
     * @param fixtureNumber
     */
    private void acquireFixture(String fixtureNumber)
    {

    }
}

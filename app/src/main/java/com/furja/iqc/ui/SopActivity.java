package com.furja.iqc.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.KeyEvent;
import android.view.View;

import com.furja.overall.R;
import com.furja.presenter.SopOnlinePresenter;
import com.furja.overall.ui.BaseActivity;

import static android.view.KeyEvent.KEYCODE_F7;
import static com.furja.utils.Utils.showLog;

public class SopActivity extends BaseActivity {
    SopOnlinePresenter sopOnlinePresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sop);
        View view=findViewById(android.R.id.content);
        sopOnlinePresenter=new SopOnlinePresenter(view,this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event!=null) {
            int keyCode = event.getKeyCode();
            if(keyCode == KeyEvent.KEYCODE_BRIGHTNESS_DOWN
                    || keyCode == KeyEvent.KEYCODE_BRIGHTNESS_UP
                    || keyCode == KEYCODE_F7) {
                sopOnlinePresenter.focusBarcodeInput();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

}

package com.furja.overall.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.furja.common.BaseFragment;
import com.furja.overall.R;
import com.furja.overall.ui.qc.InspectHistoryFragment;

import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

public class OneFragmentActivity extends BaseActivity {
    BaseFragment tempFragment;
    TextView text_title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onefragment);
        text_title = findViewById(R.id.label_appName);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        analyseIntent();
    }

    /**
     * 根据Intent跳转到不同的Fragment
     */
    private void analyseIntent() {
        String dataString="";
        if(getIntent()!=null)
            dataString=getIntent().getDataString();
        if(TextUtils.isEmpty(dataString)){
            showToast("来错地方了");
            finish();
        }
        else {
            int index=dataString.lastIndexOf(".");
            dataString=dataString.substring(index+1);
            switch (dataString){
                case "InspectHistoryFragment":
                    InspectHistoryFragment historyFragment = new InspectHistoryFragment();
                    transferFragment(historyFragment);
                    text_title.setText("质检历史");
                    break;
                    default:
                        showToast("来错地方了");
                        finish();
                        break;
            }
        }
    }

    public void transferFragment(BaseFragment to) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fts = fm.beginTransaction();
        if(tempFragment!=null) {
            if(tempFragment==to)
                return;
            fts.hide(tempFragment).commit();
        }
        this.tempFragment=to;
        fts = fm.beginTransaction();
        if(to.isAdded())
            fts.show(to);
        else
            fts.add(R.id.content_frame, to,to.toString());
        fts.commit();
    }
}

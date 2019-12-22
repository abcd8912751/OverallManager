package com.furja.overall.ui.qc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.furja.alertsop.ui.SopActivity;
import com.furja.common.BaseFragment;
import com.furja.iqc.ui.InspectIncomingActivity;
import com.furja.overall.FurjaApp;
import com.furja.overall.R;
import com.furja.overall.ui.LoginActivity;
import com.furja.overall.ui.OneFragmentActivity;
import com.furja.overall.ui.WebSurfActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.furja.utils.Constants.EXTRA_WEBVIEW_TITLE;
import static com.furja.utils.Constants.EXTRA_WEBVIEW_URL;
import static com.furja.utils.Utils.showLog;

public class QualityControlFragment extends BaseFragment {

    private QualityControlViewModel qualityControlViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qc, container, false);
        ButterKnife.bind(this,root);
        return root;
    }

    @OnClick({R.id.card_ipqc,R.id.card_qcboard,R.id.card_sopOnline,R.id.card_inspectHistory})
    public void onClick(View view){
        if(FurjaApp.getUser()==null){
            switchToLogin();
            return;
        }
        Intent intent=new Intent();
        switch (view.getId()){
            case R.id.card_ipqc:
                intent.setClass(getContext(), InspectIncomingActivity.class);
                startActivity(intent);
                break;
            case R.id.card_qcboard:
                intent.setClass(getContext(), WebSurfActivity.class);
                String iqcUrl="http://192.168.8.46:8118/FJAPIManage/views/FJ_QCAutoDispatch/FJ_QCAutoDispatchForInNetWorkForFChecker.html?FCheckerName=";
                String userName = FurjaApp.getUserName();
                iqcUrl = iqcUrl +userName;
                showLog(iqcUrl);
                intent.putExtra(EXTRA_WEBVIEW_URL,iqcUrl);
                intent.putExtra(EXTRA_WEBVIEW_TITLE,"派单看板");
                startActivity(intent);
                break;
            case R.id.card_sopOnline:
                intent.setClass(getContext(), SopActivity.class);
                startActivity(intent);
                break;
            case R.id.card_inspectHistory:
                intent.setData(Uri.parse(InspectHistoryFragment.class.getName()));
                intent.setClass(getContext(), OneFragmentActivity.class);
                startActivity(intent);
                break;
        }
    }
}
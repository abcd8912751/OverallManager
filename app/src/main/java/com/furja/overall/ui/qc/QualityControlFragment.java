package com.furja.overall.ui.qc;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.furja.iqc.ui.SopActivity;
import com.furja.common.BaseFragment;
import com.furja.iqc.ui.InspectIncomingActivity;
import com.furja.overall.FurjaApp;
import com.furja.overall.R;
import com.furja.overall.ui.OneFragmentActivity;
import com.furja.overall.ui.WebSurfActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static com.furja.utils.Constants.EXTRA_WEBVIEW_TITLE;
import static com.furja.utils.Constants.EXTRA_WEBVIEW_URL;
import static com.furja.utils.Constants.getCloudUrl;
import static com.furja.utils.Utils.dp2px;
import static com.furja.utils.Utils.showLog;

public class QualityControlFragment extends BaseFragment {
    @BindView(R.id.card_ipqc)
    CardView card_ipqc;
    @BindView(R.id.card_qcboard)
    CardView card_qcboard;
    @BindView(R.id.card_sopOnline)
    CardView card_sopOnline;
    @BindView(R.id.card_inspectHistory)
    CardView card_inspectHistory;
    private QualityControlViewModel qualityControlViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qc, container, false);
        ButterKnife.bind(this,root);
        setLayoutParamRatio(card_ipqc);
        setLayoutParamRatio(card_qcboard);
        setLayoutParamRatio(card_sopOnline);
        setLayoutParamRatio(card_inspectHistory);
        return root;
    }


    private void setLayoutParamRatio(CardView cardView) {
        ViewGroup.LayoutParams layoutParams=cardView.getLayoutParams();
        if(layoutParams!=null){
            int offset=dp2px(8);
            int width=getScreenWidth()/3-offset;
            int height= width+offset;
            layoutParams.height=height;
            layoutParams.width=width;
            cardView.setLayoutParams(layoutParams);
        }
    }

    @OnClick({R.id.card_ipqc,R.id.card_qcboard,R.id.card_sopOnline,R.id.card_inspectHistory})
    public void onClick(View view) {
        if(FurjaApp.getUser() == null){
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
                String iqcUrl = getCloudUrl()+"/FJ_QCAutoDispatch/views/FJ_QCAutoDispatch/FJ_QCAutoDispatchForInNetWorkForFChecker.html?FCheckerName=";
                String userName = FurjaApp.getUserName();
                iqcUrl = iqcUrl +userName;
                intent.putExtra(EXTRA_WEBVIEW_URL, iqcUrl);
                intent.putExtra(EXTRA_WEBVIEW_TITLE, "派单看板");
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
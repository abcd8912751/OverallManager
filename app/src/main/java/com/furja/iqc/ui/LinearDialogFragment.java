package com.furja.iqc.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.furja.overall.FurjaApp;
import com.furja.overall.R;
import com.furja.presenter.SopOnlinePresenter;
import com.furja.utils.SharpBus;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.view.KeyEvent.KEYCODE_F7;
import static com.furja.utils.Constants.TAG_CLOSE_DIALOG;
import static com.furja.utils.Constants.getCloudUrl;
import static com.furja.utils.Utils.getScreenWidth;
import static com.furja.utils.Utils.initWebView;
import static com.furja.utils.Utils.showLog;

public class LinearDialogFragment extends DialogFragment {
    public static final int DIALOG_QCDISPATCH_WEB=1;
    public static final int DIALOG_SOP_ONLINE=2;
    private int dialogType;
    SopOnlinePresenter sopPresenter;
    FragmentInterface fragmentInterface;
    Bundle webViewState;
    WebView webView;
    View rootView;
    int layoutID;
    public LinearDialogFragment() {
        this(DIALOG_QCDISPATCH_WEB);
    }

    public LinearDialogFragment(int type) {
        this.dialogType=type;
        switch (type){
            case DIALOG_SOP_ONLINE:
                this.layoutID=R.layout.layout_lineardialog_sop;
                break;
                default:
                    this.layoutID=R.layout.layout_lineardialog_web;
                    break;
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.LinearDialogFragment);
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        if(rootView==null) {
            View.OnTouchListener touchListener=new View.OnTouchListener() {
                float lastX;
                @Override
                public boolean onTouch(View v, MotionEvent motionEvent) {
                    int offsetX=getScreenWidth()/4;
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastX = motionEvent.getRawX();
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            float currX = motionEvent.getRawX();
                            if(currX-lastX>offsetX&&dialogType==DIALOG_SOP_ONLINE){
                                dismiss();
                                return true;
                            } else if(lastX-currX>offsetX&&dialogType==DIALOG_QCDISPATCH_WEB){
                                dismiss();
                                return true;
                            } else
                                return false;
                    }
                    if(v instanceof WebView)
                        v.onTouchEvent(motionEvent);
                    return true;
                }
            };
            Context context = getContext();
            rootView = View.inflate(context, layoutID, null);
            TextView hideText = rootView.findViewById(R.id.text_hide);
            if (dialogType == 1) {
                String url = getCloudUrl()+"/FJ_QCAutoDispatch/views/FJ_QCAutoDispatch/FJ_QCAutoDispatchForInNetWorkForFChecker.html?FCheckerName=";
                String userName = FurjaApp.getUserName();
                url = url + userName;
                if (webView == null) {
                    webView = rootView.findViewById(R.id.webview);
                    initWebView(webView);
                    webView.loadUrl(url);
                    webView.setOnTouchListener(touchListener);
                }
            } else {
                View stub = rootView.findViewById(R.id.viewStub);
                stub.setVisibility(View.VISIBLE);
                sopPresenter=new SopOnlinePresenter(rootView,this);
                rootView.setOnTouchListener(touchListener);
            }
            hideText.setOnClickListener(v->{
                dismiss();
            });
        }
        else{
            ViewGroup viewGroup = (ViewGroup) rootView.getParent();
            if (viewGroup != null)
                viewGroup.removeView(rootView);
        }
        if(dialogType==DIALOG_QCDISPATCH_WEB)
            dialog.getWindow().getAttributes().windowAnimations = R.style.AnimHorizontal;
        else{
            dialog.getWindow().getAttributes().windowAnimations=R.style.SopDialogFragment;
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    showLog(event.toString());
                    if(keyCode == KeyEvent.KEYCODE_BRIGHTNESS_DOWN
                            || keyCode == KeyEvent.KEYCODE_BRIGHTNESS_UP
                            || keyCode == KEYCODE_F7) {
                        if(sopPresenter!=null)
                            sopPresenter.focusBarcodeInput();
                        return true;
                    }
                    return false;
                }
            });
            if(sopPresenter!=null)
                sopPresenter.listenSharpBus(this);
            SharpBus.getInstance().register(TAG_CLOSE_DIALOG,this)
                    .observeOn(AndroidSchedulers.mainThread())
                    .as(AutoDispose.<Object>autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                    .subscribe(event->{
                        dismiss();
                    });
        }
        dialog.setContentView(rootView);
    }


    @Override
    public void onPause() {
        if(webView!=null)
            webView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(webView!=null)
            webView.onResume();
    }


    public void subscribe(FragmentInterface fragmentInterface) {
        this.fragmentInterface = fragmentInterface;
    }


    public interface FragmentInterface{
        default void onBack(){};
        void onScrollToLeft();
        void onScrollToRight();
    }
}

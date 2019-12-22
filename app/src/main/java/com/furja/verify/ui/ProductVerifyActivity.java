package com.furja.verify.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.furja.common.User;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.furja.overall.R;
import com.furja.overall.ui.LoginActivity;
import com.furja.verify.VerifyProductApp;
import com.furja.common.Preferences;
import com.furja.verify.contract.ProductVerifyContract;
import com.furja.verify.presenter.ProductVerifyPresenter;
import com.furja.verify.utils.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

import static com.furja.verify.utils.Constants.FURIA_LOGIN_URL;
import static com.furja.verify.utils.InneURL.FURJA_LOGIN_URL;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * 电池与成品校验 Activity
 */

public class ProductVerifyActivity extends AppCompatActivity implements ProductVerifyContract.View{
    @BindView(R.id.input_mainBarCode)
    TextInputLayout mainBarCode_input;  //成品条码输入框
    @BindView(R.id.input_batteryBarCode1)
    TextInputLayout batteryBarCode1_input;  //1号电池条码输入框
    @BindView(R.id.input_batteryBarCode2)
    TextInputLayout batteryBarCode2_input;  //2号电池条码输入框
    @BindView(R.id.product_info)
    TextView productInfo_text;
    ProductVerifyPresenter verifyPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyproduct);
        ButterKnife.bind(this);
        //设定滚动条
        productInfo_text.setMovementMethod(ScrollingMovementMethod.getInstance());
        verifyPresenter=new ProductVerifyPresenter(this);


    }

    @Override
    public void showProductInfo(String text) {
        productInfo_text.setText(text);
    }

    /**
     * 将当前页面清空
     */
    @Override
    public void resetView() {
        productInfo_text.setText("");
        mainBarCode_input.setVisibility(View.VISIBLE);
        mainBarCode_input.getEditText().setText("");
        batteryBarCode2_input.setVisibility(View.VISIBLE);
        clearEditAndDrawable();
        verifyPresenter.cleanDataBean();

        Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Thread.sleep(200);
                return "delayFinish";
            }}).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mainBarCode_input.requestFocus(); //成品条码输入框获取焦点
                    }
                });
    }

    public void clearEditAndDrawable()
    {
        try {
            batteryBarCode1_input.getEditText().getEditableText().clear();
            batteryBarCode2_input.getEditText().getEditableText().clear();
            batteryBarCode1_input.getEditText().
                    setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            batteryBarCode2_input.getEditText().
                    setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据View的ID来获取控件的内容
     * @param viewId
     * @return
     */
    @Override
    public String getTextString(int viewId) {
        CharSequence charSequence=null;
        switch (viewId)
        {
            case R.id.edit_mainBarCode:
                charSequence=mainBarCode_input.getEditText().getText();
                Observable.fromCallable(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        Thread.sleep(500);
                        return "delayFinish";
                    }}).observeOn(AndroidSchedulers.mainThread())
                       .subscribeOn(Schedulers.newThread())
                       .subscribe(new Consumer<Object>() {
                           @Override
                           public void accept(Object o) throws Exception {
                               batteryBarCode1_input.requestFocus();
                           }
                       });
                break;
            case R.id.edit_batteryBarCode1:
                charSequence=batteryBarCode1_input.getEditText().getText();
                setBarCode(batteryBarCode1_input,charSequence);
                if(batteryBarCode2_input.getVisibility()==View.VISIBLE)
                    Observable.fromCallable(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            Thread.sleep(500);
                            return "delayFinish";
                        }}).observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object o) throws Exception {
                                    batteryBarCode2_input.requestFocus();
                                }
                            });
                break;
            case R.id.edit_batteryBarCode2:
                charSequence=batteryBarCode2_input.getEditText().getText();
                setBarCode(batteryBarCode2_input,charSequence);
                break;
        }
        String textString="";
        if(!TextUtils.isEmpty(charSequence))
            textString=charSequence.toString();
        return textString;
    }

    private void setBarCode(TextInputLayout inputLayout, CharSequence charSequence) {
        if(!TextUtils.isEmpty(charSequence))
            inputLayout.getEditText()
                    .setText(charSequence.toString());
    }

    /**
     * 设定 按键监听
     * @param listener
     */
    @Override
    public void setKeyListener(View.OnKeyListener listener) {
        mainBarCode_input.getEditText().setOnKeyListener(listener);
        batteryBarCode1_input.getEditText().setOnKeyListener(listener);
        batteryBarCode2_input.getEditText().setOnKeyListener(listener);
    }
    /**
     * 设定输入法的Enter监听
     * @param listener
     */
    @Override
    public void setEditorActionListener(TextView.OnEditorActionListener listener) {
        mainBarCode_input.getEditText().setOnEditorActionListener(listener);
        batteryBarCode2_input.getEditText().setOnEditorActionListener(listener);
        batteryBarCode1_input.getEditText().setOnEditorActionListener(listener);
    }

    /**
     * 返回文本框里显示的成品信息
     * @return
     */
    @Override
    public String getProductInfo() {
        if(TextUtils.isEmpty(productInfo_text.getText()))
            return "";
        else
            return productInfo_text.getText().toString();
    }

    /**
     * 提示错误
     * @param viewId
     */
    public void showError(int viewId) {
        if(viewId==R.id.edit_batteryBarCode1)
        {
            batteryBarCode1_input.setError("电池包条码不匹配");
        }
        else if(viewId==R.id.edit_batteryBarCode2)
        {
            batteryBarCode2_input.setError("电池包条码不匹配");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_verify, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_release)
        {
            MaterialDialog.Builder builder
                    =new MaterialDialog.Builder(this);
            builder.title("验证用户权限");
            builder.input("以 用户名,密码 的方式输入", "", new MaterialDialog.InputCallback() {
                @Override
                public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                    if(TextUtils.isEmpty(input))
                    {
                        showToast("请输入用户名,密码");
                    }
                    else
                    {
                        String inputString=input.toString();
                        if(inputString.indexOf(",")>0||inputString.indexOf("，")>0)
                        {
                            String[] strings=new String[2];
                            try {
                                if(inputString.contains(","))
                                    strings=inputString.split(",");
                                else
                                    strings=inputString.split("，");
                                if(strings.length<2)
                                {
                                    showToast("格式不正确,请将用户名、密码以逗号分隔");
                                    return;
                                }
                            } catch (Exception e) {
                                showToast("格式不正确,请将用户名、密码以逗号分隔");
                                return;
                            }
                            String userName=strings[0];
                            String password=strings[1];
                            String url=FURJA_LOGIN_URL;
                            if(!Constants.isInnerNet)
                                url=FURIA_LOGIN_URL;
                            OkHttpUtils
                                    .get()
                                    .url(url +"GetLoginList")
                                    .addParams("user",userName)
                                    .addParams("password",password)
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int i) {
                                            showToast("网络异常");
                                        }
                                        @Override
                                        public void onResponse(String response, int i) {
                                            showLog(response);
                                            try {
                                                JSONObject jsonObject=new JSONObject(response);
                                                if(jsonObject!=null)
                                                {
                                                    showLog("jsonLength:"+jsonObject.length());
                                                    String info=jsonObject.getString("LoginInfo");
                                                    showLog("用户信息:"+info);
                                                    if(isLoginSuccess(info))
                                                    {
                                                        String string[]= info.split(",");
                                                        if(string[1].contains("2"))
                                                        {
                                                            dialog.cancel();
                                                            Intent intent
                                                                    =new Intent(ProductVerifyActivity.this,ReleaseActivity.class);
                                                            startActivity(intent);
                                                        }
                                                        else
                                                            showToast("该账户没有释放权限");
                                                    }
                                                    else
                                                        showToast("用户名或密码错误");
                                                }
                                            } catch (Exception e) {

                                            }
                                        }
                                        public boolean isLoginSuccess(String loginInfo)
                                        {
                                            if(loginInfo==null)
                                                return false;   //为空
                                            if(loginInfo.contains("false")
                                                    ||loginInfo.contains("not"))
                                                return false;
                                            else
                                                return true;
                                        }
                                    });
                        }
                        else
                            showToast("格式不正确,请将用户名、密码以逗号分隔");
                    }
                }
            });
            builder.autoDismiss(false);
            builder.show();
        }
        else if(id==R.id.action_logOut)
        {
            switchUser();
        }
        else
        {
            Intent intent=new Intent(this,JYProductVerifyActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 切换用户
     */
    private void switchUser() {
        Preferences.saveAutoLogin(false);
        User user =new User("","");
        Preferences.saveUser(user);
        VerifyProductApp.setUserAndSave(null);
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 显示校验结果的对话框
     * @param title
     */
    @Override
    public void showVerifyDialog(String title)
    {
        showLog("我进来了");
        AlertDialog.Builder builder
                =new AlertDialog.Builder(this);
        builder.setTitle("校验结果 : "+title);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                resetView();
            }
        });
        AlertDialog dialog=builder.create();
        if(title.contains("OK"))
            dialog.setIcon(R.mipmap.icon_dialog_ok);
        else
            dialog.setIcon(R.mipmap.icon_dialog_ng);
        dialog.setCanceledOnTouchOutside(false);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null&&imm.isActive())
            imm.hideSoftInputFromWindow(batteryBarCode1_input.getWindowToken(), 0);
        dialog.show();
    }

    /**
     * 有1个或2个电池包,若只有一个则隐藏第2个电池条码录入框
     */
    @Override
    public void hideBattery2Editor() {
        batteryBarCode2_input.setVisibility(View.GONE);
    }

    @Override
    public void hideBarCodeEditor() {
        mainBarCode_input.setVisibility(View.GONE);
    }

    @Override
    public void showBarCodeEditor() {
        mainBarCode_input.setVisibility(View.VISIBLE);
    }

}

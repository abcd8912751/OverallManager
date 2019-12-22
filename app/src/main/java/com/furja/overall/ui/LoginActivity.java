package com.furja.overall.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.furja.common.Preferences;
import com.furja.overall.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import static com.furja.utils.Utils.showLog;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.btn_startLogin)
    Button startLoginBtn;
    @BindView(R.id.edit_login_operator)
    AppCompatEditText operatorEdit;
    @BindView(R.id.edit_login_password)
    AppCompatEditText passwordEdit;
    private String password;
    private String user;
    int loginCount=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlogin);
        ButterKnife.bind(this);
        password= Preferences.getPassword();
        user= Preferences.getUsername();
        if(!TextUtils.isEmpty(user)) {
            operatorEdit.setText(user);
            passwordEdit.setText(password);
            if(Preferences.isAutoLogin())
                startLogin();
        }
        startLoginBtn.setOnClickListener(v->{
            startLogin();
        });
    }

    @OnTextChanged(value = {R.id.edit_login_operator,R.id.edit_login_password}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void editOperator(Editable s) {
        if (s==null)
            return;
        if(TextUtils.isEmpty(operatorEdit.getText())
                ||TextUtils.isEmpty(passwordEdit.getText()))
            startLoginBtn.setEnabled(false);
        else
            startLoginBtn.setEnabled(true);
    }

    /**
     * 开始登录
     */
    private void startLogin() {
        String user=operatorEdit.getText().toString();
        String password=passwordEdit.getText().toString();
        startLoginBtn.setText("登录中...");
        startLoginBtn.setEnabled(false);
        operatorEdit.setEnabled(false);
        passwordEdit.setEnabled(false);
        login(user, password);
    }

    @Override
    protected void resumeView() {
        startLoginBtn.setText("登录");
        startLoginBtn.setEnabled(true);
        operatorEdit.setEnabled(true);
        passwordEdit.setEnabled(true);
    }

    @Override
    protected void switchToHome() {
        Intent intent=new Intent(LoginActivity.this, MasterActivity.class);
        startActivity(intent);
        finish();
    }
}

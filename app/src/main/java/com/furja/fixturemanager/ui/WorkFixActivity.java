package com.furja.fixturemanager.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import com.furja.overall.R;
import com.furja.utils.TextInputListener;
import com.furja.fixturemanager.contract.WorkFixContract;
import com.furja.fixturemanager.presenter.WorkFixPresenter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 工装管理模块
 */
public class WorkFixActivity extends AppCompatActivity implements WorkFixContract.View {
    @BindView(R.id.edit_barCode)
    AppCompatEditText edit_barCode;
    WorkFixPresenter workFixPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workfix);
        ButterKnife.bind(this);
        setInputListener();
        workFixPresenter=new WorkFixPresenter();
    }

    private void setInputListener() {
        TextInputListener listener=new TextInputListener();
        edit_barCode.setOnKeyListener(listener);
        edit_barCode.setOnEditorActionListener(listener);
    }

}

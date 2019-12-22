package com.furja.iqc.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.furja.overall.R;

/**
 * 简洁的Dialog
 */
public class ConciseDialog extends Dialog{
    TextView mTitle;
    TextView mContent;
    Button mPositiveBtn;
    Button mNegativeBtn;
    public ConciseDialog(@NonNull Context context) {
        super(context,R.style.ConciseDialog);
        View view
                = LayoutInflater.from(context)
                .inflate(R.layout.concise_dialog_layout,null);
        mTitle=view.findViewById(R.id.dialog_title);
        mContent=view.findViewById(R.id.dialog_content);
        mPositiveBtn=view.findViewById(R.id.button_positive);
        mNegativeBtn=view.findViewById(R.id.button_negative);
        setContentView(view);
    }

    public void setPositiveButton(String positiveLabel,final OnClickListener clickListener)
    {
        mPositiveBtn.setText(positiveLabel);
        mPositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener
                        .onClick(ConciseDialog.this,DialogInterface.BUTTON_POSITIVE);
            }
        });

    }

    public void setNegativeButton(String negativeLabel,final OnClickListener clickListener)
    {
        mNegativeBtn.setText(negativeLabel);
        mNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener
                        .onClick(ConciseDialog.this,DialogInterface.BUTTON_NEGATIVE);
            }
        });
    }
    public void setTitle(String title)
    {
        mTitle.setText(title);
    }
    public void setContent(String content)
    {
        mContent.setText(content);
    }


}

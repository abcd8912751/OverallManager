package com.furja.overall.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.furja.overall.R;

import java.util.Calendar;

import static com.furja.utils.Utils.showToast;

/**
 * Created by zhangmeng on 2018/1/22.
 */

public class DatePickerDialogFragment extends AppCompatDialogFragment {
    Calendar startCalendar,endCalendar;
    DatePicker datePickerStart,datePickerEnd;
    DatePickerChangerListener pickerChangerListener;
    Button button_set;
    DialogListener dialogListener;

    public DatePickerDialogFragment()
    {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_datepicker,container);
        datePickerStart=
                (DatePicker)view.findViewById(R.id.datePickerStart);
        datePickerEnd=
                (DatePicker)view.findViewById(R.id.datePickerEnd);
        button_set=(Button)view.findViewById(R.id.cycle_set);
        pickerChangerListener
                =new DatePickerChangerListener();
        Calendar calendar=Calendar.getInstance();
        datePickerEnd.setMaxDate(calendar.getTimeInMillis());
        datePickerStart.setMaxDate(calendar.getTimeInMillis());
        calendar.set(2018,05,29);
        datePickerStart.setMinDate(calendar.getTimeInMillis());
        datePickerEnd.setMinDate(calendar.getTimeInMillis());
        datePickerStart
                .init(formatYear(startCalendar),formatMonth(startCalendar),formatDay(startCalendar),pickerChangerListener);
        datePickerEnd
                .init(formatYear(endCalendar),formatMonth(endCalendar),formatDay(endCalendar),pickerChangerListener);
        startCalendar=Calendar.getInstance();
        endCalendar=Calendar.getInstance();
        button_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(compare(endCalendar,startCalendar))
                {
                    showToast("截止日期在开始日期之前,操作无效");
                }
                else
                {
                    if(dialogListener!=null)
                    {
                        dialogListener.onSet();
                        showToast("正在请求数据");
                    }
                    Dialog dialog=getDialog();
                    if(dialog!=null)
                        dialog.cancel();
                }

            }
        });
        return view;
    }

    /**
     * 获取 Calendar的year
     * @return
     */
    private int formatYear(Calendar calendar)
    {
        if(calendar==null)
            calendar=Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }
    /**
     * 获取 Calendar的 月
     * @return
     */
    private int formatMonth(Calendar calendar)
    {
        if(calendar==null)
            calendar=Calendar.getInstance();
        return calendar.get(Calendar.MONTH)+1;
    }

    /**
     * 获取 Calendar的Day
     * @return
     */
    private int formatDay(Calendar calendar)
    {
        if(calendar==null)
            calendar=Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    private class DatePickerChangerListener implements DatePicker.OnDateChangedListener
    {
        @Override
        public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
            if(datePicker.getId()==R.id.datePickerStart)
            {
                startCalendar.set(year,month,day);
            }
            else
            {
                endCalendar.set(year,month,day);
                if(compare(endCalendar,startCalendar))
                {
                    showToast("截止日期在开始日期之前,请重设");
                }
            }
        }
    }

    /**
     * 比较前后两个Calendar
     * @param endCal
     * @param startCal
     * @return
     */
    public boolean compare(Calendar endCal,Calendar startCal)
    {
        if(endCal.compareTo(startCal)<0)
        {
            if(formatYear(startCal)==formatYear(endCal))
            {
                if(formatMonth(startCal)==formatMonth(endCal))
                    if(formatDay(startCal)==formatDay(endCal))
                        return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if(dialogListener!=null)
            dialogListener.onCancel();
    }

    public Calendar getStartCalendar() {
        return startCalendar;
    }

    public void setStartCalendar(Calendar startCalendar) {
        this.startCalendar = startCalendar;
    }

    public Calendar getEndCalendar() {
        return endCalendar;
    }

    public void setEndCalendar(Calendar endCalendar) {
        this.endCalendar = endCalendar;
    }

    public interface DialogListener
    {
        void onSet();
        void onCancel();
    }


    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

}

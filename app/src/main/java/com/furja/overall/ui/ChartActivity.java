package com.furja.overall.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.furja.overall.FurjaApp;
import com.furja.overall.R;
import com.furja.common.Preferences;
import com.furja.overall.json.BadBeanJSON;
import com.furja.utils.BadBeanUtils;
import com.furja.utils.Constants;
import com.furja.overall.view.DatePickerDialogFragment;
import com.furja.overall.view.LegendView;
import com.kyleduo.switchbutton.SwitchButton;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apmem.tools.layouts.FlowLayout;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.computator.ChartComputator;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.renderer.ColumnChartRenderer;
import lecho.lib.hellocharts.view.ColumnChartView;
import okhttp3.Call;

import static android.view.KeyEvent.META_CAPS_LOCK_ON;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * 查看报表的 Activity
 */
public class ChartActivity extends BaseActivity {
    ColumnChartView columnChartView;
    SwitchButton switchScroll;
    Button btn_datePicker;
    BadBeanUtils utils;
    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;    //用于执行定时获取数据
    Runnable scrollRunnable;
    FlowLayout linearLayout;
    float curr_Right=-1;
    public  boolean SCROLL_RIGHT=true;
    int requestCount=0;
    private float zoomLevel=1;
    private AlertDialog alertDialog;
    private DatePickerDialogFragment dialogFragment;
    Calendar startCalendar,endCalendar;
    public int deviceWidth;
    public boolean isShowing=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        columnChartView=(ColumnChartView)findViewById(R.id.columnChart);
        linearLayout=(FlowLayout) findViewById(R.id.legendLayout);
        switchScroll=(SwitchButton)findViewById(R.id.switch_autoScroll);
        btn_datePicker=(Button)findViewById(R.id.btn_pickerDate);
        scrollRunnable=new Runnable() {
            @Override
            public void run() {
                if (!switchScroll.isChecked())
                    return;
                Viewport viewport=columnChartView.getCurrentViewport();
                ChartComputator chartComputator=columnChartView.getChartComputator();
                int chartLeft=chartComputator.getContentRectMinusAllMargins().left;
                if(chartComputator.getMaximumViewport().left==viewport.left
                        ||chartComputator.getFirstLeft()>chartLeft+10)
                {
                    SCROLL_RIGHT =true;
                    showLog("开始掉头向右滚");
                    if(chartComputator.isFlyEnd())
                        return;
                }
                if(chartComputator.isFlyEnd())
                {
                    SCROLL_RIGHT =false;
                    showLog("开始掉头向左滚");
                }
                if(!SCROLL_RIGHT)
                    scrollToLeft();
                else
                    scrollToRight();
            }
        };
        deviceWidth=getScreenWidth();
        showLog("deviceWidth:"+deviceWidth);
        columnChartView.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                if(utils!=null)
                {
                    String msg=utils.getDialogMsg(columnIndex,subcolumnIndex);
                    showDetailDialog(msg);
                }
                else
                    showToast("正在更新数据,请稍候重试");
            }

            @Override
            public void onValueDeselected() {

            }
        });


        btn_datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        startCalendar=Calendar.getInstance();
        endCalendar=Calendar.getInstance();
    }

    private void showDatePickerDialog() {
        String basicDialog="datePicker";
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(basicDialog);
        if (null != fragment) {
            manager.beginTransaction().remove(fragment);
        }
        if(dialogFragment==null)
        {
            dialogFragment = new DatePickerDialogFragment();
            dialogFragment.setDialogListener(new DatePickerDialogFragment.DialogListener() {
                @Override
                public void onSet() {
                    startCalendar=dialogFragment.getStartCalendar();
                    endCalendar=dialogFragment.getEndCalendar();
                    requestJson();
                }

                @Override
                public void onCancel() {
                    isShowing=false;
                }
            });
        }
        dialogFragment.show(manager, basicDialog);
        isShowing=true;
    }


    /**
     * 点击子柱弹出对话框
     * @param msg
     */
    private void showDetailDialog(String msg)
    {
        AlertDialog.Builder builder
                =new AlertDialog.Builder(ChartActivity.this);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }



    private void scrollToLeft()
    {
        MotionEvent event;
        float X=deviceWidth*195/10000;
        float grads=(deviceWidth*95/100)/5;
        long downTime=SystemClock.uptimeMillis();
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X+grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X+grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X+grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X+grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X+grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X+grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, deviceWidth*988/1000, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
    }

    private void scrollToRight()
    {
        ChartComputator chartComputator=columnChartView.getChartComputator();
        int deviceRight=chartComputator.getContentRectMinusAllMargins().right;
        int width=chartComputator.getContentRectMinusAllMargins().width();
        float curWidth = chartComputator.getVisibleViewport().width();
        MotionEvent event;boolean isArrive=false;
        float X=deviceRight*98/100,start;
        float grads=(width*95/100)/5,distanceX=chartComputator.getLastRight()-deviceRight+20;
        start=X;
        if(grads>distanceX) {
            isArrive=true;
            return;
        }
        long downTime=SystemClock.uptimeMillis();

        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        delay();
        X=X-grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        if(isArrive)
            return;
        delay();
        X=X-grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        if(isArrive)
            return;
        delay();
        X=X-grads;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
        if(isArrive)
            return;
        delay();
        X=X-grads;
        if(isArrive)
            return;
        event=MotionEvent.obtain(downTime,
                SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, X, 420, META_CAPS_LOCK_ON);
        columnChartView.onTouchEvent(event);
    }

    /**
     * 延时200 ms
     */
    private void delay() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
            showToast(e.toString());
        }
    }




    /**
     * 自动滚动和定时获取数据刷新视图
     */
    private void autoScrollAndFreshRegular() {
        scheduledThreadPoolExecutor
                =new ScheduledThreadPoolExecutor(3);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(scrollRunnable);
            }
        },1500,5000, TimeUnit.MILLISECONDS);

        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                requestJson();
            }
        },0,60, TimeUnit.MINUTES);
    }

    /**
     *显示数据Json
     */
    private void displayJson(String response)
    {
        columnChartView.resetViewports();
        try {
            BadBeanJSON json= JSON.parseObject(response,BadBeanJSON.class);
            utils=new BadBeanUtils(json.getErrData());
            utils.buildXYValue();
            List<String> workPlaceIDs=utils.getWorkplaceIDs();
            int columnCount=workPlaceIDs.size();
            List<Column> columns=new ArrayList<Column>();
            int maxSubcolumnSize=0;
            for(int columnIndex=0;columnIndex<columnCount;columnIndex++)
            {
                List<SubcolumnValue> subcolumnValues=new ArrayList<SubcolumnValue>();
                List<Float> childValues=utils.getyValues().get(columnIndex);
                for(int i=0;i<childValues.size();i++)
                {
                    subcolumnValues.add(new SubcolumnValue(childValues.get(i),utils.getColor(columnIndex,i)));
                }
                if(columnIndex == 0)
                    maxSubcolumnSize=childValues.size();
                Column column=new Column(subcolumnValues);
                column.setHasLabels(true);
                columns.add(column);
            }
            ColumnChartData chartData=new ColumnChartData(columns);
            chartData.setValueLabelBackgroundEnabled(false);// 设置数据背景是否跟随节点颜色
            Axis axisLeft = new Axis();
            Axis axisBootom = new Axis();
            axisBootom.setTextColor(Color.BLACK);
            axisLeft.setTextColor(Color.BLACK);
            axisBootom.setHasLines(true);
            axisLeft.setHasLines(true);
            List<AxisValue> axisValues=new ArrayList<AxisValue>();
            for (int j=0;j<workPlaceIDs.size();j++) {
                axisValues.add(new AxisValue(j).setLabel(workPlaceIDs.get(j)));
            }
            axisBootom.setValues(axisValues);
            chartData.setAxisYLeft(axisLeft);
            chartData.setAxisXBottom(axisBootom);
            chartData.setValueLabelsTextColor(Color.BLACK);
            columnChartView.getChartComputator()
                    .resetTempRawX(axisBootom.getValues().size());
            columnChartView.setColumnChartData(chartData);
            columnChartView.setZoomType(ZoomType.HORIZONTAL);
            columnChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
            this.zoomLevel= Math.max(maxSubcolumnSize,4);
            ColumnChartRenderer columnChartRenderer
                    =(ColumnChartRenderer)columnChartView.getChartRenderer();
            columnChartRenderer.setBaseColumnWidth(50);
            columnChartView.setChartRenderer(columnChartRenderer);
            showLog("zoomLevel:"+zoomLevel);
            if (zoomLevel>1) {
                zoom(zoomLevel);
            }
            addLegendText(utils);

        }
        catch (Exception e) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            printWriter.close();
            showLog(writer.toString());
        }
    }

    /**
     * 显示无数据的对话框
     */
    public void showDialog()
    {
        AlertDialog.Builder builder
                =new AlertDialog.Builder(ChartActivity.this);
        builder.setMessage("暂无有效数据");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                alertDialog=null;
                showLog("置空了我");
            }
        });
        if(alertDialog==null)
            alertDialog=builder.create();
        if(!alertDialog.isShowing())
            alertDialog.show();
    }


    /**
     * 缩放指定比例
     * @param level
     */
    public void zoom(final float level)
    {
        if(scheduledThreadPoolExecutor.isTerminated())
        {
            showLog("我已经被关闭了");
        }
        scheduledThreadPoolExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                columnChartView.setZoomLevel(0,0,level);
            }
        },1,TimeUnit.SECONDS);
    }


    /**
     * 添加图例
     * @param utils
     */
    public void addLegendText(BadBeanUtils utils)
    {
        linearLayout.removeAllViews();
        List<String> typeLabels=utils.getTypeLabels();
        for(int i=0;i<typeLabels.size();i++)
        {
            LegendView view=new LegendView(this);
            view.setBackgroundColor(utils.getColor(i));
            view.setText(typeLabels.get(i));
            linearLayout.addView(view,i);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        if(scheduledThreadPoolExecutor!=null)
            scheduledThreadPoolExecutor.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoScrollAndFreshRegular();
    }

    public  int getScreenWidth()
    {
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels; //横屏时使用高度
    }


    public void requestJson()
    {
        utils=null;
        final String uploadUrl= Constants.getFjBadtypetotalWorkplaceUrl();
        final SimpleDateFormat formater
                = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Observable.fromCallable(new Callable<Object>() {
                @Override
                public URLConnection call() throws Exception {
                    URL url =  new URL(uploadUrl);
                    URLConnection uc = url.openConnection();//生成连接对象
                    uc.connect();
                    return uc;
                }})
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) {
                            URLConnection uc=(URLConnection)o;
                            Date date=new Date(uc.getDate());
                            String startString=formater.format(startCalendar.getTime());
                            String endString=formater.format(endCalendar.getTime());
                            Map<String,String> uploadParams=new HashMap<String,String>();
                            uploadParams.put("fstartdate",startString);
                            uploadParams.put("fenddate", endString);
                            OkHttpUtils
                                    .post()
                                    .url(uploadUrl)
                                    .params(uploadParams)
                                    .build()
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int i) {
                                            e.printStackTrace();
                                            requestCount++;
                                            if(requestCount<3)
                                                requestJson();
                                        }
                                        @Override
                                        public void onResponse(final String responce, int i) {
                                            requestCount=0;
                                            Log.e("ChartApp","获取Size:"+responce.length());
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    displayJson(responce);
                                                }
                                            });
                                        }
                                    });
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            requestCount++;
            if(requestCount<3)
                requestJson();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chart, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logOut)
        {

            Preferences.saveAutoLogin(false);
            FurjaApp.getInstance().setUserAndSave(null);
            toLogin();
        }

        return super.onOptionsItemSelected(item);
    }



    private void toLogin() {
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showLog("返回了");
        finish();
    }



}

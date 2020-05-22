package com.furja.iqc.presenter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.furja.overall.R;
import com.furja.iqc.json.ApplyCheckOrder;
import com.furja.iqc.json.NewQCList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.furja.utils.Utils.doubleOf;
import static com.furja.utils.Utils.intOf;
import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showLongToast;
import static com.furja.utils.Utils.showToast;
import static com.furja.utils.Utils.textOf;

/**
 * 电源线模块检验项目内容的截图
 */
public class InspectItemDetailPresenter {
    @BindView(R.id.item_edit1)
    EditText editText1;
    @BindView(R.id.item_edit2)
    EditText editText2;
    @BindView(R.id.item_edit3)
    EditText editText3;
    @BindView(R.id.item_edit4)
    EditText editText4;
    @BindView(R.id.item_edit5)
    EditText editText5;
    @BindView(R.id.item_edit6)
    EditText editText6;
    @BindView(R.id.paper_Spinner)
    AppCompatSpinner spinner;
    @BindView(R.id.btn_toPrev)
    ImageButton btn_prev;
    @BindView(R.id.btn_toNext)
    ImageButton btn_next;
    @BindView(R.id.btn_randomNumber)
    ImageButton btnRandom;
    @BindView(R.id.project_info)
    TextView projectInfo;
    @BindView(R.id.item_label1)
    TextView label1_item;
    @BindView(R.id.item_label2)
    TextView label2_item;
    @BindView(R.id.item_label3)
    TextView label3_item;
    @BindView(R.id.item_label4)
    TextView label4_item;
    @BindView(R.id.item_label5)
    TextView label5_item;
    @BindView(R.id.item_label6)
    TextView label6_item;
    @BindView(R.id.label_title)
    TextView title_result;
    @BindView(R.id.card_projectInfo)
    CardView cardView;
    int currentPosition=0;
    private List<ApplyCheckOrder> mDatas;
    Random random;
    DecimalFormat df ;
    Context context;
    ArrayAdapter<Integer> adapter;
    ProjectDetailView detailView;
    NumberKeyListener numberKeyListener;
    boolean hasSixValue=false;  //是否有6个检测值
    public InspectItemDetailPresenter(View view, ProjectDetailView contractView) {
        ButterKnife.bind(this,view);
        setEditChangeListener(editText1);
        setEditChangeListener(editText2);
        setEditChangeListener(editText3);
        setEditChangeListener(editText4);
        setEditChangeListener(editText5);
        setEditChangeListener(editText6);
        random=new Random();
        df = new DecimalFormat("#0.00");
        context=view.getContext();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    int item= adapter.getItem(position);
                    setPositionAndValue(item-1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        TextView.OnEditorActionListener actionListener=new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    if(getItem(currentPosition).hasCheck()){
                        hideSoftInput();
                        navToNext();
                    }
                }
                return false;
            }
        };
        editText5.setOnEditorActionListener(actionListener);
        editText6.setOnEditorActionListener(actionListener);
        this.detailView=contractView;
    }


    public void setEditChangeListener(final EditText editText) {
        editText.setSelectAllOnFocus(true);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String text="";
                if(s!=null)
                    text=s.toString();
                valueItemByEditText(editText.getId(),text);
                ApplyCheckOrder item = mDatas.get(currentPosition);
                String analysisWay = item.getDataBean().getAnalysisWay();
                if(item.isQualified(hasSixValue)) {
                    editText.setTextColor(Color.BLACK);
                    title_result.setTextColor(Color.WHITE);
                    title_result.setText("检验结果: 合格");
                    projectInfo.setBackgroundResource(R.color.transparent);
                }
                else {
                    if(analysisWay.contains("定量")){
                        if(!item.isFit(text))
                            editText.setTextColor(Color.RED);
                        else
                            editText.setTextColor(Color.BLACK);
                    }
                    else if(intOf(text)==0)
                            editText.setTextColor(Color.BLACK);
                        else
                            editText.setTextColor(Color.RED);
                    title_result.setTextColor(Color.MAGENTA);
                    title_result.setText("检验结果: 不合格");
                    projectInfo.setBackgroundResource(R.drawable.shape_unqualified_layout);
                }
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String text="";
                CharSequence charSequence=editText.getText();
                if(!TextUtils.isEmpty(charSequence))
                    text = charSequence.toString();
                if (!hasFocus)
                    valueItemByEditText(editText.getId(), text);
                editText.selectAll();
            }
        });
        editText.setKeyListener(new NumberKeyListener() {
            @NonNull
            @Override
            protected char[] getAcceptedChars() {
                char[] numberChars={
                        '0','1','2','3','4','5','6','7','8','9'
                };
                char[] newnumberChars={
                        '0','1','2','3','4','5','6','7','8','9','.'
                };
                ApplyCheckOrder item
                        =mDatas.get(currentPosition);
                final NewQCList.QCEntryDataBean dataBean
                        =item.getDataBean();
                if(dataBean.getAnalysisWay().contains("定性")) {
                    return numberChars;
                }
                return newnumberChars;
            }
            @Override
            public int getInputType() {
                return 3;
            }
        });
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ApplyCheckOrder item
                        =mDatas.get(currentPosition);
                final NewQCList.QCEntryDataBean dataBean
                        =item.getDataBean();
                if(dataBean.getAnalysisWay()
                        .contains("定性")) {
                    if(event.getAction()==MotionEvent.ACTION_UP)
                        switch (v.getId()) {
                            case R.id.item_edit1:
                                showLog("触发了item_edit1");
                                detailView
                                        .startQMAGroupDataUI(1);
                                break;
                            case R.id.item_edit3:
                                showLog("触发了item_edit3");
                                detailView
                                        .startQMAGroupDataUI(3);
                                break;
                            case R.id.item_edit5:
                                showLog("触发了item_edit5");
                                detailView
                                        .startQMAGroupDataUI(5);
                                break;
                        }
                }
                editText.selectAll();
                return false;
            }
        });
    }

    /**
     * @param viewID
     * @param text
     */
    public synchronized void valueItemByEditText(int viewID,String text) {
        ApplyCheckOrder item
                =mDatas.get(currentPosition);
        NewQCList.QCEntryDataBean dataBean
                =item.getDataBean();
        String analysisWay=dataBean.getAnalysisWay();
        boolean isQualitative = analysisWay.contains("定性");
        switch (viewID) {
            case    R.id.item_edit1:
                if(!isQualitative)
                    item.setFcheckvalue1(text);
                else
                    item.setFNOReason1(text);
                break;
            case    R.id.item_edit2:
                if(!isQualitative)
                    item.setFcheckvalue2(text);
                else
                    item.setFNOnumber1(text);
                break;
            case    R.id.item_edit3:
                if(!isQualitative)
                    item.setFcheckvalue3(text);
                else
                    item.setFNOReason2(text);
                break;
            case    R.id.item_edit4:
                if(!isQualitative)
                    item.setFcheckvalue4(text);
                else
                    item.setFNOnumber2(text);
                break;
            case    R.id.item_edit5:
                if(!isQualitative)
                    item.setFcheckvalue5(text);
                else
                    item.setFNOReason3(text);
                break;
            case    R.id.item_edit6:
                if(!isQualitative)
                    item.setFcheckvalue6(text);
                else
                    item.setFNOnumber3(text);
                break;
        }
        mDatas.set(currentPosition,item);
    }


    @OnClick({R.id.btn_toPrev,R.id.btn_toNext,R.id.btn_randomNumber})
    public void OnButtonClick(View view)  {
        switch (view.getId()) {
            case R.id.btn_randomNumber:
                performRandom();
                break;
            case R.id.btn_toPrev:
                navToPrev();
                break;
            case R.id.btn_toNext:
                navToNext();
                break;
        }
    }

    public void navToNext() {
        if(currentPosition==getItemCount()-1)
            return;
        if(!getItem(currentPosition).isQualified(hasSixValue))
            showLongToast("上一个检验项目不合格,请核查");
        setPositionAndValue(currentPosition + 1);
        generateSpinnerItems(currentPosition);
    }

    public void navToPrev() {
        if(currentPosition==0)
            return;
        setPositionAndValue(currentPosition-1);
        generateSpinnerItems(currentPosition);
    }

    private void performRandom() {
        ApplyCheckOrder item =getItem(currentPosition);
        if(item==null)
            return;
        final NewQCList.QCEntryDataBean dataBean =item.getDataBean();
        String projectName=dataBean.getQcProject();
        double upperSpec =doubleOf(dataBean.getUpperSpec()),
                lowerSpec=doubleOf(dataBean.getLowerSpec()),
                targetValue=doubleOf(dataBean.getTargetValue());
        alterDecimalFormat(projectName);
        String checkValue =getRandomDouble(lowerSpec,targetValue,upperSpec,projectName);
        item.setFcheckvalue1(checkValue);
        if(projectName.contains("绞距")&&hasSixValue) {
            if(projectName.contains("铜丝"))
                item.setFcheckvalue1("19");
            item.setFcheckvalue2("0");
            item.setFcheckvalue3("0");
            item.setFcheckvalue4("0");
            item.setFcheckvalue5("0");
            item.setFcheckvalue6("0");
            mDatas.set(currentPosition,item);
            valueEditWithItem(item);
            return;
        }
        double firstCheckValue=doubleOf(checkValue);
        if(doubleOf(checkValue)+60<upperSpec)
            upperSpec=firstCheckValue+60;
        if(firstCheckValue-60>targetValue)
            targetValue=firstCheckValue-60;
        checkValue=getRandomDouble(lowerSpec,targetValue,upperSpec,projectName);
        item.setFcheckvalue2(checkValue);
        checkValue=getRandomDouble(lowerSpec,targetValue,upperSpec,projectName);
        item.setFcheckvalue3(checkValue);
        if(projectName.contains("硬度")&&hasSixValue) {
            item.setFcheckvalue4("0");
            item.setFcheckvalue5("0");
            item.setFcheckvalue6("0");
            mDatas.set(currentPosition,item);
            valueEditWithItem(item);
            return;
        }
        checkValue=getRandomDouble(lowerSpec,targetValue,upperSpec,projectName);
        item.setFcheckvalue4(checkValue);
        checkValue=getRandomDouble(lowerSpec,targetValue,upperSpec,projectName);
        item.setFcheckvalue5(checkValue);
        checkValue=getRandomDouble(lowerSpec,targetValue,upperSpec,projectName);
        item.setFcheckvalue6(checkValue);
        mDatas.set(currentPosition,item);
        valueEditWithItem(item);
    }


    public  String getRandomDouble(double start,double target,double end,String projectName) {
        if(projectName.contains("插片厚度")) {
            if(isFit(start,end,1.48))
                end=1.48;
        }
        else if(projectName.contains("中心距")) {
            if(isFit(start,end,12.71)) {
                target = Math.max(start,12.68);
                end = 12.71;
            }
        }
        double gap =random.nextDouble()/20,n=90;
        if(target==end)
            target=(end-start)/2;
        if(target!=0 &&end/target<10) {
            gap=0;
            if(!projectName.contains("线径"))
                start=target;
            else {
                if(isFit(start,end,0.154))
                    start=0.154;
                if(isFit(start,end,0.156))
                    end=0.156;
            }
            n=1/(end-start);
        }
        gap = produceGapByProject(gap,projectName);
        double result =start+gap+random.nextDouble()/n;
        if(result>=end)
            result=end;
        if(projectName.contains("铜丝股数")||projectName.contains("自熄管：长度"))
            result=target;
        return df.format(result);
    }


    private boolean isFit(double start,double end,double value){
        return value>=start&&value<=end;
    }
    /**
     * @param projectName
     * @return
     */
    private double produceGapByProject(double gap,String projectName) {
        if(projectName.contains("插片"))
            gap+=random.nextDouble()/10;
        else if(projectName.contains("引线绝缘平均")
                ||projectName.contains("电源线卡尺寸"))
            gap+=0.07;
        else if(projectName.contains("引线绝缘最小"))
            gap+=0.04;
            else if(projectName.contains("护套绝缘"))
                gap+=0.2;
        if(projectName.contains("线径")
                ||projectName.contains("插片厚度")||projectName.contains("中心距"))
            gap=0;
        return gap;
    }

    /**
     * 修改 DecimalFormat
     * @param text
     */
    private void alterDecimalFormat(String text) {
        if(text.contains("剥")||text.contains("硬度")||text.contains("股数")
                ||text.contains("绞距")||text.contains("电线长度")
                    ||text.contains("自熄管：长度")||text.contains("切断面长度"))
            df.applyPattern("#");
        else if(text.contains("线径"))
            df.applyPattern("#0.000");
            else
                df.applyPattern("#0.00");
    }

    public List<ApplyCheckOrder> getmDatas() {
        return mDatas;
    }
    public void setmDatas(List<ApplyCheckOrder> mDatas) {
        this.mDatas = mDatas;
        setPositionAndValue(0);
        generateSpinnerItems(currentPosition);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setPositionAndValue(int selectedPosition) {
        if(mDatas==null)
            return;
        int itemCount=getItemCount();
        if(selectedPosition>=itemCount) {
            showToast("已到达检验项目末尾");
            return;
        }
        else if(selectedPosition<0){
            showToast("超出检验项目范围");
            return;
        }
        this.currentPosition = selectedPosition;
        ApplyCheckOrder item =mDatas.get(selectedPosition);
        if(!item.isViewed()){
            item.setViewed(true);
            mDatas.set(selectedPosition,item);
        }
        NewQCList.QCEntryDataBean dataBean =item.getDataBean();
        if(dataBean.getAnalysisWay().contains("定性")) {
            btnRandom.setVisibility(View.INVISIBLE);
            valueEditWithItem(item);
        }
        else if(!item.isFKeyInspect()||hasSixValue()){
            btnRandom.setVisibility(View.VISIBLE);
            performRandom();
        }
        else {
            btnRandom.setVisibility(View.INVISIBLE);
            valueEditWithItem(item);
            editText1.requestFocus();
        }
        if(selectedPosition==itemCount-1)
            btn_next.setVisibility(View.INVISIBLE);
        else
            btn_next.setVisibility(View.VISIBLE);
        if(selectedPosition==0)
            btn_prev.setVisibility(View.INVISIBLE);
        else
            btn_prev.setVisibility(View.VISIBLE);
    }

    /**
     * 获取position对应的Item
     * @param position
     * @return
     */
    private ApplyCheckOrder getItem(int position) {
        if(mDatas==null||position>=mDatas.size())
            return null;
        else
            return mDatas.get(position);
    }


    /**
     *如何赋值于EditText
     * @param item
     */
    private void valueEditWithItem(ApplyCheckOrder item) {
        try {
            NewQCList.QCEntryDataBean dataBean
                    =item.getDataBean();
            String analysisWay = dataBean.getAnalysisWay();
            if(analysisWay.contains("定性")) {
                label1_item.setText("不良原因1");
                label2_item.setText("不良数1");
                label3_item.setText("不良原因2");
                label4_item.setText("不良数2");
                label5_item.setText("不良原因3");
                label6_item.setText("不良数3");
                label6_item.setVisibility(View.VISIBLE);
                editText6.setVisibility(View.VISIBLE);
                String text=item.getFNOReason1().equals("0")
                        ?"":item.getFNOReason1();
                editText1.setText(text);
                editText2.setText(item.getFNOnumber1());
                text=item.getFNOReason2().equals("0")
                        ?"":item.getFNOReason2();
                editText3.setText(text);
                editText4.setText(item.getFNOnumber2());
                text=item.getFNOReason3().equals("0")
                        ?"":item.getFNOReason3();
                editText5.setText(text);
                editText6.setText(item.getFNOnumber3());
                editText1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_more,0);
                editText3.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_more,0);
                editText5.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_more,0);
            }
            else {
                if(hasSixValue){
                    label6_item.setVisibility(View.VISIBLE);
                    editText6.setVisibility(View.VISIBLE);
                }
                else {
                    label6_item.setVisibility(View.GONE);
                    editText6.setVisibility(View.GONE);
                    editText6.setText("");
                }
                label1_item.setText("检验值1");
                label2_item.setText("检验值2");
                label3_item.setText("检验值3");
                label4_item.setText("检验值4");
                label5_item.setText("检验值5");
                label6_item.setText("检验值6");
                editText1.setText(item.getFcheckvalue1());
                editText2.setText(item.getFcheckvalue2());
                editText3.setText(item.getFcheckvalue3());
                editText4.setText(item.getFcheckvalue4());
                editText5.setText(item.getFcheckvalue5());
                editText6.setText(item.getFcheckvalue6());
                editText1.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                editText3.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                editText5.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        projectInfo.setText(item.getProjectInfo());
    }

    /**
     * 生成 Spinner所需的Items,flag为判断是否spinner.setSelection(position);
     */
    public void generateSpinnerItems(int position) {
        List<Integer> spinnerItems =new ArrayList<Integer>();
        spinnerItems.clear();
        int startPosition=position-4;
        if(startPosition<0)
            startPosition=0;
        int used=position-startPosition;
        int endPosition=position+9-used;
        if(endPosition>getItemCount())
            endPosition=getItemCount();
        for(int i=startPosition;i<endPosition;i++) {
            if(i==position)
                position=spinnerItems.size();
            spinnerItems.add(i+1);
        }
        if(adapter==null)
            adapter=new ArrayAdapter<Integer>(context,R.layout.simple_list_item,spinnerItems);
        else {
            adapter.clear();
            adapter.addAll(spinnerItems);
        }
        spinner.setAdapter(adapter);
        spinner.setSelection(position);
    }



    public int getItemCount() {
        if(mDatas==null)
            return 0;
        return mDatas.size();
    }

    public boolean isEmpty()  {
        return getItemCount()==0;
    }

    public boolean hasSixValue() {
        return hasSixValue;
    }

    public void setHasSixValue(boolean hasSixValue) {
        this.hasSixValue = hasSixValue;
    }

    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null&&imm.isActive())
            imm.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
    }

    public interface ProjectDetailView {
        void backToParentActivity();
        void startQMAGroupDataUI(int viewID);
    }

}

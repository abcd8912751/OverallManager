package com.furja.iqc.json;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.furja.common.User;
import com.furja.common.FNumberBean;
import com.furja.overall.FurjaApp;

import java.util.ArrayList;
import java.util.List;

import static com.furja.utils.Utils.doubleOf;
import static com.furja.utils.Utils.intOf;
import static com.furja.utils.Utils.textOf;

/**
 * 请检单JSON
 */
public class ApplyCheckOrder {
    @JSONField(serialize = false)
    private NewQCList.QCEntryDataBean dataBean;
    @JSONField(serialize = false)
    private int position;
    @JSONField(serialize = false)
    private String fcheckvalue1;
    @JSONField(serialize = false)
    private String fcheckvalue2;
    @JSONField(serialize = false)
    private String fcheckvalue3;
    @JSONField(serialize = false)
    private String fcheckvalue4;
    @JSONField(serialize = false)
    private String fcheckvalue5;
    @JSONField(serialize = false)
    private String fcheckvalue6;
    @JSONField(serialize = false)
    private String fcheckvalue;
    @JSONField(serialize = false)
    private String fNOReason1;
    @JSONField(serialize = false)
    private String fNOnumber1;
    @JSONField(serialize = false)
    private String fNOReason2;
    @JSONField(serialize = false)
    private String fNOnumber2;
    @JSONField(serialize = false)
    private String fNOReason3;
    @JSONField(serialize = false)
    private String fNOnumber3;
    @JSONField(serialize = false)
    private String fcheckRES;
    @JSONField(serialize = false)
    private double applyQty;        // 请检数量
    @JSONField(serialize = false)
    private int fcount;
    @JSONField(serialize = false)
    private String fuser;         //制单人内码
    @JSONField(serialize = false)
    private String ftitle;
    @JSONField(serialize = false)
    private boolean isqualified;    //是否合格
    @JSONField(serialize = false)
    private boolean FKeyInspect;    //是否重点检查,当该值为否时自动生成
    private int FSampleQty1;
    private int FAcceptQty1;
    private int FRejectQty1;
    private double FTargetValQ;
    private double FUpLimitQ;
    private double FDownLimitQ;
    private double FUpOffsetQ;
    private double FDownOffsetQ;
    private List<InspectValueItem> FValueGrid;
    private FNumberBean FSampleSchemeId1;
    private FNumberBean FInspectItemId;
    private FNumberBean FInspectMethodId;
    private FNumberBean FTargetValB;
    private FNumberBean FInspectBasisId;
    private FNumberBean FInspectInstrumentId;
    public ApplyCheckOrder() {
        User user= FurjaApp.getUser();
        if(user!=null)
            this.fuser=user.getUserId();
        FValueGrid=new ArrayList<>();
    }

    public ApplyCheckOrder(NewQCList.QCEntryDataBean dataBean, int position) {
        setDataBean(dataBean);
        this.fcheckvalue1="0.00";
        setFcheckvalue2(fcheckvalue1);
        setFcheckvalue3(fcheckvalue1);
        setFcheckvalue4(fcheckvalue1);
        setFcheckvalue5(fcheckvalue1);
        setFcheckvalue6(fcheckvalue1);
        fNOnumber3=fNOnumber2=fNOnumber1="0";
        fNOReason3=fNOReason2=fNOReason1="0";
        this.fcheckRES="合格";
        this.position=position;
        FValueGrid=new ArrayList<>();
    }

    /**
     * 生成合乎规则的检测值
     */
    public void generateValue() {
        InspectValueItem inspectValueItem=new InspectValueItem();
        this.FAcceptQty1 = intOf(dataBean.getAcceptQty());
        this.FRejectQty1 = intOf(dataBean.getRejectQty());
        int valueSize=0;    FValueGrid.clear();
        if(dataBean.getAnalysisWay().contains("定性")){
            int unqualifiedQty1 = intOf(fNOnumber1);
            int unqualifiedQty2 = intOf(fNOnumber2);
            int unqualifiedQty3 = intOf(fNOnumber3);
            valueSize=unqualifiedQty1+unqualifiedQty2+unqualifiedQty3;
            if(textOf(fNOReason1).length()>0)
                for(int i=0;i<unqualifiedQty1;i++)
                    FValueGrid.add(new InspectValueItem(fNOReason1));
            if(textOf(fNOReason2).length()>0)
                for(int i=0;i<unqualifiedQty2;i++)
                    FValueGrid.add(new InspectValueItem(fNOReason2));
            if(textOf(fNOReason3).length()>0)
                for(int i=0;i<unqualifiedQty3;i++)
                    FValueGrid.add(new InspectValueItem(fNOReason3));
        } else {
            addValueItemByValueQ(fcheckvalue1);
            addValueItemByValueQ(fcheckvalue2);
            addValueItemByValueQ(fcheckvalue3);
            addValueItemByValueQ(fcheckvalue4);
            addValueItemByValueQ(fcheckvalue5);
            addValueItemByValueQ(fcheckvalue6);
            this.FAcceptQty1=0;
            this.FRejectQty1=1;
            valueSize=6;
        }
        this.FSampleQty1 = intOf(dataBean.getSampleQty());
        this.FSampleQty1 = Math.max(valueSize,FSampleQty1);
        this.FTargetValQ = doubleOf(dataBean.getTargetValue());
        this.FUpLimitQ = doubleOf(dataBean.getUpperSpec());
        this.FDownLimitQ=doubleOf(dataBean.getLowerSpec());
        this.FUpOffsetQ=dataBean.getFUpOffsetQ();
        this.FDownOffsetQ=dataBean.getFDownOffsetQ();
    }

    private void addValueItemByValueQ(String inspectValueQ) {
        double value = doubleOf(inspectValueQ);
        if(value>0)
            FValueGrid.add(new InspectValueItem(value));
    }
    @JSONField(serialize = false)
    public String getProjectName(){
        if(dataBean!=null)
            return dataBean.getQcProject();
        else
            return "";
    }
    public String getFuser() {
        return fuser;
    }

    public void setFuser(String fuser) {
        this.fuser = fuser;
    }

    public void setSampleScheme(String sampleNumber){
        FNumberBean bean=new FNumberBean(sampleNumber);
        this.FSampleSchemeId1=bean;
    }

    public FNumberBean getFSampleSchemeId1() {
        return FSampleSchemeId1;
    }

    public void setFSampleSchemeId1(FNumberBean FSampleSchemeId1) {
        this.FSampleSchemeId1 = FSampleSchemeId1;
    }

    /**
     * 返回合格或不合格的 分割字符串
     * @return
     */
    private String getCheckValue() {
        return "{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}";
    }

    public boolean isIsqualified() {
        return isqualified;
    }

    public void setIsqualified(boolean isqualified) {
        this.isqualified = isqualified;
    }

    public boolean isFKeyInspect() {
        return FKeyInspect;
    }

    public void setFKeyInspect(boolean FKeyInspect) {
        this.FKeyInspect = FKeyInspect;
    }

    public List<InspectValueItem> getFValueGrid() {
        return FValueGrid;
    }

    public void setFValueGrid(List<InspectValueItem> FValueGrid) {
        this.FValueGrid = FValueGrid;
    }

    public String getfNOReason1() {
        return fNOReason1;
    }

    public FNumberBean getFInspectInstrumentId() {
        return FInspectInstrumentId;
    }

    public void setFInspectInstrumentId(FNumberBean FInspectInstrumentId) {
        this.FInspectInstrumentId = FInspectInstrumentId;
    }

    public void setfNOReason1(String fNOReason1) {
        this.fNOReason1 = fNOReason1;
    }

    public String getfNOnumber1() {
        return fNOnumber1;
    }

    public void setfNOnumber1(String fNOnumber1) {
        this.fNOnumber1 = fNOnumber1;
    }

    public String getfNOReason2() {
        return fNOReason2;
    }

    public void setfNOReason2(String fNOReason2) {
        this.fNOReason2 = fNOReason2;
    }

    public String getfNOnumber2() {
        return fNOnumber2;
    }

    public void setfNOnumber2(String fNOnumber2) {
        this.fNOnumber2 = fNOnumber2;
    }

    public String getfNOReason3() {
        return fNOReason3;
    }

    public void setfNOReason3(String fNOReason3) {
        this.fNOReason3 = fNOReason3;
    }

    public String getfNOnumber3() {
        return fNOnumber3;
    }

    public void setfNOnumber3(String fNOnumber3) {
        this.fNOnumber3 = fNOnumber3;
    }

    public String getFcheckRES() {
        return fcheckRES;
    }

    public void setFcheckRES(String fcheckRES) {
        this.fcheckRES = fcheckRES;
    }

    public double getApplyQty() {
        return applyQty;
    }

    public void setApplyQty(double applyQty) {
        this.applyQty = applyQty;
    }

    public int getFcount() {
        return fcount;
    }

    public void setFcount(int fcount) {
        this.fcount = fcount;
    }

    public String getFtitle() {
        return ftitle;
    }

    public void setFtitle(String ftitle) {
        this.ftitle = ftitle;
    }

    public String getFcheckvalue1() {
        if(TextUtils.isEmpty(fcheckvalue1))
            fcheckvalue1="0.00";
        return fcheckvalue1;
    }

    public void setFcheckvalue1(String fcheckvalue1) {
        this.fcheckvalue1 = fcheckvalue1;
    }

    public String getFcheckvalue2() {
        if(TextUtils.isEmpty(fcheckvalue2))
            fcheckvalue2="0.00";
        return fcheckvalue2;
    }

    public void setFcheckvalue2(String fcheckvalue2) {
        this.fcheckvalue2 = fcheckvalue2;
    }

    public String getFcheckvalue3() {
        if(TextUtils.isEmpty(fcheckvalue3))
            fcheckvalue3="0.00";
        return fcheckvalue3;
    }

    public void setFcheckvalue3(String fcheckvalue3) {
        this.fcheckvalue3 = fcheckvalue3;
    }

    public String getFcheckvalue4() {
        if(TextUtils.isEmpty(fcheckvalue4))
            fcheckvalue4="0.00";
        return fcheckvalue4;
    }

    public int getFSampleQty1() {
        return FSampleQty1;
    }

    public void setFSampleQty1(int FSampleQty1) {
        this.FSampleQty1 = FSampleQty1;
    }

    public int getFAcceptQty1() {
        return FAcceptQty1;
    }

    public void setFAcceptQty1(int FAcceptQty1) {
        this.FAcceptQty1 = FAcceptQty1;
    }

    public int getFRejectQty1() {
        return FRejectQty1;
    }

    public void setFRejectQty1(int FRejectQty1) {
        this.FRejectQty1 = FRejectQty1;
    }

    public double getFTargetValQ() {
        return FTargetValQ;
    }

    public void setFTargetValQ(double FTargetValQ) {
        this.FTargetValQ = FTargetValQ;
    }

    public double getFUpLimitQ() {
        return FUpLimitQ;
    }

    public void setFUpLimitQ(double FUpLimitQ) {
        this.FUpLimitQ = FUpLimitQ;
    }

    public double getFDownLimitQ() {
        return FDownLimitQ;
    }

    public void setFDownLimitQ(double FDownLimitQ) {
        this.FDownLimitQ = FDownLimitQ;
    }

    public double getFUpOffsetQ() {
        return FUpOffsetQ;
    }

    public void setFUpOffsetQ(double FUpOffsetQ) {
        this.FUpOffsetQ = FUpOffsetQ;
    }

    public double getFDownOffsetQ() {
        return FDownOffsetQ;
    }

    public void setFDownOffsetQ(double FDownOffsetQ) {
        this.FDownOffsetQ = FDownOffsetQ;
    }

    public void setFcheckvalue4(String fcheckvalue4) {
        this.fcheckvalue4 = fcheckvalue4;
    }

    public String getFcheckvalue5() {
        if(TextUtils.isEmpty(fcheckvalue5))
            fcheckvalue5="0.00";
        return fcheckvalue5;
    }

    public void setFcheckvalue5(String fcheckvalue5) {
        this.fcheckvalue5 = fcheckvalue5;
    }

    public String getFcheckvalue6() {
        if(TextUtils.isEmpty(fcheckvalue6))
            fcheckvalue6="0.00";
        return fcheckvalue6;
    }

    public void setFcheckvalue6(String fcheckvalue6) {
        this.fcheckvalue6 = fcheckvalue6;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @JSONField(serialize = false)
    public String getProjectInfo() {
        if(getDataBean()==null)
            return "无检验项目";
        else
            return dataBean.toString();
    }

    public NewQCList.QCEntryDataBean getDataBean() {
        return dataBean;
    }

    public void setDataBean(NewQCList.QCEntryDataBean dataBean) {
        this.dataBean = dataBean;
    }


    /**
     * 加上左右大括号
     * @param paras
     * @return
     */
    public String getStringWithBrace(String paras) {
        return "{"+paras+"}";
    }


    public String getFcheckvalue() {
        return this.fcheckvalue;
    }


    public void setFcheckvalue(String fcheckvalue) {
        this.fcheckvalue = fcheckvalue;
    }


    public String getFNOReason1() {
        if(TextUtils.isEmpty(fNOReason1))
            fNOReason1="0";
        return fNOReason1;
    }


    public void setFNOReason1(String fNOReason1) {
        this.fNOReason1 = fNOReason1;
    }


    public String getFNOnumber1() {
        if(TextUtils.isEmpty(fNOnumber1))
            fNOnumber1="0";
        return this.fNOnumber1;
    }


    public void setFNOnumber1(String fNOnumber1) {
        this.fNOnumber1 = fNOnumber1;
    }


    public String getFNOReason2() {
        if(TextUtils.isEmpty(fNOReason2))
            fNOReason2="0";
        return fNOReason2;
    }


    public void setFNOReason2(String fNOReason2) {
        this.fNOReason2 = fNOReason2;
    }


    public String getFNOnumber2() {
        if(TextUtils.isEmpty(fNOnumber2))
            fNOnumber2="0";
        return fNOnumber2;
    }


    public void setFNOnumber2(String fNOnumber2) {
        this.fNOnumber2 = fNOnumber2;
    }


    public String getFNOReason3() {
        if(TextUtils.isEmpty(fNOReason3))
            fNOReason3="0";
        return this.fNOReason3;
    }


    public void setFNOReason3(String fNOReason3) {
        this.fNOReason3 = fNOReason3;
    }


    public String getFNOnumber3() {
        if(TextUtils.isEmpty(fNOnumber3))
            fNOnumber3="0";
        return fNOnumber3;
    }


    public void setFNOnumber3(String fNOnumber3) {
        this.fNOnumber3 = fNOnumber3;
    }

    /**
     * 是否合格
     * @return
     */
    @JSONField(serialize = false)
    public boolean isQualified(boolean hasSixValue) {
        boolean res=false;
        if(dataBean.getAnalysisWay().contains("定量")) {
            if (isFit(fcheckvalue1) && isFit(fcheckvalue2) && isFit(fcheckvalue3)
                    && isFit(fcheckvalue4) && isFit(fcheckvalue5))
                res = true;
            if (hasSixValue)
                res = res && isFit(fcheckvalue6);
        }
        else {
            int acqty = intOf(dataBean.getAcceptQty());
            int unQualifiedQty1=textOf(fNOReason1).isEmpty()?0:intOf(fNOnumber1);
            int unQualifiedQty2=textOf(fNOReason2).isEmpty()?0:intOf(fNOnumber2);
            int unQualifiedQty3=textOf(fNOReason3).isEmpty()?0:intOf(fNOnumber3);
            if(unQualifiedQty1+unQualifiedQty2+unQualifiedQty3>acqty)
                res=false;
            else
                res=true;
        }
        return res;
    }

    public double getDoubleOfString(String value) {
        if(TextUtils.isEmpty(value))
            return 0.00;
        return Double.valueOf(value);
    }

    /**
     * 该条目是否检验,默认是全0
     * @return
     */
    public boolean hasCheck() {
        if(dataBean.getAnalysisWay().contains("定性"))
            return true;
        else {
            if(isZero(fcheckvalue1)&&isZero(fcheckvalue2)
                    &&isZero(fcheckvalue3)&&isZero(fcheckvalue4)
                    &&isZero(fcheckvalue5)&&isZero(fcheckvalue6))
                return false;
            else
                return true;
        }
    }

    /**
     * 检验值是否为0
     * @param value
     * @return
     */
    private boolean isZero(String value) {
        try {
            double curValue=doubleOf(value);
            if(curValue==0)
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFit(String value) {
        double curValue=doubleOf(value);
        double upperSpec=doubleOf(dataBean.getUpperSpec()),
                lowerSpec=doubleOf(dataBean.getLowerSpec());
        if(curValue==0)
            return true;
        if(curValue<lowerSpec || upperSpec<curValue)
            return false;
        else
            return true;
    }

    public void fillCheckValue(NewQCList.QCValueBean qcValueBean) {
        String analyseMethod=qcValueBean.getFAnaysisMethod();
        if("定性分析".equals(analyseMethod)){
            this.fNOReason1=qcValueBean.getFInspectValue1();
            this.fNOReason2=qcValueBean.getFInspectValue3();
            this.fNOReason3=qcValueBean.getFInspectValue5();
            this.fNOnumber1=qcValueBean.getFInspectValue2();
            this.fNOnumber2=qcValueBean.getFInspectValue4();
            this.fNOnumber3=qcValueBean.getFInspectValue6();
        }
        else {
            this.fcheckvalue1=qcValueBean.getFInspectValue1();
            this.fcheckvalue2=qcValueBean.getFInspectValue2();
            this.fcheckvalue3=qcValueBean.getFInspectValue3();
            this.fcheckvalue4=qcValueBean.getFInspectValue4();
            this.fcheckvalue5=qcValueBean.getFInspectValue5();
            this.fcheckvalue6=qcValueBean.getFInspectValue6();
        }
    }

    public FNumberBean getFInspectItemId() {
        return FInspectItemId;
    }

    public void setFInspectItemId(FNumberBean FInspectItemId) {
        this.FInspectItemId = FInspectItemId;
    }

    public FNumberBean getFInspectMethodId() {
        return FInspectMethodId;
    }

    public void setFInspectMethodId(FNumberBean FInspectMethodId) {
        this.FInspectMethodId = FInspectMethodId;
    }

    public FNumberBean getFTargetValB() {
        return FTargetValB;
    }

    public void setFTargetValB(FNumberBean FTargetValB) {
        this.FTargetValB = FTargetValB;
    }

    public FNumberBean getFInspectBasisId() {
        return FInspectBasisId;
    }

    public void setFInspectBasisId(FNumberBean FInspectBasisId) {
        this.FInspectBasisId = FInspectBasisId;
    }

    public void setFInspectItemId(String fInspectItemId) {
        this.FInspectItemId=new FNumberBean(fInspectItemId);
    }

    public void setFInspectMethodId(String fInspectMethodId) {
        this.FInspectMethodId=new FNumberBean(fInspectMethodId);
    }

    public void setFInspectBasisId(String fInspectBasisId) {
        this.FInspectBasisId=new FNumberBean(fInspectBasisId);
    }

    public void setFTargetValB(String fTargetValB) {
        this.FTargetValB=new FNumberBean(fTargetValB);
    }

    public void setFInspectInstrumentId(String fInspectInstrumentId) {
        this.FInspectInstrumentId=new FNumberBean(fInspectInstrumentId);
    }
}

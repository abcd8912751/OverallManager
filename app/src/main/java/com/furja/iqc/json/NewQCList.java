package com.furja.iqc.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

/**
 * 请检单列表
 */
public class NewQCList implements Parcelable {
    private List<QCDataBean> QCData;
    private List<QCEntryDataBean> QCEntryData;
    @JSONField(serialize = false)
    private boolean hasCatchList;   //是否获取到符合条件的检验项目
    @JSONField(serialize = false)
    private double applyQcNum;
    private List<QCValueBean> QCValueData;


    public List<QCDataBean> getQCData() {
        return QCData;
    }

    public void setQCData(List<QCDataBean> QCData) {
        this.QCData = QCData;
    }

    public List<QCEntryDataBean> getQCEntryData() {
        return QCEntryData;
    }

    public void setQCEntryData(List<QCEntryDataBean> QCEntryData) {
        this.QCEntryData = QCEntryData;
    }

    public List<QCValueBean> getQCValueData() {
        return QCValueData;
    }

    public void setQCValueData(List<QCValueBean> QCValueData) {
        this.QCValueData = QCValueData;
    }

    public double getApplyQcNum() {
        return applyQcNum;
    }

    public void setApplyQcNum(double applyQcNum) {
        this.applyQcNum = applyQcNum;
    }


    public static class QCValueBean implements Parcelable {
        private int FItemSeq;
        private String FInspectValue1;
        private String FInspectValue2;
        private String FInspectValue3;
        private String FInspectValue4;
        private String FInspectValue5;
        private String FInspectValue6;
        private String FAnaysisMethod;
        public int getFItemSeq() {
            return FItemSeq;
        }

        public void setFItemSeq(int FItemSeq) {
            this.FItemSeq = FItemSeq;
        }

        public String getFInspectValue1() {
            return FInspectValue1;
        }

        public void setFInspectValue1(String FInspectValue1) {
            this.FInspectValue1 = FInspectValue1;
        }

        public String getFInspectValue2() {
            return FInspectValue2;
        }

        public void setFInspectValue2(String FInspectValue2) {
            this.FInspectValue2 = FInspectValue2;
        }

        public String getFAnaysisMethod() {
            return FAnaysisMethod;
        }

        public void setFAnaysisMethod(String FAnaysisMethod) {
            this.FAnaysisMethod = FAnaysisMethod;
        }

        public String getFInspectValue3() {
            return FInspectValue3;
        }

        public void setFInspectValue3(String FInspectValue3) {
            this.FInspectValue3 = FInspectValue3;
        }

        public String getFInspectValue4() {
            return FInspectValue4;
        }

        public void setFInspectValue4(String FInspectValue4) {
            this.FInspectValue4 = FInspectValue4;
        }

        public String getFInspectValue5() {
            return FInspectValue5;
        }

        public void setFInspectValue5(String FInspectValue5) {
            this.FInspectValue5 = FInspectValue5;
        }

        public String getFInspectValue6() {
            return FInspectValue6;
        }

        public void setFInspectValue6(String FInspectValue6) {
            this.FInspectValue6 = FInspectValue6;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.FItemSeq);
            dest.writeString(this.FInspectValue1);
            dest.writeString(this.FInspectValue2);
            dest.writeString(this.FInspectValue3);
            dest.writeString(this.FInspectValue4);
            dest.writeString(this.FInspectValue5);
            dest.writeString(this.FInspectValue6);
            dest.writeString(this.FAnaysisMethod);
        }

        public QCValueBean() {
        }

        protected QCValueBean(Parcel in) {
            this.FItemSeq = in.readInt();
            this.FInspectValue1 = in.readString();
            this.FInspectValue2 = in.readString();
            this.FInspectValue3 = in.readString();
            this.FInspectValue4 = in.readString();
            this.FInspectValue5 = in.readString();
            this.FInspectValue6 = in.readString();
            this.FAnaysisMethod = in.readString();
        }

        public static final Creator<QCValueBean> CREATOR = new Creator<QCValueBean>() {
            @Override
            public QCValueBean createFromParcel(Parcel source) {
                return new QCValueBean(source);
            }

            @Override
            public QCValueBean[] newArray(int size) {
                return new QCValueBean[size];
            }
        };
    }
    public static class QCDataBean implements Parcelable {
        /**
         * 收货日期 : 2018-5-8 0:00:00
         * 检验方案 : QCS003277
         * 供应商 : 宁波高新区孚芯润电子科技有限公司
         * 物料名称 : 贴片电容1206
         * 报检数量 : 12000.00
         * 请检单号 : 418227
         * 请检单行号 : 1
         */
        private String supplyDate;
        private String qcScheme;
        private String supplyName;
        private String materialName;
        private String applyQcNum;
        private int applyOrderID;
        private String applyOrderBillNo;
        private int applyOrderEntryID;
        private int applyOrder_Row;
        private String errorMsg;
        private String materialNumber;
        private int materialID;
        private String barcode;
        private String FOrderType;
        private String FOrderBillNo;
        private int FOrderId;
        private int FOrderEntryId;
        private int FOrderEntrySeq;
        private String FUnitNumber;
        private String FSourceOrgNumber;
        private String supplyNumber;
        public String getFOrderType() {
            return FOrderType;
        }

        public void setFOrderType(String FOrderType) {
            this.FOrderType = FOrderType;
        }

        public String getFOrderBillNo() {
            return FOrderBillNo;
        }

        public void setFOrderBillNo(String FOrderBillNo) {
            this.FOrderBillNo = FOrderBillNo;
        }

        public int getFOrderId() {
            return FOrderId;
        }

        public int getMaterialID() {
            return materialID;
        }

        public String getFUnitNumber() {
            return FUnitNumber;
        }

        public void setFUnitNumber(String FUnitNumber) {
            this.FUnitNumber = FUnitNumber;
        }

        public void setMaterialID(int materialID) {
            this.materialID = materialID;
        }

        public void setFOrderId(int FOrderId) {
            this.FOrderId = FOrderId;
        }

        public int getFOrderEntryId() {
            return FOrderEntryId;
        }

        public void setFOrderEntryId(int FOrderEntryId) {
            this.FOrderEntryId = FOrderEntryId;
        }

        public int getFOrderEntrySeq() {
            return FOrderEntrySeq;
        }

        public void setFOrderEntrySeq(int FOrderEntrySeq) {
            this.FOrderEntrySeq = FOrderEntrySeq;
        }

        public String getFSourceOrgNumber() {
            return FSourceOrgNumber;
        }

        public void setFSourceOrgNumber(String FSourceOrgNumber) {
            this.FSourceOrgNumber = FSourceOrgNumber;
        }

        public String getSupplyNumber() {
            return supplyNumber;
        }

        public void setSupplyNumber(String supplyNumber) {
            this.supplyNumber = supplyNumber;
        }

        public String getSupplyDate() {
            return supplyDate;
        }

        public String getApplyOrderBillNo() {
            return applyOrderBillNo;
        }

        public void setApplyOrderBillNo(String applyOrderBillNo) {
            this.applyOrderBillNo = applyOrderBillNo;
        }

        public int getApplyOrderEntryID() {
            return applyOrderEntryID;
        }

        public void setApplyOrderEntryID(int applyOrderEntryID) {
            this.applyOrderEntryID = applyOrderEntryID;
        }

        public void setSupplyDate(String supplyDate) {
            this.supplyDate = supplyDate;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public String getMaterialNumber() {
            return materialNumber;
        }

        public void setMaterialNumber(String materialNumber) {
            this.materialNumber = materialNumber;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String getQcScheme() {
            return qcScheme;
        }

        public void setQcScheme(String qcScheme) {
            this.qcScheme = qcScheme;
        }

        public String getSupplyName() {
            return supplyName;
        }

        public void setSupplyName(String supplyName) {
            this.supplyName = supplyName;
        }

        public String getMaterialName() {
            return materialName;
        }

        public void setMaterialName(String materialName) {
            this.materialName = materialName;
        }

        public String getApplyQcNum() {
            return applyQcNum;
        }

        public void setApplyQcNum(String applyQcNum) {
            this.applyQcNum = applyQcNum;
        }

        public int getApplyOrderID() {
            return applyOrderID;
        }

        public void setApplyOrderID(int applyOrderID) {
            this.applyOrderID = applyOrderID;
        }
        public int getApplyOrder_Row() {
            return applyOrder_Row;
        }

        public void setApplyOrder_Row(int applyOrder_Row) {
            this.applyOrder_Row = applyOrder_Row;
        }

        @JSONField(serialize = false)
        public String toString() {
            String lineSeparator= System.getProperty("line.separator", "\n");
            return
                    "     检验方案:      " + qcScheme + lineSeparator +
                            "       供应商:        " + supplyName +lineSeparator +
                            "     物料名称:      " + materialName ;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.supplyDate);
            dest.writeString(this.qcScheme);
            dest.writeString(this.supplyName);
            dest.writeString(this.materialName);
            dest.writeString(this.applyQcNum);
            dest.writeInt(this.applyOrderID);
            dest.writeString(this.applyOrderBillNo);
            dest.writeInt(this.applyOrderEntryID);
            dest.writeInt(this.applyOrder_Row);
            dest.writeString(this.errorMsg);
            dest.writeString(this.materialNumber);
            dest.writeInt(this.materialID);
            dest.writeString(this.barcode);
            dest.writeString(this.FOrderType);
            dest.writeString(this.FOrderBillNo);
            dest.writeInt(this.FOrderId);
            dest.writeInt(this.FOrderEntryId);
            dest.writeInt(this.FOrderEntrySeq);
            dest.writeString(this.FUnitNumber);
            dest.writeString(this.FSourceOrgNumber);
            dest.writeString(this.supplyNumber);
        }

        public QCDataBean() {
        }

        protected QCDataBean(Parcel in) {
            this.supplyDate = in.readString();
            this.qcScheme = in.readString();
            this.supplyName = in.readString();
            this.materialName = in.readString();
            this.applyQcNum = in.readString();
            this.applyOrderID = in.readInt();
            this.applyOrderBillNo = in.readString();
            this.applyOrderEntryID = in.readInt();
            this.applyOrder_Row = in.readInt();
            this.errorMsg = in.readString();
            this.materialNumber = in.readString();
            this.materialID = in.readInt();
            this.barcode = in.readString();
            this.FOrderType = in.readString();
            this.FOrderBillNo = in.readString();
            this.FOrderId = in.readInt();
            this.FOrderEntryId = in.readInt();
            this.FOrderEntrySeq = in.readInt();
            this.FUnitNumber = in.readString();
            this.FSourceOrgNumber = in.readString();
            this.supplyNumber=in.readString();
        }

        public static final Creator<QCDataBean> CREATOR = new Creator<QCDataBean>() {
            @Override
            public QCDataBean createFromParcel(Parcel source) {
                return new QCDataBean(source);
            }

            @Override
            public QCDataBean[] newArray(int size) {
                return new QCDataBean[size];
            }
        };
    }

    public static class QCEntryDataBean implements Parcelable {
        /**
         * 检验项目 : 物料名称
         * 目标值 : 0.000
         * 规格下限 : 14.000
         * 规格上限 : 15.000
         * 分析方法 : 定性分析
         * 缺陷类 : C
         */
        private String qcProject;
        private String targetValue;
        private String lowerSpec;
        private String upperSpec;
        private String analysisWay;
        private String faultClass;
        private String sampleQty;
        private String acceptQty;
        private String rejectQty;
        private double FUpOffsetQ;
        private double FDownOffsetQ;
        private String FSampleSchemeId1;
        private String FInspectItemId;
        private String FInspectMethodId;
        private String FTargetValB;
        private String FInspectBasisId;
        private String FInspectInstrumentId;
        private boolean FKeyInspect;
        public String getQcProject() {
            return qcProject;
        }

        public String getFInspectItemId() {
            return FInspectItemId;
        }

        public void setFInspectItemId(String FInspectItemId) {
            this.FInspectItemId = FInspectItemId;
        }

        public String getFInspectInstrumentId() {
            return FInspectInstrumentId;
        }

        public boolean isFKeyInspect() {
            return FKeyInspect;
        }

        public void setFKeyInspect(boolean FKeyInspect) {
            this.FKeyInspect = FKeyInspect;
        }

        public void setFInspectInstrumentId(String FInspectInstrumentId) {
            this.FInspectInstrumentId = FInspectInstrumentId;
        }

        public String getFInspectMethodId() {
            return FInspectMethodId;
        }

        public void setFInspectMethodId(String FInspectMethodId) {
            this.FInspectMethodId = FInspectMethodId;
        }

        public String getFTargetValB() {
            return FTargetValB;
        }

        public void setFTargetValB(String FTargetValB) {
            this.FTargetValB = FTargetValB;
        }

        public String getFInspectBasisId() {
            return FInspectBasisId;
        }

        public void setFInspectBasisId(String FInspectBasisId) {
            this.FInspectBasisId = FInspectBasisId;
        }

        public void setQcProject(String qcProject) {
            this.qcProject = qcProject;
        }

        public String getTargetValue() {
            return targetValue;
        }

        public String getAcceptQty() {
            return acceptQty;
        }

        public void setAcceptQty(String acceptQty) {
            this.acceptQty = acceptQty;
        }

        public double getFUpOffsetQ() {
            return FUpOffsetQ;
        }

        public String getFSampleSchemeId1() {
            return FSampleSchemeId1;
        }

        public void setFSampleSchemeId1(String FSampleSchemeId1) {
            this.FSampleSchemeId1 = FSampleSchemeId1;
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

        public String getRejectQty() {
            return rejectQty;
        }

        public void setRejectQty(String rejectQty) {
            this.rejectQty = rejectQty;
        }

        public void setTargetValue(String targetValue) {
            this.targetValue = targetValue;
        }

        public String getLowerSpec() {
            return lowerSpec;
        }

        public void setLowerSpec(String lowerSpec) {
            this.lowerSpec = lowerSpec;
        }

        public String getUpperSpec() {
            return upperSpec;
        }

        public void setUpperSpec(String upperSpec) {
            this.upperSpec = upperSpec;
        }

        public String getAnalysisWay() {
            return analysisWay;
        }

        public String getSampleQty() {
            return sampleQty;
        }

        public void setSampleQty(String sampleQty) {
            this.sampleQty = sampleQty;
        }

        public void setAnalysisWay(String analysisWay) {
            this.analysisWay = analysisWay;
        }

        public String getFaultClass() {
            return faultClass;
        }

        public void setFaultClass(String faultClass) {
            this.faultClass = faultClass;
        }

        @Override
        public String toString() {
            String lineSeparator
                    = System.getProperty("line.separator", "\n");
            if(analysisWay.contains("定性"))
                return "检验项目: " + qcProject + lineSeparator +
                        "目标值:   " + targetValue +lineSeparator +
                        "允收数: " + lowerSpec + lineSeparator +
                        "拒收数: " + upperSpec + lineSeparator +
                        "分析方法: " + analysisWay + "  " +
                        "缺陷等级: " + faultClass;
            return  "检验项目: " + qcProject + lineSeparator +
                    "目标值:   " + targetValue +lineSeparator +
                    "规格下限: " + lowerSpec + lineSeparator +
                    "规格上限: " + upperSpec + lineSeparator +
                    "分析方法: " + analysisWay + "  " +
                    "缺陷等级: " + faultClass;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.qcProject);
            dest.writeString(this.targetValue);
            dest.writeString(this.lowerSpec);
            dest.writeString(this.upperSpec);
            dest.writeString(this.analysisWay);
            dest.writeString(this.faultClass);
            dest.writeString(this.sampleQty);
            dest.writeString(this.acceptQty);
            dest.writeString(this.rejectQty);
            dest.writeDouble(this.FUpOffsetQ);
            dest.writeDouble(this.FDownOffsetQ);
            dest.writeString(this.FSampleSchemeId1);
            dest.writeString(this.FInspectItemId);
            dest.writeString(this.FInspectMethodId);
            dest.writeString(this.FTargetValB);
            dest.writeString(this.FInspectBasisId);
            dest.writeString(this.FInspectInstrumentId);
            dest.writeByte(this.FKeyInspect ? (byte) 1 : (byte) 0);
        }

        public QCEntryDataBean() {
        }

        protected QCEntryDataBean(Parcel in) {
            this.qcProject = in.readString();
            this.targetValue = in.readString();
            this.lowerSpec = in.readString();
            this.upperSpec = in.readString();
            this.analysisWay = in.readString();
            this.faultClass = in.readString();
            this.sampleQty = in.readString();
            this.acceptQty = in.readString();
            this.rejectQty = in.readString();
            this.FUpOffsetQ = in.readDouble();
            this.FDownOffsetQ = in.readDouble();
            this.FSampleSchemeId1 = in.readString();
            this.FInspectItemId = in.readString();
            this.FInspectMethodId = in.readString();
            this.FTargetValB = in.readString();
            this.FInspectBasisId = in.readString();
            this.FInspectInstrumentId = in.readString();
            this.FKeyInspect = in.readByte() != 0;
        }

        public static final Creator<QCEntryDataBean> CREATOR = new Creator<QCEntryDataBean>() {
            @Override
            public QCEntryDataBean createFromParcel(Parcel source) {
                return new QCEntryDataBean(source);
            }

            @Override
            public QCEntryDataBean[] newArray(int size) {
                return new QCEntryDataBean[size];
            }
        };
    }

    public boolean isHasCatchList() {
        return hasCatchList;
    }

    public void setHasCatchList(boolean hasCatchList) {
        this.hasCatchList = hasCatchList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.QCData);
        dest.writeList(this.QCEntryData);
        dest.writeList(this.QCValueData);
    }

    public NewQCList() {
    }

    protected NewQCList(Parcel in) {
        this.QCData = new ArrayList<QCDataBean>();
        in.readList(this.QCData, QCDataBean.class.getClassLoader());
        this.QCEntryData = new ArrayList<QCEntryDataBean>();
        in.readList(this.QCEntryData, QCEntryDataBean.class.getClassLoader());
        this.QCValueData = new ArrayList<QCValueBean>();
        in.readList(this.QCValueData, QCValueBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<NewQCList> CREATOR = new Parcelable.Creator<NewQCList>() {
        @Override
        public NewQCList createFromParcel(Parcel source) {
            return new NewQCList(source);
        }

        @Override
        public NewQCList[] newArray(int size) {
            return new NewQCList[size];
        }
    };
}

package com.furja.iqc.json;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据成品的条码获取
 */

public class MaterialJson {


    /**
     * ErrCode : 100
     * ErrMsg : 条码信息获取成功!
     * ErrData : {"FBCDate":"2018-05-08 00:00:00","FItemID":"146101","FNumber":"1.820.8200620201","FShortNumber":"8200620201","FName":"贴片电容1206","FModel":"10uf","FQty":2000,"FTranType":"外协","FSCDNO":"通用件及样机","FBatteryBarCodeNO1":null,"FBatteryBarCodeNO2":null,"FMaterialDescription":"1206 10uf 25v ±10% 1206B106K250NT 风华","MSL":"1"}
     */

    private int ErrCode;
    private String ErrMsg;
    private ErrDataBean ErrData;

    public int getErrCode() {
        return ErrCode;
    }

    public void setErrCode(int ErrCode) {
        this.ErrCode = ErrCode;
    }

    public String getErrMsg() {
        return ErrMsg;
    }

    public void setErrMsg(String ErrMsg) {
        this.ErrMsg = ErrMsg;
    }

    public ErrDataBean getErrData() {
        return ErrData;
    }

    public void setErrData(ErrDataBean ErrData) {
        this.ErrData = ErrData;
    }

    public static class ErrDataBean {
        /**
         * FBCDate : 2018-05-08 00:00:00
         * FItemID : 146101
         * FNumber : 1.820.8200620201
         * FShortNumber : 8200620201
         * FName : 贴片电容1206
         * FModel : 10uf
         * FQty : 2000
         * FTranType : 外协
         * FSCDNO : 通用件及样机
         * FBatteryBarCodeNO1 : null
         * FBatteryBarCodeNO2 : null
         * FMaterialDescription : 1206 10uf 25v ±10% 1206B106K250NT 风华
         * MSL : 1
         */
        private String FBCDate;
        private String FItemID;
        private String FNumber;
        private String FShortNumber;
        private String FName;
        private String FModel;
        private int FQty;
        private String FTranType;
        private String FSCDNO;
        private Object FBatteryBarCodeNO1;
        private Object FBatteryBarCodeNO2;
        private String FMaterialDescription;
        private String MSL;

        public String getFBCDate() {
            return FBCDate;
        }

        public void setFBCDate(String FBCDate) {
            this.FBCDate = FBCDate;
        }

        public String getFItemID() {
            return FItemID;
        }

        public void setFItemID(String FItemID) {
            this.FItemID = FItemID;
        }

        public String getFNumber() {
            return FNumber;
        }

        public void setFNumber(String FNumber) {
            this.FNumber = FNumber;
        }

        public String getFShortNumber() {
            return FShortNumber;
        }

        public void setFShortNumber(String FShortNumber) {
            this.FShortNumber = FShortNumber;
        }

        public String getFName() {
            return FName;
        }

        public void setFName(String FName) {
            this.FName = FName;
        }

        public String getFModel() {
            return FModel;
        }

        public void setFModel(String FModel) {
            this.FModel = FModel;
        }

        public int getFQty() {
            return FQty;
        }

        public void setFQty(int FQty) {
            this.FQty = FQty;
        }

        public String getFTranType() {
            return FTranType;
        }

        public void setFTranType(String FTranType) {
            this.FTranType = FTranType;
        }

        public String getFSCDNO() {
            return FSCDNO;
        }

        public void setFSCDNO(String FSCDNO) {
            this.FSCDNO = FSCDNO;
        }

        public Object getFBatteryBarCodeNO1() {
            return FBatteryBarCodeNO1;
        }

        public void setFBatteryBarCodeNO1(Object FBatteryBarCodeNO1) {
            this.FBatteryBarCodeNO1 = FBatteryBarCodeNO1;
        }

        public Object getFBatteryBarCodeNO2() {
            return FBatteryBarCodeNO2;
        }

        public void setFBatteryBarCodeNO2(Object FBatteryBarCodeNO2) {
            this.FBatteryBarCodeNO2 = FBatteryBarCodeNO2;
        }

        public String getFMaterialDescription() {
            return FMaterialDescription;
        }

        public void setFMaterialDescription(String FMaterialDescription) {
            this.FMaterialDescription = FMaterialDescription;
        }

        public String getMSL() {
            return MSL;
        }

        public void setMSL(String MSL) {
            this.MSL = MSL;
        }

        public String toStringWithDate()
        {
            return "";
        }

        public List<String> toStringList()
        {
            List<String> strings=new ArrayList<>();
            strings.add(FName);
            strings.add(FModel);
            strings.add(FMaterialDescription);
            strings.add(MSL);
            return  strings;
        }

        @Override
        public String toString()
        {
            String lineSeparator= System.getProperty("line.separator", "\n");
            return
                    "     物料代码:      " + FNumber + lineSeparator +
                    "     物料名称:      " + FName +lineSeparator +
                    "     物料规格:      " + FModel ;
        }
    }
}

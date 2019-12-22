package com.furja.verify.json;

import android.text.TextUtils;

import com.furja.utils.Utils;

import static com.furja.utils.Utils.showLog;


/**
 * 根据成品的条码获取
 */

public class MaterialJson {

    /**
     * ErrCode : 100
     * ErrMsg : 条码信息获取成功!
     * ErrData : {"FBCDate":"2017-09-12T08:08:57","FItemID":"140908","FNumber":"3.020.702.0251000201","FShortNumber":"0251000201","FName":"IR101吸尘器","FModel":"EB1","FQty":0,"FTranType":"成品","FSCDNO":"170727-720","FBatteryBarCodeNO1":"http://www.registeryourshark.com/reg/?m=XBAT200&s=S22FA115J2K5","FBatteryBarCodeNO2":""}
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
         * FBCDate : 2017-09-12T08:08:57
         * FItemID : 140908
         * FNumber : 3.020.702.0251000201
         * FShortNumber : 0251000201
         * FName : IR101吸尘器
         * FModel : EB1
         * FQty : 0
         * FTranType : 成品
         * FSCDNO : 170727-720
         * FBatteryBarCodeNO1 : http://www.registeryourshark.com/reg/?m=XBAT200&s=S22FA115J2K5
         * FBatteryBarCodeNO2 :
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
        private String FBatteryBarCodeNO1;
        private String FBatteryBarCodeNO2;

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
        /**
         * 获取电池包2号条码
         */
        public String getFBatteryBarCodeNO1() {
            return FBatteryBarCodeNO1;
        }

        public void setFBatteryBarCodeNO1(String FBatteryBarCodeNO1) {
            this.FBatteryBarCodeNO1 = FBatteryBarCodeNO1;
        }
        /**
         * 获取电池包2号条码
         */
        public String getFBatteryBarCodeNO2() {
            return FBatteryBarCodeNO2;
        }

        public void setFBatteryBarCodeNO2(String FBatteryBarCodeNO2) {
            this.FBatteryBarCodeNO2 = FBatteryBarCodeNO2;
        }


        public String toString(String mainBarCode) {
            String lineSeparator= System.getProperty("line.separator", "\n");
            return
                    "     生产单号:      " + FSCDNO + lineSeparator +
                    "     物料代码:      " + FShortNumber + lineSeparator +
                    "     物料名称:      " + FName +lineSeparator +
                    "     物料规格:      " + FModel + lineSeparator +
                    "     主机条码:      " + mainBarCode + lineSeparator +
                    "1号电池包条码:" + getTailString(FBatteryBarCodeNO1) + lineSeparator +
                    "2号电池包条码:" + getTailString(FBatteryBarCodeNO2);
        }

        /**
         * @return 尾部的12位短条码
         */
        public String getTailString(String string)
        {
            if(TextUtils.isEmpty(string)||string.length()<12)
                return "无";
            showLog(string.substring(string.length()-12));
            return string.substring(string.length()-12);
        }
    }
}

package com.furja.verify.json;

import android.text.TextUtils;

import java.util.List;

import static com.furja.utils.Utils.showLog;

/**
 * 九阳产品
 */

public class JYProductJson {


    /**
     * ErrCode : 100
     * ErrMsg : 信息获取成功!
     * ErrData : [{"FItemid":1,"TaskID":"","FWorkOrderID":"1","FTaskType":"","ProductCode":"1","ColorBoxCode":"1","OutBoxCode":"3","BatteryBagOne":"5","BatteryBagTwo":"4","BoxCollectDate":"2018-04-06 00:00:00","ProductCollectDate":"2018-04-06 00:00:00","UploadOrNot":""}]
     */

    private int ErrCode;
    private String ErrMsg;
    private List<ErrDataBean> ErrData;

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

    public List<ErrDataBean> getErrData() {
        return ErrData;
    }

    public void setErrData(List<ErrDataBean> ErrData) {
        this.ErrData = ErrData;
    }

    public static class ErrDataBean {
        /**
         * FItemid : 1
         * TaskID :
         * FWorkOrderID : 1
         * FTaskType :
         * ProductCode : 1
         * ColorBoxCode : 1
         * OutBoxCode : 3
         * BatteryBagOne : 5
         * BatteryBagTwo : 4
         * BoxCollectDate : 2018-04-06 00:00:00
         * ProductCollectDate : 2018-04-06 00:00:00
         * UploadOrNot :
         */
        private int FItemid;
        private String TaskID;
        private String FWorkOrderID;
        private String FTaskType;
        private String ProductCode;
        private String ColorBoxCode;
        private String OutBoxCode;
        private String BatteryBagOne;
        private String BatteryBagTwo;
        private String BoxCollectDate;
        private String ProductCollectDate;
        private String UploadOrNot;
        public String toString()
        {
            String lineSeparator= System.getProperty("line.separator", "\n");
            return
                    "工单号:      " +FWorkOrderID  + lineSeparator +
                            "成品条码:      " + getProductCode() + lineSeparator +
                            "彩箱条码:      " + getColorBoxCode() + lineSeparator +
                            "外箱条码:      " + getOutBoxCode() +lineSeparator +
                            "1号电池包条码:" + getTailString(BatteryBagOne)+ lineSeparator +
                            "2号电池包条码:" + getTailString(BatteryBagTwo);
        }

        /**
         * @return 尾部的12位短条码
         */
        public String getTailString(String string)
        {
            if(TextUtils.isEmpty(string)||string.length()<12)
                return string;
            showLog(string.substring(string.length()-12));
            return string.substring(string.length()-12);
        }

        public int getFItemid() {
            return FItemid;
        }

        public void setFItemid(int FItemid) {
            this.FItemid = FItemid;
        }

        public String getTaskID() {
            return TaskID;
        }

        public void setTaskID(String TaskID) {
            this.TaskID = TaskID;
        }

        public String getFWorkOrderID() {
            return FWorkOrderID;
        }

        public void setFWorkOrderID(String FWorkOrderID) {
            this.FWorkOrderID = FWorkOrderID;
        }

        public String getFTaskType() {
            return FTaskType;
        }

        public void setFTaskType(String FTaskType) {
            this.FTaskType = FTaskType;
        }

        public String getProductCode() {
            return ProductCode;
        }

        public void setProductCode(String ProductCode) {
            this.ProductCode = ProductCode;
        }

        public String getColorBoxCode() {
            return ColorBoxCode;
        }

        public void setColorBoxCode(String ColorBoxCode) {
            this.ColorBoxCode = ColorBoxCode;
        }

        public String getOutBoxCode() {
            return OutBoxCode;
        }

        public void setOutBoxCode(String OutBoxCode) {
            this.OutBoxCode = OutBoxCode;
        }

        public String getBatteryBagOne() {
            return BatteryBagOne;
        }

        public void setBatteryBagOne(String BatteryBagOne) {
            this.BatteryBagOne = BatteryBagOne;
        }

        public String getBatteryBagTwo() {
            return BatteryBagTwo;
        }

        public void setBatteryBagTwo(String BatteryBagTwo) {
            this.BatteryBagTwo = BatteryBagTwo;
        }

        public String getBoxCollectDate() {
            return BoxCollectDate;
        }

        public void setBoxCollectDate(String BoxCollectDate) {
            this.BoxCollectDate = BoxCollectDate;
        }

        public String getProductCollectDate() {
            return ProductCollectDate;
        }

        public void setProductCollectDate(String ProductCollectDate) {
            this.ProductCollectDate = ProductCollectDate;
        }

        public String getUploadOrNot() {
            return UploadOrNot;
        }

        public void setUploadOrNot(String UploadOrNot) {
            this.UploadOrNot = UploadOrNot;
        }
    }
}

package com.furja.iqc.json;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 勤检单
 */
public class CheckInfoJson {

    private List<IsCheckBean> IsCheck;
    private List<BCXWINCheckDataBean> BCXWINCheckData;

    public List<IsCheckBean> getIsCheck() {
        return IsCheck;
    }

    public void setIsCheck(List<IsCheckBean> IsCheck) {
        this.IsCheck = IsCheck;
    }

    public List<BCXWINCheckDataBean> getBCXWINCheckData() {
        return BCXWINCheckData;
    }

    public void setBCXWINCheckData(List<BCXWINCheckDataBean> BCXWINCheckData) {
        this.BCXWINCheckData = BCXWINCheckData;
    }

    public static class IsCheckBean {
        /**
         * IsCheck : true
         */

        private String IsCheck;

        public String getIsCheck() {
            return IsCheck;
        }

        public void setIsCheck(String IsCheck) {
            this.IsCheck = IsCheck;
        }
    }

    public static class BCXWINCheckDataBean {
        /**
         * 生产单号 : 通用件及样机
         * 采购订单号 : POORD372299
         * 物料代码 : 6518010101
         * 物料名称 : HX-82C定子骨架(49×47×28)
         * 规格型号 :
         * 送货数量 : 6
         * 标签数量 : 6
         * 进度 : 1
         * 扫码数量 : 0
         * 采购请检单内码 : 439440
         */
        @JSONField(name = "生产单号")
        private String productionNo;
        @JSONField(name = "采购订单号")
        private String purchaseOrderNo;
        @JSONField(name = "物料代码")
        private String materialCode;
        @JSONField(name = "物料名称")
        private String materialName;
        @JSONField(name = "规格型号")
        private String model;
        @JSONField(name = "送货数量")
        private String deliverNums;
        @JSONField(name = "标签数量")
        private String labelNums;
        @JSONField(name = "进度")
        private String progress;
        @JSONField(name = "扫码数量")
        private String scanNums;
        @JSONField(name = "采购请检单内码")
        private String purchaseApply;

        public String getProductionNo() {
            return productionNo;
        }

        public void setProductionNo(String productionNo) {
            this.productionNo = productionNo;
        }

        public String getPurchaseOrderNo() {
            return purchaseOrderNo;
        }

        public void setPurchaseOrderNo(String purchaseOrderNo) {
            this.purchaseOrderNo = purchaseOrderNo;
        }

        public String getMaterialCode() {
            return materialCode;
        }

        public void setMaterialCode(String materialCode) {
            this.materialCode = materialCode;
        }

        public String getMaterialName() {
            return materialName;
        }

        public void setMaterialName(String materialName) {
            this.materialName = materialName;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getDeliverNums() {
            return deliverNums;
        }

        public void setDeliverNums(String delivernNums) {
            this.deliverNums = delivernNums;
        }

        public String getLabelNums() {
            return labelNums;
        }

        public void setLabelNums(String labelNums) {
            this.labelNums = labelNums;
        }

        public String getProgress() {
            return progress;
        }

        public void setProgress(String progress) {
            this.progress = progress;
        }

        public String getScanNums() {
            return scanNums;
        }

        public void setScanNums(String scanNums) {
            this.scanNums = scanNums;
        }

        public String getPurchaseApply() {
            return purchaseApply;
        }

        public void setPurchaseApply(String purchaseApply) {
            this.purchaseApply = purchaseApply;
        }

        public int getTotalNums() {
            return Integer.valueOf(deliverNums);
        }
        public int getLabelNum() {
            return Integer.valueOf(labelNums);
        }
    }
}

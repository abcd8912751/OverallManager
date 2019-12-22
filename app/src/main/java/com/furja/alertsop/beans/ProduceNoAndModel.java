package com.furja.alertsop.beans;

/**
 * 生产单号和产品型号集
 */
public class ProduceNoAndModel {
    private String produceNo;
    private String productModel;

    public String getProduceNo() {
        return produceNo;
    }

    public void setProduceNo(String produceNo) {
        this.produceNo = produceNo;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    @Override
    public String toString() {
        return produceNo + "   客户型号:"+ productModel ;
    }
}

package com.furja.iqc.beans;

/**
 * 条码和条码对应的数量
 */

public class BarCodeQty {
    String barCode;
    int qty;

    public BarCodeQty() {
    }

    public BarCodeQty(String barCode, int qty) {
        this.barCode = barCode;
        this.qty = qty;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public boolean contains(String barCode) {
        return getBarCode()
                .contains(barCode);
    }
}

package com.furja.iqc.beans;

/**
 * LotSize(请检总数)及对应的合格允收数目
 */

public class AcLotQty {
    private int lotSize;
    private int acQty;

    public AcLotQty(int lotSize, int acQty) {
        this.lotSize = lotSize;
        this.acQty = acQty;
    }

    public int getLotSize() {
        return lotSize;
    }

    public void setLotSize(int lotSize) {
        this.lotSize = lotSize;
    }

    public int getAcQty() {
        return acQty;
    }

    public void setAcQty(int acQty) {
        this.acQty = acQty;
    }
}

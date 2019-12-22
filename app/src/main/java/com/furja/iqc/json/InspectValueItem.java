package com.furja.iqc.json;

public class InspectValueItem {

    /**
     * FInspectResult2 :
     * FInspectValueB : {"FNUMBER":""}
     * FInspectValueT :
     * FInspectValue : 20
     */

    public InspectValueItem(){

    }

    public InspectValueItem(double inspectValue){
        this.FInspectValue=inspectValue;
    }

    public InspectValueItem(String inspectValueB){
        FInspectValueBBean valueBBean=new FInspectValueBBean();
        valueBBean.setFNUMBER(inspectValueB);
        this.FInspectValueB = valueBBean;
    }

    private String FInspectResult2;
    private FInspectValueBBean FInspectValueB;
    private String FInspectValueT;
    private double FInspectValue;

    public String getFInspectResult2() {
        return FInspectResult2;
    }

    public void setFInspectResult2(String FInspectResult2) {
        this.FInspectResult2 = FInspectResult2;
    }

    public FInspectValueBBean getFInspectValueB() {
        return FInspectValueB;
    }

    public void setFInspectValueB(FInspectValueBBean FInspectValueB) {
        this.FInspectValueB = FInspectValueB;
    }


    public String getFInspectValueT() {
        return FInspectValueT;
    }

    public void setFInspectValueT(String FInspectValueT) {
        this.FInspectValueT = FInspectValueT;
    }

    public double getFInspectValue() {
        return FInspectValue;
    }

    public void setFInspectValue(double FInspectValue) {
        this.FInspectValue = FInspectValue;
    }

    public static class FInspectValueBBean {
        /**
         * FNUMBER :
         */

        private String FNUMBER;

        public String getFNUMBER() {
            return FNUMBER;
        }

        public void setFNUMBER(String FNUMBER) {
            this.FNUMBER = FNUMBER;
        }
    }
}

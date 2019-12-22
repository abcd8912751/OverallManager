package com.furja.verify.beans;

/**
 * 成品校验状态,电池包1校验完成,电池包2校验完成
 */

public class CheckState {
    private boolean checkedBattery1;
    private boolean checkedBattery2;
    private boolean checkedOutBox;
    private boolean checkedColorBox;
    int verifiedNum;  //校验成功的数目
    private int verifyNum;  //待校验的电池包数目

    public void reset()
    {
        this.verifyNum=2;
        this.verifiedNum=0;
        setCheckedBattery1(false);
        setCheckedBattery2(false);
        setCheckedColorBox(false);
        setCheckedOutBox(false);
    }
    public void minusVerifyNum()
    {
        verifyNum--;
    }

    public int getVerifyNum() {
        return verifyNum;
    }

    public void setVerifyNum(int verifyNum) {
        this.verifyNum = verifyNum;
    }
    public boolean verifyFinish()
    {
        if(verifiedNum==verifyNum)
        {
            if(isCheckedColorBox()&&isCheckedOutBox())
                return true;
        }
        return false;
    }

    public boolean isCheckedBattery1() {
        return checkedBattery1;
    }

    public void setCheckedBattery1(boolean checkedBattery1) {
        this.checkedBattery1 = checkedBattery1;
        if(checkedBattery1)
            verifiedNum++;
    }

    public boolean isCheckedBattery2() {
        return checkedBattery2;
    }

    public void setCheckedBattery2(boolean checkedBattery2) {
        this.checkedBattery2 = checkedBattery2;
        if(checkedBattery2)
            verifiedNum++;
    }

    public boolean isCheckedOutBox() {
        return checkedOutBox;
    }

    public void setCheckedOutBox(boolean checkedOutBox) {
        this.checkedOutBox = checkedOutBox;
    }

    public boolean isCheckedColorBox() {
        return checkedColorBox;
    }

    public void setCheckedColorBox(boolean checkedColorBox) {
        this.checkedColorBox = checkedColorBox;
    }
}

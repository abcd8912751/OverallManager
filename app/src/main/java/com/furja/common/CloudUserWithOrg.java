package com.furja.common;

public class CloudUserWithOrg {
    private String username;
    private int FUserID;
    private int FOrgID;
    private String FOrgNumber;
    private String FOrgName;
    private String FNote;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFNote() {
        return FNote;
    }

    public void setFNote(String FNote) {
        this.FNote = FNote;
    }

    public int getFUserID() {
        return FUserID;
    }

    public void setFUserID(int FUserID) {
        this.FUserID = FUserID;
    }

    public int getFOrgID() {
        return FOrgID;
    }

    public void setFOrgID(int FOrgID) {
        this.FOrgID = FOrgID;
    }

    public String getFOrgNumber() {
        return FOrgNumber;
    }

    public void setFOrgNumber(String FOrgNumber) {
        this.FOrgNumber = FOrgNumber;
    }

    public String getFOrgName() {
        return FOrgName;
    }

    public void setFOrgName(String FOrgName) {
        this.FOrgName = FOrgName;
    }

    @Override
    public String toString() {
        return FOrgName;
    }
}

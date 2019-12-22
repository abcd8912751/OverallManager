package com.furja.iqc.json;

/**
 *获取服务器版本信息的 JSON串
 */

public class AutoUpdateJson {

    /**
     * latestApkUrl : \\192.168.10.5\公司开发软件\富佳条码管理系统\条码管理APP移动平台WebService服务\BCWebService\APK\AutoUpdate\latestAuto.apk
     * packageName : com.furja.overallmanager
     * versionCode : 5
     * versionName : 5.0809
     */

    private String latestApkUrl;
    private String packageName;
    private int versionCode;
    private String versionName;
    private String updateInfo;
    public String getLatestApkUrl() {
        return latestApkUrl;
    }

    public void setLatestApkUrl(String latestApkUrl) {
        this.latestApkUrl = latestApkUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfos) {
        this.updateInfo = updateInfos;
    }
}

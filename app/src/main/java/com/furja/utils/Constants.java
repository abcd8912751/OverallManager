package com.furja.utils;


import androidx.annotation.NonNull;

import com.furja.alertsop.services.NetworkChangeReceiver;

public class Constants {
    public static final String LOG_TAG="Overall_manager";
    public static final String FURJA_BCWEBOUTER_URL ="http://61.164.75.234:5050/bcwebservice/Service.asmx/";
    public static final String FURJA_BCWEBINNER_URL ="http://192.168.10.5:5050/bcwebservice/Service.asmx/";
    public static final String FURJA_QCWEBINNER_URL ="http://192.168.10.5:5050/qCWEBSERVICE/Service.asmx/";
    public static final String FURJA_QCWEBOUTER_URL ="http://61.164.75.234:5050/qCWEBSERVICE/Service.asmx/";
    public static final String VERTX_TEST_URL="http://192.168.10.92:8378";
    public static final String VERTX_INNER_URL="http://192.168.8.46:8378";
    public static final String VERTX_OUTER_URL="http://www.nbfurja.com:8378";
    public static final String FURJA_INNER_URL="http://192.168.8.46:8118/";
    public static final String FURJA_OUTER_URL="http://www.nbfurja.com/";
    public static final String EXCEPTION_LOG_URL="http://192.168.10.92:8377/";
    public static final String FURJA_BARCODEINFO_URL ="http://192.168.8.46:8118/FJCommonInterface/GetBarCodeInfo/";
    public static final String FURJA_RELEASE_URL ="http://192.168.8.46:8118/FJBadTypeInterface/SendProductRelease/";
    public static final String UPLOAD_FINISH="uploadfinish";
    public static final String RESET_CONFIG="reset_config";
    public static final String INTERNET_ABNORMAL="网络连接异常";
    public static final String SERVER_ABNORMAL="服务器异常";
    public static final String MATERIAL_INTERNET_ABNORMAL="获取物料信息网络连接异常";
    public static final String OPERATOR_LOGIN_ERROR="账号或密码错误";
    public static final String TAG_GET_QCLIST="获取到满足条件的质检方案";
    public static final String TAG_CANCEL_REQUEST ="当得到Qclist相关信息时取消请求并隐藏对话框";
    public static final String TAG_ERROR_NET ="网络等异常";
    public static final String TAG_ERROR_BARCODE="携带异常条形码信息";
    public static final String TAG_SCAN_BARCODE="条码扫描信息";
    public static final String EXTRA_QCENTRY_DATA ="质检方案检验项目信息";
    public static final String EXTRA_QCDATA_BEAN ="质检方案信息";
    public static final String EXTRA_QCVALUE_DATA ="检验项目检测值信息";
    public static final String EXTRA_BARCODE="二维码信息";
    public static final String EXTRA_APPLYQC_NUM="请检数量信息";
    public static final String TAG_SWITCH_POSITION="切换RecyclerView子项";
    public static final String TAG_ACTIVITY_RESET="ActivityReset";
    public static final String EXTRA_NOREASON_NUMBER="EXTRA_NOREASON_NUMBER";
    public static final String PRE_STORE_ITEM="准备保存数据";
    public static final String STORE_ITEM_OVER="保存数据完成";
    public static final String REQUEST_RESET="重置视图";
    public static final int CODE_TO_DETAIL=300;
    public static final String HTTPS_INNER_URL="https://192.168.8.46:7070";
    public static final String HTTPS_OUTER_URL="https://www.nbfurja.com:7070";
    public static final String FURIA_BCWEBOUTER_URL ="http://61.164.75.234:5050/bcwebservice/Service.asmx/";
    public static final String FURIA_BCWEBINNER_URL ="http://192.168.10.5:5050/bcwebservice/Service.asmx/";
    public static final String FURIA_LOGININNER_URL ="http://192.168.10.5:5050/bcwebservice/Service.asmx/";
    public static final String FURIA_UPLOAD_URL ="/FJBadTypeInterface/SendBadTypeLog/";
    public static final String FURIA_BADTYPEBASIC_URL ="/FJBadTypeInterface/GetBadTypeBasicData";
    public static final String FJ_BADTYPETOTAL_WORKPLACE ="/FJBadTypeInterface/GetBadTypeTotal/";
    public static final String ZOOM_EXTRA_NAME="url-List";
    //当点击Button或提交时传递该Tag事件
    public static final String UPDATE_BAD_COUNT ="UPDATE_BAD_COUNT";
    public static final String FRAGMENT_ON_TOUCH ="FRAGMENT_ON_TOUCH";
    public static final String BADLOG_FRAGMENT_INITFINISH ="updateBadLogWithBtn_Data";
    //根据物料信息更新工单列表
    public static final String UPDATE_WORKORDER_BY_MATERIAL ="updateBY_MATERIAL_INFO";
    //更新Button计数所在Fragment数据
    public static final String UPDATE_BADLOGWITHBTN="freshTheButtonFragment";
    //更新KeyBoard所在Fragment数据
    public static final String UPDATE_BADLOGWITHKEY="freshTheKeyBoardFragment";
    //同步异常类型配置完成后传递此事件
    public static final String SYNCOVER_BADTYPE_CONFIG="freshTheKeyBoardFragment";
    public static final String ALARM_ACTION_ON="ui.splash.alarm";
    public static final String INTER_SPLIT =" -> ";
    public static final String NODATA_AVAILABLE="没有找到符合条件的结果";
    //为录入异常数据使用视图的标识,-1为没有设置
    public static final int TYPE_BADLOG_WITHBTN=1;  //注塑车间
    public static final int TYPE_BADLOG_WITHKEY=2;  //装配车间
    public static final int TYPE_BADLOG_EMPTY=-1;
    public static final String BUTTON_FRAGMENT_TITLE="注塑车间";
    public static final String KEY_FRAGMENT_TITLE="装配车间";
    public static final String INFORMATION_HAS_NUL="INFORMATION_HAS_NULL";
    public static final String TAG_GOT_TOURLOG="获取到注塑巡检数据";
    public static final String TAG_GOT_DIMENLOG="获取到注塑尺寸记录数据";
    public static final String ACTION_UPDATE_APK="UPDATE_APK";
    public static final String DIMENITEM_TOUCH ="点击了RecyclerView的ChildItem";
    public static final int TYPE_RULER_GAUGE =0;  //卡尺测试
    public static final int TYPE_PIN_GAUGE=1;    //针规测试
    public static final int TYPE_NODIMEN_GAUGE =2; //没实测值,试检具判断
    public static final int TYPE_HARDNESS_GAUGE =3; //硬度测试
    public static final int TYPE_FEELER_GAUGE =4;   //塞尺平面度测试
    public static boolean isInnerNet=false;
    public static final String FURJA_LOGIN_URL ="http://61.164.75.234:5050/bcwebservice/Service.asmx/";
    public static final String FURJA_SCDHINFO_URL="http://61.164.75.234:5050/bcwebservice/Service.asmx/GetSCDHInfo";
    public static final String FURJA_BCWEB_URL="http://192.168.10.5:5050/bcwebservice/";
    public static final String TAG_GOT_NETWORK="TAG_GOT_NETWORK";
    public static final String TRANSFER_EVENT="transfer_event";
    public static final String EXTRA_WEBVIEW_URL="EXTRA_VIEW_URL";
    public static final String EXTRA_WEBVIEW_TITLE="EXTRA_WEBVIEW_TITLE";
    public static final String CHAT_PUSH_URL="http://192.168.10.92:8379";  //聊天的URL
    public static final int CONNECTING_STATE=101;
    public static final int CONNECTED_STATE=102;
    public static final int UNCONNECTED_STATE=103;
    public static final String FURJA_UPLOAD_URL ="http://192.168.8.46:8118/FJBadTypeInterface/SendBadTypeLog/";
    public static final String FURJA_BADTYPEBASIC_URL ="http://192.168.8.46:8118/FJBadTypeInterface/GetBadTypeBasicData";
    //当点击Button或提交时传递该Tag事件
    public static final String CHAT_EVENT="chat_event";
    public static final String PUSH_EVENT="push_event";
    public static String getFurjaLoginUrl()
    {
        if(isInnerNet)
            return InneURL.FURJA_LOGIN_URL;
        else
            return FURJA_LOGIN_URL;
    }

    public static String getFjBadtypetotalWorkplaceUrl()
    {
        if(isInnerNet)
            return InneURL.FJ_BADTYPETOTAL_WORKPLACE;
        else
            return FJ_BADTYPETOTAL_WORKPLACE;
    }

    public static String getFurjaReleaseUrl() {
        if(isInnerNet)
            return InneURL.FURIA_RELEASE_URL;
        else
            return FURJA_RELEASE_URL;
    }

    public static String getFurjaSCDHINFO() {
        if(isInnerNet)
            return InneURL.FURJA_SCDHINFO_URL;
        else
            return FURJA_SCDHINFO_URL;
    }

    public static String getFurjaBarCodeINFO() {
        if(isInnerNet)
            return InneURL.FURIA_RELEASE_URL;
        else
            return FURJA_RELEASE_URL;
    }
    @NonNull
    public static String getBaseUrl() {
        String url="";
//        if(NetworkChangeReceiver.isInnerNet())
        url= FURJA_INNER_URL;
//        else
//            url= FURJA_OUTER_URL;
        return url;
    }

    @NonNull
    public static String getHttpsUrl() {
        String url="";
//        if(NetworkChangeReceiver.isInnerNet())
        url= HTTPS_INNER_URL;
//        else
//            url= HTTPS_OUTER_URL;
        return url;
    }



    @NonNull
    public static String getVertxUrl() {
        String url="";
        if(NetworkChangeReceiver.isInnerNet())
            url= VERTX_INNER_URL;
        else
            url= VERTX_OUTER_URL;
        return url;
    }
}

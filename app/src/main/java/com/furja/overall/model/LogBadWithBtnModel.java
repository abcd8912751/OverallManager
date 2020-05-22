package com.furja.overall.model;

import android.text.TextUtils;

import com.furja.common.BadMaterialLogDao;
import com.furja.common.DaoSession;
import com.furja.overall.FurjaApp;
import com.furja.overall.beans.MaterialInfo;
import com.furja.overall.beans.WorkOrderInfo;
import com.furja.contract.LogBadWithBnContract;
import com.furja.common.BadMaterialLog;
import com.furja.utils.Constants;
import com.furja.utils.SharpBus;
import com.furja.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

import static com.furja.utils.Utils.showLog;
import static com.furja.utils.Utils.showToast;

/**
 * Created by zhangmeng on 2017/12/3.
 */

public class LogBadWithBtnModel implements LogBadWithBnContract.Model {
    private String[] btn_titles;
    private List<Long> markCounts;
    private List<String> badcodes;
    private DaoSession daoSession;
    private BadMaterialLog badMaterialLog;
    private WorkOrderInfo workOrderInfo;
    public LogBadWithBtnModel(String[] titles)
    {
        this.daoSession= FurjaApp.getDaoSession();

        this.btn_titles=titles;
        initArrays();
    }

    /**
     * 判断物料代码是否为空
     * @return
     */
    public boolean ISNisNull()
    {

        if(TextUtils.isEmpty(badMaterialLog.getMaterialISN()))
            return true;
        return false;
    }

    /**
     * 判断物料tiaoma、员工号、机台号中是否有空的情况
     */
    public boolean infoHasNull()
    {
        if(badMaterialLog==null)
            return true;
        if(ISNisNull())
        {
            SharpBus.getInstance()
                    .post(Constants.INFORMATION_HAS_NUL,0);
            return true;
        }
        if(TextUtils.isEmpty(badMaterialLog.getOperatorId()))
        {
            SharpBus.getInstance()
                    .post(Constants.INFORMATION_HAS_NUL,4);
            return true;
        }
        if(TextUtils.isEmpty(badMaterialLog.getWorkPlaceId()))
        {
            SharpBus.getInstance()
                    .post(Constants.INFORMATION_HAS_NUL,5);
            return true;
        }
        return false;
    }

    /**
     * 判断物料tiaoma、员工号、机台号中是否有空的情况
     */
    public boolean infoHasNullL()
    {
        if(badMaterialLog==null)
            return true;
        if(ISNisNull())
        {
            return true;
        }
        if(TextUtils.isEmpty(badMaterialLog.getOperatorId()))
        {
            return true;
        }
        if(TextUtils.isEmpty(badMaterialLog.getWorkPlaceId()))
        {
            return true;
        }
        return false;
    }


    /**
     * 将所有MarkerButton清零
     */
    public void clearCount()
    {
        for(int i=0;i<markCounts.size();i++)
            markCounts.set(i, (long) 0);
        if(badMaterialLog !=null)
        {
            badMaterialLog.setBadCount(0);
            badMaterialLog.setLongBadCounts(markCounts);
        }
    }

    /**
     * 初始化按钮视图,从本地数据库提取数据加载
     * 如果本地数据库没有则从arrays.xml里加载
     */
    private void initArrays() {
        markCounts=new ArrayList<Long>();
        badcodes= new ArrayList<String>();
        for(int i=0;i<getItemCount();i++)
        {
            markCounts.add((long) 0);
            badcodes.add(i+"");
        }
    }

    @Override
    public int getItemCount() {
        return btn_titles.length;
    }

    @Override
    public String getOptionTitle(int position) {

        return btn_titles[position];
    }

    @Override
    public long getMarkerCount(int position) {
        return markCounts.get(position);
    }

    @Override
    public void addMarkerCount(int position) {
        markCounts.set(position,markCounts.get(position)+1);
        badMaterialLog.setBadCount(getTotalBad());
        badMaterialLog.setLongBadCounts(markCounts);
    }

    @Override
    public long getTotalBad() {
        long count=0;
        for(long i:markCounts)
            count=count+i;
        return count;
    }

    /**
     * 将物料、工号、员工号等信息传入更新视图数据
     * @param workOrderInfo
     */
    @Override
    public void updateData(WorkOrderInfo workOrderInfo) {
        setWorkOrderInfo(workOrderInfo);
        clearCount();
        if(badMaterialLog ==null)
            badMaterialLog
                    =new BadMaterialLog(workOrderInfo, Constants.TYPE_BADLOG_WITHBTN,markCounts, getTotalBad());
        else
        {
            badMaterialLog.setWorkOrderInfo(workOrderInfo);
            badMaterialLog.setLongBadCounts(markCounts);
            badMaterialLog.setBadCount(getTotalBad());
        }
        if(badcodes==null)
        {
            badcodes=new ArrayList<String>();
            for(int i=0;i<markCounts.size();i++)
                badcodes.add(i+"");
        }
        badMaterialLog.setBadTypeCode(badcodes);
        setMarkCounts(badMaterialLog.toLongList());
    }


    public void setMarkCounts(List<Long> markCounts) {
        this.markCounts = markCounts;
    }

    /**
     * 以MaterialInfo来查找DefectLog
     * @return
     */
    public List<BadMaterialLog> queryBadLogByInfo()
    {
        BadMaterialLogDao dao=daoSession.getBadMaterialLogDao();
        QueryBuilder queryBuilder=dao.queryBuilder();
        List<BadMaterialLog> badMaterialLogs =queryBuilder.where
                (BadMaterialLogDao.Properties.MaterialISN.eq(workOrderInfo.getMaterialISN()))
                .where(BadMaterialLogDao.Properties.OperatorId.eq(workOrderInfo.getOperatorId()))
                .where(BadMaterialLogDao.Properties.WorkPlaceId.eq(workOrderInfo.getWorkPlaceId()))
                .list();
        if(badMaterialLogs ==null)
            return Collections.EMPTY_LIST;
        else
            return badMaterialLogs;
    }


    /**
     * 同步数据至本地及服务器
     */
    public void syncData()
    {
        if(badMaterialLog!=null)
        {
            if(TextUtils.isEmpty(badMaterialLog.getMaterialISN()))
                return;
            if(TextUtils.isEmpty(badMaterialLog.getOperatorId()))
            {
                return;
            }
            if(TextUtils.isEmpty(badMaterialLog.getWorkPlaceId()))
            {
                return;
            }
            syncToLocal();
            badMaterialLog.setMaterialISN("");
            badMaterialLog.setOperatorId("");
            badMaterialLog.setWorkPlaceId("");
        }
    }

    /**
     * 同步至本地
     */
    public void syncToLocal()
    {
        if(badMaterialLog ==null)
            showLog(getClass()+"->当前执有的BadMaterialLog数据库为空,不予保存");
        else
        {
            badMaterialLog.setLongBadCounts(markCounts);
            badMaterialLog.setBadCount(getTotalBad());
            if(badcodes==null)
            {
                badcodes=new ArrayList<String>();
                for(int i=0;i<markCounts.size();i++)
                    badcodes.add(i+"");
            }
            badMaterialLog.setBadTypeCode(badcodes);
            Utils.saveToLocal(badMaterialLog);
        }
    }

    /**
     * 获取品质异常数据库基类
     * @return
     */
    public BadMaterialLog getBadMaterialLog() {
        return badMaterialLog;
    }
    /**
     * 设置品质异常数据库 基类
     */
    public void setBadMaterialLog(BadMaterialLog badMaterialLog) {
        this.badMaterialLog = badMaterialLog;
    }

    public WorkOrderInfo getWorkOrderInfo() {
        return workOrderInfo;
    }

    public void setWorkOrderInfo(WorkOrderInfo workOrderInfo) {
        this.workOrderInfo = workOrderInfo;
    }

    /**
     * 将当前状态整理成字符串以供备忘录保存
     * @return
     */
    public String getMarkCountString() {
        StringBuffer stringBuffer=new StringBuffer();
        for(Long entity:markCounts)
        {
            stringBuffer.append(entity);
            stringBuffer.append(",");
        }
        if(stringBuffer.length()>0)
            stringBuffer.deleteCharAt(stringBuffer.length()-1);
        return stringBuffer.toString();
    }

    /**
     * 设置指定位置的异常个数
     * @param position
     * @param count
     */
    public void setMarkerCount(int position, long count) {
        markCounts.set(position,count);
        badMaterialLog.setBadCount(getTotalBad());
        badMaterialLog.setLongBadCounts(markCounts);

    }

    public void getMaterialISNbyBarCode(String barCode) {
        OkHttpUtils
                .get()
                .url(Constants.FURJA_BARCODEINFO_URL)
                .addParams("Barcode", barCode)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        SharpBus.getInstance().post(Constants.MATERIAL_INTERNET_ABNORMAL,"true");
                    }

                    @Override
                    public void onResponse(String response, int i) {
                        showLog(getClass()+response);
                        MaterialInfo info = new MaterialInfo();
                        try {
                            info.formatJson(response);
                            if(!TextUtils.isEmpty(info.getMaterialName()))
                            {
                                if(badMaterialLog!=null)
                                {
                                    badMaterialLog.setMaterialISN(info.getMaterialISN());
                                    syncData();
                                    SharpBus.getInstance()
                                            .post(Constants.UPLOAD_FINISH,"unloadFinish");
                                    clearCount();
                                }
                            }
                        }
                        catch (Exception e) {
                            showToast("没有找到物料,需重输");
                        }
                        finally {

                        }
                    }
                });

    }
}

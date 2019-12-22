package com.furja.iqc.utils;

import com.furja.devicemanager.databases.DaoSession;
import com.furja.iqc.beans.QMAGroupData;
import com.furja.iqc.beans.QMAGroupDataDao;
import com.furja.utils.Utils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.furja.utils.Utils.showLog;

/**
 * 本地搜索
 */
public class LocalQuery {
    private DaoSession daoSession;
    public LocalQuery()
    {
        this.daoSession= Utils.getDaoSession();
    }

    public List<QMAGroupData> query(CharSequence queryString)
    {
        String query
                ="%"+queryString+"%";
        List<QMAGroupData> results
                =getFilterList(query);
        return results;
    }

    public List<QMAGroupData> getFilterList(String query)
    {
        QMAGroupDataDao dao=daoSession.getQMAGroupDataDao();
        QueryBuilder queryBuilder
                =dao.queryBuilder();
        List<QMAGroupData> productNumbers=queryBuilder.where(queryBuilder.or(QMAGroupDataDao.Properties.FNumber.like(query)
                ,QMAGroupDataDao.Properties.FName.like(query)))
                .list();
        if(productNumbers==null)
            return Collections.<QMAGroupData>emptyList();
        showLog("找到符合的条数:"+productNumbers.size());
        List<String> results=new ArrayList<String>();
        for(QMAGroupData productNumber:productNumbers)
            results.add(productNumber.getFName());
        return productNumbers;
    }



}

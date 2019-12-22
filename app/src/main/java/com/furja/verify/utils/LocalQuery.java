package com.furja.verify.utils;

import com.furja.devicemanager.databases.DaoSession;
import com.furja.overall.FurjaApp;
import com.furja.verify.model.ProductNumber;
import com.furja.verify.model.ProductNumberDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.furja.utils.Utils.showLog;

/**
 * Created by zhangmeng on 2018/1/8.
 */

public class LocalQuery {
    private DaoSession daoSession;
    public LocalQuery()
    {
        this.daoSession= FurjaApp.getDaoSession();
    }

    public List<String> query(CharSequence queryString)
    {
        String query
                ="%"+queryString+"%";
        List<String> results
                =getFilterList(query);
        return results;

    }
    public List<String> getFilterList(String query)
    {
        ProductNumberDao dao=daoSession.getProductNumberDao();
        QueryBuilder queryBuilder
                =dao.queryBuilder();
        List<ProductNumber> productNumbers=queryBuilder.where(ProductNumberDao.Properties.ProductNo.like(query))
                .list();
        if(productNumbers==null)
            return Collections.<String>emptyList();
        showLog("找到符合的条数:"+productNumbers.size());
        List<String> results=new ArrayList<String>();
        for(ProductNumber productNumber:productNumbers)
            results.add(productNumber.getProductNo());
        return results;
    }



}

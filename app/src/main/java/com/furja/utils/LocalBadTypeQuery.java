package com.furja.utils;

import android.text.TextUtils;

import com.furja.overall.FurjaApp;
import com.furja.common.BadTypeConfig;
import com.furja.common.BadTypeConfigDao;
import com.furja.common.DaoSession;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通过本地数据库查询内容
 */

public class LocalBadTypeQuery implements BadTypeQuery {

    @Override
    public List<String> query(String input) {
        List<BadTypeConfig> badTypeConfigs=queryLocal(input);
        List<String> results=new ArrayList<String>();
        for(BadTypeConfig config:badTypeConfigs)
            results.add(config.toString());
        return results;
    }

    private  List<BadTypeConfig> queryLocal(String some)
    {
        if(TextUtils.isEmpty(some))
            return Collections.EMPTY_LIST;
        some=some.toUpperCase();
        DaoSession daoSession= FurjaApp.getDaoSession();
        BadTypeConfigDao dao=daoSession.getBadTypeConfigDao();
        QueryBuilder queryBuilder=dao.queryBuilder();
        List<BadTypeConfig> badLogs=
                queryBuilder.where(BadTypeConfigDao.Properties.BadTypeCode.like("%"+some+"%"))
                        .where(BadTypeConfigDao.Properties.SourcType.eq(2)).list();
        if(badLogs==null)
            return Collections.EMPTY_LIST;
        else
            return badLogs;
    }

}

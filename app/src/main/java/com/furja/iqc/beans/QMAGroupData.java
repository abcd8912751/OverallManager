package com.furja.iqc.beans;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import com.furja.devicemanager.databases.DaoSession;

/**
 * 从网络获取不良项目后保存到本地
 */
@Entity(active = true)
public class QMAGroupData {
    @Id(autoincrement = true)
    private Long id;
    private String FNumber;
    private String FName;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1171737613)
    private transient QMAGroupDataDao myDao;
    @Generated(hash = 602245871)
    public QMAGroupData(Long id, String FNumber, String FName) {
        this.id = id;
        this.FNumber = FNumber;
        this.FName = FName;
    }
    @Generated(hash = 538397431)
    public QMAGroupData() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFNumber() {
        return this.FNumber;
    }
    public void setFNumber(String FNumber) {
        this.FNumber = FNumber;
    }
    public String getFName() {
        return this.FName;
    }
    public void setFName(String FName) {
        this.FName = FName;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    @Override
    public String toString() {
        return FNumber;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1015966543)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getQMAGroupDataDao() : null;
    }
}

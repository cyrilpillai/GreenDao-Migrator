package com.cyrilpillai.greendao_migrator.database.util;


import com.cyrilpillai.greendao_migrator.dao.DaoSession;

public class DAOSessionHolder {
    private static volatile DAOSessionHolder singleton = null;
    private DaoSession daoSession;

    public static DAOSessionHolder getInstance() {
        if (singleton == null) {
            synchronized (DAOSessionHolder.class) {
                if (singleton == null) {
                    singleton = new DAOSessionHolder();
                }
            }
        }
        return singleton;
    }

    public void putDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
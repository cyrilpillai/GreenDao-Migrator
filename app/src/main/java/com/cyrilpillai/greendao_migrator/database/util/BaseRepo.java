package com.cyrilpillai.greendao_migrator.database.util;

import com.cyrilpillai.greendao_migrator.App;
import com.cyrilpillai.greendao_migrator.dao.DaoMaster;
import com.cyrilpillai.greendao_migrator.dao.DaoSession;

import org.greenrobot.greendao.database.Database;

public class BaseRepo {

    private static BaseRepo instance = null;
    protected DaoSession daoSession;

    public BaseRepo() {
        daoSession = DAOSessionHolder.getInstance().getDaoSession();
        if (daoSession == null) {
            MySQLiteOpenHelper helper = new MySQLiteOpenHelper(App.getContext(), "test.db");
            Database db = helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();
            DAOSessionHolder.getInstance().putDaoSession(daoSession);
        }
    }

    public static BaseRepo getInstance() {
        if (instance == null) {
            synchronized (BaseRepo.class) {
                if (instance == null) {
                    instance = new BaseRepo();
                }
            }
        }
        return instance;
    }

    public void truncate(Class classToDelete) {
        daoSession.deleteAll(classToDelete);
    }


    public void truncateAllTables() {
        daoSession.getDatabase().beginTransaction();
        try {
            DaoMaster.dropAllTables(daoSession.getDatabase(), true);
            DaoMaster.createAllTables(daoSession.getDatabase(), true);
            daoSession.getDatabase().setTransactionSuccessful();
        } finally {
            daoSession.getDatabase().endTransaction();
        }
    }
}

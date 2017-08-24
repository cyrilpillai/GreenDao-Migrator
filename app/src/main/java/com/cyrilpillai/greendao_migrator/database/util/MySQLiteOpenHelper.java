package com.cyrilpillai.greendao_migrator.database.util;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cyrilpillai.greendao_migrator.dao.DaoHelper;
import com.cyrilpillai.greendao_migrator.dao.DaoMaster;


public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {

    public MySQLiteOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        MigrationHelper.migrate(db, DaoHelper.getAllDaos());
    }
}

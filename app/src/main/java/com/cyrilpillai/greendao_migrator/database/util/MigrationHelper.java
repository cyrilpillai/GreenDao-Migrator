package com.cyrilpillai.greendao_migrator.database.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.internal.DaoConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Createdby PedroOkawa and modified by MBH on 16/08/16.
 */
public final class MigrationHelper {

    public static void migrate(SQLiteDatabase sqliteDatabase, List<Class<? extends AbstractDao<?, ?>>> daoClasses) {
        StandardDatabase db = new StandardDatabase(sqliteDatabase);
        generateNewTablesIfNotExists(db, daoClasses);
        generateTempTables(db, daoClasses);
        dropAllTables(db, true, daoClasses);
        createAllTables(db, false, daoClasses);
        restoreData(db, daoClasses);
    }

    @SuppressWarnings("unused")
    public static void migrate(StandardDatabase db, List<Class<? extends AbstractDao<?, ?>>> daoClasses) {
        generateNewTablesIfNotExists(db, daoClasses);
        generateTempTables(db, daoClasses);
        dropAllTables(db, true, daoClasses);
        createAllTables(db, false, daoClasses);
        restoreData(db, daoClasses);
    }

    private static void generateNewTablesIfNotExists(StandardDatabase db, List<Class<? extends AbstractDao<?, ?>>> daoClasses) {
        reflectMethod(db, "createTable", true, daoClasses);
    }

    private static void generateTempTables(StandardDatabase db, List<Class<? extends AbstractDao<?, ?>>> daoClasses) {
        for (Class<? extends AbstractDao<?, ?>> daoClass : daoClasses) {
            DaoConfig daoConfig = new DaoConfig(db, daoClass);
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");
            String insertTableStringBuilder = "CREATE TEMP TABLE " + tempTableName +
                    " AS SELECT * FROM " + tableName + ";";
            db.execSQL(insertTableStringBuilder);
        }
    }

    private static void dropAllTables(StandardDatabase db, boolean ifExists, @NonNull List<Class<? extends AbstractDao<?, ?>>> daoClasses) {
        reflectMethod(db, "dropTable", ifExists, daoClasses);
    }

    private static void createAllTables(StandardDatabase db, boolean ifNotExists, @NonNull List<Class<? extends AbstractDao<?, ?>>> daoClasses) {
        reflectMethod(db, "createTable", ifNotExists, daoClasses);
    }

    /**
     * dao class already define the sql exec method, so just invoke it
     */
    private static void reflectMethod(StandardDatabase db, String methodName, boolean isExists, @NonNull List<Class<? extends AbstractDao<?, ?>>> daoClasses) {
        if (daoClasses.size() < 1) {
            return;
        }
        try {
            for (Class cls : daoClasses) {
                Method method = cls.getDeclaredMethod(methodName, Database.class, boolean.class);
                method.invoke(null, db, isExists);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void restoreData(StandardDatabase db, List<Class<? extends AbstractDao<?, ?>>> daoClasses) {
        for (Class<? extends AbstractDao<?, ?>> daoClass : daoClasses) {
            DaoConfig daoConfig = new DaoConfig(db, daoClass);
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");
            // get all columns from tempTable, take careful to use the columns list
            List<String> columns = getColumns(db, tempTableName);
            ArrayList<String> properties = new ArrayList<>(columns.size());
            for (int j = 0; j < daoConfig.properties.length; j++) {
                String columnName = daoConfig.properties[j].columnName;
                if (columns.contains(columnName)) {
                    properties.add(columnName);
                }
            }
            if (properties.size() > 0) {
                final String columnSQL = TextUtils.join(",", properties);

                String insertTableStringBuilder = "INSERT INTO " + tableName + " (" +
                        columnSQL +
                        ") SELECT " +
                        columnSQL +
                        " FROM " + tempTableName + ";";
                try {
                    db.execSQL(insertTableStringBuilder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            db.execSQL("DROP TABLE " + tempTableName);
        }
    }

    private static List<String> getColumns(StandardDatabase db, String tableName) {
        List<String> columns = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 0", null);
            if (null != cursor && cursor.getColumnCount() > 0) {
                columns = Arrays.asList(cursor.getColumnNames());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (null == columns)
                columns = new ArrayList<>();
        }
        return columns;
    }

}
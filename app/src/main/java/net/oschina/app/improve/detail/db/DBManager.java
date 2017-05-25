package net.oschina.app.improve.detail.db;

import android.content.Context;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 数据库帮助类
 * Created by haibin on 2017/5/24.
 */
@SuppressWarnings("all")
public final class DBManager {
    private static DBManager mManager;
    private DBHelper mHelper;
    private String sql;

    public static DBManager from(Context context) {
        if (mManager == null) {
            mManager = new DBManager();
            mManager.mHelper = new DBHelper(context);
        }
        return mManager;
    }

    public void create(Class<?> cls) {
        mManager.mHelper.create(cls);
    }

    public boolean alter(String tableName, String columnName, String type) {
        return mManager.mHelper.alter(tableName, columnName, type);
    }

    public boolean alter(Class<?> cls) {
        return mManager.mHelper.alter(cls);
    }

    /**
     * 选择表结构
     *
     * @param object object Annotation with table
     * @return DBManager
     */
    public DBHelper select(Object object) {
        Class<?> cls = object.getClass();
        Annotation[] annotations = cls.getDeclaredAnnotations();
        if (annotations == null || annotations.length == 0)
            return null;
        return mManager.mHelper;
    }


    public DBManager where(String where) {
        mHelper.where(where);
        return mManager;
    }

    public DBManager where(String where, String... args) {
        mHelper.where(where, args);
        return mManager;
    }

    public boolean update(Object object) {
        if (object == null)
            return false;
        return mManager.mHelper.update(object);
    }

    public long getCount(Class<?> cls) {
        return mHelper.getCount(cls);
    }

    public boolean update(String table, String column, Object object) {
        return mManager.mHelper.update(table, column, object);
    }

    public boolean insert(Object object) {
        if (object == null)
            return false;
        return mManager.mHelper.insert(object);
    }

    public DBManager limit(int limit, int offset) {
        mHelper.limit(limit, offset);
        return mManager;
    }

    public <T> List<T> get(Class<T> cls) {
        return mHelper.get(cls);
    }

    public boolean delete(Class<?> cls) {
        return mHelper.delete(cls);
    }
}

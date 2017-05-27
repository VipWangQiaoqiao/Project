package net.oschina.app.improve.detail.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 详情习惯收集
 * Created by haibin on 2017/5/22.
 */
@SuppressWarnings("unused")
final class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "detail";
    private static final int DB_VERSION = 1;
    private String where;
    private String[] args;
    private int limit, offset;

    DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    private String getTableName(Class<?> cla) {
        Annotation[] annotations = cla.getDeclaredAnnotations();
        if (annotations == null || annotations.length == 0) {
            throw new IllegalStateException("you must use Table annotation for bean");
        }
        String tableName = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof Table)
                tableName = ((Table) annotation).tableName();
        }
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalStateException("you must use Table annotation for bean");
        }
        return tableName;
    }

    void create(Class<?> cls) {
        String tableName = getTableName(cls);
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " ";
        String table = "";
        String primary = "";
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                boolean isAutoincrement = primaryKey.autoincrement();
                String name = primaryKey.column();
                primary = String.format(name + " %s primary key " +
                        (isAutoincrement ? "autoincrement" : "") +
                        (i == fields.length - 1 ? "" : ","), getTypeString(field));
            } else if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                boolean isNotNull = column.isNotNull();
                String name = column.column();
                table = table + String.format(name + " %s" +
                        (i == fields.length - 1 ? "" : ","), getTypeString(field));
            }
        }
        sql = sql + "(" + primary + table + ")";
        getWritableDatabase().execSQL(sql);
    }

    boolean update(Object obj) {
        Class<?> cls = obj.getClass();
        String tableName = getTableName(cls);
        if (!isExist(tableName)) {
            return false;
        }
        SQLiteDatabase db = null;
        ContentValues values = new ContentValues();
        Field[] fields = cls.getDeclaredFields();
        try {
            db = getWritableDatabase();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    boolean isNotNull = column.isNotNull();
                    String name = column.column();
                    Object object = field.get(obj);
                    values.put(name, object == null ? "" : object.toString());
                } else if (field.isAnnotationPresent(PrimaryKey.class)) {
                    PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                    boolean isAutoincrement = primaryKey.autoincrement();
                    String name = primaryKey.column();
                    if (!isAutoincrement) {
                        Object object = field.get(obj);
                        values.put(name, object == null ? "" : object.toString());
                    }
                }
            }
            db.update(tableName, values, where, args);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            where = null;
            limit = 0;
            offset = 0;
            args = null;
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return false;
    }

    boolean insert(Object obj) {
        Class<?> cls = obj.getClass();
        String tableName = getTableName(cls);
        if (!isExist(tableName)) {
            return false;
        }
        SQLiteDatabase db = null;
        ContentValues values = new ContentValues();
        Field[] fields = cls.getDeclaredFields();
        try {
            db = getWritableDatabase();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    boolean isNotNull = column.isNotNull();
                    String name = column.column();
                    Object object = field.get(obj);
                    values.put(name, object == null ? "" : object.toString());
                } else if (field.isAnnotationPresent(PrimaryKey.class)) {
                    PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                    boolean isAutoincrement = primaryKey.autoincrement();
                    String name = primaryKey.column();
                    if (!isAutoincrement) {
                        Object object = field.get(obj);
                        values.put(name, object == null ? "" : object.toString());
                    }
                }
            }
            return db.insert(tableName, "", values) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
            where = null;
            limit = 0;
            offset = 0;
            args = null;
        }
        return false;
    }

    long getCount(Class<?> cls) {
        String tableName = getTableName(cls);
        if (!isExist(tableName))
            return -1;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            cursor = db.rawQuery(String.format("select count(*) from %s", tableName), null);
            cursor.moveToFirst();
            long count = cursor.getLong(0);
            cursor.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return 0;
    }

    DBHelper where(String cause) {
        this.where = cause;
        this.args = null;
        return this;
    }

    DBHelper where(String cause, String[] args) {
        this.where = cause;
        this.args = args;
        return this;
    }

    boolean update(String table, String column, Object object) {
        if (!isExist(table)) {
            return false;
        }
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            String sql = String.format("UPDATE %s SET %s = ? where %s ", table, column, where);
            db.execSQL(sql, new String[]{object.toString()});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            where = null;
            args = null;
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * 新增字段
     *
     * @param tableName  tableName
     * @param columnName columnName
     * @param type       type
     * @return true or false
     */
    boolean alter(String tableName, String columnName, String type) {
        if (!isExist(tableName)) return false;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.execSQL(String.format("ALTER TABLE %s ADD %s %s", tableName, columnName, type));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    /**
     * 新增字段
     *
     * @param cls cls
     * @return true or false
     */
    boolean alter(Class<?> cls) {
        String tableName = getTableName(cls);
        String primary = "";
        if (!isExist(tableName))
            return false;
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                boolean isNotNull = column.isNotNull();
                String name = column.column();
                if (!isColumnExist(tableName, name)) {
                    alter(tableName, name, getTypeString(field));
                }
            }
        }
        return false;
    }

    private boolean isColumnExist(String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0"
                    , null);
            result = cursor != null && cursor.getColumnIndex(columnName) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return result;
    }

    private boolean isExist(String tableName) {
        if (TextUtils.isEmpty(tableName)) {
            return false;
        }
        boolean exits = false;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String sql = "select * from sqlite_master where name=" + "'" + tableName + "'";
        try {
            db = getReadableDatabase();
            cursor = db.rawQuery(sql, null);
            if (cursor.getCount() != 0) {
                exits = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return exits;
    }

    private String getTypeString(Field field) {
        Class<?> type = field.getType();
        if (type.equals(int.class)) {
            return "integer";
        } else if (type.equals(String.class)) {
            return "text";
        } else if (type.equals(long.class)) {
            return "long";
        } else if (type.equals(float.class)) {
            return "feal";
        } else if (type.equals(double.class)) {
            return "feal";
        }
        return "varchar";
    }

    DBHelper limit(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
        return this;
    }

    <T> List<T> get(Class<T> cls) {
        String tableName = getTableName(cls);
        if (!isExist(tableName))
            return null;
        List<T> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getReadableDatabase();
            String sql = limit != 0 ? String.format("SELECT * from %s limit %s offset %s",
                    tableName, String.valueOf(limit), String.valueOf(offset)) :
                    String.format("SELECT * from %s", tableName);
            cursor = db.rawQuery(sql, null);
            Field[] fields = cls.getDeclaredFields();
            while (cursor.moveToNext()) {
                T t = cls.newInstance();
                for (Field field : fields) {
                    field.setAccessible(true);
                    String name = "";
                    if (field.isAnnotationPresent(Column.class))
                        name = field.getAnnotation(Column.class).column();
                    else if (field.isAnnotationPresent(PrimaryKey.class))
                        name = field.getAnnotation(PrimaryKey.class).column();
                    if (!TextUtils.isEmpty(name)) {
                        Class<?> type = field.getType();
                        if (type.equals(int.class)) {
                            field.set(t, cursor.getInt(cursor.getColumnIndex(name)));
                        } else if (type.equals(String.class)) {
                            field.set(t, cursor.getString(cursor.getColumnIndex(name)));
                        } else if (type.equals(long.class)) {
                            field.set(t, cursor.getLong(cursor.getColumnIndex(name)));
                        } else if (type.equals(float.class)) {
                            field.set(t, cursor.getFloat(cursor.getColumnIndex(name)));
                        } else if (type.equals(double.class)) {
                            field.set(t, cursor.getDouble(cursor.getColumnIndex(name)));
                        }
                    }
                }
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return list;
    }

    boolean delete(Class<?> cls) {
        String tableName = getTableName(cls);
        if (!isExist(tableName))
            return false;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            int i = db.delete(tableName, where, args);
            return i > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

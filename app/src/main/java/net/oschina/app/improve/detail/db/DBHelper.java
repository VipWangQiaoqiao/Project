package net.oschina.app.improve.detail.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

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
                    values.put(name, field.get(obj).toString());
                } else if (field.isAnnotationPresent(PrimaryKey.class)) {
                    PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                    boolean isAutoincrement = primaryKey.autoincrement();
                    String name = primaryKey.column();
                    if (!isAutoincrement) {
                        values.put(name, field.get(obj).toString());
                    }
                }
            }
            db.update(tableName, values, where, args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            where = null;
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
                    values.put(name, field.get(obj).toString());
                } else if (field.isAnnotationPresent(PrimaryKey.class)) {
                    PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                    boolean isAutoincrement = primaryKey.autoincrement();
                    String name = primaryKey.column();
                    if (!isAutoincrement) {
                        values.put(name, field.get(obj).toString());
                    }
                }
            }
            db.insert(tableName, "", values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return false;
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
            return "int(16)";
        } else if (type.equals(float.class)) {
            return "feal";
        } else if (type.equals(double.class)) {
            return "feal";
        }
        return "varchar";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

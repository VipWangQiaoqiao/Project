package net.oschina.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建便签的数据库
 * 
 * @author kymjs
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static String TABLE_NAME = " Notebook ";
    public static final String CREATE_TABLE = "create table " + TABLE_NAME
            + " (_id integer primary key autoincrement,"
            + " date varchar(10), time varchar(10), title text, content text)";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

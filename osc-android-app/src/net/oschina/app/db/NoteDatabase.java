package net.oschina.app.db;

import java.util.ArrayList;

import net.oschina.app.bean.NotebookData;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NoteDatabase {
    private final DatabaseHelper dbHelper;

    public NoteDatabase(Context context) {
        super();
        dbHelper = new DatabaseHelper(context);
    }

    public void add(String date, String time, String title, String content) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("insert into" + DatabaseHelper.TABLE_NAME + "(date, time, title, content) values(?, ?, ?, ?)");
        sqlite.execSQL(sql, new String[] { date, time, title, content });
        dbHelper.close();
    }

    public ArrayList<NotebookData> query() {
        ArrayList<NotebookData> data = null;
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();

        if (sqlite.isOpen()) {
            data = new ArrayList<NotebookData>();
            Cursor cursor = sqlite.rawQuery("select * from "
                    + DatabaseHelper.TABLE_NAME, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NotebookData notebookData = new NotebookData();
                notebookData.setDate(cursor.getString(1)); // 第0列为主键
                notebookData.setTime(cursor.getString(2));
                notebookData.setTitle(cursor.getString(3));
                notebookData.setContent(cursor.getString(4));
                data.add(notebookData);
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            dbHelper.close();
        }

        return data;
    }

    public void delete(String titleFlag) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from" + DatabaseHelper.TABLE_NAME + "where title=?");
        sqlite.execSQL(sql, new String[] { titleFlag });
        dbHelper.close();
    }
}

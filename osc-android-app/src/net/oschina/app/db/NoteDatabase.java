package net.oschina.app.db;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 合并一条数据到本地(通过更新时间判断仅保留最新)
     * 
     * @param data
     */
    public void merge(NotebookData data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();

        Cursor cursor = sqlite.rawQuery("select * from "
                + DatabaseHelper.NOTE_TABLE_NAME + " where id=" + data.getId(),
                null);
        NotebookData localData = new NotebookData();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            localData.setId(cursor.getInt(0));
            localData.setUnixTime(cursor.getLong(1));
            localData.setDate(cursor.getString(2));
            localData.setContent(cursor.getString(3));
            localData.setStar(0 != cursor.getInt(4)); // C判断法：非0即真
            localData.setColor(cursor.getInt(5));
        }
        data = localData.getUnixTime() > data.getUnixTime() ? localData : data;
        save(data);
        sqlite.close();
    }

    /**
     * 保存一条数据到本地(若已存在则直接覆盖)
     * 
     * @param data
     */
    public void save(NotebookData data) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        if (data.getId() != 0) {
            String sql = ("update " + DatabaseHelper.NOTE_TABLE_NAME + " set time=?, date=?, content=?, star=?,color=? where _id=?");
            sqlite.execSQL(
                    sql,
                    new String[] { data.getUnixTime() + "", data.getDate(),
                            data.getContent(), data.isStar() ? "1" : "0",
                            data.getColor() + "", data.getId() + "" });
        } else {
            String sql = ("insert into " + DatabaseHelper.NOTE_TABLE_NAME + "(time, date, content, star, color) values(?, ?, ?, ?, ?)");
            sqlite.execSQL(
                    sql,
                    new String[] { data.getUnixTime() + "", data.getDate(),
                            data.getContent(), data.isStar() ? "1" : "0",
                            data.getColor() + "" });
        }
        dbHelper.close();
        sqlite.close();
    }

    public void reset(List<NotebookData> datas) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        sqlite.execSQL("delete from " + DatabaseHelper.NOTE_TABLE_NAME);
        for (NotebookData data : datas) {
            String sql = ("insert into " + DatabaseHelper.NOTE_TABLE_NAME + "(time, date, content, star, color) values(?, ?, ?, ?, ?)");
            sqlite.execSQL(
                    sql,
                    new String[] { data.getUnixTime() + "", data.getDate(),
                            data.getContent(), data.isStar() ? "1" : "0",
                            data.getColor() + "" });
        }
    }

    public ArrayList<NotebookData> query() {
        ArrayList<NotebookData> data = null;
        SQLiteDatabase sqlite = dbHelper.getReadableDatabase();

        if (sqlite.isOpen()) {
            data = new ArrayList<NotebookData>();
            Cursor cursor = sqlite.rawQuery("select * from "
                    + DatabaseHelper.NOTE_TABLE_NAME, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                NotebookData notebookData = new NotebookData();
                notebookData.setId(cursor.getInt(0));
                notebookData.setUnixTime(cursor.getLong(1));
                notebookData.setDate(cursor.getString(2));
                notebookData.setContent(cursor.getString(3));
                notebookData.setStar(0 != cursor.getInt(4)); // C判断法：非0即真

                notebookData.setColor(cursor.getInt(5));
                data.add(notebookData);
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            dbHelper.close();
            sqlite.close();
        }

        return data;
    }

    public void delete(int id) {
        SQLiteDatabase sqlite = dbHelper.getWritableDatabase();
        String sql = ("delete from " + DatabaseHelper.NOTE_TABLE_NAME + " where _id=?");
        sqlite.execSQL(sql, new Integer[] { id });
        dbHelper.close();
        sqlite.close();
    }
}
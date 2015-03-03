package net.oschina.app.db;

import java.util.ArrayList;
import java.util.List;

import net.oschina.app.bean.NotebookData;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NoteDatabase {
    private final DatabaseHelper dbHelper;
    private final SQLiteDatabase sqlite;

    public NoteDatabase(Context context) {
        super();
        dbHelper = new DatabaseHelper(context);
        sqlite = dbHelper.getWritableDatabase();
    }

    /**
     * 合并一条数据到本地(通过更新时间判断仅保留最新)
     * 
     * @param data
     * @return 数据是否被合并了
     */
    public boolean merge(NotebookData data) {
        Cursor cursor = sqlite.rawQuery(
                "select * from " + DatabaseHelper.NOTE_TABLE_NAME
                        + " where _id=" + data.getId(), null);
        NotebookData localData = new NotebookData();
        // 本循环其实只执行一次
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            localData.setId(cursor.getInt(0));
            localData.setUnixTime(cursor.getLong(1));
            localData.setDate(cursor.getString(2));
            localData.setContent(cursor.getString(3));
            localData.setStar(0 != cursor.getInt(4)); // C判断法：非0即真
            localData.setColor(cursor.getInt(5));
        }
        // 是否需要合这条数据
        boolean isMerge = localData.getUnixTime() < data.getUnixTime();
        if (isMerge) {
            save(data);
        }
        return isMerge;
    }

    /**
     * 保存一条数据到本地(若已存在则直接覆盖)
     * 
     * @param data
     */
    public void save(NotebookData data) {
        if (data.getId() != 0) {
            ArrayList<NotebookData> datas = query(" where _id=" + data.getId());
            if (datas != null && !datas.isEmpty()) {
                update(data);
            } else {
                insert(data);
            }
        } else {
            insert(data);
        }
    }

    public void insert(NotebookData data) {
        String sql = "insert into " + DatabaseHelper.NOTE_TABLE_NAME;

        if (data.getId() == 0) {
            sql += "(time, date, content, star, color) values(?, ?, ?, ?, ?)";
            sqlite.execSQL(
                    sql,
                    new String[] { data.getUnixTime() + "", data.getDate(),
                            data.getContent(), data.isStar() ? "1" : "0",
                            data.getColor() + "" });
        } else {
            sql += "(_id, time, date, content, star, color) values(?, ?, ?, ?, ?, ?)";
            sqlite.execSQL(sql,
                    new String[] { data.getId() + "", data.getUnixTime() + "",
                            data.getDate(), data.getContent(),
                            data.isStar() ? "1" : "0", data.getColor() + "" });
        }
    }

    public void update(NotebookData data) {
        String sql = ("update " + DatabaseHelper.NOTE_TABLE_NAME + " set time=?, date=?, content=?, star=?,color=? where _id=?");
        sqlite.execSQL(
                sql,
                new String[] { data.getUnixTime() + "", data.getDate(),
                        data.getContent(), data.isStar() ? "1" : "0",
                        data.getColor() + "", data.getId() + "" });
    }

    public void reset(List<NotebookData> datas) {
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

    public ArrayList<NotebookData> query(String where) {
        ArrayList<NotebookData> data = null;

        if (sqlite.isOpen()) {
            data = new ArrayList<NotebookData>();
            Cursor cursor = sqlite.rawQuery("select * from "
                    + DatabaseHelper.NOTE_TABLE_NAME + where, null);
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
        }

        return data;
    }

    public ArrayList<NotebookData> query() {
        return query(" ");
    }

    public void delete(int id) {
        String sql = ("delete from " + DatabaseHelper.NOTE_TABLE_NAME + " where _id=?");
        sqlite.execSQL(sql, new Integer[] { id });
    }

    public void destroy() {
        dbHelper.close();
        sqlite.close();
    }
}
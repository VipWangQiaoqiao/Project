package net.oschina.app.improve.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.oschina.app.util.TLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haibin
 * on 2016/11/7.
 */

public final class CacheManager {
    public static <T> void saveToJson(Context context, String fileName, List<T> list) {
        String json = new Gson().toJson(list);
        String path = context.getCacheDir() + "/" + fileName;
        File file = new File(path);
        FileOutputStream os = null;
        try {
            if (!file.exists())
                file.createNewFile();
            os = new FileOutputStream(file);
            os.write(json.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtils.close(os);
        }
    }

    public static void saveToJson(Context context, String fileName, Object object) {
        String json = new Gson().toJson(object);
        String path = context.getCacheDir() + "/" + fileName;
        File file = new File(path);
        FileOutputStream os = null;
        try {
            if (!file.exists())
                file.createNewFile();
            os = new FileOutputStream(file);
            os.write(json.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtils.close(os);
        }
    }

    public static <T> ArrayList<T> readFromJson(Context context, String fileName, Class<T> cls) {
        String json = readJson(context, fileName);
        if (json == null)
            return null;
        try {
            ArrayList<T> mList = new ArrayList<>();
            Gson gson = new Gson();
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for (final JsonElement elem : array) {
                mList.add(gson.fromJson(elem, cls));
            }
            return mList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(Context context, String fileName, Class<T> cla) {
        String json = readJson(context, fileName);
        if (json == null)
            return null;
        try {
            return new Gson().fromJson(json, cla);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readJson(Context context, String fileName) {
        String path = context.getCacheDir() + "/" + fileName;
        File file = new File(path);
        if (!file.exists())
            return null;
        FileInputStream is = null;
        try {
            StringBuilder sb = new StringBuilder();
            is = new FileInputStream(file);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            sb.append(new String(bytes));
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtils.close(is);
        }
        return null;
    }
}

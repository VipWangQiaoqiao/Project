package net.oschina.app.improve.utils;

import android.content.Context;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.util.TLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 已读位置管理类
 * <p>
 * 博客和资讯
 * Created by thanatosx on 2016/12/27.
 */

public class ReadedIndexCacheManager {

    public static final String FILE_NAME = ReadedIndexCacheManager.class.getSimpleName();
    public static List<Pair<String, Integer>> pairs;

    public static List<Pair<String, Integer>> getPairs(Context context) {
        if (pairs == null) {
            pairs = CacheManager.readJson(context, FILE_NAME
                    , new TypeToken<List<Pair<String, Integer>>>(){}.getType());
            if (pairs == null) pairs = new ArrayList<>();
        }
        return pairs;
    }

    public static int getIndex(Context context, long id, int type) {
        String in = getIndexName(id, type);
        List<Pair<String, Integer>> pairs = getPairs(context);
        for (Pair<String, Integer> pair : pairs) {
            if (pair.first.equals(in)) return pair.second;
        }
        return 0;
    }

    public static void saveIndex(Context context, long id, int type, int index) {
        if (index == 0) return;
        String in = getIndexName(id, type);
        List<Pair<String, Integer>> pairs = getPairs(context);
        pairs.add(0, Pair.create(in, index));
        while (pairs.size() > 50) {
            pairs.remove(pairs.size() - 1);
        }
        final List<Pair<String, Integer>> data = new ArrayList<>(pairs);
        final Context app = context.getApplicationContext();
        AppOperator.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                CacheManager.saveToJson(app, FILE_NAME, data);
            }
        });

    }

    public static String getIndexName(long id, int type) {
        return String.format("%s-%s", id, type);
    }


}

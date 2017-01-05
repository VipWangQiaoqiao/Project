package net.oschina.app.improve.utils;

import android.content.Context;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.improve.app.AppOperator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 已读位置管理类
 * <p>
 * 博客和资讯
 * Created by thanatosx on 2016/12/27.
 */

public class ReadedIndexCacheManager {

    public static final String FILE_NAME = ReadedIndexCacheManager.class.getSimpleName();
    public static final int MIN_LIMIT_READED_POSITION = 30;
    public static final int MAX_LIMIT_READED_COUNT = 50;
    public static final int LIMIT_READED_CLEAR_COUNT = 30;
    public static List<Pair<String, Integer>> pairs;

    /**
     * 得到缓存的已读位置Key-Value
     *
     * @param context Context
     * @return {@link List<Pair<String, Integer>>}
     */
    public static List<Pair<String, Integer>> getPairs(Context context) {
        if (pairs == null) {
            pairs = CacheManager.readJson(context, FILE_NAME
                    , new TypeToken<List<Pair<String, Integer>>>() {
                    }.getType());
            if (pairs == null) pairs = new ArrayList<>();
        }
        return pairs;
    }

    /**
     * 得到某篇文章的已读位置
     *
     * @param context {@link Context}
     * @param id      The Article Id
     * @param type    The Article Type {@link net.oschina.app.api.remote.OSChinaApi#CATALOG_NEWS}
     *                {@link net.oschina.app.api.remote.OSChinaApi#CATALOG_BLOG}
     * @return 已读位置
     */
    public static int getIndex(Context context, long id, int type) {
        String in = getIndexName(id, type);
        List<Pair<String, Integer>> pairs = getPairs(context);
        for (Pair<String, Integer> pair : pairs) {
            if (pair.first.equals(in)) return pair.second;
        }
        return 0;
    }

    /**
     * 保存文章的已读位置
     *
     * @param context Context
     * @param id      The Article Id
     * @param type    The Article Type The Article Type {@link net.oschina.app.api.remote.OSChinaApi#CATALOG_NEWS}
     *                {@link net.oschina.app.api.remote.OSChinaApi#CATALOG_BLOG}
     * @param index   已读位置， 已读位置小于等于{@link #MIN_LIMIT_READED_POSITION} 将会移除储存的已读位置
     */
    public static void saveIndex(Context context, long id, int type, int index) {
        String in = getIndexName(id, type);
        if (index <= MIN_LIMIT_READED_POSITION) {
            removeIndex(context, in);
            return;
        }
        List<Pair<String, Integer>> pairs = getPairs(context);

        // 去重
        Iterator<Pair<String, Integer>> iterator = pairs.iterator();
        while (iterator.hasNext()) {
            Pair<String, Integer> pair = iterator.next();
            if (pair.first.equals(in)) {
                iterator.remove();
                break;
            }
        }

        pairs.add(0, Pair.create(in, index));

        if (pairs.size() > MAX_LIMIT_READED_COUNT) {
            while (pairs.size() > LIMIT_READED_CLEAR_COUNT) {
                pairs.remove(pairs.size() - 1);
            }
        }
        save(context.getApplicationContext(), new ArrayList<>(pairs));
    }

    /**
     * 移除指定文章的已读位置
     *
     * @param context Context
     * @param in      Create By {@link #getIndexName(long, int)}
     */
    public static void removeIndex(Context context, String in) {
        List<Pair<String, Integer>> pairs = getPairs(context);
        Iterator<Pair<String, Integer>> iterator = pairs.iterator();
        while (iterator.hasNext()) {
            Pair<String, Integer> pair = iterator.next();
            if (pair.first.equals(in)) {
                iterator.remove();
                save(context.getApplicationContext(), new ArrayList<>(pairs));
                break;
            }
        }
    }

    /**
     * 保存已读位置
     *
     * @param context Context
     * @param pairs   {@link #pairs}
     */
    private static void save(final Context context, final List<Pair<String, Integer>> pairs) {
        AppOperator.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                CacheManager.saveToJson(context, FILE_NAME, pairs);
            }
        });
    }

    /**
     * Key Name Scheme
     *
     * @param id   The Article id
     * @param type The Article Type
     * @return Key Name Scheme
     */
    public static String getIndexName(long id, int type) {
        return String.format("%s-%s", id, type);
    }


}

package net.oschina.app.improve.detail.v2;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.util.TLog;
import net.oschina.common.utils.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * 详情页面缓存
 * Created by haibin
 * on 2016/12/29.
 */
@SuppressWarnings("All")
public final class DetailCache {
    private static final long ONE_DAY = 86400000;//1天毫秒
    private static String CUSTOM_CACHE;
    private static String COLLECTION_CACHE;

    public static void init(Context context) {
        CUSTOM_CACHE = context.getCacheDir() + "/" + "custom_cache/";
        COLLECTION_CACHE = context.getCacheDir() + "/" + "collection_cache/";
        update(false);
        update(true);
    }

    /**
     * 更新文件夹过期的文件
     *
     * @param isCollection 是否是收藏 文件夹名 custom_cache | collection_cache
     */
    private static void update(boolean isCollection) {
        File file = new File(isCollection ? COLLECTION_CACHE : CUSTOM_CACHE);
        if (!file.exists()) {
            file.mkdirs();
            return;
        }
        File[] files = file.listFiles();
        long currentDate = new Date().getTime();//当前时间
        long delayDate = (isCollection ? 10 : 2) * ONE_DAY;
        for (File f : files) {
            if (currentDate - f.lastModified() >= delayDate) {
                f.delete();
            }
        }
    }

    /**
     * 添加到缓存文件
     */
    static void addCache(SubBean bean) {
        if (bean == null)
            return;
        String name = bean.getId() + String.valueOf(bean.getType());
        String path = (bean.isFavorite() ? COLLECTION_CACHE : CUSTOM_CACHE)
                + name;
        File file = new File(path);
        FileOutputStream os = null;
        try {
            if (!file.exists())
                file.createNewFile();
            os = new FileOutputStream(file);
            os.write(new Gson().toJson(bean).getBytes());
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(os);
        }
    }

    /**
     * 读取缓存
     */
    static SubBean readCache(SubBean bean) {
        if (bean == null || bean.getId() <= 0)
            return null;
        String path = (bean.isFavorite() ? COLLECTION_CACHE : CUSTOM_CACHE)
                + bean.getId() + String.valueOf(bean.getType());
        File file = new File(path);
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            Type type = new TypeToken<SubBean>() {
            }.getType();
            SubBean subBean = new Gson().fromJson(reader, type);
            reader.close();
            return subBean;
        } catch (Exception e) {
            TLog.error(e.getMessage());
            return null;
        } finally {
            StreamUtil.close(reader);
        }
    }
}

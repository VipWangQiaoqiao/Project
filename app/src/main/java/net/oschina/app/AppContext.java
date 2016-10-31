package net.oschina.app;

import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.base.BaseApplication;
import net.oschina.app.bean.User;
import net.oschina.app.cache.DataCleanManager;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.util.MethodsCompat;
import net.oschina.app.util.TLog;

import org.kymjs.kjframe.Core;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.Properties;

import static net.oschina.app.AppConfig.KEY_FRITST_START;
import static net.oschina.app.AppConfig.KEY_LOAD_IMAGE;
import static net.oschina.app.AppConfig.KEY_TWEET_DRAFT;

/**
 * 全局应用程序类
 * 用于保存和调用全局应用配置及访问网络数据
 */
public class AppContext extends BaseApplication {
    public static final int PAGE_SIZE = 20;// 默认分页大小
    private static AppContext instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        init();
    }

    private void init() {
        AppCrashHandler handler = AppCrashHandler.getInstance();
        if (!BuildConfig.DEBUG)
            handler.init(this);

        // 初始化网络请求
        ApiHttpClient.init(this);

        // Log控制器
        KJLoger.openDebutLog(BuildConfig.DEBUG);
        TLog.DEBUG = BuildConfig.DEBUG;

        // Bitmap缓存地址
        HttpConfig.CACHEPATH = "OSChina/ImageCache";
    }

    /**
     * 获得当前app运行的AppContext
     *
     * @return
     */
    public static AppContext getInstance() {
        return instance;
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 获得登录用户的信息
     *
     * @deprecated
     */
    public User getLoginUser() {
        return new User();
    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        DataCleanManager.cleanDatabases(this);
        // 清除数据缓存
        DataCleanManager.cleanInternalCache(this);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            DataCleanManager.cleanCustomCache(MethodsCompat
                    .getExternalCacheDir(this));
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
        Core.getKJBitmap().cleanCache();
    }

    public static void setLoadImage(boolean flag) {
        set(KEY_LOAD_IMAGE, flag);
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    public static void setTweetDraft(String draft) {
        set(KEY_TWEET_DRAFT + AccountHelper.getUserId(), draft);
    }

    public static String getNoteDraft() {
        return getPreferences().getString(
                AppConfig.KEY_NOTE_DRAFT + AccountHelper.getUserId(), "");
    }

    public static void setNoteDraft(String draft) {
        set(AppConfig.KEY_NOTE_DRAFT + AccountHelper.getUserId(), draft);
    }

    public static boolean isFirstStart() {
        return getPreferences().getBoolean(KEY_FRITST_START, true);
    }

    public static void setFirstStart(boolean frist) {
        set(KEY_FRITST_START, frist);
    }

    /**
     * 夜间模式
     *
     * @deprecated
     */
    public static boolean getNightModeSwitch() {
        return false;
    }

    /**
     * 设置夜间模式
     *
     * @deprecated
     */
    public static void setNightModeSwitch(boolean on) {
        // Con't do thing
    }
}
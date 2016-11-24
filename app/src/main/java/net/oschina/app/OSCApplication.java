package net.oschina.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.improve.account.AccountHelper;

/**
 * Created by qiujuer
 * on 2016/10/27.
 */
public class OSCApplication extends AppContext {
    private static final String CONFIG_READ_STATE_PRE = "CONFIG_READ_STATE_PRE_";

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        // 初始化账户基础信息
        AccountHelper.init(this);
        // 初始化网络请求
        ApiHttpClient.init(this);
        // 初始化异常捕获类
        AppCrashHandler handler = AppCrashHandler.getInstance();
        if (!BuildConfig.DEBUG)
            handler.init(this);
    }

    /**
     * 获取已读状态管理器
     *
     * @param mark 传入标示，如：博客：blog; 新闻：news
     * @return 已读状态管理器
     */
    public static ReadState getReadState(String mark) {
        SharedPreferences preferences = getInstance()
                .getSharedPreferences(CONFIG_READ_STATE_PRE + mark, Context.MODE_PRIVATE);
        return new ReadState(preferences);
    }

    /**
     * 一个已读状态管理器
     */
    public static class ReadState {
        private SharedPreferences sp;

        ReadState(SharedPreferences sp) {
            this.sp = sp;
        }

        /**
         * 添加已读状态
         *
         * @param key 一般为资讯等Id
         */
        public void put(long key) {
            put(String.valueOf(key));
        }

        /**
         * 添加已读状态
         *
         * @param key 一般为资讯等Id
         */
        public void put(String key) {
            SharedPreferences.Editor editor = sp.edit().putBoolean(key, true);
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }

        /**
         * 获取是否为已读
         *
         * @param key 一般为资讯等Id
         * @return True 已读
         */
        public boolean already(long key) {
            return already(String.valueOf(key));
        }

        /**
         * 获取是否为已读
         *
         * @param key 一般为资讯等Id
         * @return True 已读
         */
        public boolean already(String key) {
            return sp.contains(key);
        }
    }
}

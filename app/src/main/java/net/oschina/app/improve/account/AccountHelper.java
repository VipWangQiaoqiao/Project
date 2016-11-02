package net.oschina.app.improve.account;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import net.oschina.app.AppContext;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.bean.Constants;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.improve.tweet.fragments.TweetFragment;
import net.oschina.common.helper.SharedPreferencesHelper;

import cz.msebera.android.httpclient.Header;

/**
 * 账户辅助类，
 * 用于更新用户信息和保存当前账户等操作
 */
public final class AccountHelper {
    private User user;
    private Application application;
    @SuppressLint("StaticFieldLeak")
    private static AccountHelper instances;

    private AccountHelper(Application application) {
        this.application = application;
    }

    public static void init(Application application) {
        instances = new AccountHelper(application);
    }

    public static boolean isLogin() {
        return getUserId() > 0;
    }

    public static long getUserId() {
        return getUser().getId();
    }

    public synchronized static User getUser() {
        if (instances.user == null)
            instances.user = SharedPreferencesHelper.load(instances.application, User.class);
        if (instances.user == null)
            instances.user = new User();
        return instances.user;
    }

    public static void updateUserCache(User user) {
        if (user == null)
            return;
        instances.user = user;
        SharedPreferencesHelper.save(instances.application, user);
    }

    public static void clearUserCache() {
        instances.user = null;
        SharedPreferencesHelper.remove(instances.application, User.class);
    }

    public static void login(User user, Header[] headers) {
        // 保存缓存
        updateUserCache(user);
        // 登陆成功,重新启动消息服务
        NoticeManager.init(instances.application);
        // 更新Cookie
        if (headers != null)
            ApiHttpClient.updateCookie(ApiHttpClient.getHttpClient(), headers);
    }

    public static void logout() {
        Context context = instances.application;

        // 用户退出时清理红点并退出服务
        NoticeManager.clear(context, NoticeManager.FLAG_CLEAR_ALL);
        NoticeManager.exitServer(context);

        // 清理网络相关
        ApiHttpClient.destroy((AppContext) context);

        // 清理动弹对应数据
        CacheManager.deleteObject(context, TweetFragment.CACHE_USER_TWEET);

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses("net.oschina.app.tweet.TweetPublishService");
        activityManager.killBackgroundProcesses("net.oschina.app.notice.NoticeServer");
        // 清除用户缓存
        clearUserCache();

        // Logou 广播
        Intent intent = new Intent(Constants.INTENT_ACTION_LOGOUT);
        context.sendBroadcast(intent);
    }
}

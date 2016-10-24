package net.oschina.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.gson.Gson;

import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.base.BaseApplication;
import net.oschina.app.bean.Constants;
import net.oschina.app.bean.User;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.cache.DataCleanManager;
import net.oschina.app.improve.bean.UserV2;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.improve.tweet.fragments.TweetFragment;
import net.oschina.app.improve.user.constants.UserConstants;
import net.oschina.app.improve.user.fragments.NewUserInfoFragment;
import net.oschina.app.util.CyptoUtils;
import net.oschina.app.util.MethodsCompat;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TLog;

import org.kymjs.kjframe.Core;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.utils.KJLoger;

import java.util.Properties;
import java.util.UUID;

import static net.oschina.app.AppConfig.KEY_FRITST_START;
import static net.oschina.app.AppConfig.KEY_LOAD_IMAGE;
import static net.oschina.app.AppConfig.KEY_NIGHT_MODE_SWITCH;
import static net.oschina.app.AppConfig.KEY_TWEET_DRAFT;

/**
 * 全局应用程序类
 * 用于保存和调用全局应用配置及访问网络数据
 */
public class AppContext extends BaseApplication {
    public static final int PAGE_SIZE = 20;// 默认分页大小
    private static AppContext instance;
    private long loginUid;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        init();
        initLogin();
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

    private void initLogin() {
        UserV2 user = getLoginUserV2();
        if (null != user && user.getId() > 0) {
            loginUid = user.getId();
        } else {
            //this.cleanLoginInfo();
            this.cleanLoginInfoV2();
        }
    }

    /**
     * 获得当前app运行的AppContext
     *
     * @return appContext
     */
    public static AppContext getInstance() {
        return instance;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
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
     * @param key key
     * @return key
     */
    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 获取App唯一标识
     *
     * @return appId
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (TextUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 获取App安装包信息
     *
     * @return packageInfo
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 保存登录信息
     *
     * @param user 用户信息
     */
    @SuppressWarnings("serial")
    public void saveUserInfo(final User user) {
        this.loginUid = user.getId();
        setProperties(new Properties() {
            {
                setProperty("user.uid", String.valueOf(user.getId()));
                setProperty("user.name", user.getName());
                setProperty("user.face", user.getPortrait());// 用户头像-文件名
                setProperty("user.account", user.getAccount());
                setProperty("user.pwd",
                        CyptoUtils.encode("oschinaApp", user.getPwd()));
                setProperty("user.location", user.getLocation());
                setProperty("user.followers", String.valueOf(user.getFollowers()));
                setProperty("user.fans", String.valueOf(user.getFans()));
                setProperty("user.score", String.valueOf(user.getScore()));
                setProperty("user.favoritecount",
                        String.valueOf(user.getFavoritecount()));
                setProperty("user.gender", String.valueOf(user.getGender()));
                setProperty("user.isRememberMe",
                        String.valueOf(user.isRememberMe()));// 是否记住我的信息
            }
        });

        // 登陆成功,重新启动消息服务
        NoticeManager.init(this);
    }

    /**
     * 保存登录信息 v2
     *
     * @param user 用户信息
     */
    @SuppressWarnings("serial")
    public void saveUserInfo(final UserV2 user) {
        this.loginUid = user.getId();
        setProperties(new Properties() {{
            setProperty(UserConstants.UID, String.valueOf(user.getId()));
            setProperty(UserConstants.NAME, user.getName());
            setProperty(UserConstants.PORTRAIT, user.getPortrait());// 用户头像-文件名
            setProperty(UserConstants.GENDER, String.valueOf(user.getGender()));
            setProperty(UserConstants.RELATION, String.valueOf(user.getRelation()));

            //save more
            setProperty(UserConstants.JOIN_DATE, user.getMore().getJoinDate());
            setProperty(UserConstants.CITY, user.getMore().getCity());
            setProperty(UserConstants.EXPERTISE, user.getMore().getExpertise());
            setProperty(UserConstants.PLATFORM, user.getMore().getPlatform());

            //save statistics
            setProperty(UserConstants.SCORE, String.valueOf(user.getStatistics().getScore()));
            setProperty(UserConstants.TWEET, String.valueOf(user.getStatistics().getTweet()));
            setProperty(UserConstants.COLLECT, String.valueOf(user.getStatistics().getCollect()));
            setProperty(UserConstants.FANS, String.valueOf(user.getStatistics().getFans()));
            setProperty(UserConstants.FOLLOW, String.valueOf(user.getStatistics().getFollow()));
            setProperty(UserConstants.BLOG, String.valueOf(user.getStatistics().getBlog()));
            setProperty(UserConstants.ANSWER, String.valueOf(user.getStatistics().getAnswer()));
            setProperty(UserConstants.DISCUSS, String.valueOf(user.getStatistics().getDiscuss()));
        }});

        // 登陆成功,重新启动消息服务
        NoticeManager.init(this);
    }

    /**
     * 更新用户信息
     *
     * @param user
     */
    @SuppressWarnings("serial")
    public void updateUserInfo(final User user) {
        setProperties(new Properties() {
            {
                setProperty("user.name", user.getName());
                setProperty("user.face", user.getPortrait());// 用户头像-文件名
                setProperty("user.followers",
                        String.valueOf(user.getFollowers()));
                setProperty("user.fans", String.valueOf(user.getFans()));
                setProperty("user.score", String.valueOf(user.getScore()));
                setProperty("user.favoritecount",
                        String.valueOf(user.getFavoritecount()));
                setProperty("user.gender", String.valueOf(user.getGender()));
            }
        });
    }

    /**
     * 获得登录用户的信息
     *
     * @return user
     */
    public User getLoginUser() {
        User user = new User();
        user.setId(StringUtils.toInt(getProperty("user.uid"), 0));
        user.setName(getProperty("user.name"));
        user.setPortrait(getProperty("user.face"));
        user.setAccount(getProperty("user.account"));
        user.setLocation(getProperty("user.location"));
        user.setFollowers(StringUtils.toInt(getProperty("user.followers"), 0));
        user.setFans(StringUtils.toInt(getProperty("user.fans"), 0));
        user.setScore(StringUtils.toInt(getProperty("user.score"), 0));
        user.setFavoritecount(StringUtils.toInt(
                getProperty("user.favoritecount"), 0));
        user.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
        user.setGender(getProperty("user.gender"));
        return user;
    }

    /**
     * 获得登录用户的信息
     *
     * @return userV2
     */
    public UserV2 getLoginUserV2() {

        UserV2 user = new UserV2();

        user.setId(Integer.parseInt(getProperty(UserConstants.UID), 0));
        user.setName(getProperty(UserConstants.NAME));
        user.setPortrait(getProperty(UserConstants.PORTRAIT));
        user.setGender(Integer.parseInt(getProperty(UserConstants.GENDER)));
        user.setDesc(getProperty(UserConstants.DESC));
        user.setRelation(Integer.parseInt(getProperty(UserConstants.RELATION)));

        UserV2.More more = new UserV2.More();

        more.setCity(getProperty(UserConstants.CITY));
        more.setExpertise(getProperty(UserConstants.EXPERTISE));
        more.setJoinDate(getProperty(UserConstants.JOIN_DATE));
        more.setPlatform(getProperty(UserConstants.PLATFORM));
        user.setMore(more);

        UserV2.Statistics statistics = new UserV2.Statistics();

        statistics.setScore(Integer.parseInt(getProperty(UserConstants.SCORE)));
        statistics.setTweet(Integer.parseInt(getProperty(UserConstants.TWEET)));
        statistics.setCollect(Integer.parseInt(getProperty(UserConstants.COLLECT)));
        statistics.setFans(Integer.parseInt(getProperty(UserConstants.FANS)));
        statistics.setFollow(Integer.parseInt(getProperty(UserConstants.FOLLOW)));
        statistics.setBlog(Integer.parseInt(getProperty(UserConstants.BLOG)));
        statistics.setAnswer(Integer.parseInt(getProperty(UserConstants.ANSWER)));
        statistics.setDiscuss(Integer.parseInt(getProperty(UserConstants.DISCUSS)));
        user.setStatistics(statistics);

        return user;
    }

    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {
        this.loginUid = 0;
        removeProperty("user.uid", "user.name", "user.face", "user.location",
                "user.followers", "user.fans", "user.score",
                "user.isRememberMe", "user.gender", "user.favoritecount");
    }

    /**
     * 清除登录信息  v2
     */
    public void cleanLoginInfoV2() {
        this.loginUid = 0;

        removeProperty(UserConstants.UID, UserConstants.NAME, UserConstants.PORTRAIT, UserConstants.GENDER,
                UserConstants.DESC, UserConstants.RELATION, UserConstants.JOIN_DATE,
                UserConstants.CITY, UserConstants.EXPERTISE, UserConstants.PLATFORM, UserConstants.SCORE,
                UserConstants.TWEET, UserConstants.FOLLOW, UserConstants.FANS, UserConstants.FOLLOW,
                UserConstants.BLOG, UserConstants.ANSWER, UserConstants.DISCUSS);
    }

    /**
     * 获取登陆用户Id, 已过时
     *
     * @return 用户Id
     */
    @Deprecated
    public int getLoginUid() {
        return (int) loginUid;
    }

    /**
     * 获取登陆用户Id
     *
     * @return 用户Id
     */
    public long getLoginId() {
        return loginUid;
    }

    public boolean isLogin() {
        return loginUid != 0;
    }

    /**
     * 用户注销
     */
    public void Logout() {
        // 用户退出时清理红点并退出服务
        NoticeManager.clear(this, NoticeManager.FLAG_CLEAR_ALL);
        NoticeManager.exitServer(this);

        //cleanLoginInfo();
        cleanLoginInfoV2();
        ApiHttpClient.destroy(this);

        CacheManager.deleteObject(context(), TweetFragment.CACHE_USER_TWEET);
        CacheManager.deleteObject(context(), NewUserInfoFragment.CACHE_NAME);

        Intent intent = new Intent(Constants.INTENT_ACTION_LOGOUT);
        sendBroadcast(intent);

        this.loginUid = 0;

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses("net.oschina.app.tweet.TweetPublishService");
        activityManager.killBackgroundProcesses("net.oschina.app.notice.NoticeServer");
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

    public static String getTweetDraft() {
        return getPreferences().getString(
                KEY_TWEET_DRAFT + getInstance().getLoginUid(), "");
    }

    public static void setTweetDraft(String draft) {
        set(KEY_TWEET_DRAFT + getInstance().getLoginUid(), draft);
    }

    public static String getNoteDraft() {
        return getPreferences().getString(
                AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), "");
    }

    public static void setNoteDraft(String draft) {
        set(AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), draft);
    }

    public static boolean isFristStart() {
        return getPreferences().getBoolean(KEY_FRITST_START, true);
    }

    public static void setFristStart(boolean frist) {
        set(KEY_FRITST_START, frist);
    }

    //夜间模式
    public static boolean getNightModeSwitch() {
        //return getPreferences().getBoolean(KEY_NIGHT_MODE_SWITCH, false);
        return false;
    }

    // 设置夜间模式
    public static void setNightModeSwitch(boolean on) {
        set(KEY_NIGHT_MODE_SWITCH, on);
    }

    public static Gson createGson() {
        com.google.gson.GsonBuilder gsonBuilder = new com.google.gson.GsonBuilder();
        //gsonBuilder.setExclusionStrategies(new SpecificClassExclusionStrategy(null, Model.class));
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        return gsonBuilder.create();
    }


    public static GlideUrl getGlideUrlByUser(String url) {
        if (AppContext.getInstance().isLogin()) {
            return new GlideUrl(url,
                    new LazyHeaders
                            .Builder()
                            .addHeader("Cookie", ApiHttpClient.getCookieString(AppContext.getInstance()))
                            .build());
        } else {
            return new GlideUrl(url);
        }
    }
}

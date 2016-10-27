package net.oschina.app.improve.account.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

import net.oschina.app.AppContext;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.improve.account.constants.UserConstants;
import net.oschina.app.improve.account.service.UserService;
import net.oschina.app.improve.account.utils.SharedPreferencesUtils;
import net.oschina.app.improve.bean.UserV2;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.improve.tweet.fragments.TweetFragment;


/**
 * Created by fei
 * on 2016/10/24.
 * desc:
 */

public class UserCacheManager implements UserService {

    private UserCacheManager() {
    }

    @Override
    public boolean saveUserCache(Context context, UserV2 userV2) {

        SharedPreferences sp = SharedPreferencesUtils.createSp(UserConstants.USER_CACHE, context);
        SharedPreferences.Editor edit = SharedPreferencesUtils.getEditor(sp);

        edit.putLong(UserConstants.UID, userV2.getId());
        edit.putString(UserConstants.NAME, userV2.getName());
        edit.putString(UserConstants.PORTRAIT, userV2.getPortrait());
        edit.putInt(UserConstants.GENDER, userV2.getGender());
        edit.putString(UserConstants.DESC, userV2.getDesc());
        edit.putInt(UserConstants.RELATION, userV2.getRelation());

        edit.putString(UserConstants.JOIN_DATE, userV2.getMore().getJoinDate());
        edit.putString(UserConstants.CITY, userV2.getMore().getCity());
        edit.putString(UserConstants.EXPERTISE, userV2.getMore().getExpertise());
        edit.putString(UserConstants.PLATFORM, userV2.getMore().getPlatform());
        edit.putString(UserConstants.COMPANY, userV2.getMore().getCompany());
        edit.putString(UserConstants.POSITION, userV2.getMore().getPosition());

        edit.putInt(UserConstants.SCORE, userV2.getStatistics().getScore());
        edit.putInt(UserConstants.TWEET, userV2.getStatistics().getTweet());
        edit.putInt(UserConstants.COLLECT, userV2.getStatistics().getCollect());
        edit.putInt(UserConstants.FANS, userV2.getStatistics().getFans());
        edit.putInt(UserConstants.FOLLOW, userV2.getStatistics().getFollow());
        edit.putInt(UserConstants.BLOG, userV2.getStatistics().getBlog());
        edit.putInt(UserConstants.ANSWER, userV2.getStatistics().getAnswer());
        edit.putInt(UserConstants.DISCUSS, userV2.getStatistics().getDiscuss());

        SharedPreferencesUtils.commit(edit);
        return true;
    }

    @Override
    public boolean deleteUserCache(Context context) {

        SharedPreferences sp = SharedPreferencesUtils.createSp(UserConstants.USER_CACHE, context);
        SharedPreferences.Editor edit = SharedPreferencesUtils.getEditor(sp);
        edit.clear();
        SharedPreferencesUtils.commit(edit);
        return true;
    }

    @Override
    public boolean updatePairUserCache(Context context, String key, Object value) {

        SharedPreferences sp = SharedPreferencesUtils.createSp(UserConstants.USER_CACHE, context);
        SharedPreferences.Editor edit = SharedPreferencesUtils.getEditor(sp);

        if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
        } else if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        } else {
            edit.putFloat(key, (Float) value);
        }
        SharedPreferencesUtils.commit(edit);
        return true;
    }

    @Override
    public boolean updateUserCache(Context context, UserV2 userV2) {
        saveUserCache(context, userV2);
        return true;
    }

    @Override
    public boolean isLogin(Context context) {
        long uid = loginId(context);
        return uid > 0;
    }

    @Override
    public long loginId(Context context) {
        SharedPreferences sp = SharedPreferencesUtils.createSp(UserConstants.USER_CACHE, context);
        return sp.getLong(UserConstants.UID, 0);
    }

    @Override
    public boolean logout(Context context) {

        // 用户退出时清理红点并退出服务
        NoticeManager.clear(context, NoticeManager.FLAG_CLEAR_ALL);
        NoticeManager.exitServer(context);

        ApiHttpClient.destroy((AppContext) context);

        CacheManager.deleteObject(context, TweetFragment.CACHE_USER_TWEET);
        // CacheManager.deleteObject(context, NewUserInfoFragment.CACHE_NAME);

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses("net.oschina.app.tweet.TweetPublishService");
        activityManager.killBackgroundProcesses("net.oschina.app.notice.NoticeServer");
        //清除用户缓存
        deleteUserCache(context);

        return true;
    }

    @Override
    public boolean login(Context context, UserV2 userV2) {

        //1.保存缓存
        saveUserCache(context, userV2);
        //2.登陆成功,重新启动消息服务
        NoticeManager.init(context);
        return true;
    }

    @Override
    public UserV2 getUserCache(Context context) {
        SharedPreferences sp = SharedPreferencesUtils.createSp(UserConstants.USER_CACHE, context);
        long uid = sp.getLong(UserConstants.UID, 0);
        if (uid > 0) {
            UserV2 userV2 = new UserV2();
            String name = sp.getString(UserConstants.NAME, null);
            String portrait = sp.getString(UserConstants.PORTRAIT, null);
            int gender = sp.getInt(UserConstants.GENDER, 0);
            String desc = sp.getString(UserConstants.DESC, null);
            int relation = sp.getInt(UserConstants.RELATION, 0);

            userV2.setId(uid);
            userV2.setName(name);
            userV2.setPortrait(portrait);
            userV2.setGender(gender);
            userV2.setDesc(desc);
            userV2.setRelation(relation);

            UserV2.More more = new UserV2.More();

            String joinDate = sp.getString(UserConstants.JOIN_DATE, null);
            String city = sp.getString(UserConstants.CITY, null);
            String expertise = sp.getString(UserConstants.EXPERTISE, null);
            String platform = sp.getString(UserConstants.PLATFORM, null);
            String company = sp.getString(UserConstants.COMPANY, null);
            String position = sp.getString(UserConstants.POSITION, null);

            more.setJoinDate(joinDate);
            more.setCity(city);
            more.setExpertise(expertise);
            more.setPlatform(platform);
            more.setCompany(company);
            more.setPosition(position);

            userV2.setMore(more);


            UserV2.Statistics statistics = new UserV2.Statistics();

            int score = sp.getInt(UserConstants.SCORE, 0);
            int tweet = sp.getInt(UserConstants.TWEET, 0);
            int collect = sp.getInt(UserConstants.COLLECT, 0);
            int fans = sp.getInt(UserConstants.FANS, 0);
            int follow = sp.getInt(UserConstants.FOLLOW, 0);
            int blog = sp.getInt(UserConstants.BLOG, 0);
            int answer = sp.getInt(UserConstants.ANSWER, 0);
            int discuss = sp.getInt(UserConstants.DISCUSS, 0);

            statistics.setScore(score);
            statistics.setTweet(tweet);
            statistics.setCollect(collect);
            statistics.setFans(fans);
            statistics.setFollow(follow);
            statistics.setBlog(blog);
            statistics.setAnswer(answer);
            statistics.setDiscuss(discuss);

            userV2.setStatistics(statistics);

            return userV2;
        } else {
            return null;
        }
    }

    private static class UserHolder {
        private static UserCacheManager INSTANCE = new UserCacheManager();
    }

    public static UserCacheManager initUserManager() {
        return UserHolder.INSTANCE;
    }


}

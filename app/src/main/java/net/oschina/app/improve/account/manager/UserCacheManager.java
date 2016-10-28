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
import net.oschina.app.improve.bean.User;
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
    public boolean saveUserCache(Context context, User user) {

        SharedPreferences sp = SharedPreferencesUtils.createSp(UserConstants.USER_CACHE, context);
        SharedPreferences.Editor edit = SharedPreferencesUtils.getEditor(sp);

        edit.putLong(UserConstants.UID, user.getId());
        edit.putString(UserConstants.NAME, user.getName());
        edit.putString(UserConstants.PORTRAIT, user.getPortrait());
        edit.putInt(UserConstants.GENDER, user.getGender());
        edit.putString(UserConstants.DESC, user.getDesc());
        edit.putInt(UserConstants.RELATION, user.getRelation());

        edit.putString(UserConstants.JOIN_DATE, user.getMore().getJoinDate());
        edit.putString(UserConstants.CITY, user.getMore().getCity());
        edit.putString(UserConstants.EXPERTISE, user.getMore().getExpertise());
        edit.putString(UserConstants.PLATFORM, user.getMore().getPlatform());
        edit.putString(UserConstants.COMPANY, user.getMore().getCompany());
        edit.putString(UserConstants.POSITION, user.getMore().getPosition());

        edit.putInt(UserConstants.SCORE, user.getStatistics().getScore());
        edit.putInt(UserConstants.TWEET, user.getStatistics().getTweet());
        edit.putInt(UserConstants.COLLECT, user.getStatistics().getCollect());
        edit.putInt(UserConstants.FANS, user.getStatistics().getFans());
        edit.putInt(UserConstants.FOLLOW, user.getStatistics().getFollow());
        edit.putInt(UserConstants.BLOG, user.getStatistics().getBlog());
        edit.putInt(UserConstants.ANSWER, user.getStatistics().getAnswer());
        edit.putInt(UserConstants.DISCUSS, user.getStatistics().getDiscuss());

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
    public boolean updateUserCache(Context context, User user) {
        saveUserCache(context, user);
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
    public boolean login(Context context, User user) {

        //1.保存缓存
        saveUserCache(context, user);
        //2.登陆成功,重新启动消息服务
        NoticeManager.init(context);
        return true;
    }

    @Override
    public User getUserCache(Context context) {
        SharedPreferences sp = SharedPreferencesUtils.createSp(UserConstants.USER_CACHE, context);
        long uid = sp.getLong(UserConstants.UID, 0);
        if (uid > 0) {
            User user = new User();
            String name = sp.getString(UserConstants.NAME, null);
            String portrait = sp.getString(UserConstants.PORTRAIT, null);
            int gender = sp.getInt(UserConstants.GENDER, 0);
            String desc = sp.getString(UserConstants.DESC, null);
            int relation = sp.getInt(UserConstants.RELATION, 0);

            user.setId(uid);
            user.setName(name);
            user.setPortrait(portrait);
            user.setGender(gender);
            user.setDesc(desc);
            user.setRelation(relation);

            User.More more = new User.More();

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

            user.setMore(more);


            User.Statistics statistics = new User.Statistics();

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

            user.setStatistics(statistics);

            return user;
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

package net.oschina.app.improve.account.activity.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

import net.oschina.app.improve.account.activity.constants.UserConstants;
import net.oschina.app.improve.account.activity.service.UserService;
import net.oschina.app.improve.bean.UserV2;

/**
 * Created by fei
 * on 2016/10/24.
 * desc:
 */

public class UserManager implements UserService {

    private UserManager() {
    }

    /**
     * init editor
     *
     * @param sp sp
     * @return editor
     */
    private SharedPreferences.Editor getEditor(SharedPreferences sp) {
        return sp.edit();
    }

    /**
     * init sp
     *
     * @param context context
     * @return sp
     */
    private SharedPreferences createSp(Context context) {
        return context.getSharedPreferences(UserConstants.USER_CONFIG, Context.MODE_PRIVATE);
    }

    /**
     * 官方写法 更加安全,可以参考
     *
     * @param edit editor
     */
    private void commit(SharedPreferences.Editor edit) {
        //官方安全commit写法,值得学习里面的源代码
        SharedPreferencesCompat.EditorCompat.getInstance().apply(edit);
    }

    @Override
    public boolean saveUserCache(Context context, UserV2 userV2) {

        SharedPreferences sp = createSp(context);
        SharedPreferences.Editor edit = getEditor(sp);

        edit.putLong(UserConstants.UID, userV2.getId());
        edit.putString(UserConstants.NAME, userV2.getName());
        edit.putString(UserConstants.PLATFORM, userV2.getPortrait());
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

        commit(edit);
        return false;
    }

    @Override
    public boolean deleteUserCache(Context context) {

        SharedPreferences sp = createSp(context);
        SharedPreferences.Editor edit = getEditor(sp);
        edit.clear();
        commit(edit);
        return false;
    }

    @Override
    public boolean updatePairUserCache(Context context, String key, Object value) {
        SharedPreferences sp = createSp(context);
        SharedPreferences.Editor edit = getEditor(sp);

        if (value instanceof Integer) {

        } else if (value instanceof Long) {

        } else if (value instanceof String) {

        } else if (value instanceof Boolean) {

        }


        return false;
    }

    @Override
    public boolean updateUserCache(Context context, UserV2 userV2) {
        saveUserCache(context, userV2);
        return false;
    }

    @Override
    public boolean isLogin(Context context) {
        SharedPreferences sp = createSp(context);
        long uid = sp.getLong(UserConstants.UID, 0);
        return uid > 0;
    }

    private static class UserHolder {
        private static UserManager INSTANCE = new UserManager();
    }

    public static UserManager initUserManager() {
        return UserHolder.INSTANCE;
    }


}

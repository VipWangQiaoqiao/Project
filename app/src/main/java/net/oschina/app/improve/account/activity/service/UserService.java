package net.oschina.app.improve.account.activity.service;

import android.content.Context;

import net.oschina.app.improve.bean.UserV2;

/**
 * Created by fei
 * on 2016/10/24.
 * desc:
 */

public interface UserService {

    boolean saveUserCache(Context context, UserV2 userV2);

    boolean deleteUserCache(Context context);

    boolean updatePairUserCache(Context context, String key, Object value);

    boolean updateUserCache(Context context, UserV2 userV2);

    boolean isLogin(Context context);

    long loginId(Context context);

    boolean logout(Context context);

    boolean login(Context context, UserV2 userV2);

    UserV2 getUserCache(Context context);

}

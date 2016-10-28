package net.oschina.app.improve.account.service;

import android.content.Context;

import net.oschina.app.improve.bean.User;

/**
 * Created by fei
 * on 2016/10/24.
 * desc:
 */

public interface UserService {

    boolean saveUserCache(Context context, User user);

    boolean deleteUserCache(Context context);

    boolean updatePairUserCache(Context context, String key, Object value);

    boolean updateUserCache(Context context, User user);

    boolean isLogin(Context context);

    long loginId(Context context);

    boolean logout(Context context);

    boolean login(Context context, User user);

    User getUserCache(Context context);

}

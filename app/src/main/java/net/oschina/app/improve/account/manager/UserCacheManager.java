package net.oschina.app.improve.account.manager;

import android.content.Context;

import net.oschina.app.improve.account.AccountHelper;


/**
 * Created by fei
 * on 2016/10/24.
 * desc:
 */

public class UserCacheManager {

    private UserCacheManager() {
    }

    public boolean isLogin(Context context) {
        return AccountHelper.isLogin();
    }

    public long loginId(Context context) {
        return AccountHelper.getUserId();
    }

    private static UserCacheManager INSTANCE = new UserCacheManager();

    public static UserCacheManager initUserManager() {
        return INSTANCE;
    }
}

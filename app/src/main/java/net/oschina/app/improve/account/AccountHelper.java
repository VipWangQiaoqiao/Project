package net.oschina.app.improve.account;

import net.oschina.app.AppContext;
import net.oschina.app.improve.account.manager.UserCacheManager;

/**
 * 账户辅助类，
 * 用于更新用户信息和保存当前账户等操作
 */
public final class AccountHelper {
    public static boolean isLogin() {
        return UserCacheManager.initUserManager().isLogin(AppContext.getInstance());
    }
}

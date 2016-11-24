package net.oschina.app;

import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.improve.account.AccountHelper;

/**
 * Created by qiujuer
 * on 2016/10/27.
 */
public class OSCApplication extends AppContext {
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
}

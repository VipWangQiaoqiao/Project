package net.oschina.app;

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
        AccountHelper.init(this);
    }
}

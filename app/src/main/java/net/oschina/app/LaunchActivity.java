package net.oschina.app;

import android.app.Activity;
import android.content.Intent;

import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.main.MainActivity;

/**
 * 应用启动界面
 */
public class LaunchActivity extends BaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.app_start;
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        /*
        // 防止第三方跳转时出现双实例
        Activity aty = get ;
        if (aty != null && !aty.isFinishing()) {
            finish();
        }
        */
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        findViewById(R.id.app_start_view).postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectTo();
            }
        }, 800);
        //TODO Check use new version
        //int currentVersion = TDevice.getVersionCode();
    }

    private void redirectTo() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

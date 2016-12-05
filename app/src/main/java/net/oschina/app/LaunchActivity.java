package net.oschina.app;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.improve.main.tabs.DynamicTabFragment;
import net.oschina.common.utils.StreamUtil;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

        //TODO Check use new version
        //int currentVersion = TDevice.getVersionCode();

        DynamicTabFragment.initTabPickerManager();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectTo();
            }
        }, 800);

    }

    private void redirectTo() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

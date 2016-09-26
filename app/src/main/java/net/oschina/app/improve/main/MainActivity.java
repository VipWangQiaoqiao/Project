package net.oschina.app.improve.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.base.BaseApplication;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.main.nav.NavFragment;
import net.oschina.app.improve.main.nav.NavigationButton;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.util.UpdateManager;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements NavFragment.OnNavigationReselectListener {
    public static final String ACTION_NOTICE = "ACTION_NOTICE";
    private long mBackPressedTime;

    @Bind(R.id.activity_main_ui)
    FrameLayout mMainUi;

    private NavFragment mNavBar;

    @Override
    protected int getContentView() {
        return R.layout.activity_main_ui;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        FragmentManager manager = getSupportFragmentManager();
        mNavBar = ((NavFragment) manager.findFragmentById(R.id.fag_nav));
        mNavBar.setup(this, manager, R.id.main_container, this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doNewIntent(getIntent(), true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        doNewIntent(intent, false);
    }

    private void doNewIntent(Intent intent, boolean isCreate) {
        if (intent == null || intent.getAction() == null)
            return;
        String action = intent.getAction();
        Log.e("TAG", "onNewIntent action:" + action + " isCreate:" + isCreate);
        if (action.equals(ACTION_NOTICE)) {
            NavFragment bar = mNavBar;
            if (bar != null) {
                bar.select(3);
            }
        }
    }

    @Override
    public void onReselect(NavigationButton navigationButton) {
        Fragment fragment = navigationButton.getFragment();
        if (fragment != null
                && fragment instanceof OnTabReselectListener) {
            OnTabReselectListener listener = (OnTabReselectListener) fragment;
            listener.onTabReselect();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        NoticeManager.init(this);
        // in this we can check update
        checkUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NoticeManager.stopListen(this);
    }

    @Override
    public void onBackPressed() {
        boolean isDoubleClick = BaseApplication.get(AppConfig.KEY_DOUBLE_CLICK_EXIT, true);
        if (isDoubleClick) {
            long curTime = SystemClock.uptimeMillis();
            if ((curTime - mBackPressedTime) < (3 * 1000)) {
                finish();
            } else {
                mBackPressedTime = curTime;
                Toast.makeText(this, R.string.tip_double_click_exit, Toast.LENGTH_LONG).show();
            }
        } else {
            finish();
        }
    }

    private void checkUpdate() {
        if (!AppContext.get(AppConfig.KEY_CHECK_UPDATE, true)) {
            return;
        }
        new UpdateManager(MainActivity.this, false).checkUpdate();
    }
}

package net.oschina.app.improve.main;

import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.oschina.app.AppConfig;
import net.oschina.app.R;
import net.oschina.app.base.BaseApplication;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.main.nav.NavFragment;
import net.oschina.app.improve.main.nav.NavigationButton;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.interf.OnTabReselectListener;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements NavFragment.OnNavigationReselectListener {
    public static final String ACTION_NOTICE = "ACTION_NOTICE";
    private long mBackPressedTime;

    @Bind(R.id.activity_main_ui)
    FrameLayout activity_main_ui;

    @Override
    protected int getContentView() {
        return R.layout.activity_main_ui;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        activity_main_ui.setBackgroundColor(getResources().getColor(R.color.main_green));
        FragmentManager manager = getSupportFragmentManager();
        ((NavFragment) manager.findFragmentById(R.id.fag_nav))
                .setup(this, manager, R.id.main_container, this);
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
}

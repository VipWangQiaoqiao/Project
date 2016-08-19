package net.oschina.app.improve.main;

import android.support.v4.app.Fragment;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.main.nav.NavFragment;
import net.oschina.app.improve.main.nav.NavigationButton;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.interf.OnTabReselectListener;

public class MainActivity extends BaseActivity implements NavFragment.OnNavigationReselectListener {
    NavFragment mNavigation;

    @Override
    protected int getContentView() {
        return R.layout.activity_main_ui;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mNavigation = (NavFragment) getSupportFragmentManager().findFragmentById(R.id.fag_nav);
        mNavigation.setup(this, getSupportFragmentManager(), R.id.main_container, this);
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
}

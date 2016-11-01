package net.oschina.app.improve.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import net.oschina.app.improve.bean.Version;
import net.oschina.app.improve.main.nav.NavFragment;
import net.oschina.app.improve.main.nav.NavigationButton;
import net.oschina.app.improve.main.update.CheckUpdateManager;
import net.oschina.app.improve.main.update.DownloadService;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.util.DialogHelp;

import java.util.List;

import butterknife.Bind;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements
        NavFragment.OnNavigationReselectListener,
        EasyPermissions.PermissionCallbacks,
        CheckUpdateManager.RequestPermissions {

    private final int RC_EXTERNAL_STORAGE = 0x04;//存储权限
    public static final String ACTION_NOTICE = "ACTION_NOTICE";
    private long mBackPressedTime;

    private Version mVersion;

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
    public void call(Version version) {
        this.mVersion = version;
        requestExternalStorage();
    }

    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            DownloadService.startService(this, mVersion.getDownloadUrl());
        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        DialogHelp.getConfirmDialog(this, "没有权限, 你需要去设置中开启读取手机存储权限.", "去设置", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
            }
        }, null).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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
        CheckUpdateManager manager = new CheckUpdateManager(this, false);
        manager.setCaller(this);
        manager.checkUpdate();
    }
}

package net.oschina.app.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.bean.Version;
import net.oschina.app.improve.main.FeedBackActivity;
import net.oschina.app.improve.main.update.CheckUpdateManager;
import net.oschina.app.improve.main.update.DownloadService;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.SystemConfigView;
import net.oschina.app.improve.widget.togglebutton.ToggleButton;
import net.oschina.app.improve.widget.togglebutton.ToggleButton.OnToggleChanged;
import net.oschina.app.util.FileUtil;
import net.oschina.app.util.MethodsCompat;
import net.oschina.app.util.UIHelper;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 系统设置界面
 *
 * @author kymjs
 */
public class SettingsFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks, CheckUpdateManager.RequestPermissions {

    private static final int RC_EXTERNAL_STORAGE = 0x04;//存储权限

    @Bind(R.id.tv_cache_size)
    TextView mTvCacheSize;
    @Bind(R.id.rl_check_version)
    FrameLayout mRlCheck_version;
    @Bind(R.id.tb_double_click_exit)
    ToggleButton mTbDoubleClickExit;
    @Bind(R.id.setting_line_top)
    View mSettingLineTop;
    @Bind(R.id.setting_line_bottom)
    View mSettingLineBottom;
    @Bind(R.id.rl_cancel)
    FrameLayout mCancel;

    private Version mVersion;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container,
                false);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        mTbDoubleClickExit.setOnToggleChanged(new OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                AppContext.set(AppConfig.KEY_DOUBLE_CLICK_EXIT, on);
            }
        });

        view.findViewById(R.id.rl_clean_cache).setOnClickListener(this);
        view.findViewById(R.id.rl_double_click_exit).setOnClickListener(this);
        view.findViewById(R.id.rl_about).setOnClickListener(this);
        view.findViewById(R.id.rl_check_version).setOnClickListener(this);
        // view.findViewById(R.id.rl_exit).setOnClickListener(this);
        view.findViewById(R.id.rl_feedback).setOnClickListener(this);
        mCancel.setOnClickListener(this);

        SystemConfigView.show((ViewGroup) view.findViewById(R.id.lay_linear));
    }

    @Override
    public void initData() {
        if (AppContext.get(AppConfig.KEY_DOUBLE_CLICK_EXIT, true)) {
            mTbDoubleClickExit.setToggleOn();
        } else {
            mTbDoubleClickExit.setToggleOff();
        }
        calculateCacheSize();
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean login = AccountHelper.isLogin();
        if (!login) {
            mCancel.setVisibility(View.INVISIBLE);
            mSettingLineTop.setVisibility(View.INVISIBLE);
            mSettingLineBottom.setVisibility(View.INVISIBLE);
        } else {
            mCancel.setVisibility(View.VISIBLE);
            mSettingLineTop.setVisibility(View.VISIBLE);
            mSettingLineBottom.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 计算缓存的大小
     */
    private void calculateCacheSize() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getActivity().getFilesDir();
        File cacheDir = getActivity().getCacheDir();

        fileSize += FileUtil.getDirSize(filesDir);
        fileSize += FileUtil.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat
                    .getExternalCacheDir(getActivity());
            fileSize += FileUtil.getDirSize(externalCacheDir);
        }
        if (fileSize > 0)
            cacheSize = FileUtil.formatFileSize(fileSize);
        mTvCacheSize.setText(cacheSize);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.rl_clean_cache:
                onClickCleanCache();
                break;
            case R.id.rl_double_click_exit:
                mTbDoubleClickExit.toggle();
                break;
            case R.id.rl_feedback:
                //UIHelper.showSimpleBack(getActivity(), SimpleBackPage.FEED_BACK);
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(getContext());
                    return;
                }
                FeedBackActivity.show(getActivity());
                break;
            case R.id.rl_about:
                UIHelper.showAboutOSC(getActivity());
                break;
            case R.id.rl_check_version:
                onClickUpdate();
                break;
            case R.id.rl_cancel:
                // 清理所有缓存
                UIHelper.clearAppCache(false);
                // 注销操作
                AccountHelper.logout(mCancel, new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        //getActivity().finish();
                        mTvCacheSize.setText("0KB");
                        AppContext.showToastShort(getString(R.string.logout_success_hint));
                        mCancel.setVisibility(View.INVISIBLE);
                        mSettingLineTop.setVisibility(View.INVISIBLE);
                        mSettingLineBottom.setVisibility(View.INVISIBLE);
                    }
                });
                break;
            default:
                break;
        }

    }

    private void onClickUpdate() {
        CheckUpdateManager manager = new CheckUpdateManager(getActivity(), true);
        manager.setCaller(this);
        manager.checkUpdate();
    }

    private void onClickCleanCache() {
        DialogHelper.getConfirmDialog(getActivity(), "是否清空缓存?", new DialogInterface.OnClickListener
                () {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UIHelper.clearAppCache(true);
                mTvCacheSize.setText("0KB");
            }
        }).show();
    }

    @Override
    public void call(Version version) {
        this.mVersion = version;
        requestExternalStorage();
    }

    @SuppressLint("InlinedApi")
    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            DownloadService.startService(getActivity(), mVersion.getDownloadUrl());
        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        DialogHelper.getConfirmDialog(getActivity(), "温馨提示", "需要开启开源中国对您手机的存储权限才能下载安装，是否现在开启", "去开启", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
            }
        }).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}

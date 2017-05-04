package net.oschina.app.improve.main.update;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.Version;
import net.oschina.app.improve.utils.DialogHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 在线更新对话框
 * Created by haibin on 2017/5/4.
 */

public class UpdateActivity extends BaseActivity implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks,
        CheckUpdateManager.RequestPermissions {
    @Bind(R.id.tv_update_info)
    TextView mTextUpdateInfo;
    private Version mVersion;
    private static final int RC_EXTERNAL_STORAGE = 0x04;//存储权限

    public static void show(Fragment fragment, Version version) {
        Intent intent = new Intent(fragment.getActivity(), UpdateActivity.class);
        intent.putExtra("version", version);
        fragment.startActivityForResult(intent, 0x01);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_update;
    }

    @Override
    protected void initData() {
        super.initData();
        checkUpdate();
    }

    @OnClick({R.id.btn_update, R.id.btn_close})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                break;
            case R.id.btn_close:
                finish();
                break;
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
        DialogHelper.getConfirmDialog(this, "温馨提示", "需要开启开源中国对您手机的存储权限才能下载安装，是否现在开启", "去开启", "取消", true, new DialogInterface.OnClickListener() {
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

}

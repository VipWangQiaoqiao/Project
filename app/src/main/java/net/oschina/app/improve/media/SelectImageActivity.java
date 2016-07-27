package net.oschina.app.improve.media;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.media.config.ImageConfig;
import net.oschina.app.improve.media.contract.SelectImageContract;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by huanghaibin_dev
 * on 2016/7/13.
 */
@SuppressWarnings("All")
public class SelectImageActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, SelectImageContract.Operator {
    private static ImageConfig mConfig;
    private final int RC_CAMERA_PERM = 0x03;
    private final int RC_EXTERNAL_STORAGE = 0x04;

    private SelectImageContract.View mView;

    public static void showImage(Context context, ImageConfig config) {
        Intent intent = new Intent(context, SelectImageActivity.class);
        mConfig = config;
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_select_image;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        requestExternalStorage();
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    @Override
    public void requestCamera() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            if (mView != null) {
                mView.onOpenCameraSuccess();
            }
        } else {
            EasyPermissions.requestPermissions(this, "", RC_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    @Override
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (mView == null) {
                handleView();
            } else {
                mView.onReadExternalStorageSuccess();
            }
        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onBack() {
        onSupportNavigateUp();
    }

    @Override
    public void setDataView(SelectImageContract.View view) {
        mView = view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == RC_EXTERNAL_STORAGE) {
            if (mView != null)
                mView.onExternalStorageDenied();
            getConfirmDialog(this, "没有权限, 你需要去设置中开启读取手机存储权限.", "去设置", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                    finish();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        } else {
            if (mView != null)
                mView.onCameraPermissionDenied();
            getConfirmDialog(this, "没有权限, 你需要去设置中开启相机权限.", "去设置", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        }

    }

    private void handleView() {
        try {
            Fragment fragment = SelectImageFragment.getInstance(mConfig);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_content, fragment)
                    .commitNowAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AlertDialog.Builder getConfirmDialog(Context context, String message, String
            okString, String cancelString,
                                                       DialogInterface.OnClickListener
                                                               okClickListener,
                                                       DialogInterface.OnClickListener
                                                               cancelClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(okString, okClickListener);
        builder.setNegativeButton(cancelString, cancelClickListener);
        builder.setCancelable(false);
        return builder;
    }
}

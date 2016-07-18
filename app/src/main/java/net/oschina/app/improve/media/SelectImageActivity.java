package net.oschina.app.improve.media;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;


import net.oschina.app.R;
import net.oschina.app.improve.media.config.ImageConfig;
import net.oschina.app.improve.media.contract.ISelectImageContract;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by huanghaibin_dev
 * on 2016/7/13.
 */
@SuppressWarnings("All")
public class SelectImageActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, ISelectImageContract.Operator {
    private static ImageConfig mConfig;
    private final int RC_CAMERA_PERM = 0x03;
    private final int RC_EXTERNAL_STORAGE = 0x04;

    private ISelectImageContract.View mView;

    public static void showImage(Activity activity, ImageConfig config) {
        Intent intent = new Intent(activity, SelectImageActivity.class);
        mConfig = config;
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        initData();
    }

    private void initData() {
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
        if (mView != null) {
            if (requestCode == RC_EXTERNAL_STORAGE) {
                mView.onExternalStorageDenied();
            } else {
                mView.onCameraPermissionDenied();
            }
        }
    }

    private void handleView() {
        try {
            SelectImageFragment fragment = SelectImageFragment.getInstance(mConfig);
            mView = fragment;
            FragmentTransaction trans = getSupportFragmentManager()
                    .beginTransaction();
            trans.replace(R.id.fl_content, fragment);
            trans.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mConfig = null;
        mView = null;
        super.onDestroy();
    }
}

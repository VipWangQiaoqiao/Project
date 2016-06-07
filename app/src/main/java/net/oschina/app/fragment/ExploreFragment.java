package net.oschina.app.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.ui.FindUserActivity;
import net.oschina.app.ui.ShakeActivity;
import net.oschina.app.util.UIHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 发现页面
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年11月4日 下午3:34:07
 */

public class ExploreFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks {

    public static final int CAMERA_PERM = 1;
    @Bind(R.id.rl_soft)
    View mRlActive;

    @Bind(R.id.rl_find_osc)
    View mFindOSCer;

    @Bind(R.id.rl_city)
    View mCity;


    @Bind(R.id.rl_scan)
    View mScan;

    @Bind(R.id.rl_shake)
    View mShake;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_explore, null);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_soft:
                UIHelper.showSimpleBack(getActivity(),
                        SimpleBackPage.OPENSOURCE_SOFTWARE);
                break;
            case R.id.rl_find_osc:
                showFindUser();
                break;
            case R.id.rl_city:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SAME_CITY);
                break;
            case R.id.rl_scan:

                cameraTask();

                break;
            case R.id.rl_shake:
                showShake();
                break;
            default:
                break;
        }
    }

    @AfterPermissionGranted(CAMERA_PERM)
    private void cameraTask() {
        if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
            UIHelper.showScanActivity(getActivity());
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, getString(R.string.str_request_camera_message),
                    CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    private void showShake() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ShakeActivity.class);
        getActivity().startActivity(intent);
    }

    private void showFindUser() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), FindUserActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    public void initView(View view) {
        mRlActive.setOnClickListener(this);

        mFindOSCer.setOnClickListener(this);
        mCity.setOnClickListener(this);
        mScan.setOnClickListener(this);
        mShake.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        EasyPermissions.checkDeniedPermissionsNeverAskAgain(this,
                getString(R.string.str_request_camera_message),
                R.string.str_action_submic, R.string.str_action_cancle, perms);

    }
}

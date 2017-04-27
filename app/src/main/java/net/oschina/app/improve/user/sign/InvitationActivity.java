package net.oschina.app.improve.user.sign;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.target.Target;

import net.oschina.app.R;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.improve.media.ImageGalleryActivity;
import net.oschina.app.improve.utils.DialogHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 邀请函
 * Created by haibin on 2017/4/11.
 */
public class InvitationActivity extends BaseBackActivity implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {
    private ShareDialog mShareDialog;
    @Bind(R.id.iv_invitation)
    ImageView mImageInvitation;
    private String mUrl;
    private Bitmap mBitmap;
    private ProgressDialog mWaitDialog;

    public static void show(Context context, String url) {
        Intent intent = new Intent(context, InvitationActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_invitation;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mShareDialog = new ShareDialog(this, -1);
        mUrl = getIntent().getStringExtra("url");
    }

    @Override
    protected void initData() {
        super.initData();
        tryLoadBitmap(false);
    }

    private void tryLoadBitmap(final boolean showDialog) {
        getImageLoader().load(mUrl)
                .asBitmap()
                .fitCenter()
                .into(mImageInvitation);
    }

    @OnClick({R.id.btn_share, R.id.iv_invitation})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share:
                saveToFileByPermission();
                break;
            case R.id.iv_invitation:
                ImageGalleryActivity.show(this, mUrl);
                break;
        }
    }

    private void showWaitDialog() {
        if (mWaitDialog == null) {
            mWaitDialog = DialogHelper.getProgressDialog(this);
            mWaitDialog.setMessage("正在加载图片");
            mWaitDialog.setCancelable(true);
        }
        mWaitDialog.show();
    }

    private void hideDialog() {
        if (mWaitDialog == null) return;
        mWaitDialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mShareDialog != null) {
            mShareDialog.dismiss();
        }
    }

    private static final int PERMISSION_ID = 0x0001;
    @SuppressWarnings("unused")
    @AfterPermissionGranted(PERMISSION_ID)
    public void saveToFileByPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            showWaitDialog();
            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Bitmap thumbBitmap =
                                getImageLoader()
                                        .load(mUrl)
                                        .asBitmap()
                                        .fitCenter()
                                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideDialog();
                                mShareDialog.bitmap(thumbBitmap);
                                mShareDialog.show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            EasyPermissions.requestPermissions(this, "请授予文件读写权限", PERMISSION_ID, permissions);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        DialogHelper.getConfirmDialog(this, "", "没有权限, 你需要去设置中开启读取手机存储权限.", "去设置", "取消", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                //finish();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}

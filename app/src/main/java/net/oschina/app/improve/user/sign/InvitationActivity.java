package net.oschina.app.improve.user.sign;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.target.Target;

import net.oschina.app.R;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.improve.media.ImageGalleryActivity;
import net.oschina.app.improve.utils.DialogHelper;

import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 邀请函
 * Created by haibin on 2017/4/11.
 */
public class InvitationActivity extends BaseBackActivity implements View.OnClickListener {
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
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
}

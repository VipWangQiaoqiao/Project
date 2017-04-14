package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BackActivity;
import net.oschina.app.improve.dialog.ShareDialog;
import net.oschina.app.improve.media.ImageGalleryActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 邀请函
 * Created by haibin on 2017/4/11.
 */
public class InvitationActivity extends BackActivity implements View.OnClickListener {
    private ShareDialog mShareDialog;
    @Bind(R.id.iv_invitation)
    ImageView mImageInvitation;
    private String mUrl;

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
        mShareDialog = new ShareDialog(this);
        mUrl = getIntent().getStringExtra("url");
        mShareDialog.imageUrl(mUrl);
    }

    @Override
    protected void initData() {
        super.initData();
        getImageLoader().load(mUrl)
                .asBitmap()
                .into(mImageInvitation);
    }

    @OnClick({R.id.btn_share, R.id.iv_invitation})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share:
                mShareDialog.show();
                break;
            case R.id.iv_invitation:
                ImageGalleryActivity.show(this, mUrl);
                break;
        }
    }
}

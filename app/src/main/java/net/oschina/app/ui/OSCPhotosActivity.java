package net.oschina.app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.ui.dialog.ImageMenuDialog;
import net.oschina.app.util.ImageUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.widget.TouchImageView;

import java.io.IOException;

/**
 * 图片预览界面
 */
public class OSCPhotosActivity extends BaseActivity {

    public static final String BUNDLE_KEY_IMAGES = "bundle_key_images";
    private TouchImageView mTouchImageView;
    private ProgressBar mProgressBar;
    private ImageView mOption;
    private String mImageUrl;

    public static void showImagePreview(Context context,
                                        String imageUrl) {
        Intent intent = new Intent(context, OSCPhotosActivity.class);
        intent.putExtra(BUNDLE_KEY_IMAGES, imageUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_browse);
        mImageUrl = getIntent().getStringExtra(BUNDLE_KEY_IMAGES);

        mTouchImageView = (TouchImageView) findViewById(R.id.photoview);

        mTouchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading);

        mOption = (ImageView) findViewById(R.id.iv_more);
        mOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionMenu();
            }
        });

        loadImage(mTouchImageView, mImageUrl);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void showOptionMenu() {
        final ImageMenuDialog dialog = new ImageMenuDialog(this);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setOnMenuClickListener(new ImageMenuDialog.OnMenuClickListener() {
            @Override
            public void onClick(TextView menuItem) {
                if (menuItem.getId() == R.id.menu1) {
                    saveImg();
                } else if (menuItem.getId() == R.id.menu2) {
                    sendTweet();
                } else if (menuItem.getId() == R.id.menu3) {
                    copyUrl();
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 复制链接
     */
    private void copyUrl() {
        TDevice.copyTextToBoard(mImageUrl);
        AppContext.showToastShort("已复制到剪贴板");
    }

    /**
     * 发送到动弹
     */
    private void sendTweet() {
        finish();
    }

    /**
     * 保存图片
     */
    private void saveImg() {
        final String filePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH
                + getFileName(mImageUrl);

        Drawable drawable = mTouchImageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            try {
                ImageUtils.saveImageToSD(this, filePath, bitmap, 100);
                AppContext.showToastShort(getString(R.string.tip_save_image_suc));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Core.getKJBitmap().saveImage(this, mImageUrl, filePath);
    }

    private String getFileName(String imgUrl) {
        int index = imgUrl.lastIndexOf('/') + 1;
        if (index == -1) {
            return System.currentTimeMillis() + ".jpeg";
        }
        return imgUrl.substring(index);
    }

    /**
     * Load the item's thumbnail image into our {@link ImageView}.
     */
    private void loadImage(final ImageView mHeaderImageView, final String imageUrl) {
        Glide.with(this).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mHeaderImageView.setImageBitmap(resource);
                mProgressBar.setVisibility(View.GONE);
                mHeaderImageView.setVisibility(View.VISIBLE);
                mOption.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}

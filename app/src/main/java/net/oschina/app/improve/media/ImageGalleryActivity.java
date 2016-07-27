package net.oschina.app.improve.media;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.Target;

import net.oschina.app.R;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.tweet.service.PicturesCompress;
import net.oschina.app.improve.utils.StreamUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * 图片预览Activity
 */
public class ImageGalleryActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    public static final String KEY_IMAGE = "images";
    public static final String KEY_POSITION = "position";
    public static final String KEY_NEED_SAVE = "save";
    private ViewPager mImagePager;
    private TextView mIndexText;
    private String[] mImageSources;
    private int mCurPosition;
    private boolean mNeedSaveLocal;

    public static void show(Context context, List<String> imageList, int position) {
        show(context, imageList, position, true);
    }

    public static void show(Context context, List<String> imageList, int position, boolean needSaveLocal) {
        if (imageList == null || imageList.size() == 0)
            return;
        String[] images = new String[imageList.size()];
        imageList.toArray(images);
        show(context, images, position, needSaveLocal);
    }

    public static void show(Context context, String[] images, int position) {
        show(context, images, position, true);
    }

    public static void show(Context context, String[] images, int position, boolean needSaveLocal) {
        if (images == null || images.length == 0)
            return;
        Intent intent = new Intent(context, ImageGalleryActivity.class);
        intent.putExtra(KEY_IMAGE, images);
        intent.putExtra(KEY_POSITION, position);
        intent.putExtra(KEY_NEED_SAVE, needSaveLocal);
        context.startActivity(intent);
    }

    public static void show(Context context, String images) {
        if (images == null)
            return;
        show(context, new String[]{images}, 0);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mImageSources = bundle.getStringArray(KEY_IMAGE);
        mCurPosition = bundle.getInt(KEY_POSITION, 0);
        mNeedSaveLocal = bundle.getBoolean(KEY_NEED_SAVE, true);
        return mImageSources != null;
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_image_gallery;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        mImagePager = (ViewPager) findViewById(R.id.vp_image);
        mIndexText = (TextView) findViewById(R.id.tv_index);
        mImagePager.addOnPageChangeListener(this);
        if (mNeedSaveLocal)
            findViewById(R.id.iv_save).setOnClickListener(this);
        else
            findViewById(R.id.iv_save).setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        int len = mImageSources.length;
        if (mCurPosition < 0 || mCurPosition >= len)
            mCurPosition = 0;

        // If only one, we not need the text to show
        if (len == 1)
            mIndexText.setVisibility(View.GONE);

        mImagePager.setAdapter(new ViewPagerAdapter());
        mImagePager.setCurrentItem(mCurPosition);
        // First we call to init the TextView
        onPageSelected(mCurPosition);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_save:
                saveToFile();
                break;
        }
    }

    private void saveToFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.gallery_save_file_not_have_external_storage, Toast.LENGTH_SHORT).show();
            return;
        }

        String path = mImageSources[mCurPosition];

        // In this save max image size is source
        final Future<File> future = getImageLoader().load(path).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    File sourceFile = future.get();
                    String extension = PicturesCompress.getExtension(sourceFile.getAbsolutePath());
                    String extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            .getAbsolutePath() + File.separator + "开源中国";
                    File extDirFile = new File(extDir);
                    if (!extDirFile.exists()) {
                        if (!extDirFile.mkdirs()) {
                            callSaveStatus(false, null);
                            return;
                        }
                    }
                    final File saveFile = new File(extDirFile, String.format("IMG_%s.%s", System.currentTimeMillis(), extension));
                    final boolean isSuccess = StreamUtils.copyFile(sourceFile, saveFile);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callSaveStatus(isSuccess, saveFile);
                        }
                    });
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void callSaveStatus(boolean success, File savePath) {
        if (success) {
            // notify
            Uri uri = Uri.fromFile(savePath);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            Toast.makeText(ImageGalleryActivity.this, R.string.gallery_save_file_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ImageGalleryActivity.this, R.string.gallery_save_file_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurPosition = position;
        mIndexText.setText(String.format("%s/%s", (position + 1), mImageSources.length));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageSources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImagePreviewView view = new ImagePreviewView(ImageGalleryActivity.this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            getImageLoader().load(mImageSources[position]).into(view);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImagePreviewView) object);
        }
    }
}

package net.oschina.app.ui;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.RecyclingPagerAdapter;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.fragment.TweetPubFragment;
import net.oschina.app.ui.dialog.ImageMenuDialog;
import net.oschina.app.ui.dialog.ImageMenuDialog.OnMenuClickListener;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.HackyViewPager;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.http.core.KJAsyncTask;

import uk.co.senab.photoview.PhotoView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ImagePreviewActivity extends BaseActivity implements
        OnPageChangeListener {

    public static final String BUNDLE_KEY_IMAGES = "bundle_key_images";
    private static final String BUNDLE_KEY_INDEX = "bundle_key_index";
    private HackyViewPager mViewPager;
    private SamplePagerAdapter mAdapter;
    private TextView mTvImgIndex;
    private ImageView mIvMore;
    private int mCurrentPostion = 0;
    private String[] mImageUrls;

    private KJBitmap kjb;

    public static void showImagePrivew(Context context, int index,
            String[] images) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);

        intent.putExtra(BUNDLE_KEY_IMAGES, images);
        intent.putExtra(BUNDLE_KEY_INDEX, index);
        context.startActivity(intent);
    }

    @Override
    protected boolean hasActionBar() {
        getActionBar().hide();
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_preview;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        kjb = KJBitmap.create();
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);

        mImageUrls = getIntent().getStringArrayExtra(BUNDLE_KEY_IMAGES);
        int index = getIntent().getIntExtra(BUNDLE_KEY_INDEX, 0);

        mAdapter = new SamplePagerAdapter(mImageUrls);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(index);

        mTvImgIndex = (TextView) findViewById(R.id.tv_img_index);
        mIvMore = (ImageView) findViewById(R.id.iv_more);
        mIvMore.setOnClickListener(this);

        onPageSelected(index);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.iv_more:
            showOptionMenu();
            break;
        default:
            break;
        }
    }

    @Override
    public void initView() {}

    @Override
    public void initData() {}

    private void showOptionMenu() {
        final ImageMenuDialog dialog = new ImageMenuDialog(this);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setOnMenuClickListener(new OnMenuClickListener() {
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

    private void copyUrl() {

    }

    /**
     * 发送到动弹
     */
    private void sendTweet() {
        if (mAdapter != null && mAdapter.getCount() > 0) {
            String imgUrl = mAdapter.getItem(mCurrentPostion);
            Bundle bundle = new Bundle();
            bundle.putString(TweetPubFragment.FROM_IMAGEPAGE_KEY, imgUrl);
            UIHelper.showSimpleBack(this, SimpleBackPage.TWEET_PUB, bundle);
            finish();
        }
    }

    /**
     * 保存图片
     */
    private void saveImg() {
        if (mAdapter != null && mAdapter.getCount() > 0) {
            final String imgUrl = mAdapter.getItem(mCurrentPostion);
            final String filePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH
                    + getFileName(imgUrl);
            KJAsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    kjb.saveImage(imgUrl, filePath);
                }
            });
            AppContext.showToastShort(getString(R.string.tip_save_image_suc,
                    filePath));
        } else {
            AppContext.showToastShort(R.string.tip_save_image_faile);
        }
    }

    private String getFileName(String imgUrl) {
        int index = imgUrl.lastIndexOf('/') + 1;
        if (index == -1) {
            return System.currentTimeMillis() + ".jpeg";
        }
        return imgUrl.substring(index);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {}

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {}

    @Override
    public void onPageSelected(int idx) {
        mCurrentPostion = idx;
        if (mImageUrls != null && mImageUrls.length > 1) {
            if (mTvImgIndex != null) {
                mTvImgIndex.setText((mCurrentPostion + 1) + "/"
                        + mImageUrls.length);
            }
        }
    }

    static class SamplePagerAdapter extends RecyclingPagerAdapter {

        private String[] images = new String[] {};

        SamplePagerAdapter(String[] images) {
            this.images = images;
        }

        public String getItem(int position) {
            return images[position];
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.image_preview_item, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            final ProgressBar bar = vh.progress;
            KJBitmap kjbitmap = KJBitmap.create();
            kjbitmap.setCallback(new BitmapCallBack() {
                @Override
                public void onPreLoad(View view) {
                    super.onPreLoad(view);
                    bar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish(View view) {
                    super.onFinish(view);
                    bar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Exception arg0) {
                    AppContext.showToast(R.string.tip_load_image_faile);
                }
            });
            kjbitmap.display(vh.image, images[position]);
            return convertView;
        }

        static class ViewHolder {
            PhotoView image;
            ProgressBar progress;

            ViewHolder(View view) {
                image = (PhotoView) view.findViewById(R.id.photoview);
                progress = (ProgressBar) view.findViewById(R.id.progress);
            }
        }
    }
}

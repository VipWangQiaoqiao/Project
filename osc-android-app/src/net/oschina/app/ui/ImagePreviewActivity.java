package net.oschina.app.ui;

import uk.co.senab.photoview.PhotoView;
import net.oschina.app.R;
import net.oschina.app.adapter.RecyclingPagerAdapter;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.widget.HackyViewPager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class ImagePreviewActivity extends BaseActivity implements
		OnPageChangeListener {

	public static final String BUNDLE_KEY_IMAGES = "bundle_key_images";
	private static final String BUNDLE_KEY_INDEX = "bundle_key_index";
	private HackyViewPager mViewPager;
	private SamplePagerAdapter mAdapter;
	private int mCurrentPostion = 0;
	private String[] mImageUrls;

	public static void showImagePrivew(Context context, int index,
			String[] images) {
		Intent intent = new Intent(context, ImagePreviewActivity.class);
		intent.putExtra(BUNDLE_KEY_IMAGES, images);
		intent.putExtra(BUNDLE_KEY_INDEX, index);
		context.startActivity(intent);
	}

	@Override
	protected boolean hasBackButton() {
		return true;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_image_preview;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		super.init(savedInstanceState);
		mViewPager = (HackyViewPager) findViewById(R.id.view_pager);

		mImageUrls = getIntent().getStringArrayExtra(BUNDLE_KEY_IMAGES);
		int index = getIntent().getIntExtra(BUNDLE_KEY_INDEX, 0);

		mAdapter = new SamplePagerAdapter(mImageUrls);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setCurrentItem(index);
		
		onPageSelected(index);
	}

	static class SamplePagerAdapter extends RecyclingPagerAdapter {

		private String[] images = new String[] {};

		private DisplayImageOptions options;

		SamplePagerAdapter(String[] images) {
			this.images = images;
			options = new DisplayImageOptions.Builder().cacheInMemory(true)
					.postProcessor(new BitmapProcessor() {

						@Override
						public Bitmap process(Bitmap arg0) {
							return arg0;
						}
					}).cacheOnDisk(true).build();
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
			bar.setVisibility(View.GONE);
			ImageLoader.getInstance().displayImage(images[position], vh.image,
					options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							// bar.show();
							bar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// bar.hide();
							bar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							bar.setVisibility(View.GONE);
						}
					});
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

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int idx) {
		mCurrentPostion = idx;
		if (mImageUrls != null && mImageUrls.length > 1) {
			setActionBarTitle((mCurrentPostion + 1) + "/" + mImageUrls.length);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
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
